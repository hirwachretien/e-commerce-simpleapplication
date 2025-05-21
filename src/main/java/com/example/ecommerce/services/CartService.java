package com.example.ecommerce.services;

import com.example.ecommerce.entities.CartItem;
import com.example.ecommerce.entities.Product;
import com.example.ecommerce.entities.User;
import com.example.ecommerce.repositories.CartItemRepository;
import com.example.ecommerce.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    @Transactional
    public void addToCart(User user, Long productId, int quantity) {
        // Check if the product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if the item is already in the cart
        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProductId(user, productId);

        if (existingItem.isPresent()) {
            // Update quantity if already in cart
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem(user, product, quantity);
            cartItemRepository.save(newItem);
        }
    }

    @Transactional
    public void updateCartItemQuantity(Long cartItemId, int quantity, User user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Verify the item belongs to the current user
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        if (quantity <= 0) {
            // Remove item if quantity is 0 or negative
            cartItemRepository.delete(cartItem);
        } else {
            // Update quantity
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeFromCart(Long cartItemId, User user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Verify the item belongs to the current user
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    public BigDecimal getCartTotal(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        return cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}