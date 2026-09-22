# UML & ARCHITECTURAL DIAGRAMS SUITE
### Smart Retail Management System
**Department of Computer Science and Engineering | B.E. Degree Capstone**

---

## 1. System Architecture Diagram

```mermaid
graph TD
    subgraph ClientTier ["Client Tier (React 18 SPA - Vite)"]
        UI_Cust["Customer Views (Catalog, Cart, Checkout, Invoices)"]
        UI_Admin["Admin Views (Dashboard, Products, Inventory, Discounts)"]
        UI_Modal["Design Pattern Explorer Modal (29 Patterns)"]
    end

    subgraph APITier ["API & Controller Tier (Spring Web MVC)"]
        AuthController["AuthController"]
        ProductController["ProductController"]
        CartController["CartController"]
        CheckoutController["CheckoutController"]
        OrderController["OrderController"]
        BillController["BillController"]
        InvController["InventoryController"]
        NotificationController["NotificationController"]
    end

    subgraph AppPatterns ["Application Architecture Patterns"]
        MVP["MVP: Discount Presenter"]
        MVI["MVI: Reactive Cart Intent Flow"]
        MVVM["MVVM: UI Hooks & ViewModels"]
        VIPER["VIPER: High-Reliability Inventory Module"]
    end

    subgraph DomainCore ["GoF Design Patterns Core"]
        Creational["Creational Patterns (Factory, Builder, Prototype, Singleton, Abstract Factory)"]
        Structural["Structural Patterns (Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy)"]
        Behavioral["Behavioral Patterns (Chain of Resp, Command, Iterator, Mediator, Memento, Observer, State, Strategy, Template, Visitor)"]
    end

    subgraph PersistenceTier ["Persistence Tier (Spring Data JPA)"]
        UserRepo["UserRepository"]
        ProductRepo["ProductRepository"]
        OrderRepo["OrderRepository"]
        BillRepo["BillReceiptRepository"]
        InvRepo["InventoryRepository"]
    end

    subgraph DataStorage ["Data Storage Layer"]
        MySQL[("MySQL 8.0 / H2 Database")]
    end

    ClientTier -->|JSON over HTTP| APITier
    APITier --> AppPatterns
    APITier --> DomainCore
    AppPatterns --> DomainCore
    DomainCore --> PersistenceTier
    PersistenceTier --> DataStorage
```

---

## 2. Use Case Diagram

```mermaid
graph LR
    actor Customer as "Customer"
    actor Admin as "Administrator"
    actor Gateway as "External Payment Gateway"

    subgraph Storefront ["Smart Retail Storefront"]
        UC1(["Browse & Search Products"])
        UC2(["Filter by Stock (Iterator)"])
        UC3(["Add / Modify Cart (Command)"])
        UC4(["Undo Clear Cart (Memento)"])
        UC5(["Apply Voucher (Strategy)"])
        UC6(["Checkout Order (Facade)"])
        UC7(["Print Thermal Bill (Bridge)"])
        UC8(["Track Order Status (State)"])
    end

    subgraph Administration ["Store Administration"]
        UC9(["Create Product Subtypes (Factory)"])
        UC10(["Clone Product SKU (Prototype)"])
        UC11(["Inspect Composite Valuation (Composite)"])
        UC12(["Thread-Safe Stock Adjust (Singleton)"])
        UC13(["Receive Low-Stock Alerts (Observer)"])
        UC14(["Advance Order State (State)"])
        UC15(["Run GST Tax Audit (Visitor)"])
        UC16(["Delete Product (Security Proxy)"])
    end

    Customer --> UC1
    Customer --> UC2
    Customer --> UC3
    Customer --> UC4
    Customer --> UC5
    Customer --> UC6
    Customer --> UC7
    Customer --> UC8

    Admin --> UC9
    Admin --> UC10
    Admin --> UC11
    Admin --> UC12
    Admin --> UC13
    Admin --> UC14
    Admin --> UC15
    Admin --> UC16

    UC6 --> Gateway
```

---

## 3. Creational Patterns Class Diagram

