# COMPREHENSIVE DESIGN PATTERNS SPECIFICATION
### Smart Retail Management System
**Department of Computer Science and Engineering | B.E. Degree Capstone**

---

## 1. Introduction

This specification details the design and implementation of **all 23 Gang of Four (GoF) Design Patterns** and **6 Modern Application Architecture Patterns** in the **Smart Retail Management System**.

Every pattern in this project is implemented with concrete business logic solving authentic retail challenges, strictly avoiding superficial or fake implementations.

---

## 2. Creational Design Patterns

Creational design patterns abstract the instantiation process, making a system independent of how its objects are created, composed, and represented.

### 2.1 Factory Method Pattern
- **GoF Classification:** Creational (Class)
- **Formal Intent:** Define an interface for creating an object, but let subclasses decide which class to instantiate. Factory Method lets a class defer instantiation to subclasses.
- **Retail Problem Solved:** Retail products have specialized attributes and behavior based on category (e.g. `FoodProduct` requires shelf life tracking and refrigeration flags; `ElectronicsProduct` requires warranty and voltage checks; `GroceryProduct` has GST tax exemptions). Directly instantiating concrete products in controllers leads to high coupling and brittle conditional logic.
- **Concrete Class Mappings:**
  - Creator: `com.smartretail.designpatterns.creational.factory.ProductFactory`
  - Concrete Creators: `FoodProductFactory`, `ElectronicsProductFactory`, `ClothingProductFactory`, `GroceryProductFactory`
  - Product Interface: `com.smartretail.entity.Product`
- **Code Snippet:**
  ```java
  public class FoodProductFactory extends ProductFactory {
      @Override
      public Product createProduct(ProductRequestDTO request) {
          FoodProduct product = new FoodProduct();
          product.setName(request.getName());
          product.setPrice(request.getPrice());
          product.setShelfLifeDays(request.getShelfLifeDays());
          product.setRequiresRefrigeration(request.isRequiresRefrigeration());
          return product;
      }
  }
  ```
- **Architectural Rationale:** Enables adding new product categories (e.g., `PerishableMeatFactory` or `PharmacyFactory`) without modifying existing controller or catalog code (Open/Closed Principle).

---

### 2.2 Abstract Factory Pattern
- **GoF Classification:** Creational (Object)
- **Formal Intent:** Provide an interface for creating families of related or dependent objects without specifying their concrete classes.
- **Retail Problem Solved:** A retail system must produce coordinated families of checkout artifacts depending on the sales channel:
  - *Physical POS Store Channel:* Requires 58mm/80mm Thermal Paper Receipts and Local Card/Cash Terminal processors.
  - *Online E-Commerce Channel:* Requires PDF/HTML E-Invoices, GST digital tax seals, and UPI/Payment Gateway integrations.
- **Concrete Class Mappings:**
  - Abstract Factory: `com.smartretail.designpatterns.creational.abstractfactory.RetailReceiptFactory`
  - Concrete Factories: `PhysicalStoreReceiptFactory`, `OnlineReceiptFactory`
  - Abstract Products: `ReceiptFormatter`, `PaymentProcessorAdapter`
- **Code Snippet:**
  ```java
  public interface RetailReceiptFactory {
      ReceiptFormatter createReceiptFormatter();
      PaymentProcessor createPaymentProcessor();
  }
  public class PhysicalStoreReceiptFactory implements RetailReceiptFactory {
      public ReceiptFormatter createReceiptFormatter() { return new ThermalReceiptFormatter(); }
      public PaymentProcessor createPaymentProcessor() { return new PosTerminalProcessor(); }
  }
  ```
- **Architectural Rationale:** Ensures thermal receipts and POS terminal processors are never accidentally cross-mixed with online digital PDF generation and web payment gateways.

---

### 2.3 Builder Pattern
- **GoF Classification:** Creational (Object)
- **Formal Intent:** Separate the construction of a complex object from its representation so that the same construction process can create different representations.
- **Retail Problem Solved:** Generating a retail invoice bill is an intricate multi-step process involving customer metadata, order items, coupon discounts, membership loyalty deductions, CGST, SGST, and store signatures. Constructing this via a 15-parameter constructor is unreadable, error-prone, and fragile.
- **Concrete Class Mappings:**
  - Target Class: `com.smartretail.entity.BillReceipt`
  - Builder Class: `com.smartretail.designpatterns.creational.builder.BillReceiptBuilder`
