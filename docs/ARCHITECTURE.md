# SYSTEM ARCHITECTURE SPECIFICATION
### Smart Retail Management System
**Department of Computer Science and Engineering | B.E. Degree Capstone**

---

## 1. Architectural Overview

The **Smart Retail Management System** is architected as an enterprise-grade, clean-layered system that marries Spring Boot's robust server-side dependency injection and transaction management with a modern React 18 single-page application (SPA).

The architecture is governed by two core engineering tenets:
1. **Clean Architecture Separation of Concerns:** Rigid boundaries between presentation, application orchestration, domain business patterns, and data persistence.
2. **Design Pattern Idiomaticity:** Patterns are applied strictly to solve concrete retail engineering problems rather than as ornamental or academic abstractions.

```
+-------------------------------------------------------------------------+
|                       PRESENTATION TIER (React 18 SPA)                  |
|  - Customer Storefront (Catalog, Cart, Checkout, Order Tracking)        |
|  - Admin Management (Dashboard, Products, Inventory, Discounts, Alerts)  |
|  - Pattern Explorer Modal (23 GoF + 6 Architectural Pattern Inspector)  |
+-------------------------------------------------------------------------+
                                    │  HTTPS / JSON (REST API)
                                    ▼
+-------------------------------------------------------------------------+
|                  REST API CONTROLLERS (Spring Web MVC)                  |
|  ProductController  •  CartController  •  CheckoutController            |
|  OrderController    •  BillController  •  InventoryController           |
+-------------------------------------------------------------------------+
                                    │
                                    ▼
+-------------------------------------------------------------------------+
|                     APPLICATION ARCHITECTURE PATTERNS                   |
|  MVC (Spring MVC)   •  MVP (Discount Presenter)  •  MVI (Cart Intent)   |
|  MVVM (UI Hooks)    •  VIPER (Inventory Module)  •  Microservices       |
+-------------------------------------------------------------------------+
                                    │
                                    ▼
+-------------------------------------------------------------------------+
|                        GoF DESIGN PATTERNS CORE                         |
|  Creational:   Factory Method, Abstract Factory, Builder,               |
|                Prototype, Singleton                                     |
|  Structural:   Adapter, Bridge, Composite, Decorator, Facade,           |
|                Flyweight, Proxy                                         |
|  Behavioral:   Chain of Responsibility, Command, Iterator, Mediator,    |
|                Memento, Observer, State, Strategy, Template, Visitor   |
+-------------------------------------------------------------------------+
                                    │
                                    ▼
+-------------------------------------------------------------------------+
|                      CORE BUSINESS LOGIC SERVICES                       |
|  ProductService  •  OrderService  •  InventoryService  •  AuthService   |
+-------------------------------------------------------------------------+
                                    │
                                    ▼
+-------------------------------------------------------------------------+
|                     PERSISTENCE LAYER (Spring Data JPA)                 |
|  ProductRepo  •  OrderRepo  •  UserRepo  •  BillRepo  •  InventoryRepo  |
+-------------------------------------------------------------------------+
                                    │  JDBC
                                    ▼
+-------------------------------------------------------------------------+
|                      DATABASE STORAGE ENGINE                            |
|       MySQL 8.0 (Production)  /  H2 In-Memory (Standalone Dev)          |
+-------------------------------------------------------------------------+
```

---

## 2. Layered Responsibilities

### 2.1 Presentation Layer (Frontend)
- **Technology:** React 18, Vite 5, React Router 6, Lucide Icons.
- **Role:** Renders dynamic views, manages client state, captures customer intents (e.g. cart modifications, discount codes), and executes role switching between Customer and Administrator.
- **Key Characteristics:**
  - Zero third-party UI framework lock-in (pure, hand-crafted CSS tokens with responsive typography and card grids).
  - Native print stylesheet (`@media print`) rendering 58mm POS thermal receipt styling.
  - Interactive Memento **[ UNDO ]** toast container with timeout countdowns.

### 2.2 REST Controller Layer (API Gateway)
- **Technology:** Spring Web MVC (`@RestController`, `@RequestMapping`).
- **Role:** Validates inbound HTTP JSON payloads, manages HTTP status codes (200 OK, 201 Created, 400 Bad Request, 403 Forbidden, 404 Not Found), and invokes application services.
- **Key Characteristics:**
  - Stateless authentication via JSON Web Tokens (JJWT 0.12.5).
  - Cross-Origin Resource Sharing (CORS) configured for port `3000` (Vite) and port `8080` (Spring Boot).

### 2.3 Application Patterns Layer
- **MVP (Model-View-Presenter):** Used in the discount engine to decouple discount presentation logic from repository calls, making pricing rule verification 100% testable without a browser.
- **MVI (Model-View-Intent):** Unidirectional data flow in cart operations: User Intent (`AddToCartIntent`) -> Reducer -> Immutable State Snapshot -> React View.
- **VIPER:** Segregates inventory operations into View, Interactor (business math), Presenter (display formatting), Entity (database model), and Router (navigation transitions).
- **MVVM:** Custom React hooks act as ViewModels that bind view components to REST API data sources with automatic reactivity.

