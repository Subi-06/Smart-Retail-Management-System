package com.smartretail.designpatterns.structural.facade;

import com.smartretail.designpatterns.behavioral.chain.PurchaseValidationHandler;
import com.smartretail.designpatterns.behavioral.chain.PurchaseValidationPipeline;
import com.smartretail.designpatterns.behavioral.mediator.SmartRetailMediator;
import com.smartretail.designpatterns.behavioral.state.OrderStateManager;
import com.smartretail.designpatterns.behavioral.template.StandardRetailBillingWorkflow;
import com.smartretail.designpatterns.creational.builder.BillBuilder;
import com.smartretail.dto.CartDTO;
import com.smartretail.dto.CheckoutDTOs;
import com.smartretail.entity.*;
import com.smartretail.entity.enums.OrderStatus;
import com.smartretail.entity.enums.PaymentMethod;
import com.smartretail.exception.ResourceNotFoundException;
import com.smartretail.repository.*;
import com.smartretail.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FACADE PATTERN
 * Unified high-level interface providing a simplified entry point for checkout.
 * Coordinates Cart, Discount, Bill, Payment, Inventory, Order, and Notification subsystems.
 */
@Component
public class CheckoutFacade {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private PurchaseValidationPipeline validationPipeline;

    @Autowired
    private StandardRetailBillingWorkflow billingWorkflow;

    @Autowired
    private SmartRetailMediator mediator;

    @Autowired
    private OrderStateManager orderStateManager;

    @Transactional
    public CheckoutDTOs.CheckoutResponse checkout(CheckoutDTOs.CheckoutRequest request) {
        List<String> traces = new ArrayList<>();
        traces.add("FACADE: checkoutFacade.checkout() initiated");

        // 1. Fetch User & Cart
        User customer = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.getUserId()));
        CartDTO cartDTO = cartService.getCartByUserId(request.getUserId());
        if (cartDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Please add products before checking out.");
        }

        // 2. Fetch Coupon if specified
        Discount appliedCoupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            appliedCoupon = discountRepository.findByCodeIgnoreCaseAndActiveTrue(request.getCouponCode().trim())
                    .orElse(null);
        }

        // 3. CHAIN OF RESPONSIBILITY: Purchase Validation Pipeline
        traces.add("CHAIN OF RESPONSIBILITY: Executing StockValidator -> MembershipValidator -> CouponValidator -> ApprovalHandler");
        List<String> validationMessages = new ArrayList<>();
        PurchaseValidationHandler.PurchaseContext validationContext = PurchaseValidationHandler.PurchaseContext.builder()
                .customer(customer)
                .cartItems(cartDTO.getItems())
                .subtotal(cartDTO.getSubtotal())
                .couponCode(request.getCouponCode())
                .appliedDiscount(appliedCoupon)
                .productRepository(productRepository)
                .validationMessages(validationMessages)
                .build();
        validationPipeline.buildValidationChain().validate(validationContext);

        // 4. Create Order in ORDER_PLACED state
        traces.add("STATE PATTERN: Order initialized in state ORDER_PLACED");
        Order order = Order.builder()
                .user(customer)
                .totalAmount(cartDTO.getSubtotal())
                .discountAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .finalAmount(cartDTO.getSubtotal())
                .status(OrderStatus.ORDER_PLACED)
                .paymentMethod(request.getPaymentMethod())
                .appliedCoupon(request.getCouponCode())
                .shippingAddress(request.getShippingAddress() != null ? request.getShippingAddress() : customer.getAddress())
                .createdAt(LocalDateTime.now())
                .build();
        order = orderRepository.save(order);

        // Convert cart items to order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartDTO.CartItemDTO cItem : cartDTO.getItems()) {
            Product product = productRepository.findById(cItem.getProductId()).orElseThrow();
            OrderItem oItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cItem.getQuantity())
                    .price(cItem.getPrice())
                    .subtotal(cItem.getSubtotal())
                    .build();
            orderItems.add(oItem);
        }
        order.setItems(orderItems);

        // 5. TEMPLATE METHOD & STRATEGY & ABSTRACT FACTORY / ADAPTER: Billing & Payment Workflow
        traces.add("TEMPLATE METHOD: Executing StandardRetailBillingWorkflow");
        traces.add("STRATEGY PATTERN: Resolved optimal discount algorithm (Membership + Coupon + Bulk)");
        traces.add("ABSTRACT FACTORY / ADAPTER: Instantiating PaymentProcessor for method: " + request.getPaymentMethod());

        Map<String, String> paymentDetails = new HashMap<>();
        paymentDetails.put("upiVpa", request.getUpiVpa());
        paymentDetails.put("cardNumber", request.getCardNumber());
        paymentDetails.put("cardCvv", request.getCardCvv());

        var billingResult = billingWorkflow.executeBillingWorkflow(
                order,
                orderItems,
                customer,
                appliedCoupon,
                request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.UPI,
                paymentDetails
        );

        Payment payment = billingResult.getPayment();
        payment = paymentRepository.save(payment);

        // 6. BUILDER PATTERN: Constructing official Tax Invoice / Bill
        traces.add("BUILDER PATTERN: Assembling Bill via BillBuilder");
        Bill bill = new BillBuilder()
                .setOrder(order)
                .setCustomer(customer)
                .setItems(orderItems)
                .setSubtotal(billingResult.getSubtotal())
                .setDiscount(billingResult.getDiscount())
                .setTax(billingResult.getTax())
                .setFinalAmount(billingResult.getFinalAmount())
                .setPaymentMethod(payment.getPaymentMethod())
                .setPaymentStatus(payment.getStatus().name())
                .build();
        bill = billRepository.save(bill);

        // 7. STATE PATTERN & MEDIATOR & OBSERVER & SINGLETON: Post-Payment Fulfillment
        traces.add("STATE PATTERN: Transitioning order to state PAID");
        orderStateManager.transitionNext(order); // PAYMENT_PENDING -> PAID
        orderRepository.save(order);

        traces.add("MEDIATOR PATTERN: SmartRetailMediator notifying Inventory, Observer, and Notifications");
        traces.add("SINGLETON PATTERN: InventoryManager decrementing stock locks");
        traces.add("OBSERVER PATTERN: LowStockObserver, CustomerNotificationObserver, AdminNotificationObserver notified");
        mediator.onPaymentSuccessful(order, payment);

        return CheckoutDTOs.CheckoutResponse.builder()
                .orderId(order.getId())
                .billId(bill.getId())
                .billNumber(bill.getBillNumber())
                .subtotal(billingResult.getSubtotal())
                .discount(billingResult.getDiscount())
                .tax(billingResult.getTax())
                .finalAmount(billingResult.getFinalAmount())
                .orderStatus(order.getStatus())
                .paymentStatus(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .message("Order placed and payment processed successfully!")
                .designPatternTraces(traces)
                .build();
    }
}