- **Code Snippet:**
  ```java
  BillReceipt bill = new BillReceiptBuilder()
      .withBillNumber("BILL-" + UUID.randomUUID().toString().substring(0, 8))
      .forOrder(order)
      .withCustomer(user.getName(), user.getEmail())
      .withSubtotal(order.getSubtotal())
      .applyDiscount(order.getDiscountAmount())
      .calculateTax(0.05) // 5% GST
      .withPaymentDetails(paymentMethod, transactionId)
      .build();
  ```
- **Architectural Rationale:** Enforces compile-time validation, immutability, and clean fluent syntax for complex retail receipts.

---

### 2.4 Prototype Pattern
- **GoF Classification:** Creational (Object)
- **Formal Intent:** Specify the kinds of objects to create using a prototypical instance, and create new objects by copying this prototype.
- **Retail Problem Solved:** Retail managers frequently create product variations (e.g., duplicate "Whole Milk 1L" into "Whole Milk 500ml", or clone "USB-C Fast Cable 1m" into "USB-C Fast Cable 2m") which share 90% of attributes (brand, category, supplier, specifications, GST code). Re-entering all metadata manually is inefficient and error-prone.
- **Concrete Class Mappings:**
  - Prototype Interface: `com.smartretail.designpatterns.creational.prototype.ProductPrototype`
  - Concrete Prototype: `com.smartretail.entity.Product` implementing `cloneProduct()`
- **Code Snippet:**
  ```java
  public Product cloneProduct(String newName, Double newPrice, String newSku) {
      Product clone = (Product) this.clone();
      clone.setId(null); // Fresh database identity
      clone.setName(newName);
      clone.setPrice(newPrice);
      clone.setSku(newSku);
      return clone;
  }
  ```
- **Architectural Rationale:** Drastically reduces database write overhead and administrative effort when onboarding thousands of SKU variants.

---

### 2.5 Singleton Pattern
- **GoF Classification:** Creational (Object)
- **Formal Intent:** Ensure a class only has one instance, and provide a global point of access to it.
- **Retail Problem Solved:** Inventory stock adjustment requires thread-safe concurrency control. If two customers simultaneously purchase the last unit of a product, race conditions can cause negative stock counts. A centralized, thread-safe Inventory Ledger lock manager is essential.
- **Concrete Class Mappings:**
  - Singleton Class: `com.smartretail.designpatterns.creational.singleton.InventoryLedgerManager`
- **Code Snippet:**
  ```java
  public class InventoryLedgerManager {
      private static volatile InventoryLedgerManager instance;
      private final ConcurrentHashMap<Long, ReentrantLock> productLocks = new ConcurrentHashMap<>();

      private InventoryLedgerManager() {}

      public static InventoryLedgerManager getInstance() {
          if (instance == null) {
              synchronized (InventoryLedgerManager.class) {
                  if (instance == null) {
                      instance = new InventoryLedgerManager();
                  }
              }
          }
          return instance;
      }

      public void lockProduct(Long productId) {
          productLocks.computeIfAbsent(productId, k -> new ReentrantLock()).lock();
      }
      public void unlockProduct(Long productId) {
          ReentrantLock lock = productLocks.get(productId);
          if (lock != null && lock.isHeldByCurrentThread()) lock.unlock();
      }
  }
  ```
- **Architectural Rationale:** Double-checked locking prevents multi-threading inventory discrepancies across concurrent customer checkouts.

---

## 3. Structural Design Patterns

Structural design patterns are concerned with how classes and objects are composed to form larger structures while keeping the structures flexible and efficient.

### 3.1 Adapter Pattern
- **GoF Classification:** Structural (Object)
- **Formal Intent:** Convert the interface of a class into another interface clients expect. Adapter lets classes work together that couldn't otherwise because of incompatible interfaces.
- **Retail Problem Solved:** A modern retail system must process payments from varied gateways: Stripe (REST tokens), UPI QR (string payloads), and Legacy Bank terminals (ISO-8583 message packets). Client checkout code should not know the idiosyncratic API differences of each provider.
- **Concrete Class Mappings:**
  - Standard Interface: `com.smartretail.designpatterns.structural.adapter.PaymentGateway`
  - Adaptees: `ThirdPartyStripeService`, `LegacyBankSwitchService`, `UpiPaymentService`
  - Adapters: `StripePaymentAdapter`, `LegacyBankPaymentAdapter`, `UpiPaymentAdapter`
