package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.domain.Order;
import com.ecommerce.orderservice.domain.OrderLine;
import com.ecommerce.orderservice.dto.OrderLineRequest;
import com.ecommerce.orderservice.dto.OrderRequest;
import com.ecommerce.orderservice.dto.ProductResponse;
import com.ecommerce.orderservice.exception.OrderNotFoundException;
import com.ecommerce.orderservice.messaging.OrderEventPublisher;
import com.ecommerce.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderValidationService validationService;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                         OrderValidationService validationService,
                         OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.validationService = validationService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Order createOrder(OrderRequest request) {
        System.out.println(">>> createOrder started for customer: " + request.customerId());
        List<OrderLine> lines = request.lines().stream()
                .map(this::toOrderLine)
                .toList();

        System.out.println(">>> order lines validated and fetched. Saving order...");
        Order order = Order.create(request.customerId(), lines);
        Order saved = orderRepository.save(order);
        System.out.println(">>> order saved. Publishing event...");

        // Publish the domain event AFTER the order is committed, so that
        // Payment Service never receives an order.created event for an
        // order that failed to persist.
        eventPublisher.publishOrderCreated(saved, request.paymentMethod());
        System.out.println(">>> event published.");

        return saved;
    }

    private OrderLine toOrderLine(OrderLineRequest lineRequest) {
        ProductResponse product = validationService.validateAndFetchProduct(lineRequest);
        return new OrderLine(lineRequest.productId(), lineRequest.quantity(), product.price());
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersForCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
