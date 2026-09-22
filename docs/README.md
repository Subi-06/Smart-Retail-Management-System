# SMART RETAIL MANAGEMENT SYSTEM
### A Full-Stack Academic Capstone Project in Software Architecture & GoF Design Patterns
**Department of Computer Science and Engineering | B.E. Degree Course**

---

## 1. Executive Summary

The **Smart Retail Management System** is an enterprise-grade full-stack web application meticulously engineered to demonstrate the concrete, practical implementation of **all 23 Gang of Four (GoF) Design Patterns** alongside **6 Modern Application Architecture Patterns** (MVC, MVP, MVI, MVVM, VIPER, and Microservices).

Unlike theoretical toy examples, every single pattern in this project solves an authentic, high-impact business problem in contemporary retail operations—from dynamic discount strategies, hierarchical catalog valuation, and undoable cart commands to state-driven order fulfillment, proxy-protected administrative actions, and decoupled observer alerts.

---

## 2. Technical Stack

| Tier | Technology | Version | Purpose & Capabilities |
| :--- | :--- | :--- | :--- |
| **Backend** | Java | 21 (LTS) | Modern language features (records, pattern matching, sealed types) |
| **Framework** | Spring Boot | 3.3.3 | Dependency Injection, MVC, REST APIs, Security, Transactional Management |
| **Persistence** | Spring Data JPA / Hibernate | 6.x | ORM, Repository Pattern, Schema Auto-generation |
| **Database** | MySQL / H2 Fallback | 8.0 / In-Memory | Production MySQL support with zero-config standalone H2 fallback |
| **Security** | Spring Security + JJWT | 0.12.5 | Stateless JWT authentication, role-based authorization, BCrypt |
| **Frontend** | React | 18.3.1 | Component-based interactive UI with React Hooks and Context |
| **Build Tool** | Vite | 5.4.x | Blazing fast ESM bundler and development server |
| **Icons & UI** | Lucide React | 0.441.0 | Clean, accessible vector icons across retail & admin workflows |
| **Routing** | React Router DOM | 6.26.x | Client-side routing with nested admin and customer layouts |

---

## 3. Design Patterns Implementation Matrix

### 3.1 Creational Design Patterns (5 / 5)
| Pattern | Concrete Implementation Class | Retail Problem Solved |
| :--- | :--- | :--- |
| **Factory Method** | `ProductFactory`, `FoodProductFactory`, `ElectronicsProductFactory`, `ClothingProductFactory` | Polymorphic creation of diverse product types (Food with shelf life, Electronics with warranty, Groceries with GST exemption) without coupling client code. |
| **Abstract Factory** | `RetailReceiptFactory`, `PhysicalStoreReceiptFactory`, `OnlineReceiptFactory` | Produces coordinated families of invoice elements: Thermal POS receipts vs. GST E-Invoices, and Card vs. UPI/Cash payment processors. |
| **Builder** | `BillReceipt.Builder`, `Order.Builder` | Step-by-step construction of complex invoice receipts containing variable customer data, line items, tiered discounts, and GST calculations. |
| **Prototype** | `ProductPrototype`, `Product.cloneProduct()` | Rapid duplication of existing products with deep-cloned attributes (e.g. creating size or color variations of an item without re-entering all metadata). |
| **Singleton** | `InventoryLedgerManager`, `DatabaseConnectionManager` | Centralized, thread-safe access to critical inventory lock synchronization and retail ledger counters. |