- **Code Snippet:**
  ```java
  public class UpiPaymentAdapter implements PaymentGateway {
      private final UpiPaymentService upiService;
      @Override
      public PaymentResult processPayment(PaymentRequest request) {
          UpiResponse resp = upiService.initiateVpaTransfer(request.getCustomerVpa(), request.getAmount());
          return new PaymentResult(resp.isSuccess(), resp.getRrn(), "UPI");
      }
  }
  ```
- **Architectural Rationale:** Swapping or adding a payment gateway (e.g. Razorpay or PayPal) requires writing a new adapter without touching checkout logic.

---

### 3.2 Bridge Pattern
- **GoF Classification:** Structural (Object)
- **Formal Intent:** Decouple an abstraction from its implementation so that the two can vary independently.
- **Retail Problem Solved:** Retail receipts exist in multiple logical forms (e.g., `StandardCustomerReceipt`, `TaxInvoiceReceipt`, `WarrantyReceipt`) and must be rendered onto multiple physical media (e.g., Thermal 58mm text printer, PDF document, HTML web view). A Cartesian class hierarchy (`ThermalCustomerReceipt`, `PdfTaxInvoice`, etc.) causes class explosion.
- **Concrete Class Mappings:**
  - Abstraction: `com.smartretail.designpatterns.structural.bridge.ReceiptAbstraction`
  - Refined Abstractions: `StandardReceipt`, `TaxInvoiceReceipt`
  - Implementor: `ReceiptRenderer`
  - Concrete Implementors: `ThermalReceiptRenderer`, `PdfReceiptRenderer`, `HtmlReceiptRenderer`
- **Code Snippet:**
  ```java
  public abstract class ReceiptAbstraction {
      protected final ReceiptRenderer renderer;
      public ReceiptAbstraction(ReceiptRenderer renderer) { this.renderer = renderer; }
      public abstract String generateReceipt(BillReceipt bill);
  }
  ```
- **Architectural Rationale:** New receipt types and new output renderers can be developed independently without exponential class multiplication.

---

### 3.3 Composite Pattern
- **GoF Classification:** Structural (Object)
- **Formal Intent:** Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly.
- **Retail Problem Solved:** Retail departments consist of nested categories (e.g., `Beverages` -> `Hot Beverages` -> `Coffee` -> `Espresso Roast`). Calculating total inventory quantity and stock valuation across entire catalog trees requires uniform recursion over categories and leaf products.
- **Concrete Class Mappings:**
  - Component: `com.smartretail.designpatterns.structural.composite.CatalogComponent`
  - Composite: `com.smartretail.designpatterns.structural.composite.CategoryComposite`
  - Leaf: `com.smartretail.designpatterns.structural.composite.ProductLeaf`
- **Code Snippet:**
  ```java
  public class CategoryComposite implements CatalogComponent {
      private List<CatalogComponent> children = new ArrayList<>();
      @Override
      public double getStockValuation() {
          return children.stream().mapToDouble(CatalogComponent::getStockValuation).sum();
      }
      @Override
      public int getTotalItems() {
          return children.stream().mapToInt(CatalogComponent::getTotalItems).sum();
      }
  }
  ```
- **Architectural Rationale:** The administration dashboard queries catalog valuation identically whether evaluating a single product or the entire hierarchical store catalog.

---

### 3.4 Decorator Pattern
- **GoF Classification:** Structural (Object)
- **Formal Intent:** Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality.
- **Retail Problem Solved:** Customers frequently request checkout value additions (e.g., Gift Wrapping +₹50, Express Same-Day Delivery +₹100, Extended 1-Year Warranty +₹250). Subclassing every permutation creates dozens of bloated classes.
- **Concrete Class Mappings:**
  - Component: `com.smartretail.designpatterns.structural.decorator.OrderPackage`
  - Concrete Component: `com.smartretail.designpatterns.structural.decorator.BaseOrderPackage`
  - Decorator: `OrderPackageDecorator`
  - Concrete Decorators: `GiftWrapDecorator`, `ExpressDeliveryDecorator`, `WarrantyDecorator`
