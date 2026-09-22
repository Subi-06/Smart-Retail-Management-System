package com.smartretail;

import com.smartretail.designpatterns.behavioral.chain.PurchaseValidationHandler;
import com.smartretail.designpatterns.behavioral.chain.PurchaseValidationPipeline;
import com.smartretail.designpatterns.behavioral.iterator.ProductCollection;
import com.smartretail.designpatterns.behavioral.iterator.RetailIterator;
import com.smartretail.designpatterns.behavioral.memento.CartCaretaker;
import com.smartretail.designpatterns.behavioral.memento.CartMemento;
import com.smartretail.designpatterns.behavioral.observer.RetailEvents;
import com.smartretail.designpatterns.behavioral.observer.RetailEventSubject;
import com.smartretail.designpatterns.behavioral.state.OrderLifecycleState;
import com.smartretail.designpatterns.behavioral.state.OrderStateManager;
import com.smartretail.designpatterns.behavioral.strategy.DiscountStrategy;
import com.smartretail.designpatterns.behavioral.strategy.NoDiscountStrategy;
import com.smartretail.designpatterns.behavioral.visitor.InventoryAuditVisitor;
import com.smartretail.designpatterns.behavioral.visitor.ProductVisitableDispatcher;
import com.smartretail.designpatterns.behavioral.visitor.TaxVisitor;
import com.smartretail.dto.CartDTO;
import com.smartretail.entity.Order;
import com.smartretail.entity.Product;
import com.smartretail.entity.User;
import com.smartretail.entity.enums.MembershipType;
import com.smartretail.entity.enums.OrderStatus;
import com.smartretail.entity.enums.ProductType;
import com.smartretail.entity.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class BehavioralPatternsTest {

    @Test
    @DisplayName("Iterator: Filters in-stock products and traverses collection")
    void testIterator() {
        List<Product> products = List.of(
                Product.builder().name("Apples").quantity(5).build(),
                Product.builder().name("Bananas").quantity(0).build(), // out of stock
                Product.builder().name("Oranges").quantity(12).build()
        );

        ProductCollection collection = new ProductCollection(products);
        RetailIterator<Product> iterator = collection.inStockIterator();

        assertTrue(iterator.hasNext());
        assertEquals("Apples", iterator.next().getName());
        assertTrue(iterator.hasNext());
        assertEquals("Oranges", iterator.next().getName());
        assertFalse(iterator.hasNext());
    }

    @Test
    @DisplayName("Memento: Stores and restores cart snapshots for undo")
    void testMemento() {
        CartCaretaker caretaker = new CartCaretaker();
        Long userId = 42L;

        List<CartMemento.CartItemSnapshot> snapshots = List.of(
                new CartMemento.CartItemSnapshot(1L, "Milk", 2, new BigDecimal("60.00")),
                new CartMemento.CartItemSnapshot(2L, "Bread", 1, new BigDecimal("45.00"))
        );
        CartMemento memento = new CartMemento(10L, userId, snapshots, null, LocalDateTime.now());

        caretaker.saveMemento(userId, memento);
        assertTrue(caretaker.hasMemento(userId));

        CartMemento retrieved = caretaker.popMemento(userId);
        assertNotNull(retrieved);
        assertEquals(2, retrieved.getItemsSnapshot().size());
        assertEquals("Milk", retrieved.getItemsSnapshot().get(0).getProductName());
        assertFalse(caretaker.hasMemento(userId));
    }

    @Test
    @DisplayName("State: Progresses order through correct lifecycle state machine")
    void testState() {
        OrderStateManager manager = new OrderStateManager();
        Order order = Order.builder().status(OrderStatus.ORDER_PLACED).build();

        manager.transitionNext(order); // ORDER_PLACED -> PAYMENT_PENDING
        assertEquals(OrderStatus.PAYMENT_PENDING, order.getStatus());

        manager.transitionNext(order); // PAYMENT_PENDING -> PAID
        assertEquals(OrderStatus.PAID, order.getStatus());

        manager.transitionNext(order); // PAID -> PROCESSING
        assertEquals(OrderStatus.PROCESSING, order.getStatus());

        manager.transitionNext(order); // PROCESSING -> COMPLETED
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    @DisplayName("Observer: Notifies registered listeners upon events")
    void testObserver() {
        RetailEventSubject subject = new RetailEventSubject();
        AtomicBoolean notified = new AtomicBoolean(false);

        subject.registerObserver(new RetailEvents.RetailObserver() {
            @Override
            public void onEvent(RetailEvents.RetailEvent event) {
                if (event.getType() == RetailEvents.EventType.LOW_STOCK) {
                    notified.set(true);
                }
            }
            @Override
            public String getObserverName() { return "TestLowStockObserver"; }
        });

        subject.notifyObservers(RetailEvents.RetailEvent.builder()
                .type(RetailEvents.EventType.LOW_STOCK)
                .productName("Milk")
                .remainingStock(4)
                .message("Low Stock alert for Milk")
                .build());

        assertTrue(notified.get());
    }

    @Test
    @DisplayName("Visitor: TaxVisitor computes exact GST based on product category")
    void testVisitor() {
        Product food = Product.builder().name("Yogurt").price(new BigDecimal("100.00")).productType(ProductType.FOOD).build();
        Product electronics = Product.builder().name("Earbuds").price(new BigDecimal("1000.00")).productType(ProductType.ELECTRONICS).build();

        TaxVisitor taxVisitor = new TaxVisitor();
        ProductVisitableDispatcher dispatcher = new ProductVisitableDispatcher();

        dispatcher.dispatch(food, taxVisitor);
        dispatcher.dispatch(electronics, taxVisitor);

        // 100 * 5% = 5.00, 1000 * 18% = 180.00 => Total: 185.00
        assertEquals(new BigDecimal("185.00"), taxVisitor.getComputedTax());
    }
}
