package com.smartretail.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/patterns")
public class DesignPatternCatalogController {

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDesignPatternsSummary() {
        Map<String, Object> result = new LinkedHashMap<>();

        // 1. Creational Patterns
        List<Map<String, String>> creational = List.of(
            Map.of("name", "Factory Method", "role", "Creates category-specific products (Food, Grocery, Beverage, Electronics)", "classes", "ProductFactory, FoodProductFactory, GroceryProductFactory, BeverageProductFactory, ElectronicsProductFactory"),
            Map.of("name", "Abstract Factory", "role", "Produces families of payment objects (Processor, Validator, ReceiptGenerator) for UPI, Card, Cash", "classes", "PaymentFactory, UPIFactory, CardFactory, CashFactory"),
            Map.of("name", "Builder", "role", "Step-by-step assembly of complex Bill (invoice) objects with tax & line-item calculations", "classes", "BillBuilder, Bill"),
            Map.of("name", "Prototype", "role", "Enables cloning existing products with customized name, size, unit, and pricing", "classes", "ProductPrototype, Product.cloneProduct()"),
            Map.of("name", "Singleton", "role", "Centralized, thread-safe Inventory Manager and Store Global Configuration", "classes", "InventoryManager.getInstance(), StoreConfiguration.getInstance()")
        );

        // 2. Structural Patterns
        List<Map<String, String>> structural = List.of(
            Map.of("name", "Adapter", "role", "Adapts third-party external UPI and Card payment gateway APIs into internal RetailPaymentService", "classes", "RetailPaymentService, UPIPaymentAdapter, CardPaymentAdapter"),
            Map.of("name", "Bridge", "role", "Decouples Billing abstractions (NormalBilling, DiscountBilling) from Payment implementations (UPI, Card, Cash)", "classes", "BillingSystem, NormalBilling, DiscountBilling, PaymentImplementation"),
            Map.of("name", "Composite", "role", "Treats individual products (Leaf) and category hierarchies (Composite) uniformly for valuation and catalog traversal", "classes", "CatalogComponent, CategoryComposite, ProductLeaf"),
            Map.of("name", "Decorator", "role", "Dynamically stacks discounts (Festival, Membership, Coupon) without modifying Product or Order classes", "classes", "PriceComponent, FestivalDiscountDecorator, MembershipDiscountDecorator, CouponDecorator"),
            Map.of("name", "Facade", "role", "Coordinates Cart, Discount, Bill, Payment, Inventory, Order, and Notification services during checkout", "classes", "CheckoutFacade.checkout()"),
            Map.of("name", "Flyweight", "role", "Reuses immutable shared brand, category, and tax metadata to prevent duplicate object allocations", "classes", "ProductMetadataFlyweight, ProductMetadataFactory"),
            Map.of("name", "Proxy", "role", "Protection proxy verifying ROLE_ADMIN authorization and writing audit logs for product and stock mutations", "classes", "ProductAdminOperations, AdminInventoryProxy, RealProductAdminService")
        );

        // 3. Behavioral Patterns
        List<Map<String, String>> behavioral = List.of(
            Map.of("name", "Chain of Responsibility", "role", "Pipeline of validators (StockValidator -> MembershipValidator -> CouponValidator -> ApprovalHandler)", "classes", "PurchaseValidationHandler, StockValidationHandler, CouponValidationHandler"),
            Map.of("name", "Command", "role", "Encapsulates shopping actions (Add, Remove, UpdateQty, ClearCart) with execute() and undo()", "classes", "RetailCommand, AddToCartCommand, ClearCartCommand, CommandInvoker"),
            Map.of("name", "Iterator", "role", "Provides custom sequential, in-stock, and category-based traversals over product collections", "classes", "RetailIterator, ProductCollection, InStockProductIterator"),
            Map.of("name", "Mediator", "role", "Coordinates decoupled events across Cart, Order, Inventory, Payment, and Notification subsystems", "classes", "SmartRetailMediator, RetailEventMediator"),
            Map.of("name", "Memento", "role", "Captures cart snapshots before clearing and restores cart state when user clicks UNDO", "classes", "CartMemento, CartCaretaker"),
            Map.of("name", "Observer", "role", "Broadcasts events to CustomerNotificationObserver, AdminNotificationObserver, and LowStockObserver", "classes", "RetailEvents, RetailEventSubject, LowStockObserver"),
            Map.of("name", "State", "role", "Manages order lifecycle state transitions (CART -> ORDER_PLACED -> PAID -> PROCESSING -> COMPLETED)", "classes", "OrderLifecycleState, PlacedOrderState, PaidOrderState, OrderStateManager"),
            Map.of("name", "Strategy", "role", "Dynamically selects discount algorithm (Festival, Membership, Bulk, Coupon) at runtime", "classes", "DiscountStrategy, MembershipDiscountStrategy, DiscountStrategyResolver"),
            Map.of("name", "Template Method", "role", "Defines the 6-step invariant billing workflow with customizable tax and receipt generation primitives", "classes", "BillingWorkflowTemplate, StandardRetailBillingWorkflow"),
            Map.of("name", "Visitor", "role", "Executes type-specific GST calculations and inventory audit checks across Food, Grocery, Beverage, Electronics", "classes", "ProductVisitor, TaxVisitor, InventoryAuditVisitor")
        );

        // 4. Application Design Patterns
        List<Map<String, String>> application = List.of(
            Map.of("name", "MVC (Model-View-Controller)", "role", "Clean layered architecture separating HTTP controllers, business services, and JPA repositories", "classes", "ProductController -> ProductService -> ProductRepository"),
            Map.of("name", "MVP (Model-View-Presenter)", "role", "Separates Discount business model and view contract via interactive Presenter", "classes", "DiscountMVP.DiscountModel, DiscountViewContract, DiscountPresenter"),
            Map.of("name", "MVI (Model-View-Intent)", "role", "Unidirectional reactive stream (Intent -> Reducer -> Immutable ViewState) for Order Management", "classes", "OrderMVI.OrderIntent, OrderViewState, OrderMviProcessor"),
            Map.of("name", "MVVM (Model-View-ViewModel)", "role", "Presentation layer binding Cart data, reactive calculations, and checkout states", "classes", "CartMVVM.CartModel, CartViewModel"),
            Map.of("name", "VIPER", "role", "Modular View-Interactor-Presenter-Entity-Router pattern for Inventory Management", "classes", "InventoryVIPER.InventoryPresenter, InventoryInteractor, InventoryRouter"),
            Map.of("name", "Microservices Design Pattern", "role", "Service boundaries for Product, Inventory, Order, Payment, Notification with API Gateway routing specs", "classes", "MicroserviceContracts, DomainEventEnvelope, API_GATEWAY_ROUTES")
        );

        result.put("projectTitle", "SMART RETAIL MANAGEMENT SYSTEM");
        result.put("totalPatternsDemonstrated", 29);
        result.put("gofCreationalPatterns", creational);
        result.put("gofStructuralPatterns", structural);
        result.put("gofBehavioralPatterns", behavioral);
        result.put("applicationPatterns", application);

        return ResponseEntity.ok(result);
    }
}