- **Code Snippet:**
  ```java
  OrderPackage orderPkg = new BaseOrderPackage(order.getTotalAmount(), order.getDescription());
  if (checkoutRequest.isGiftWrap()) orderPkg = new GiftWrapDecorator(orderPkg);
  if (checkoutRequest.isExpress()) orderPkg = new ExpressDeliveryDecorator(orderPkg);
  double finalAmount = orderPkg.getCost();
  ```
- **Architectural Rationale:** Add-on services can be combined dynamically at runtime without altering core order entities.

---

### 3.5 Facade Pattern
- **GoF Classification:** Structural (Object)
- **Formal Intent:** Provide a unified interface to a set of interfaces in a subsystem. Facade defines a higher-level interface that makes the subsystem easier to use.
- **Retail Problem Solved:** Placing an order touches 6 disparate subsystems: validating stock, applying discount strategy, executing payment adapter, updating inventory counts, generating bill builder, and notifying customer observers. Exposing this orchestration to controllers leads to spaghetti code.
- **Concrete Class Mappings:**
  - Facade: `com.smartretail.designpatterns.structural.facade.RetailCheckoutFacade`
  - Subsystems: `InventoryService`, `DiscountEngine`, `PaymentGateway`, `OrderService`, `BillService`, `NotificationService`
- **Code Snippet:**
  ```java
  @Service
  public class RetailCheckoutFacade {
      public CheckoutResponseDTO processCheckout(CheckoutRequestDTO req) {
          validationChain.validate(req);
          double discount = discountEngine.calculate(req);
          PaymentResult payResult = paymentGateway.processPayment(req);
          Order order = orderService.createOrder(req, payResult);
          inventoryService.deductStock(order.getItems());
          BillReceipt bill = billService.generateBill(order);
          notificationService.broadcastOrderPlaced(order);
          return new CheckoutResponseDTO(order.getId(), bill.getBillNumber(), "SUCCESS");
      }
  }
  ```
- **Architectural Rationale:** `CheckoutController` interacts with a single, clear method, isolating controllers from internal subsystem complexities.

---

### 3.6 Flyweight Pattern
- **GoF Classification:** Structural (Object)
- **Formal Intent:** Use sharing to support large numbers of fine-grained objects efficiently.
- **Retail Problem Solved:** An enterprise store manages 50,000+ items across aisles. Many items share identical static specifications (e.g. manufacturer name, organic certification badges, country of origin, handling warnings). Storing duplicate string blobs in every product instance wastes heap memory.
- **Concrete Class Mappings:**
  - Flyweight: `com.smartretail.designpatterns.structural.flyweight.SharedProductSpecs`
  - Flyweight Factory: `com.smartretail.designpatterns.structural.flyweight.ProductMetadataFlyweightFactory`
  - Context: `Product` containing extrinsic state (id, price, quantity) and referencing shared flyweight.
- **Code Snippet:**
  ```java
  public class ProductMetadataFlyweightFactory {
      private static final Map<String, SharedProductSpecs> pool = new ConcurrentHashMap<>();
      public static SharedProductSpecs getSpecs(String brand, String origin, String certification) {
          String key = brand + ":" + origin + ":" + certification;
          return pool.computeIfAbsent(key, k -> new SharedProductSpecs(brand, origin, certification));
      }
  }
  ```
- **Architectural Rationale:** Reduces JVM memory footprint significantly when caching high-volume inventory records.

---

### 3.7 Proxy Pattern
- **GoF Classification:** Structural (Object)
- **Formal Intent:** Provide a surrogate or placeholder for another object to control access to it.
- **Retail Problem Solved:** Sensitive product catalog mutations (e.g. deleting items, modifying base prices, auditing security logs) must strictly verify administrative credentials and log audit trails before delegating to the actual product repository.
- **Concrete Class Mappings:**
  - Subject Interface: `com.smartretail.designpatterns.structural.proxy.ProductService`
  - Real Subject: `com.smartretail.service.ProductServiceImpl`
  - Proxy: `com.smartretail.designpatterns.structural.proxy.ProductSecurityProxy`