### 3.2 Structural Design Patterns (7 / 7)
| Pattern | Concrete Implementation Class | Retail Problem Solved |
| :--- | :--- | :--- |
| **Adapter** | `PaymentGatewayAdapter`, `LegacyBankAdapter`, `ThirdPartyUpiAdapter` | Unifies disparate third-party payment gateways (Razorpay, UPI, Stripe, Legacy ISO-8583 banking) under a standardized `PaymentGateway` interface. |
| **Bridge** | `ReceiptBridge`, `ThermalReceiptRenderer`, `PdfInvoiceRenderer` | Decouples receipt abstractions (`CustomerReceipt`, `TaxInvoiceReceipt`) from rendering implementations (Thermal 58mm printer vs. PDF/HTML). |
| **Composite** | `CatalogComponent`, `CategoryComposite`, `ProductLeaf` | Treats individual products and nested product categories uniformly to calculate total catalog valuation and recursive tree traversal. |
| **Decorator** | `GiftWrapDecorator`, `ExpressDeliveryDecorator`, `WarrantyExtensionDecorator` | Dynamically augments purchased orders with add-on services without altering base product or order classes. |
| **Facade** | `RetailCheckoutFacade` | Simplifies the complex checkout workflow (inventory reservation, discount computation, payment execution, invoice generation, notification) behind one clean API method. |
| **Flyweight** | `ProductMetadataFlyweightFactory`, `SharedProductSpecs` | Shares immutable intrinsic metadata (brand specifications, regulatory compliance, origin) across thousands of inventory items to conserve RAM. |
| **Proxy** | `ProductSecurityProxy`, `AuditLoggingProxy` | Enforces role-based security checks and audit logging before delegating sensitive catalog modification actions to the real service. |

### 3.3 Behavioral Design Patterns (11 / 11)
| Pattern | Concrete Implementation Class | Retail Problem Solved |
| :--- | :--- | :--- |
| **Chain of Responsibility** | `OrderValidationHandler`, `StockValidationHandler`, `CreditLimitHandler`, `FraudCheckHandler` | Sequentially validates checkout requests through independent verification filters; halts execution on failure. |
| **Command** | `AddToCartCommand`, `RemoveFromCartCommand`, `ClearCartCommand`, `CartCommandInvoker` | Encapsulates shopping cart actions as command objects, enabling transaction logs and undo operations. |
| **Iterator** | `ProductCatalogIterator`, `InStockProductIterator`, `CategoryFilterIterator` | Provides sequential access to product aggregates while encapsulating internal storage details. |
| **Mediator** | `RetailOrderMediator` | Coordinates communication between decoupled subsystems (Inventory, Order Processing, Notifications, Billing) during purchase workflows. |
| **Memento** | `CartMemento`, `CartCaretaker` | Captures and externalizes the shopping cart's internal state, enabling instant **[ UNDO ]** restoration after clearing. |
| **Observer** | `InventorySubject`, `AdminAlertObserver`, `CustomerNotificationObserver` | Automatically broadcasts real-time alerts when stock levels drop below safety thresholds or order statuses change. |
| **State** | `OrderState`, `PendingState`, `PaidState`, `ProcessingState`, `ShippedState`, `DeliveredState`, `CancelledState` | Manages valid lifecycle transitions of customer orders, altering system behavior dynamically as state evolves. |
| **Strategy** | `DiscountStrategy`, `PercentageDiscountStrategy`, `FlatDiscountStrategy`, `TieredLoyaltyDiscountStrategy` | Encapsulates interchangeable pricing and discount algorithms applied at checkout based on user tier and promo codes. |
| **Template Method** | `OrderProcessingTemplate`, `PhysicalOrderProcessor`, `DigitalOrderProcessor` | Defines the rigid skeleton of order fulfillment while deferring specific steps (dispatching shipping courier vs. email download link) to subclasses. |
| **Visitor** | `CatalogVisitor`, `TaxCalculationVisitor`, `InventoryAuditVisitor` | Adds new operations (GST audit, physical space estimation) to product elements without modifying their class definitions. |

### 3.4 Modern Application Design Patterns (6 / 6)
| Pattern | Implementation Layer | Concrete Realization |
| :--- | :--- | :--- |
| **MVC** | Backend REST Architecture | Model (`Product`, `Order`), View (JSON DTOs / React Views), Controller (`ProductController`, `OrderController`). |
| **MVP** | Admin Discount Management | `DiscountPresenter` completely decouples pricing domain logic from the display view contract. |
| **MVI** | Cart State Flow | Unidirectional data flow: Intent (`ADD_ITEM`, `CLEAR_CART`) -> Reducer/Model -> Immutable View State. |
| **MVVM** | Frontend React Hooks | View (`ProductCatalog.jsx`), ViewModel (`useProductViewModel` / React state hooks), Model (`productAPI`). |
| **VIPER** | Inventory Stock Module | View, Interactor (`InventoryInteractor`), Presenter, Entity, and Router for modular inventory auditing. |
| **Microservice** | Bounded Context Architecture | Decoupled domain contexts: Catalog Service, Cart Service, Checkout Service, Inventory Service, and Notification Service. |

