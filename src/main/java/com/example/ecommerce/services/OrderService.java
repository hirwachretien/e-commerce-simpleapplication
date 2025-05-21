package com.example.ecommerce.services;

import com.example.ecommerce.entities.*;
import com.example.ecommerce.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Transactional
    public Order createOrderFromCart(User user, String shippingAddress) {
        // Get cart items
        List<CartItem> cartItems = cartService.getCartItems(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot create order.");
        }

        // Create a new order
        Order order = new Order(user);
        order.setShippingAddress(shippingAddress);

        // Add items from cart to order
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(cartItem.getProduct(), cartItem.getQuantity());
            order.addOrderItem(orderItem);
        }

        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Clear the cart
        cartService.clearCart(user);

        return savedOrder;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}