- **Code Snippet:**
  ```java
  public class ProductSecurityProxy implements ProductService {
      private final ProductServiceImpl realService;
      @Override
      public void deleteProduct(Long id, User caller) {
          if (!caller.getRole().equals("ADMIN")) {
              throw new SecurityException("Access Denied: Only ADMIN can delete catalog items.");
          }
          auditLogger.log("Product " + id + " deleted by " + caller.getEmail());
          realService.deleteProduct(id, caller);
      }
  }
  ```
- **Architectural Rationale:** Enforces role-based security transparently without polluting business logic in the target service.

---

## 4. Behavioral Design Patterns

Behavioral design patterns are concerned with algorithms and the assignment of responsibilities between objects.

### 4.1 Chain of Responsibility Pattern
- **GoF Classification:** Behavioral (Object)
- **Formal Intent:** Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle the request. Chain the receiving objects and pass the request along the chain until an object handles it.
- **Retail Problem Solved:** Multi-point pre-checkout validation requires checking item availability, account credit limits, store operational hours, and potential fraud indicators sequentially. Monolithic validation methods become unmaintainable when rules change.
- **Concrete Class Mappings:**
  - Handler: `com.smartretail.designpatterns.behavioral.chain.CheckoutValidationHandler`
  - Concrete Handlers: `StockAvailabilityHandler`, `CustomerActiveStatusHandler`, `MinimumOrderAmountHandler`, `FraudCheckHandler`
- **Code Snippet:**
  ```java
  public class StockAvailabilityHandler extends CheckoutValidationHandler {
      @Override
      public void handle(CheckoutRequest request) {
          for (CartItem item : request.getCart().getItems()) {
              if (item.getProduct().getQuantity() < item.getQuantity()) {
                  throw new ValidationException("Insufficient stock for " + item.getProduct().getName());
              }
          }
          passToNext(request);
      }
  }
  ```
- **Architectural Rationale:** New business validation rules can be inserted, re-ordered, or removed dynamically without editing other handlers.

---

### 4.2 Command Pattern
- **GoF Classification:** Behavioral (Object)
- **Formal Intent:** Encapsulate a request as an object, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations.
- **Retail Problem Solved:** The customer shopping cart requires atomic modifications (Add Item, Update Quantity, Remove Item, Clear Cart) that must be recorded, logged for session audits, and rolled back upon customer request.
- **Concrete Class Mappings:**
  - Command Interface: `com.smartretail.designpatterns.behavioral.command.CartCommand`
  - Concrete Commands: `AddToCartCommand`, `RemoveItemCommand`, `ClearCartCommand`, `UpdateQuantityCommand`
  - Receiver: `Cart`
  - Invoker: `CartCommandInvoker`
- **Code Snippet:**
  ```java
  public class AddToCartCommand implements CartCommand {
      private final Cart cart;
      private final Product product;
      private final int quantity;
      public void execute() { cart.addItem(product, quantity); }
      public void undo() { cart.removeItem(product.getId()); }
  }
  ```
- **Architectural Rationale:** Decouples user interactions from cart mutations, providing an architectural foundation for multi-level undo operations.

---

### 4.3 Iterator Pattern
- **GoF Classification:** Behavioral (Object)
- **Formal Intent:** Provide a way to access the elements of an aggregate object sequentially without exposing its underlying representation.
- **Retail Problem Solved:** Product catalogs are stored in indexed database sets. Clients need to iterate over products with specialized filter criteria (e.g. In-Stock only, On-Sale only, Category-specific) without writing repetitive filtering loops throughout client UI code.
- **Concrete Class Mappings:**
  - Iterator Interface: `com.smartretail.designpatterns.behavioral.iterator.ProductIterator`
  - Aggregate Interface: `com.smartretail.designpatterns.behavioral.iterator.ProductCollection`
  - Concrete Iterators: `ProductCatalogIterator`, `InStockProductIterator`
- **Code Snippet:**
  ```java
  public class InStockProductIterator implements ProductIterator {
      private final List<Product> list;
      private int position = 0;
      @Override
      public boolean hasNext() {
          while (position < list.size()) {
              if (list.get(position).getQuantity() > 0) return true;
              position++;
          }
          return false;
      }
  }
  ```