---

## 4. Default Demonstration Accounts

The database is pre-seeded with realistic retail records, categories, and credentials:

| Role | Email | Password | Pre-seeded Permissions & Features |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@smartretail.com` | `admin123` | Full access to Admin Portal, Inventory Adjustments, Observer Alerts, Product Cloning, GST Audits. |
| **Customer (Gold)** | `john@example.com` | `customer123` | Gold Member (10% automatic loyalty discount), Cart management with Memento Undo, Order history. |
| **Customer (Regular)** | `jane@example.com` | `customer123` | Regular Member (standard pricing), Promo code testing (`SAVE10`, `FESTIVAL20`, `WELCOME50`). |

> **Pro Tip:** In the frontend header, use the **[ ⇄ Switch to Admin / Shop as Customer ]** button for instant one-click switching between customer shopping and admin controls!

---

## 5. Quick Start Guide

### Prerequisites
- **Java 21** or later (`java -version`)
- **Maven 3.8+** (`mvn -version`)
- **Node.js 18+** (`node -v` and `npm -v`)

### 5.1 Backend Launch
1. Open a terminal in the `backend/` directory:
   ```bash
   cd backend
   ```
2. Run automated tests to verify all 23 GoF patterns:
   ```bash
   mvn test
   ```
3. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
   *The server starts at `http://localhost:8080` with embedded H2 database pre-seeded with 22 items.*
   *To use MySQL, update `backend/src/main/resources/application.properties` with your MySQL database credentials.*

### 5.2 Frontend Launch
1. Open a second terminal in the `frontend/` directory:
   ```bash
   cd frontend
   ```
2. Run the production build or dev server:
   ```bash
   npm run build
   npm run dev
   ```
3. Open your browser and navigate to:
   ```
   http://localhost:3000
   ```

---

## 6. Interactive 15-Step Demonstration Script

Follow this step-by-step flow to verify every architectural capability:

1. **Explore Store Catalog (`/products`):**
   - Browse 22 realistic products across 6 categories (Dairy, Bakery, Beverages, Electronics, Produce, Snacks).
   - Test category filtering and stock status indicators (**Iterator Pattern**).
2. **Inspect Composite Catalog Valuation (`/categories`):**
   - Click **"Inspect Composite Hierarchy"** to view recursive valuation printout and tree traversal (**Composite Pattern**).
3. **Cart Operations & Memento Undo (`/cart`):**
   - Add items to the cart (**Command Pattern**).
   - Click **"Clear Cart"**; observe the instant notification with the **[ UNDO ]** action button.
   - Click **[ UNDO ]**; observe the cart items immediately restored (**Memento Pattern**).
4. **Apply Strategy Discounts:**
   - Enter coupon code `SAVE10` or `FESTIVAL20` in the cart summary (**Strategy Pattern**).
   - Observe automatic recalculation of subtotal, loyalty discount, and GST.
5. **Multi-Step Checkout (`/checkout`):**
   - Fill delivery address and select payment method (Credit Card, UPI QR, Cash on Delivery).
   - Click **"Confirm & Place Order"** (**Facade & Abstract Factory Patterns**).
6. **Printable Thermal Invoice:**
   - On order completion, view the formatted invoice receipt (**Builder & Bridge Patterns**).
   - Click **"Print Thermal Receipt"** to verify `@media print` formatting.
7. **Switch to Admin Portal:**
   - Click the role switcher in the navbar to switch to `Admin`.
8. **View Real-Time Dashboard (`/admin/dashboard`):**
   - Review total revenue, active orders, product counts, and low-stock telemetry.
