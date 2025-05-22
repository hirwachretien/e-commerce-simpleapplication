package com.example.ecommerce.controllers;

import com.example.ecommerce.entities.Order;
import com.example.ecommerce.entities.OrderItem;
import com.example.ecommerce.repositories.OrderItemRepository;
import com.example.ecommerce.repositories.OrderRepository;
import com.example.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @GetMapping("/user-dashboard")
    public String userDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        model.addAttribute("username", username);
        return "user-dashboard";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        model.addAttribute("adminName", username);
        return "admin-dashboard";
    }

    @GetMapping("/admin/customers")
    public String viewCustomers(Model model) {
        List<com.example.ecommerce.entities.User> customers = userRepository.findAll();
        model.addAttribute("customers", customers);
        return "customer-list";
    }

    @GetMapping("/admin/reports")
    public String viewSalesReport(Model model) {
        List<Order> allOrders = orderRepository.findAll();
        List<OrderItem> allOrderItems = orderItemRepository.findAll();

        long totalOrders = allOrders.size();
        BigDecimal totalRevenue = allOrders.stream()
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalProductsSold = allOrderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        List<Order> recentOrders = allOrders.stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .limit(5)
                .toList();

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalProductsSold", totalProductsSold);
        model.addAttribute("recentOrders", recentOrders);

        return "sales-report";
    }

    // Removed the /admin/orders mapping to resolve duplicate handler conflict.
}