- **Architectural Rationale:** Encapsulates internal product filtering logic, shielding clients from internal collection structures.

---

### 4.4 Mediator Pattern
- **GoF Classification:** Behavioral (Object)
- **Formal Intent:** Define an object that encapsulates how a set of objects interact. Mediator promotes loose coupling by keeping objects from referring to each other explicitly.
- **Retail Problem Solved:** When an order is completed, four peer subsystems must react: `InventoryManager` decrements stock, `AccountingManager` updates revenue ledgers, `NotificationManager` sends SMS/Email, and `LoyaltyService` awards reward points. Having these subsystems talk directly to one another creates an unmanageable mesh network of dependencies.
- **Concrete Class Mappings:**
  - Mediator Interface: `com.smartretail.designpatterns.behavioral.mediator.OrderFulfillmentMediator`
  - Concrete Mediator: `com.smartretail.designpatterns.behavioral.mediator.RetailOrderMediatorImpl`
- **Code Snippet:**
  ```java
  public class RetailOrderMediatorImpl implements OrderFulfillmentMediator {
      @Override
      public void onOrderPlaced(Order order) {
          inventorySubsystem.reserve(order.getItems());
          billingSubsystem.recordRevenue(order.getTotalAmount());
          loyaltySubsystem.creditPoints(order.getUser(), (int)(order.getTotalAmount() / 100));
          notificationSubsystem.sendReceipt(order);
      }
  }
  ```
- **Architectural Rationale:** Reduces n-to-n dependencies between retail subsystems to a clean 1-to-n star topology.

---

### 4.5 Memento Pattern
- **GoF Classification:** Behavioral (Object)
- **Formal Intent:** Without violating encapsulation, capture and externalize an object's internal state so that the object can be restored to this state later.
- **Retail Problem Solved:** If a customer accidentally clicks "Clear Cart", they lose their entire selected basket. Storing public setters exposes internal cart representations. The Memento pattern captures an immutable snapshot of cart state before clearing, allowing instant one-click **[ UNDO ]** recovery.
- **Concrete Class Mappings:**
  - Originator: `com.smartretail.entity.Cart`
  - Memento: `com.smartretail.designpatterns.behavioral.memento.CartMemento`
  - Caretaker: `com.smartretail.designpatterns.behavioral.memento.CartCaretaker`
- **Code Snippet:**
  ```java
  public class CartMemento {
      private final List<CartItemSnapshot> itemsSnapshot;
      public CartMemento(List<CartItem> items) {
          this.itemsSnapshot = items.stream().map(CartItemSnapshot::new).toList();
      }
      List<CartItemSnapshot> getSavedState() { return itemsSnapshot; }
  }
  ```
- **Architectural Rationale:** Encapsulates cart state preservation safely without exposing internal collections to outside classes.

---

### 4.6 Observer Pattern
- **GoF Classification:** Behavioral (Object)
- **Formal Intent:** Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically.
- **Retail Problem Solved:** When a product's stock drops below safe thresholds (<= 5 units), warehouse managers and automated supplier ordering systems must be alerted immediately. Hardcoding notifications inside stock adjustment methods tightly couples inventory management to notification channels.
- **Concrete Class Mappings:**
  - Subject: `com.smartretail.designpatterns.behavioral.observer.InventorySubject`
  - Observer Interface: `com.smartretail.designpatterns.behavioral.observer.InventoryObserver`
  - Concrete Observers: `AdminAlertObserver`, `SupplierReorderObserver`, `CustomerStockWatcherObserver`
- **Code Snippet:**
  ```java
  public class InventorySubject {
      private final List<InventoryObserver> observers = new ArrayList<>();
      public void notifyLowStock(Product product) {
          observers.forEach(obs -> obs.onLowStockAlert(product));
      }
  }
  ```
- **Architectural Rationale:** Decouples stock deduction events from multi-channel alert mechanisms (UI toast, database notifications, SMS).

---