9. **Clone Product (`/admin/products`):**
   - Click **"Clone"** on any product; modify brand or price; save the clone (**Prototype Pattern**).
10. **Create New Product with Factory (`/admin/products`):**
    - Click **"+ Create New Product"**; select type `FOOD`, `ELECTRONICS`, or `GROCERY` (**Factory Method Pattern**).
11. **Run Visitor Tax & Inventory Audit (`/admin/products`):**
    - Click **"Audit"** on any product; observe GST breakdown and shelf metrics (**Visitor Pattern**).
12. **Inventory Stock Lock & Observer Alert (`/admin/inventory`):**
    - Decrease stock of any item below 5 units (**Singleton Ledger**).
    - Notice real-time notification badge incrementing in the navbar.
13. **Observer Notification Center (`/admin/notifications`):**
    - Inspect the low-stock alert triggered automatically (**Observer Pattern**).
14. **Advance Order State Machine (`/admin/orders`):**
    - Advance customer orders through `PAID` -> `PROCESSING` -> `SHIPPED` -> `COMPLETED` (**State Pattern**).
15. **Interactive Pattern Explorer Modal:**
    - Click **"Patterns (29)"** in the top navigation at any time to inspect design pattern details, class mappings, and live status.

---

## 7. Project Directory Structure

```
smart-retail-management-system/
├── backend/
│   ├── pom.xml                               # Maven Project Descriptor (Java 21, Spring Boot 3.3.3)
│   └── src/
│       ├── main/java/com/smartretail/
│       │   ├── SmartRetailApplication.java   # Spring Boot Main Entry Point
│       │   ├── applicationpatterns/          # 6 Application Architecture Patterns
│       │   │   ├── microservices/            # Bounded Context Services
│       │   │   ├── mvc/                      # Spring Web MVC
│       │   │   ├── mvi/                      # MVI Cart State & Intents
│       │   │   ├── mvp/                      # MVP Presenter & View Interface
│       │   │   ├── mvvm/                     # MVVM ViewModel Pattern
│       │   │   └── viper/                    # VIPER Module for Inventory
│       │   ├── designpatterns/               # 23 GoF Design Patterns
│       │   │   ├── behavioral/               # Chain, Command, Iterator, Mediator, Memento,
│       │   │   │                             # Observer, State, Strategy, Template, Visitor
│       │   │   ├── creational/               # Factory, Abstract Factory, Builder, Prototype, Singleton
│       │   │   └── structural/               # Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy
│       │   ├── controller/                   # 12 REST API Controllers
│       │   ├── entity/                       # JPA Database Entities
│       │   ├── repository/                   # Spring Data JPA Repositories
│       │   ├── service/                      # Core Business Logic Services
│       │   └── config/                       # Security, JWT, Web, and Seed Data Initializer
│       └── test/java/com/smartretail/        # Automated Unit & Pattern Verification Tests
├── frontend/
│   ├── package.json                          # Node dependencies (React 18, Vite 5, Lucide)
│   ├── vite.config.js                        # Proxy and server configuration
│   ├── index.html                            # HTML5 Shell
│   └── src/
│       ├── App.jsx                           # Application Router & Modals
│       ├── main.jsx                          # React 18 DOM Root
│       ├── index.css                         # Custom Retail Theme & Responsive CSS
│       ├── context/AuthContext.jsx           # Auth State & 1-Click Role Switcher
│       ├── components/                       # Navbar, Sidebar, ProductCard, Modals, Toast
│       ├── pages/                            # Customer Pages (Catalog, Cart, Checkout, Orders)
│       └── pages/admin/                      # Admin Pages (Dashboard, Products, Inventory, Orders, Discounts)
└── docs/
    ├── README.md                             # Project Overview & Quick Start
    ├── DESIGN_PATTERNS.md                    # In-depth GoF & Architectural Pattern Guide
    ├── ARCHITECTURE.md                       # Layered & Microservice Architecture Specs
    ├── API_DOCUMENTATION.md                  # Complete REST API Specifications
    └── UML_DIAGRAMS.md                       # Comprehensive Mermaid Diagrams Suite
```
