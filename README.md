# 🛒 SMART RETAIL MANAGEMENT SYSTEM
### An Enterprise-Grade Indian Supermarket & Inventory Management System Architected with GoF Design Patterns

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![Vite](https://img.shields.io/badge/Vite-5-purple.svg)](https://vitejs.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind-CSS-38bdf8.svg)](https://tailwindcss.com/)
[![Tests](https://img.shields.io/badge/Tests-16%2F16%20Passing-success.svg)]()
[![License](https://img.shields.io/badge/License-MIT-green.svg)]()

---

## 📌 Project Overview
The **Smart Retail Management System** is a full-stack, enterprise-grade online supermarket and inventory management web platform built using **Spring Boot (Java 21)** and **React + TypeScript (Tailwind CSS)**.

It provides a realistic shopping experience with authentic Indian brands (*Amul, Aashirvaad, Tata, Britannia, India Gate, Colgate*) in **Indian Rupees (₹)**, featuring dynamic cart management with **1-click Undo**, multi-tier coupon discounts, automated **GST tax calculation**, multi-gateway payment processing (**UPI, Card, Cash**), and live order lifecycle tracking.

The entire backend is architected with **all 23 Gang of Four (GoF) Design Patterns** and **6 Modern Application Architecture Patterns** (MVC, MVP, MVVM, MVI, VIPER, Microservices Bounded Contexts) in genuine, operational retail workflows.

---

## 🚀 Key Features

* **Authentic Indian Supermarket Catalog:** 48+ products across 6 departments (*Food & Bakery, Grocery & Staples, Beverages, Snacks, Electronics, Personal Care*).
* **Smart Cart with 1-Click Undo:** Add, modify, or remove items. If the cart is accidentally cleared, restore it instantly with 1-click **Undo** powered by Command & Memento patterns.
* **Pricing & Coupon Engine:** Layered percentage & flat discounts (`SAVE10`, `FESTIVAL20`) and Platinum/Gold membership tiers.
* **GST Taxation & Billing Engine:** Automatic 5% & 18% GST calculation with itemized, tamper-proof invoice receipt generation.
* **Unified Payment Gateways:** Seamless checkout supporting **UPI (Google Pay / PhonePe)**, **Credit/Debit Card**, and **Cash on Delivery**.
* **Live Order Lifecycle Tracking:** Real-time progression: `PLACED` ➔ `PAID` ➔ `PROCESSING` ➔ `SHIPPED` ➔ `DELIVERED`.
* **Admin Store Dashboard:** Role-based security gate, live stock management, low-stock alerts ($\le 5$ units), product cloning (Prototype), and automated GST & warehouse audit reports (Visitor).

---

## 🏛️ Design Patterns Implemented (23 GoF + 6 Architecture)

| Category | Patterns Implemented |
|---|---|
| **Creational (5)** | Factory Method, Abstract Factory, Builder, Prototype, Singleton |
| **Structural (7)** | Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy |
| **Behavioral (10)** | Chain of Responsibility, Command, Iterator, Mediator, Memento, Observer, State, Strategy, Template Method, Visitor |
| **Architecture (6)** | MVC, MVP, MVVM, MVI, VIPER, Microservices Bounded Contracts |

---

## ⚡ Quick Start Guide

### 1. Prerequisites
- **Java 21 JDK** installed
- **Apache Maven 3.8+**
- **Node.js 18+** & **npm**

### 2. Run the Backend (Spring Boot)
```bash
cd backend
mvn test              # Runs all 16 pattern verification unit tests (100% pass)
mvn spring-boot:run   # Starts backend on http://localhost:8080 (pre-seeded with H2 DB)
```

### 3. Run the Frontend (React + Vite)
```bash
cd frontend
npm install           # Installs dependencies
npm run dev           # Starts frontend on http://localhost:3000
```

---

## 🔑 Demo Login Credentials

| Role | Name | Email | Password | Perks |
|---|---|---|---|---|
| **Customer** | Subiksha | `subiksha@gmail.com` | `customer123` | Platinum Member (15% Off) + Promo Coupons |
| **Admin** | Store Administrator | `admin@smartretail.com` | `admin123` | Full Inventory, Stock Control & Auditing |

*(You can also use the **Switch Role** toggle in the top header for instant switching!)*

---

## 📁 Repository Structure

```
smart-retail-management-system/
├── backend/                  # Spring Boot (Java 21) REST API
│   ├── src/main/java/com/smartretail/
│   │   ├── applicationpatterns/  # MVP, MVVM, MVI, VIPER, Microservices
│   │   ├── designpatterns/       # 23 GoF Creational, Structural, Behavioral Patterns
│   │   ├── controller/           # REST API Controllers (MVC)
│   │   ├── entity/               # JPA Database Entities
│   │   └── service/              # Core Business Logic Services
│   └── pom.xml
├── frontend/                 # React 18 + Vite + Tailwind CSS SPA
│   ├── src/
│   │   ├── components/       # Reusable UI Components & Modals
│   │   ├── pages/            # Customer & Admin Dashboard Pages
│   │   └── services/         # Axios API Services
│   ├── vercel.json           # Vercel SPA Routing Configuration
│   └── package.json
└── docs/                     # Detailed Architectural & UML Documentation
```

---

## 📄 License
This project is licensed under the MIT License.