```mermaid
classDiagram
    class ProductFactory {
        <<abstract>>
        +createProduct(ProductDTO dto)* Product
    }
    class FoodProductFactory {
        +createProduct(ProductDTO dto) Product
    }
    class ElectronicsProductFactory {
        +createProduct(ProductDTO dto) Product
    }
    class GroceryProductFactory {
        +createProduct(ProductDTO dto) Product
    }
    ProductFactory <|-- FoodProductFactory
    ProductFactory <|-- ElectronicsProductFactory
    ProductFactory <|-- GroceryProductFactory

    class ProductPrototype {
        <<interface>>
        +cloneProduct(String name, Double price, String sku) Product
    }
    class Product {
        -Long id
        -String name
        -Double price
        -String sku
        +cloneProduct(String name, Double price, String sku) Product
    }
    ProductPrototype <|.. Product

    class BillReceiptBuilder {
        -BillReceipt bill
        +withBillNumber(String num) BillReceiptBuilder
        +forOrder(Order order) BillReceiptBuilder
        +withCustomer(String name, String email) BillReceiptBuilder
        +withSubtotal(Double subtotal) BillReceiptBuilder
        +applyDiscount(Double discount) BillReceiptBuilder
        +calculateTax(Double rate) BillReceiptBuilder
        +build() BillReceipt
    }
    class BillReceipt {
        -String billNumber
        -Double subtotal
        -Double taxAmount
        -Double grandTotal
    }
    BillReceiptBuilder ..> BillReceipt : constructs

    class InventoryLedgerManager {
        -static InventoryLedgerManager instance
        -ConcurrentHashMap productLocks
        -InventoryLedgerManager()
        +static getInstance() InventoryLedgerManager
        +lockProduct(Long productId) void
        +unlockProduct(Long productId) void
    }

    class RetailReceiptFactory {
        <<interface>>
        +createFormatter() ReceiptFormatter
        +createProcessor() PaymentProcessor
    }
    class PhysicalStoreReceiptFactory {
        +createFormatter() ReceiptFormatter
        +createProcessor() PaymentProcessor
    }
    class OnlineReceiptFactory {
        +createFormatter() ReceiptFormatter
        +createProcessor() PaymentProcessor
    }
    RetailReceiptFactory <|.. PhysicalStoreReceiptFactory
    RetailReceiptFactory <|.. OnlineReceiptFactory
```

---

## 4. Structural Patterns Class Diagram

```mermaid
classDiagram
    class RetailCheckoutFacade {
        -ValidationChain validationChain
        -DiscountEngine discountEngine
        -PaymentGateway paymentGateway
        -OrderService orderService
        -BillService billService
        -NotificationService notificationService
        +processCheckout(CheckoutRequestDTO req) CheckoutResponseDTO
    }

    class CatalogComponent {
        <<interface>>
        +getName() String
        +getStockValuation() double
        +getTotalItems() int
    }
    class ProductLeaf {
        -Product product
        +getStockValuation() double
        +getTotalItems() int
    }
    class CategoryComposite {
        -String categoryName
        -List~CatalogComponent~ children
        +add(CatalogComponent c) void
        +getStockValuation() double
        +getTotalItems() int
    }
    CatalogComponent <|.. ProductLeaf
    CatalogComponent <|.. CategoryComposite
    CategoryComposite o-- CatalogComponent

    class PaymentGateway {
        <<interface>>
        +processPayment(PaymentRequest req) PaymentResult
    }
    class StripePaymentAdapter {
        -StripeService stripe
        +processPayment(PaymentRequest req) PaymentResult
    }
    class UpiPaymentAdapter {
        -UpiService upi
        +processPayment(PaymentRequest req) PaymentResult
    }
    PaymentGateway <|.. StripePaymentAdapter
    PaymentGateway <|.. UpiPaymentAdapter

    class ReceiptAbstraction {
        <<abstract>>
        #ReceiptRenderer renderer
        +generate(BillReceipt b)* String
    }
    class StandardReceipt {
        +generate(BillReceipt b) String
    }
    class ReceiptRenderer {
        <<interface>>
        +renderHeader(String store) String
        +renderItems(List items) String
        +renderTotals(double total) String
    }
    class ThermalReceiptRenderer {
        +renderHeader(String store) String
    }
    ReceiptAbstraction <|-- StandardReceipt
    ReceiptAbstraction o-- ReceiptRenderer
    ReceiptRenderer <|.. ThermalReceiptRenderer

    class ProductSecurityProxy {
        -ProductServiceImpl realService
        +deleteProduct(Long id, User caller) void
    }
    ProductSecurityProxy --> ProductServiceImpl : delegates
```

---

## 5. Behavioral Patterns Class Diagram

