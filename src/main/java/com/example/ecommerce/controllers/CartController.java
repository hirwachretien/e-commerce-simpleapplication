package com.example.ecommerce.controllers;

import com.example.ecommerce.entities.CartItem;
import com.example.ecommerce.entities.User;
import com.example.ecommerce.services.CartService;
import com.example.ecommerce.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public String viewCart(Model model, Authentication authentication) {
        // Get the current logged-in user
        User currentUser = userService.findByUsername(authentication.getName());

        // Get cart items for the user
        List<CartItem> cartItems = cartService.getCartItems(currentUser);
        BigDecimal cartTotal = cartService.getCartTotal(currentUser);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);

        return "cart"; // Thymeleaf template for cart
    }

    @PostMapping("/add/{productId}")
    public String addToCart(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User currentUser = userService.findByUsername(authentication.getName());

        try {
            cartService.addToCart(currentUser, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Product added to cart successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/products"; // Redirect back to products page
    }

    @PostMapping("/update/{cartItemId}")
    public String updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam int quantity,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User currentUser = userService.findByUsername(authentication.getName());

        try {
            cartService.updateCartItemQuantity(cartItemId, quantity, currentUser);
            redirectAttributes.addFlashAttribute("success", "Cart updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove/{cartItemId}")
    public String removeFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User currentUser = userService.findByUsername(authentication.getName());

        try {
            cartService.removeFromCart(cartItemId, currentUser);
            redirectAttributes.addFlashAttribute("success", "Item removed from cart successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User currentUser = userService.findByUsername(authentication.getName());

        try {
            cartService.clearCart(currentUser);
            redirectAttributes.addFlashAttribute("success", "Cart cleared successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/cart";
    }
}