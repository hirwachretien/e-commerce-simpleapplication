package com.example.ecommerce.controllers;

import com.example.ecommerce.entities.CartItem;
import com.example.ecommerce.entities.Order;
import com.example.ecommerce.entities.User;
import com.example.ecommerce.services.CartService;
import com.example.ecommerce.services.OrderService;
import com.example.ecommerce.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    public OrderController(OrderService orderService, CartService cartService, UserService userService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
    }

    // View checkout page
    @GetMapping("/checkout")
    public String checkout(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        List<CartItem> cartItems = cartService.getCartItems(user);

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartService.getCartTotal(user));

        return "checkout";
    }

    // Process the order
    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam String shippingAddress,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByUsername(authentication.getName());

        try {
            Order order = orderService.createOrderFromCart(user, shippingAddress);
            redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
            return "redirect:/orders/confirmation/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }

    // Order confirmation page
    @GetMapping("/orders/confirmation/{orderId}")
    public String orderConfirmation(@PathVariable Long orderId, Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());

        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to order");
        }

        model.addAttribute("order", order);
        return "order-confirmation";
    }

    // View all orders for a user
    @GetMapping("/orders")
    public String viewOrders(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        List<Order> orders = orderService.getUserOrders(user);

        model.addAttribute("orders", orders);
        return "orders";
    }

    // View single order details (user or admin access)
    @GetMapping("/orders/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());

        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId()) &&
                !user.getRoles().contains("ADMIN")) {
            throw new RuntimeException("Unauthorized access to order");
        }

        model.addAttribute("order", order);
        return "order-details";
    }

    // Admin: View all orders
    @GetMapping("/admin/orders")
    public String adminViewOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin-orders";
    }
    // Admin: View a specific order in detail using shared view
    @GetMapping("/admin/orders/{orderId}/details")
    public String adminViewOrderDetails(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        model.addAttribute("order", order);
        model.addAttribute("isAdminView", true);
        return "order-details"; // ✅ Use the same template
    }


    // Admin: Update order status
    @PostMapping("/admin/orders/{orderId}/status")
    public String updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        try {
            orderService.updateOrderStatus(orderId, status);
            redirectAttributes.addFlashAttribute("success", "Order status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/orders";
    }
}