```mermaid
classDiagram
    class CartCommand {
        <<interface>>
        +execute() void
        +undo() void
    }
    class AddToCartCommand {
        -Cart cart
        -Product product
        -int quantity
        +execute() void
        +undo() void
    }
    class ClearCartCommand {
        -Cart cart
        -CartCaretaker caretaker
        +execute() void
        +undo() void
    }
    CartCommand <|.. AddToCartCommand
    CartCommand <|.. ClearCartCommand

    class CartMemento {
        -List~CartItemSnapshot~ state
        ~getSavedState() List~CartItemSnapshot~
    }
    class CartCaretaker {
        -Deque~CartMemento~ history
        +saveSnapshot(CartMemento m) void
        +popSnapshot() CartMemento
    }
    CartCaretaker o-- CartMemento

    class OrderState {
        <<interface>>
        +next(Order context) void
        +cancel(Order context) void
        +getStatusName() String
    }
    class PaidState {
        +next(Order context) void
        +cancel(Order context) void
    }
    class ProcessingState {
        +next(Order context) void
        +cancel(Order context) void
    }
    class ShippedState {
        +next(Order context) void
        +cancel(Order context) void
    }
    OrderState <|.. PaidState
    OrderState <|.. ProcessingState
    OrderState <|.. ShippedState

    class DiscountStrategy {
        <<interface>>
        +applyDiscount(double amount) double
    }
    class PercentageDiscountStrategy {
        -double percentage
        +applyDiscount(double amount) double
    }
    class FlatDiscountStrategy {
        -double discountAmount
        +applyDiscount(double amount) double
    }
    DiscountStrategy <|.. PercentageDiscountStrategy
    DiscountStrategy <|.. FlatDiscountStrategy

    class InventorySubject {
        -List~InventoryObserver~ observers
        +attach(InventoryObserver o) void
        +notifyLowStock(Product p) void
    }
    class InventoryObserver {
        <<interface>>
        +onLowStockAlert(Product p) void
    }
    class AdminAlertObserver {
        +onLowStockAlert(Product p) void
    }
    InventorySubject o-- InventoryObserver
    InventoryObserver <|.. AdminAlertObserver
```

---

## 6. Sequence Diagram: Multi-Step Checkout Flow

```mermaid
sequenceDiagram
    autonumber
    actor Customer as "Customer Client"
    participant CheckoutCtrl as "CheckoutController"
    participant Facade as "RetailCheckoutFacade"
    participant Chain as "CheckoutValidationChain"
    participant Strategy as "DiscountStrategy"
    participant Adapter as "PaymentGatewayAdapter"
    participant OrderSvc as "OrderService (Builder & State)"
    participant InvLedger as "InventoryLedger (Singleton)"
    participant BillBuilder as "BillReceiptBuilder"
    participant Observer as "NotificationObserver"

    Customer->>CheckoutCtrl: POST /api/checkout (Order Data)
    CheckoutCtrl->>Facade: processCheckout(CheckoutDTO)
    
    Facade->>Chain: validate(cart, user)
    Chain-->>Facade: Validation Passed
    
    Facade->>Strategy: applyDiscount(subtotal, promoCode)
    Strategy-->>Facade: netPayableAmount
    
    Facade->>Adapter: processPayment(netPayableAmount, method)
    Adapter-->>Facade: PaymentResult (Success, TxnId)
    
    Facade->>OrderSvc: createOrder(orderData, InitialState=PAID)
    OrderSvc-->>Facade: Order Entity
    
    Facade->>InvLedger: lock & deductStock(orderItems)
    InvLedger-->>Facade: Stock Decremented Safely
    
    Facade->>BillBuilder: build(order, discount, tax, payment)
    BillBuilder-->>Facade: BillReceipt (BILL-xxxx)
    
    Facade->>Observer: notifyOrderPlaced(order)
    Observer-->>Facade: Alerts Broadcasted
    
    Facade-->>CheckoutCtrl: CheckoutResponseDTO (Success)
    CheckoutCtrl-->>Customer: 200 OK (Order ID, Bill Receipt)
```

---

## 7. Sequence Diagram: Cart Clear & Memento Undo

