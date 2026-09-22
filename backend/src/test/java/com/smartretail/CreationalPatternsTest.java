package com.smartretail;

import com.smartretail.designpatterns.creational.abstractfactory.*;
import com.smartretail.designpatterns.creational.builder.BillBuilder;
import com.smartretail.designpatterns.creational.factory.*;
import com.smartretail.designpatterns.creational.singleton.InventoryManager;
import com.smartretail.designpatterns.creational.singleton.StoreConfiguration;
import com.smartretail.dto.CheckoutDTOs;
import com.smartretail.dto.ProductDTO;
import com.smartretail.entity.Bill;
import com.smartretail.entity.Category;
import com.smartretail.entity.Order;
import com.smartretail.entity.Product;
import com.smartretail.entity.User;
import com.smartretail.entity.enums.MembershipType;
import com.smartretail.entity.enums.PaymentMethod;
import com.smartretail.entity.enums.ProductType;
import com.smartretail.entity.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CreationalPatternsTest {

    @Test
    @DisplayName("Factory Method: Successfully creates category-specific products")
    void testFactoryMethod() {
        FoodProductFactory foodFactory = new FoodProductFactory();
        GroceryProductFactory groceryFactory = new GroceryProductFactory();
        BeverageProductFactory beverageFactory = new BeverageProductFactory();
        ElectronicsProductFactory electronicsFactory = new ElectronicsProductFactory();

        ProductFactoryProvider provider = new ProductFactoryProvider(foodFactory, groceryFactory, beverageFactory, electronicsFactory);

        Category dairy = Category.builder().name("Dairy").build();
        ProductDTO foodDto = ProductDTO.builder().name("Milk").price(new BigDecimal("60.00")).quantity(10).shelfLifeDays(4).build();
        Product foodProduct = provider.getFactory(ProductType.FOOD).makeProduct(foodDto, dairy);
        assertEquals(ProductType.FOOD, foodProduct.getProductType());
        assertEquals(4, foodProduct.getShelfLifeDays());

        Category electronics = Category.builder().name("Electronics").build();
        ProductDTO elecDto = ProductDTO.builder().name("Charger").price(new BigDecimal("1500.00")).quantity(5).warrantyMonths(24).build();
        Product elecProduct = provider.getFactory(ProductType.ELECTRONICS).makeProduct(elecDto, electronics);
        assertEquals(ProductType.ELECTRONICS, elecProduct.getProductType());
        assertEquals(24, elecProduct.getWarrantyMonths());
    }

    @Test
    @DisplayName("Abstract Factory: Creates matching processor, validator, and receipt generator families")
    void testAbstractFactory() {
        UPIFactory upiFactory = new UPIFactory();
        PaymentFactoryProvider provider = new PaymentFactoryProvider(upiFactory, new CardFactory(), new CashFactory());

        PaymentFactory selectedFactory = provider.getFactory(PaymentMethod.UPI);
        assertNotNull(selectedFactory);

        PaymentFactory.PaymentValidator validator = selectedFactory.createPaymentValidator();
        CheckoutDTOs.CheckoutRequest validReq = CheckoutDTOs.CheckoutRequest.builder().upiVpa("user@okaxis").build();
        assertDoesNotThrow(() -> validator.validate(validReq, new BigDecimal("500.00")));

        Order mockOrder = Order.builder().id(101L).build();
        var payment = selectedFactory.createPaymentProcessor().processPayment(mockOrder, new BigDecimal("500.00"), validReq);
        assertEquals(PaymentMethod.UPI, payment.getPaymentMethod());
        assertTrue(payment.getTransactionId().startsWith("UPI-"));
    }

    @Test
    @DisplayName("Builder: Step-by-step construction of complex Bill object")
    void testBillBuilder() {
        User user = User.builder().name("Alice").email("alice@gmail.com").role(Role.ROLE_CUSTOMER).membershipType(MembershipType.GOLD).build();
        Order order = Order.builder().id(99L).build();

        Bill bill = new BillBuilder()
                .setOrder(order)
                .setCustomer(user)
                .setSubtotal(new BigDecimal("1000.00"))
                .setDiscount(new BigDecimal("100.00"))
                .setTax(new BigDecimal("45.00"))
                .setFinalAmount(new BigDecimal("945.00"))
                .setPaymentMethod(PaymentMethod.UPI)
                .setPaymentStatus("PAID")
                .build();

        assertNotNull(bill);
        assertTrue(bill.getBillNumber().startsWith("INV-"));
        assertEquals("Alice", bill.getCustomerName());
        assertEquals(new BigDecimal("945.00"), bill.getTotal());
        assertTrue(bill.getInvoiceJson().contains("alice@gmail.com"));
    }

    @Test
    @DisplayName("Prototype: Clones existing product with independent instance identity")
    void testPrototype() {
        Product coffee = Product.builder()
                .id(1L)
                .name("Coffee 250g")
                .price(new BigDecimal("120.00"))
                .quantity(20)
                .brand("Nescafe")
                .productType(ProductType.BEVERAGE)
                .unit("250g")
                .build();

        Product clonedCoffee = coffee.cloneProduct();
        assertNotSame(coffee, clonedCoffee);
        assertEquals("Coffee 250g (Copy)", clonedCoffee.getName());
        assertEquals(coffee.getPrice(), clonedCoffee.getPrice());
        assertEquals(coffee.getBrand(), clonedCoffee.getBrand());
    }

    @Test
    @DisplayName("Singleton: Ensures unique instance across calls")
    void testSingleton() {
        InventoryManager manager1 = InventoryManager.getInstance();
        InventoryManager manager2 = InventoryManager.getInstance();
        assertSame(manager1, manager2);

        StoreConfiguration config1 = StoreConfiguration.getInstance();
        StoreConfiguration config2 = StoreConfiguration.getInstance();
        assertSame(config1, config2);
        assertEquals("SMART RETAIL", config1.getStoreName());
    }
}