### 4.7 State Pattern
- **GoF Classification:** Behavioral (Object)
- **Formal Intent:** Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.
- **Retail Problem Solved:** An order moves through strict lifecycle stages: `PENDING` -> `PAID` -> `PROCESSING` -> `SHIPPED` -> `DELIVERED`. Certain operations are illegal in specific states (e.g., an order cannot be cancelled once `SHIPPED`; an order cannot be refunded before it is `PAID`). Massive `switch-case` blocks are brittle and difficult to maintain.
- **Concrete Class Mappings:**
  - State Interface: `com.smartretail.designpatterns.behavioral.state.OrderState`
  - Concrete States: `PendingState`, `PaidState`, `ProcessingState`, `ShippedState`, `DeliveredState`, `CancelledState`
  - Context: `com.smartretail.entity.Order`
- **Code Snippet:**
  ```java
  public class ShippedState implements OrderState {
      @Override
      public void next(Order context) { context.setState(new DeliveredState()); }
      @Override
      public void cancel(Order context) {
          throw new IllegalStateException("Cannot cancel an order that is already SHIPPED.");
      }
  }
  ```
- **Architectural Rationale:** State-specific logic is localized into dedicated classes, ensuring clean transitions and preventing invalid business actions.

---

### 4.8 Strategy Pattern
- **GoF Classification:** Behavioral (Object)
- **Formal Intent:** Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.
- **Retail Problem Solved:** Retail pricing rules change constantly (e.g. Flat ₹50 off, 10% Festival Discount, Gold Loyalty Tier 15% off, Buy-One-Get-One). Hardcoding `if-else` discount calculations in checkout services makes promotional campaigns hard to manage and expand.
- **Concrete Class Mappings:**
  - Strategy Interface: `com.smartretail.designpatterns.behavioral.strategy.DiscountStrategy`
  - Concrete Strategies: `PercentageDiscountStrategy`, `FlatDiscountStrategy`, `TieredLoyaltyDiscountStrategy`, `NoDiscountStrategy`
  - Context: `PricingCalculator`
- **Code Snippet:**
  ```java
  public class PercentageDiscountStrategy implements DiscountStrategy {
      private final double percentage;
      @Override
      public double applyDiscount(double originalAmount) {
          return originalAmount * (percentage / 100.0);
      }
  }
  ```
- **Architectural Rationale:** Promotional discount engines can be modified, tested, and assigned dynamically at runtime based on active vouchers.

---

### 4.9 Template Method Pattern
- **GoF Classification:** Behavioral (Class)
- **Formal Intent:** Define the skeleton of an algorithm in an operation, deferring some steps to subclasses. Template Method lets subclasses redefine certain steps of an algorithm without changing the algorithm's structure.
- **Retail Problem Solved:** Order fulfillment follows an invariant five-step workflow: `1. Verify Payment` -> `2. Pick Items` -> `3. Package Order` -> `4. Dispatch` -> `5. Notify Customer`. However, step 3 and 4 differ drastically between *Physical Goods* (physical parcel packaging and shipping truck dispatch) and *Digital Goods* (license key generator and email delivery).
- **Concrete Class Mappings:**
  - Abstract Template: `com.smartretail.designpatterns.behavioral.template.OrderProcessingTemplate`
  - Concrete Subclasses: `PhysicalOrderProcessor`, `DigitalOrderProcessor`
- **Code Snippet:**
  ```java
  public abstract class OrderProcessingTemplate {
      public final void processOrder(Order order) {
          verifyPayment(order);
          reserveStock(order);
          packageItems(order); // Hook / abstract step
          dispatch(order);     // Hook / abstract step
          sendConfirmation(order);
      }
      protected abstract void packageItems(Order order);
      protected abstract void dispatch(Order order);
  }
  ```
- **Architectural Rationale:** Guarantees that payment verification and confirmation can never be bypassed while allowing fulfillment steps to vary.

---

### 4.10 Visitor Pattern
- **GoF Classification:** Behavioral (Object)
- **Formal Intent:** Represent an operation to be performed on the elements of an object structure. Visitor lets you define a new operation without changing the classes of the elements on which it operates.
- **Retail Problem Solved:** Inventory items need frequent new operations: dynamic GST calculations, warehouse shelf space estimation, refrigeration cost projections, and fiscal audit reports. Adding methods directly to the `Product` entity repeatedly pollutes domain models with unrelated operational logic.
- **Concrete Class Mappings:**
  - Visitor Interface: `com.smartretail.designpatterns.behavioral.visitor.ProductVisitor`
  - Concrete Visitors: `TaxCalculationVisitor`, `InventoryAuditVisitor`, `SpaceEstimationVisitor`
  - Element Interface: `VisitableProduct`