### 2.4 GoF Design Patterns Layer
- The central domain core containing all 23 GoF pattern abstractions and concrete classes under `com.smartretail.designpatterns`.
- Services call these pattern components directly, establishing high cohesion and loose coupling.

### 2.5 Persistence Layer
- **Technology:** Spring Data JPA with Hibernate ORM.
- **Role:** Maps domain entities (`Product`, `Category`, `Order`, `OrderItem`, `BillReceipt`, `InventoryItem`, `User`, `Notification`) to relational tables.
- **Key Characteristics:**
  - Automatic schema generation (`ddl-auto: update`).
  - Pessimistic and Optimistic locking configurations for high-concurrency inventory adjustments.

---

## 3. SOLID Principles Realization

The system adheres strictly to the five SOLID principles of object-oriented design:

### 3.1 Single Responsibility Principle (SRP)
- Each class has one, and only one, reason to change:
  - `ProductFactory`: Responsible solely for instantiating specialized product subtypes.
  - `BillReceiptBuilder`: Responsible solely for constructing bill entity instances.
  - `TaxCalculationVisitor`: Responsible solely for computing category-specific tax formulas.
  - `InventoryLedgerManager`: Responsible solely for thread synchronization around stock counts.

### 3.2 Open/Closed Principle (OCP)
- Classes are open for extension but closed for modification:
  - Adding a new promotional discount rule requires creating a new class implementing `DiscountStrategy` without touching existing discount classes or checkout logic.
  - Adding a new payment method (e.g. Crypto or Apple Pay) requires creating a new `PaymentGatewayAdapter` without modifying `RetailCheckoutFacade`.
  - Adding a new product category requires a new `ProductFactory` subclass without altering existing factories.

### 3.3 Liskov Substitution Principle (LSP)
- Subclasses or interface implementations can be substituted without altering system correctness:
  - Any concrete `OrderState` (`PaidState`, `ProcessingState`, `ShippedState`) can be passed to `Order.setState()` and invoke `next()` or `cancel()` without unexpected behavior.
  - Both `FoodProduct` and `ElectronicsProduct` can be processed by `CatalogVisitor` or iterated over by `ProductIterator`.

### 3.4 Interface Segregation Principle (ISP)
- Interfaces are lean and client-focused rather than fat and monolithic:
  - `ProductIterator` contains only `hasNext()` and `next()`.
  - `PaymentGateway` contains only `processPayment(PaymentRequest)`.
  - `InventoryObserver` contains only `onLowStockAlert(Product)`.
  - Clients are never forced to depend on methods they do not use.

### 3.5 Dependency Inversion Principle (DIP)
- High-level modules do not depend on low-level modules; both depend on abstractions:
  - `RetailCheckoutFacade` depends on the `PaymentGateway` interface, not on concrete `StripePaymentService` or `UpiPaymentService`.
  - `ReceiptAbstraction` depends on the `ReceiptRenderer` interface, not on a physical printer driver.
  - Controllers depend on Spring Service interfaces injected via Spring's IoC container.

---

## 4. Microservices Bounded Contexts

The architecture implements a modular monolith designed with strict **Domain-Driven Design (DDD) Bounded Contexts**, providing an immediate pathway for microservice extraction:

```
┌─────────────────────────────────────────────────────────────┐
│                      SMART RETAIL DOMAINS                   │
├─────────────────┬─────────────────┬─────────────────────────┤
│ Catalog Context │  Cart Context   │ Order & Checkout Context│
│  - Products     │  - Cart Items   │  - Order Lifecycle      │
│  - Categories   │  - Commands     │  - Facade Orchestration │
│  - Composite    │  - Memento Undo │  - Payment Adapters     │
├─────────────────┼─────────────────┼─────────────────────────┤
│Inventory Context│ Billing Context │  Notification Context   │
│  - Stock Ledger │  - Bill Builder │  - Observer Alerts      │
│  - VIPER Module │  - Bridge Render│  - Stock Warnings       │
│  - Concurrency  │  - GST Invoicing│  - Customer Updates     │
└─────────────────┴─────────────────┴─────────────────────────┘
```

### Inter-Context Communication
- **Synchronous Calls:** Direct method calls via interfaces within the Spring application context.
- **Asynchronous Event Broadcasting:** Observer Pattern subjects publish events (e.g. `LowStockEvent`, `OrderPlacedEvent`) that decoupled observer listeners consume without circular dependencies.

---

## 5. Security & Concurrency Architecture

### 5.1 Authentication & Authorization
- **Stateless JWT:** Token issued on login containing user ID, email, and role.
- **Role-Based Access Control (RBAC):**
  - `ROLE_CUSTOMER`: Access to catalog, cart mutations, checkout, personal orders.
  - `ROLE_ADMIN`: Access to dashboard analytics, product creation/cloning, stock overrides, order state transitions, and audit logs.
- **Proxy Protection:** `ProductSecurityProxy` performs secondary authorization checks on critical database deletions.

### 5.2 Concurrency & Race-Condition Prevention
- **Inventory Concurrency:** When thousands of customers checkout simultaneously, inventory contention on popular SKUs is serialized via `InventoryLedgerManager`'s granular `ConcurrentHashMap<Long, ReentrantLock>` locking mechanism.
- This guarantees that stock decrements are atomic, preventing overselling and negative inventory states.
