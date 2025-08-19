package com.bookstore.service;

import com.bookstore.entity.Order;
import com.bookstore.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendOrderConfirmationEmail(Order order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(order.getUser().getEmail());
            message.setSubject("Order Confirmation - " + order.getOrderNumber());
            message.setText(buildOrderConfirmationText(order));

            mailSender.send(message);
            log.info("Order confirmation email sent to: {}", order.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send order confirmation email", e);
        }
    }

    @Async
    public void sendOrderStatusUpdateEmail(Order order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(order.getUser().getEmail());
            message.setSubject("Order Status Update - " + order.getOrderNumber());
            message.setText(buildOrderStatusUpdateText(order));

            mailSender.send(message);
            log.info("Order status update email sent to: {}", order.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send order status update email", e);
        }
    }

    @Async
    public void sendWelcomeEmail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Welcome to Digital Bookstore!");
            message.setText(buildWelcomeText(user));

            mailSender.send(message);
            log.info("Welcome email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email", e);
        }
    }

    private String buildOrderConfirmationText(Order order) {
        return String.format("""
            Dear %s,
            
            Thank you for your order! Your order has been confirmed.
            
            Order Details:
            Order Number: %s
            Total Amount: $%.2f
            Order Date: %s
            
            Your order will be processed and shipped soon.
            
            Best regards,
            Digital Bookstore Team
            """,
                order.getUser().getName(),
                order.getOrderNumber(),
                order.getTotalPrice(),
                order.getOrderDate()
        );
    }

    private String buildOrderStatusUpdateText(Order order) {
        return String.format("""
            Dear %s,
            
            Your order status has been updated.
            
            Order Number: %s
            New Status: %s
            
            You can track your order by logging into your account.
            
            Best regards,
            Digital Bookstore Team
            """,
                order.getUser().getName(),
                order.getOrderNumber(),
                order.getStatus()
        );
    }

    private String buildWelcomeText(User user) {
        return String.format("""
            Dear %s,
            
            Welcome to Digital Bookstore!
            
            Your account has been successfully created. You can now:
            - Browse our extensive collection of books
            - Add books to your cart and wishlist
            - Place orders and track them
            - Write reviews for books you've purchased
            
            Happy reading!
            
            Best regards,
            Digital Bookstore Team
            """,
                user.getName()
        );
    }
}