- **Code Snippet:**
  ```java
  public class TaxCalculationVisitor implements ProductVisitor {
      private double totalTax = 0;
      public void visit(FoodProduct p) { totalTax += p.getPrice() * 0.05; }
      public void visit(ElectronicsProduct p) { totalTax += p.getPrice() * 0.18; }
      public void visit(GroceryProduct p) { totalTax += 0.0; } // Exempt
  }
  ```
- **Architectural Rationale:** New reporting, auditing, and tax compliance calculations can be introduced without modifying the core `Product` class hierarchy.

---

## 5. Modern Application Architecture Patterns

In addition to the 23 GoF patterns, the system incorporates **6 foundational architectural patterns** used in modern software engineering.

### 5.1 Model-View-Controller (MVC)
- **Implementation:** Spring Boot Web Tier
- **Model:** JPA Entities (`Product`, `Order`, `Category`, `CartItem`) representing the business domain.
- **View:** React Client Pages & JSON serialization schemas.
- **Controller:** Spring REST Controllers (`ProductController`, `OrderController`) mapping HTTP verbs to application service calls.
- **Rationale:** Strict separation of data access, presentation, and routing.

### 5.2 Model-View-Presenter (MVP)
- **Implementation:** Admin Discount Management (`com.smartretail.applicationpatterns.mvp`)
- **Model:** `Discount` entity and repository.
- **View:** `DiscountViewContract` specifying UI capabilities (`showDiscounts()`, `showErrorMessage()`).
- **Presenter:** `DiscountPresenter` which interacts with the model and pushes computed view data to the view without referencing HTTP or UI components.
- **Rationale:** Facilitates high-speed unit testing of administrative discount logic in complete isolation from UI frameworks.

### 5.3 Model-View-Intent (MVI)
- **Implementation:** Cart Reactive State Flow (`com.smartretail.applicationpatterns.mvi`)
- **Intent:** Explicit user intents (`AddItemIntent`, `UpdateQtyIntent`, `ClearCartIntent`).
- **Model:** Immutable `CartState` (items, subtotal, discount, grandTotal, undoAvailable).
- **View:** React Cart Page rendering immutable state streams.
- **Rationale:** Unidirectional, deterministic state mutations eliminate state synchronization bugs during shopping.

### 5.4 Model-View-ViewModel (MVVM)
- **Implementation:** Frontend React Custom ViewModels
- **Model:** `productAPI` Axios HTTP communication layer.
- **ViewModel:** React State & Hooks (`useProductCatalog`, `useCartState`) encapsulating sorting, search filtering, and state transformation.
- **View:** `ProductCatalog.jsx` and `ProductCard.jsx` purely rendering ViewModel properties.
- **Rationale:** Two-way binding behavior in React, keeping presentation components dumb and reusable.

### 5.5 VIPER (View-Interactor-Presenter-Entity-Router)
- **Implementation:** High-Reliability Inventory Module (`com.smartretail.applicationpatterns.viper`)
- **Entity:** `InventoryItem`
- **Interactor:** `InventoryInteractor` executing stock math and low-stock rules.
- **Presenter:** `InventoryPresenter` formatting stock values for display.
- **View:** `InventoryViewContract`
- **Router:** `InventoryRouter` managing navigation and drill-down routes.
- **Rationale:** Enterprise modularity used in mission-critical applications where business logic must be isolated from presentation routing.

### 5.6 Microservices Architecture Pattern
- **Implementation:** Bounded Context Domain Modules (`com.smartretail.applicationpatterns.microservices`)
- **Domain Services:**
  1. *Catalog Service:* Product definition, hierarchy, and search.
  2. *Cart Service:* Session cart management and command dispatch.
  3. *Order & Checkout Service:* Orchestration of payment, tax, and order state.
  4. *Inventory Service:* Stock locks, concurrency, and telemetry.
  5. *Notification Service:* Observer alerts and message delivery.
- **Rationale:** Ensures clean domain boundaries and independence of data concerns, allowing future containerized decomposition.
