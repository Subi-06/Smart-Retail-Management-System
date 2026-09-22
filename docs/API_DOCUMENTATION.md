# REST API DOCUMENTATION
### Smart Retail Management System
**Department of Computer Science and Engineering | B.E. Degree Capstone**

---

## 1. Overview & Base Configuration

- **Base URL:** `http://localhost:8080/api`
- **Frontend Proxy:** Requests to `/api/*` on port `3000` are proxied to `http://localhost:8080/api/*`.
- **Authentication:** Bearer Token via `Authorization: Bearer <JWT_TOKEN>`.
- **Content Type:** `application/json` for all request/response payloads.

---

## 2. Authentication Endpoints (`/api/auth`)

### 2.1 User Login
- **Endpoint:** `POST /api/auth/login`
- **Access:** Public
- **Request Body:**
  ```json
  {
    "email": "admin@smartretail.com",
    "password": "admin123"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "name": "Store Administrator",
    "email": "admin@smartretail.com",
    "role": "ADMIN",
    "membershipType": "PLATINUM"
  }
  ```

### 2.2 User Registration
- **Endpoint:** `POST /api/auth/register`
- **Access:** Public
- **Request Body:**
  ```json
  {
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "password": "password123",
    "membershipType": "REGULAR"
  }
  ```
- **Response (201 Created):** User authentication payload with generated JWT.

### 2.3 Current User Profile
- **Endpoint:** `GET /api/auth/me`
- **Access:** Authenticated (User / Admin)
- **Response (200 OK):** Current logged-in user profile details.

---

## 3. Product Catalog Endpoints (`/api/products`)

### 3.1 Get All Products
- **Endpoint:** `GET /api/products`
- **Query Params:** `inStockOnly` (boolean, optional)
- **Pattern:** **Iterator Pattern** (`ProductIterator`, `InStockProductIterator`)
- **Response (200 OK):** Array of product objects.

### 3.2 Create Product via Factory
- **Endpoint:** `POST /api/products`
- **Access:** Admin
- **Pattern:** **Factory Method Pattern** (`FoodProductFactory`, `ElectronicsProductFactory`)
- **Request Body:**
  ```json
  {
    "name": "Organic Almond Milk",
    "price": 240.00,
    "categoryId": 1,
    "productType": "FOOD",
    "brand": "PureHealth",
    "unit": "1 Litre",
    "quantity": 50,
    "shelfLifeDays": 30,
    "requiresRefrigeration": true
  }
  ```
- **Response (201 Created):** Created product entity.

### 3.3 Clone Product via Prototype
- **Endpoint:** `POST /api/products/{id}/clone`
- **Access:** Admin
- **Pattern:** **Prototype Pattern** (`ProductPrototype`, `cloneProduct()`)
- **Request Body:**
  ```json
  {
    "name": "Organic Almond Milk 500ml",
    "price": 130.00,
    "sku": "FOOD-ALM-500ML"
  }
  ```
- **Response (201 Created):** Cloned product instance with deep-copied metadata.

### 3.4 Product Audit & Visitor
- **Endpoint:** `GET /api/products/{id}/audit`
- **Access:** Admin
- **Pattern:** **Visitor Pattern** (`TaxCalculationVisitor`, `InventoryAuditVisitor`)
- **Response (200 OK):**
  ```json
  {
    "productId": 1,
    "productName": "Organic Almond Milk",
    "gstRate": 0.05,
    "taxAmount": 12.00,
    "shelfLifeAudit": "Refrigerated perishable - 30 days safe",
    "estimatedStorageVolumeCm3": 1200
  }
  ```

### 3.5 Delete Product (Proxy Protected)
- **Endpoint:** `DELETE /api/products/{id}`
- **Access:** Admin Only
- **Pattern:** **Proxy Pattern** (`ProductSecurityProxy`)
- **Response (200 OK):** Empty response on success; 403 Forbidden if non-admin attempts deletion.

---

## 4. Category & Composite Endpoints (`/api/categories`)

### 4.1 Get All Categories
- **Endpoint:** `GET /api/categories`
- **Access:** Public

### 4.2 Composite Catalog Hierarchy & Valuation
- **Endpoint:** `GET /api/categories/composite-hierarchy`
- **Access:** Public / Admin
- **Pattern:** **Composite Pattern** (`CategoryComposite`, `ProductLeaf`)
- **Response (200 OK):**
  ```json
  {
    "catalogName": "SMART RETAIL CENTRAL CATALOG",
    "totalInventoryItems": 540,
    "totalStockValuation": 142850.00,
    "hierarchyText": "+ [ROOT] Store Catalog\n  + [CATEGORY] Dairy & Fresh\n    - Whole Milk 1L (Qty: 45, Price: 65.00)\n..."
  }
  ```

---

## 5. Shopping Cart Endpoints (`/api/cart`)

### 5.1 Get User Cart
- **Endpoint:** `GET /api/cart/{userId}`
- **Pattern:** **MVI Pattern** (Immutable `CartState` model)
- **Response (200 OK):** Cart items, quantities, subtotal, discount, and undoAvailable boolean.

