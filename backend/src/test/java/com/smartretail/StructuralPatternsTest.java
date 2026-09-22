package com.smartretail;

import com.smartretail.designpatterns.structural.adapter.UPIPaymentAdapter;
import com.smartretail.designpatterns.structural.bridge.*;
import com.smartretail.designpatterns.structural.composite.CategoryComposite;
import com.smartretail.designpatterns.structural.composite.ProductLeaf;
import com.smartretail.designpatterns.structural.decorator.*;
import com.smartretail.designpatterns.structural.flyweight.ProductMetadataFactory;
import com.smartretail.designpatterns.structural.flyweight.ProductMetadataFlyweight;
import com.smartretail.designpatterns.structural.proxy.AdminInventoryProxy;
import com.smartretail.entity.Order;
import com.smartretail.entity.Payment;
import com.smartretail.entity.Product;
import com.smartretail.entity.User;
import com.smartretail.entity.enums.MembershipType;
import com.smartretail.entity.enums.PaymentMethod;
import com.smartretail.entity.enums.ProductType;
import com.smartretail.entity.enums.Role;
import com.smartretail.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StructuralPatternsTest {

    @Test
    @DisplayName("Adapter: Adapts external third-party API to internal RetailPaymentService")
    void testAdapter() {
        UPIPaymentAdapter adapter = new UPIPaymentAdapter();
        Order order = Order.builder().id(12L).build();
        Payment payment = adapter.executePayment(order, new BigDecimal("350.00"), Map.of("upiVpa", "customer@upi"));

        assertNotNull(payment);
        assertEquals(PaymentMethod.UPI, payment.getPaymentMethod());
        assertEquals(new BigDecimal("350.00"), payment.getAmount());
        assertTrue(payment.getResponsePayload().contains("Adapted NPCI UPI"));
    }

    @Test
    @DisplayName("Bridge: Decouples Billing schemes from Payment implementations")
    void testBridge() {
        PaymentImplementation upiBridge = new UPIPaymentImpl();
        Order order = Order.builder().finalAmount(new BigDecimal("1000.00")).build();

        BillingSystem normalBilling = new NormalBilling(upiBridge);
        Payment p1 = normalBilling.processBill(order);
        assertEquals(new BigDecimal("1000.00"), p1.getAmount());

        BillingSystem discountBilling = new DiscountBilling(upiBridge, new BigDecimal("100.00"));
        Payment p2 = discountBilling.processBill(order);
        assertEquals(new BigDecimal("900.00"), p2.getAmount());
    }

    @Test
    @DisplayName("Composite: Treats individual products and nested categories uniformly")
    void testComposite() {
        CategoryComposite root = new CategoryComposite("Catalog");
        CategoryComposite food = new CategoryComposite("Food");

        Product milk = Product.builder().name("Milk").price(new BigDecimal("50.00")).quantity(10).build();
        Product bread = Product.builder().name("Bread").price(new BigDecimal("40.00")).quantity(5).build();

        food.add(new ProductLeaf(milk));
        food.add(new ProductLeaf(bread));
        root.add(food);

        assertEquals(15, root.getInventoryCount());
        // 10*50 + 5*40 = 500 + 200 = 700
        assertEquals(new BigDecimal("700.00"), root.calculateTotalValue());
    }

    @Test
    @DisplayName("Decorator: Stacks discounts dynamically without modifying Product class")
    void testDecorator() {
        PriceComponent basePrice = new BasePriceComponent(new BigDecimal("1000.00"));

        // Add 10% festival discount -> 900.00
        PriceComponent festival = new FestivalDiscountDecorator(basePrice, new BigDecimal("10.0"));
        assertEquals(new BigDecimal("900.00"), festival.calculatePrice());

        // Add Platinum member discount (15% on 900) -> 900 - 135 = 765.00
        PriceComponent membership = new MembershipDiscountDecorator(festival, MembershipType.PLATINUM);
        assertEquals(new BigDecimal("765.00"), membership.calculatePrice());

        // Add Coupon discount of ₹50 flat -> 765 - 50 = 715.00
        PriceComponent coupon = new CouponDecorator(membership, "FLAT50", new BigDecimal("50.00"), false);
        assertEquals(new BigDecimal("715.00"), coupon.calculatePrice());
        assertTrue(coupon.getDescription().contains("FLAT50"));
    }

    @Test
    @DisplayName("Flyweight: Reuses cached metadata objects for identical brand/category keys")
    void testFlyweight() {
        ProductMetadataFactory factory = new ProductMetadataFactory();

        ProductMetadataFlyweight meta1 = factory.getMetadata("Amul", "Dairy", new BigDecimal("0.05"), 3, "Keep chilled", "India");
        ProductMetadataFlyweight meta2 = factory.getMetadata("Amul", "Dairy", new BigDecimal("0.05"), 3, "Keep chilled", "India");

        assertSame(meta1, meta2);
        assertEquals(1, factory.getCachedFlyweightsCount());
    }

    @Test
    @DisplayName("Proxy: Rejects unauthorized non-admin user operations with 403 Forbidden")
    void testProxy() {
        AdminInventoryProxy proxy = new AdminInventoryProxy();
        User customer = User.builder().name("Bob").email("bob@gmail.com").role(Role.ROLE_CUSTOMER).build();

        assertThrows(UnauthorizedException.class, () -> proxy.updateStock(1L, 50, customer));
        assertThrows(UnauthorizedException.class, () -> proxy.deleteProduct(1L, customer));
    }
}