```mermaid
sequenceDiagram
    autonumber
    actor Customer as "Customer Client"
    participant CartCtrl as "CartController"
    participant Invoker as "CartCommandInvoker"
    participant ClearCmd as "ClearCartCommand"
    participant Cart as "Cart Originator"
    participant Caretaker as "CartCaretaker"

    Note over Customer,Caretaker: Phase 1: Clear Cart with Memento Snapshot
    Customer->>CartCtrl: POST /api/cart/clear/{userId}
    CartCtrl->>Invoker: executeCommand(new ClearCartCommand(cart, caretaker))
    Invoker->>ClearCmd: execute()
    ClearCmd->>Cart: createMemento()
    Cart-->>ClearCmd: CartMemento (Snapshot of items)
    ClearCmd->>Caretaker: saveSnapshot(memento)
    ClearCmd->>Cart: clearItems()
    CartCtrl-->>Customer: 200 OK (Empty Cart, undoAvailable: true)

    Note over Customer,Caretaker: Phase 2: Customer Clicks [ UNDO ]
    Customer->>CartCtrl: POST /api/cart/undo/{userId}
    CartCtrl->>Caretaker: popSnapshot()
    Caretaker-->>CartCtrl: CartMemento
    CartCtrl->>Cart: restoreState(memento)
    Cart-->>CartCtrl: Items Restored
    CartCtrl-->>Customer: 200 OK (Restored Cart Items)
```

---

## 8. State Machine Diagram: Order Lifecycle

```mermaid
stateDiagram-v2
    [*] --> PENDING : Order Created

    PENDING --> PAID : Payment Success
    PENDING --> CANCELLED : Customer Abort / Stock Failure

    PAID --> PROCESSING : Warehouse Packing
    PAID --> CANCELLED : Refund & Restock Command

    PROCESSING --> SHIPPED : Dispatched via Courier
    PROCESSING --> CANCELLED : Administrative Cancellation

    SHIPPED --> DELIVERED : Delivery Signature Confirmed
    DELIVERED --> [*]
    CANCELLED --> [*]

    note right of SHIPPED
      Once an order is SHIPPED,
      cancel() throws IllegalStateException
      enforced by the State Pattern.
    end note
```

---

## 9. Entity Relationship (ER) Diagram

```mermaid
erDiagram
    USER ||--o{ ORDER : places
    USER ||--o| CART : owns
    USER ||--o{ NOTIFICATION : receives

    CATEGORY ||--o{ PRODUCT : categorizes
    CATEGORY ||--o{ CATEGORY : parent_child

    PRODUCT ||--o{ CART_ITEM : included_in
    PRODUCT ||--o{ ORDER_ITEM : ordered_as
    PRODUCT ||--o| INVENTORY_ITEM : tracks_stock

    CART ||--o{ CART_ITEM : contains

    ORDER ||--o{ ORDER_ITEM : contains
    ORDER ||--|| BILL_RECEIPT : generates

    DISCOUNT ||--o{ ORDER : applied_to

    USER {
        bigint id PK
        string email
        string password
        string name
        string role
        string membership_type
    }

    PRODUCT {
        bigint id PK
        string name
        decimal price
        string sku
        string product_type
        string brand
        int quantity
        int min_stock_threshold
        bigint category_id FK
    }

    CATEGORY {
        bigint id PK
        string name
        string description
        bigint parent_id FK
    }

    CART {
        bigint id PK
        bigint user_id FK
        timestamp last_updated
    }

    CART_ITEM {
        bigint id PK
        bigint cart_id FK
        bigint product_id FK
        int quantity
    }

    ORDER {
        bigint id PK
        string order_number
        bigint user_id FK
        decimal total_amount
        decimal discount_amount
        decimal tax_amount
        string status
        string payment_method
        timestamp created_at
    }

    ORDER_ITEM {
        bigint id PK
        bigint order_id FK
        bigint product_id FK
        string product_name
        decimal price
        int quantity
        decimal subtotal
    }

    BILL_RECEIPT {
        bigint id PK
        string bill_number
        bigint order_id FK
        string customer_name
        decimal subtotal
        decimal discount_amount
        decimal tax_amount
        decimal grand_total
        text receipt_content
    }

    INVENTORY_ITEM {
        bigint id PK
        bigint product_id FK
        int current_stock
        int minimum_threshold
        string location_aisle
    }

    NOTIFICATION {
        bigint id PK
        bigint user_id FK
        string type
        string message
        boolean read_status
        timestamp created_at
    }

    DISCOUNT {
        bigint id PK
        string code
        string name
        string type
        decimal value
        boolean is_percentage
        decimal min_spend
        boolean active
    }
```