### 5.2 Add Item to Cart
- **Endpoint:** `POST /api/cart/items`
- **Pattern:** **Command Pattern** (`AddToCartCommand`)
- **Request Body:**
  ```json
  {
    "userId": 2,
    "productId": 1,
    "quantity": 2
  }
  ```
- **Response (200 OK):** Updated cart state.

### 5.3 Clear Cart (With Memento Snapshot)
- **Endpoint:** `POST /api/cart/clear/{userId}`
- **Pattern:** **Command & Memento Patterns** (`ClearCartCommand`, `CartMemento`, `CartCaretaker`)
- **Response (200 OK):** Empty cart state with `undoAvailable: true`.

### 5.4 Undo Cart Clear
- **Endpoint:** `POST /api/cart/undo/{userId}`
- **Pattern:** **Memento Pattern** (Restores previous cart state from caretaker)
- **Response (200 OK):** Fully restored cart state with original items and quantities.

---

## 6. Checkout & Facade Endpoints (`/api/checkout`)

### 6.1 Process Checkout
- **Endpoint:** `POST /api/checkout`
- **Access:** Customer / Admin
- **Pattern:** **Facade Pattern** (`RetailCheckoutFacade`), **Chain of Responsibility** (Validation), **Abstract Factory** & **Adapter** (Payment)
- **Request Body:**
  ```json
  {
    "userId": 2,
    "shippingAddress": "124 Park Avenue, Suite 4B",
    "phoneNumber": "9876543210",
    "paymentMethod": "CARD",
    "couponCode": "SAVE10",
    "isGiftWrap": true,
    "isExpressDelivery": false
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "orderId": 101,
    "orderNumber": "ORD-68F9B2C1",
    "billNumber": "BILL-A87E1204",
    "totalAmount": 485.50,
    "discountAmount": 48.55,
    "taxAmount": 21.85,
    "paymentStatus": "PAID",
    "orderStatus": "PAID"
  }
  ```

---

## 7. Orders & State Pattern Endpoints (`/api/orders`)

### 7.1 Get Orders by User
- **Endpoint:** `GET /api/orders/user/{userId}`
- **Access:** Customer (own orders) / Admin

### 7.2 Transition Order Status
- **Endpoint:** `PUT /api/orders/{id}/status`
- **Access:** Admin
- **Pattern:** **State Pattern** (Advances lifecycle: `PENDING` -> `PAID` -> `PROCESSING` -> `SHIPPED` -> `COMPLETED`)
- **Response (200 OK):** Updated order with new status.

### 7.3 Cancel Order
- **Endpoint:** `POST /api/orders/{id}/cancel`
- **Access:** Customer / Admin
- **Pattern:** **State & Command Pattern** (Validates cancellation legality; restocks items)
- **Response (200 OK):** Updated order in `CANCELLED` status.

---

## 8. Billing & Receipt Endpoints (`/api/bills`)

### 8.1 Get Bill by Order ID
- **Endpoint:** `GET /api/bills/order/{orderId}`
- **Pattern:** **Builder Pattern** (`BillReceiptBuilder`), **Bridge Pattern** (`ThermalReceiptRenderer`)
- **Response (200 OK):** Formatted invoice details, item lines, GST breakdown, and thermal print string.

---

## 9. Inventory & Concurrency Endpoints (`/api/inventory`)

### 9.1 Get Inventory Status & VIPER Metrics
- **Endpoint:** `GET /api/inventory`
- **Pattern:** **VIPER Module** (`InventoryInteractor`, `InventoryPresenter`)

### 9.2 Adjust Stock Count
- **Endpoint:** `PUT /api/inventory/{productId}?delta={delta}`
- **Access:** Admin
- **Pattern:** **Singleton Pattern** (Thread-safe `InventoryLedgerManager` locking), **Observer Pattern** (Triggers low-stock alert when <= 5)
- **Response (200 OK):** Updated inventory item.

---

## 10. Discount Engine Endpoints (`/api/discounts`)

### 10.1 Get Active Discounts
- **Endpoint:** `GET /api/discounts`
- **Pattern:** **MVP Pattern** (`DiscountPresenter`)

### 10.2 Verify Coupon by Code
- **Endpoint:** `GET /api/discounts/code/{code}`
- **Pattern:** **Strategy Pattern** (`DiscountStrategy`, `PercentageDiscountStrategy`)

---

## 11. Observer Notification Endpoints (`/api/notifications`)

### 11.1 Get Admin Alerts
- **Endpoint:** `GET /api/notifications`
- **Access:** Admin
- **Pattern:** **Observer Pattern** (`AdminAlertObserver`)
- **Response (200 OK):** List of unread low-stock and order event notifications.

### 11.2 Mark Notification as Read
- **Endpoint:** `PUT /api/notifications/{id}/read`
- **Response (200 OK):** Empty response.

---

## 12. Design Pattern Catalog Endpoint (`/api/patterns`)

### 12.1 Get Design Patterns Matrix
- **Endpoint:** `GET /api/patterns/summary`
- **Access:** Public
- **Response (200 OK):** Complete array of all 29 design patterns with their GoF category, description, and concrete class locations.
