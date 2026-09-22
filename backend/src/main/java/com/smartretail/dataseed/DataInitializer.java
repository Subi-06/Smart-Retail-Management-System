package com.smartretail.dataseed;

import com.smartretail.entity.*;
import com.smartretail.entity.enums.DiscountType;
import com.smartretail.entity.enums.MembershipType;
import com.smartretail.entity.enums.ProductType;
import com.smartretail.entity.enums.Role;
import com.smartretail.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already seeded with initial data.");
            return;
        }

        log.info("Seeding SMART RETAIL database with sample users, categories, products, and discounts...");

        // 1. Seed Users (1 Admin, 2 Customers)
        User admin = User.builder()
                .name("Store Administrator")
                .email("admin@smartretail.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ROLE_ADMIN)
                .membershipType(MembershipType.PLATINUM)
                .phone("+91 9876543210")
                .address("HQ 101 Retail Tower, Bangalore")
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(admin);

        User customer1 = User.builder()
                .name("Subiksha")
                .email("subiksha@gmail.com")
                .password(passwordEncoder.encode("customer123"))
                .role(Role.ROLE_CUSTOMER)
                .membershipType(MembershipType.PLATINUM) // 15% membership discount!
                .phone("+91 9876543211")
                .address("42 Green Meadow Lane, Bangalore")
                .createdAt(LocalDateTime.now())
                .build();
        customer1 = userRepository.save(customer1);

        User customerLegacy = User.builder()
                .name("Subiksha")
                .email("john.doe@gmail.com") // fallback alias
                .password(passwordEncoder.encode("customer123"))
                .role(Role.ROLE_CUSTOMER)
                .membershipType(MembershipType.PLATINUM)
                .phone("+91 9876543211")
                .address("42 Green Meadow Lane, Bangalore")
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(customerLegacy);

        User customer2 = User.builder()
                .name("Jane Smith")
                .email("jane.smith@gmail.com")
                .password(passwordEncoder.encode("customer123"))
                .role(Role.ROLE_CUSTOMER)
                .membershipType(MembershipType.REGULAR)
                .phone("+91 9876543212")
                .address("15 Palm Avenue, Bangalore")
                .createdAt(LocalDateTime.now())
                .build();
        customer2 = userRepository.save(customer2);

        // Initialize empty Carts for customers
        cartRepository.save(Cart.builder().user(customer1).createdAt(LocalDateTime.now()).build());
        cartRepository.save(Cart.builder().user(customer2).createdAt(LocalDateTime.now()).build());

        // 2. Seed Categories (6 Major Retail Categories)
        Category catFood = categoryRepository.save(Category.builder().name("Food & Bakery").description("Fresh dairy, breads, and baked goods").icon("Utensils").build());
        Category catGrocery = categoryRepository.save(Category.builder().name("Grocery & Staples").description("Grains, flours, sugars, and everyday cooking essentials").icon("ShoppingBag").build());
        Category catBeverages = categoryRepository.save(Category.builder().name("Beverages").description("Fresh juices, coffees, premium teas, and soft drinks").icon("Coffee").build());
        Category catSnacks = categoryRepository.save(Category.builder().name("Snacks & Confectionery").description("Chocolates, biscuits, cookies, and savory crisps").icon("Cookie").build());
        Category catElectronics = categoryRepository.save(Category.builder().name("Electronics & Accessories").description("Chargers, cables, audio accessories, and batteries").icon("Zap").build());
        Category catPersonalCare = categoryRepository.save(Category.builder().name("Personal Care").description("Soaps, shampoos, oral care, and skin care").icon("Sparkles").build());

        // 3. Seed at least 22 Realistic Products across the 6 categories
        List<Product> products = List.of(
            // Food & Bakery — 8
            Product.builder()
                .name("Amul Milk 500ml")
                .description("Fresh pasteurized homogenized toned milk rich in calcium and essential vitamins")
                .price(new BigDecimal("30.00"))
                .quantity(20)
                .category(catFood)
                .brand("Amul")
                .image("https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.FOOD)
                .shelfLifeDays(3)
                .unit("500ml Pouch")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Britannia Bread")
                .description("Soft and nutritious daily sliced white sandwich bread baked fresh")
                .price(new BigDecimal("45.00"))
                .quantity(15)
                .category(catFood)
                .brand("Britannia")
                .image("https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.FOOD)
                .shelfLifeDays(5)
                .unit("400g Pack")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Amul Butter")
                .description("Pure, delicious salted dairy butter made from fresh churned cream")
                .price(new BigDecimal("58.00"))
                .quantity(18)
                .category(catFood)
                .brand("Amul")
                .image("https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.FOOD)
                .shelfLifeDays(180)
                .unit("100g")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Aashirvaad Atta 5kg")
                .description("100% pure whole wheat flour processed with traditional stone grinding")
                .price(new BigDecimal("235.00"))
                .quantity(25)
                .category(catFood)
                .brand("Aashirvaad")
                .image("https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.FOOD)
                .shelfLifeDays(180)
                .unit("5kg Bag")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Eggs – 6 Pack")
                .description("Farm fresh grade A high protein nutritious brown table eggs")
                .price(new BigDecimal("55.00"))
                .quantity(20)
                .category(catFood)
                .brand("Eggoz")
                .image("https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.FOOD)
                .shelfLifeDays(14)
                .unit("6 Pack")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Brown Bread")
                .description("Healthy high-fiber brown wheat sandwich bread with no artificial flavors")
                .price(new BigDecimal("50.00"))
                .quantity(12)
                .category(catFood)
                .brand("Britannia")
                .image("https://images.unsplash.com/photo-1549931319-a545dcf3bc73?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.FOOD)
                .shelfLifeDays(5)
                .unit("400g Pack")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Paneer 200g")
                .description("Fresh and soft malai cottage cheese cubes for delicious curries and gravies")
                .price(new BigDecimal("95.00"))
                .quantity(15)
                .category(catFood)
                .brand("Milky Mist")
                .image("https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.FOOD)
                .shelfLifeDays(30)
                .unit("200g Pack")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Fresh Bun")
                .description("Soft and sweet bakery burger buns perfect for breakfast and snacks")
                .price(new BigDecimal("25.00"))
                .quantity(4) // Low Stock to trigger Observer Pattern alert!
                .category(catFood)
                .brand("Modern")
                .image("https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.FOOD)
                .shelfLifeDays(4)
                .unit("Pack of 2")
                .minStockThreshold(5)
                .build(),

            // 🌾 Grocery & Staples — 8
            Product.builder()
                .name("India Gate Basmati Rice 5kg")
                .description("Aged aromatic premium long grain Basmati rice for biryani, pulao, and fried rice")
                .price(new BigDecimal("450.00"))
                .quantity(30)
                .category(catGrocery)
                .brand("India Gate")
                .image("https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.GROCERY)
                .shelfLifeDays(720)
                .unit("5kg Bag")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Aashirvaad Atta 5kg")
                .description("100% whole grain wheat flour with high dietary fibre and natural softness")
                .price(new BigDecimal("235.00"))
                .quantity(25)
                .category(catGrocery)
                .brand("Aashirvaad")
                .image("https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.GROCERY)
                .shelfLifeDays(180)
                .unit("5kg Bag")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Tata Salt 1kg")
                .description("India's trusted vacuum evaporated iodised salt for mental development and taste")
                .price(new BigDecimal("28.00"))
                .quantity(50)
                .category(catGrocery)
                .brand("Tata")
                .image("https://images.unsplash.com/photo-1518110925495-5fe2fda0442c?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.GROCERY)
                .shelfLifeDays(720)
                .unit("1kg Pouch")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Tata Sugar 1kg")
                .description("Sparkling white sulphur-free pure crystal refined cane sugar")
                .price(new BigDecimal("52.00"))
                .quantity(40)
                .category(catGrocery)
                .brand("Tata")
                .image("https://images.unsplash.com/photo-1622484212850-cab596d68b8e?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.GROCERY)
                .shelfLifeDays(720)
                .unit("1kg Pouch")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Toor Dal 1kg")
                .description("Nutritious unpolished yellow arhar dal rich in natural dietary protein")
                .price(new BigDecimal("165.00"))
                .quantity(25)
                .category(catGrocery)
                .brand("Tata Sampann")
                .image("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.GROCERY)
                .shelfLifeDays(365)
                .unit("1kg Pouch")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Fortune Sunflower Oil 1L")
                .description("Light and healthy refined sunflower cooking oil with Vitamin A and D")
                .price(new BigDecimal("145.00"))
                .quantity(30)
                .category(catGrocery)
                .brand("Fortune")
                .image("https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.GROCERY)
                .shelfLifeDays(270)
                .unit("1L Pouch")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Aashirvaad Rava 500g")
                .description("Roasted fine wheat sooji rava for crispy upma, halwa, and rava dosa")
                .price(new BigDecimal("42.00"))
                .quantity(20)
                .category(catGrocery)
                .brand("Aashirvaad")
                .image("https://images.unsplash.com/photo-1586444248902-2f64eddc13df?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.GROCERY)
                .shelfLifeDays(180)
                .unit("500g Pouch")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Green Gram 500g")
                .description("High-protein whole green moong dal for healthy sprouts and dals")
                .price(new BigDecimal("75.00"))
                .quantity(18)
                .category(catGrocery)
                .brand("Tata Sampann")
                .image("https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.GROCERY)
                .shelfLifeDays(365)
                .unit("500g Pouch")
                .minStockThreshold(5)
                .build(),

            // 🥤 Beverages — 8
            Product.builder()
                .name("Coca-Cola 750ml")
                .description("Refreshing effervescent cola beverage chilled to perfection")
                .price(new BigDecimal("45.00"))
                .quantity(40)
                .category(catBeverages)
                .brand("Coca-Cola")
                .image("https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.BEVERAGE)
                .shelfLifeDays(120)
                .unit("750ml Bottle")
                .minStockThreshold(10)
                .build(),
            Product.builder()
                .name("Pepsi 750ml")
                .description("Bold and zesty carbonated refreshing cola drink")
                .price(new BigDecimal("45.00"))
                .quantity(40)
                .category(catBeverages)
                .brand("Pepsi")
                .image("https://images.unsplash.com/photo-1553456558-aff63285bdd1?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.BEVERAGE)
                .shelfLifeDays(120)
                .unit("750ml Bottle")
                .minStockThreshold(10)
                .build(),
            Product.builder()
                .name("Frooti 1L")
                .description("Juicy mango fruit drink made from real King of Mango Totapuri pulp")
                .price(new BigDecimal("70.00"))
                .quantity(30)
                .category(catBeverages)
                .brand("Frooti")
                .image("https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.BEVERAGE)
                .shelfLifeDays(180)
                .unit("1 Litre Tetra")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Real Fruit Juice 1L")
                .description("Natural mixed fruit nectar packed with essential vitamin C and natural taste")
                .price(new BigDecimal("120.00"))
                .quantity(25)
                .category(catBeverages)
                .brand("Real")
                .image("https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.BEVERAGE)
                .shelfLifeDays(180)
                .unit("1 Litre Tetra")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Nescafé Classic 100g")
                .description("100% pure rich aroma instant coffee granules for the ultimate morning boost")
                .price(new BigDecimal("210.00"))
                .quantity(20)
                .category(catBeverages)
                .brand("Nescafé")
                .image("https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.BEVERAGE)
                .shelfLifeDays(540)
                .unit("100g Jar")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Tata Tea 250g")
                .description("Rich blend of premium garden teas offering robust color and brisk taste")
                .price(new BigDecimal("150.00"))
                .quantity(30)
                .category(catBeverages)
                .brand("Tata Tea")
                .image("https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.BEVERAGE)
                .shelfLifeDays(365)
                .unit("250g Pack")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Red Bull 250ml")
                .description("Vitalizes body and mind energy drink with taurine and B-group vitamins")
                .price(new BigDecimal("125.00"))
                .quantity(25)
                .category(catBeverages)
                .brand("Red Bull")
                .image("https://images.unsplash.com/photo-1527661591475-527312dd65f5?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.BEVERAGE)
                .shelfLifeDays(365)
                .unit("250ml Can")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Bisleri Water 1L")
                .description("Purified mineral water with essential minerals and 10-step quality check")
                .price(new BigDecimal("20.00"))
                .quantity(60)
                .category(catBeverages)
                .brand("Bisleri")
                .image("https://images.unsplash.com/photo-1548839140-29a749e1bc4e?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.BEVERAGE)
                .shelfLifeDays(180)
                .unit("1L Bottle")
                .minStockThreshold(10)
                .build(),

            // 🍫 Snacks & Confectionery — 8
            Product.builder()
                .name("Oreo Biscuits")
                .description("Crunchy dark cocoa chocolate cookies filled with smooth rich vanilla cream")
                .price(new BigDecimal("35.00"))
                .quantity(50)
                .category(catSnacks)
                .brand("Cadbury")
                .image("https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.SNACKS)
                .shelfLifeDays(180)
                .unit("120g Pack")
                .minStockThreshold(10)
                .build(),
            Product.builder()
                .name("Lays Classic Salted")
                .description("Thinly sliced premium quality golden potato chips sprinkled with sea salt")
                .price(new BigDecimal("20.00"))
                .quantity(45)
                .category(catSnacks)
                .brand("Lay's")
                .image("https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.SNACKS)
                .shelfLifeDays(90)
                .unit("50g Pouch")
                .minStockThreshold(10)
                .build(),
            Product.builder()
                .name("Dairy Milk Chocolate")
                .description("Creamy and smooth Cadbury milk chocolate bar with unforgettable taste")
                .price(new BigDecimal("40.00"))
                .quantity(40)
                .category(catSnacks)
                .brand("Cadbury")
                .image("https://images.unsplash.com/photo-1548907040-4baa42d10919?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.SNACKS)
                .shelfLifeDays(270)
                .unit("50g Bar")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("KitKat")
                .description("Crispy light wafer fingers covered in smooth delicious milk chocolate")
                .price(new BigDecimal("30.00"))
                .quantity(35)
                .category(catSnacks)
                .brand("Nestlé")
                .image("https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.SNACKS)
                .shelfLifeDays(270)
                .unit("38g 4-Finger")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("5 Star")
                .description("Chewy golden caramel and soft nougat covered with rich chocolate")
                .price(new BigDecimal("20.00"))
                .quantity(35)
                .category(catSnacks)
                .brand("Cadbury")
                .image("https://images.unsplash.com/photo-1511381939415-e44015466834?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.SNACKS)
                .shelfLifeDays(270)
                .unit("40g Bar")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Good Day Biscuits")
                .description("Butter rich crispy baked biscuits generously topped with real cashew nuts")
                .price(new BigDecimal("40.00"))
                .quantity(40)
                .category(catSnacks)
                .brand("Britannia")
                .image("https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.SNACKS)
                .shelfLifeDays(180)
                .unit("200g Pack")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Bingo Chips")
                .description("Tangy tomato flavored crispy potato chips for an exciting spicy bite")
                .price(new BigDecimal("20.00"))
                .quantity(45)
                .category(catSnacks)
                .brand("Bingo")
                .image("https://images.unsplash.com/photo-1527515637462-cff94eecc1ac?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.SNACKS)
                .shelfLifeDays(90)
                .unit("50g Pouch")
                .minStockThreshold(10)
                .build(),
            Product.builder()
                .name("Hide & Seek Biscuits")
                .description("Rich chocolate chip cookies embedded with pure melted dark chocolate chips")
                .price(new BigDecimal("35.00"))
                .quantity(35)
                .category(catSnacks)
                .brand("Parle")
                .image("https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.SNACKS)
                .shelfLifeDays(180)
                .unit("120g Pack")
                .minStockThreshold(5)
                .build(),

            // 🔌 Electronics & Accessories — 8
            Product.builder()
                .name("USB-C Charging Cable")
                .description("Tangle-free 1.2m durable fast charging and data transfer USB Type-C cable")
                .price(new BigDecimal("199.00"))
                .quantity(20)
                .category(catElectronics)
                .brand("Boat")
                .image("https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.ELECTRONICS)
                .warrantyMonths(24)
                .unit("1.2m Cable")
                .minStockThreshold(3)
                .build(),
            Product.builder()
                .name("20W Fast Charger")
                .description("Compact 20-Watt power delivery fast adapter for iPhones and Android devices")
                .price(new BigDecimal("499.00"))
                .quantity(15)
                .category(catElectronics)
                .brand("Portronics")
                .image("https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.ELECTRONICS)
                .warrantyMonths(12)
                .unit("1 Unit")
                .minStockThreshold(3)
                .build(),
            Product.builder()
                .name("Bluetooth Earphones")
                .description("Magnetic wireless neckband earphones with 20hr battery and bass boost")
                .price(new BigDecimal("899.00"))
                .quantity(12)
                .category(catElectronics)
                .brand("Boat")
                .image("https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.ELECTRONICS)
                .warrantyMonths(12)
                .unit("1 Set")
                .minStockThreshold(3)
                .build(),
            Product.builder()
                .name("Wired Earphones")
                .description("In-ear bass earphones with inline HD microphone and 3.5mm gold-plated jack")
                .price(new BigDecimal("399.00"))
                .quantity(18)
                .category(catElectronics)
                .brand("Realme")
                .image("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.ELECTRONICS)
                .warrantyMonths(6)
                .unit("1 Set 3.5mm")
                .minStockThreshold(3)
                .build(),
            Product.builder()
                .name("Power Bank 10,000mAh")
                .description("Dual USB output pocket power bank with 18W fast charge support")
                .price(new BigDecimal("1199.00"))
                .quantity(10)
                .category(catElectronics)
                .brand("Mi")
                .image("https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.ELECTRONICS)
                .warrantyMonths(12)
                .unit("1 Unit")
                .minStockThreshold(3)
                .build(),
            Product.builder()
                .name("USB Flash Drive 64GB")
                .description("Ultra-compact high-speed USB 3.0 flash drive for secure data storage")
                .price(new BigDecimal("449.00"))
                .quantity(25)
                .category(catElectronics)
                .brand("SanDisk")
                .image("https://images.unsplash.com/photo-1618410320928-25228d811631?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.ELECTRONICS)
                .warrantyMonths(60)
                .unit("1 Unit")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Wireless Mouse")
                .description("Ergonomic 2.4GHz wireless optical mouse with quiet click and long battery life")
                .price(new BigDecimal("649.00"))
                .quantity(14)
                .category(catElectronics)
                .brand("Logitech")
                .image("https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.ELECTRONICS)
                .warrantyMonths(12)
                .unit("1 Unit")
                .minStockThreshold(3)
                .build(),
            Product.builder()
                .name("AA Battery Pack")
                .description("Guaranteed leak-resistant alkaline power cells for TV remotes and clocks")
                .price(new BigDecimal("160.00"))
                .quantity(3) // Low Stock (3) to trigger Observer Pattern alert!
                .category(catElectronics)
                .brand("Duracell")
                .image("https://images.unsplash.com/photo-1619725002198-6a689b72f41d?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.ELECTRONICS)
                .warrantyMonths(60)
                .unit("Pack of 4")
                .minStockThreshold(5)
                .build(),

            // 🧴 Personal Care — 8
            Product.builder()
                .name("Dove Soap")
                .description("Moisturizing beauty bathing bar with 1/4th moisturizing cream for soft skin")
                .price(new BigDecimal("65.00"))
                .quantity(40)
                .category(catPersonalCare)
                .brand("Dove")
                .image("https://images.unsplash.com/photo-1607006314647-75e182390f70?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.PERSONAL_CARE)
                .shelfLifeDays(720)
                .unit("100g Bar")
                .minStockThreshold(10)
                .build(),
            Product.builder()
                .name("Lux Soap")
                .description("Fragrant beauty bar infused with rich rose water and almond oil")
                .price(new BigDecimal("45.00"))
                .quantity(40)
                .category(catPersonalCare)
                .brand("Lux")
                .image("https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.PERSONAL_CARE)
                .shelfLifeDays(720)
                .unit("100g Bar")
                .minStockThreshold(10)
                .build(),
            Product.builder()
                .name("Head & Shoulders Shampoo")
                .description("Anti-dandruff daily shampoo that leaves scalp refreshed and flake-free")
                .price(new BigDecimal("180.00"))
                .quantity(20)
                .category(catPersonalCare)
                .brand("Head & Shoulders")
                .image("https://images.unsplash.com/photo-1535585209827-a15fcdbc4c2d?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.PERSONAL_CARE)
                .shelfLifeDays(720)
                .unit("180ml Bottle")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Clinic Plus Shampoo")
                .description("Milk protein and multivitamin enriched formula for strong, resilient hair")
                .price(new BigDecimal("120.00"))
                .quantity(30)
                .category(catPersonalCare)
                .brand("Clinic Plus")
                .image("https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.PERSONAL_CARE)
                .shelfLifeDays(720)
                .unit("175ml Bottle")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Colgate Toothpaste")
                .description("Calcium boost formula that adds natural calcium to teeth, fighting cavities")
                .price(new BigDecimal("95.00"))
                .quantity(35)
                .category(catPersonalCare)
                .brand("Colgate")
                .image("https://images.unsplash.com/photo-1559599101-f09722fb4948?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.PERSONAL_CARE)
                .shelfLifeDays(720)
                .unit("150g Tube")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Closeup Toothpaste")
                .description("Red hot spicy gel toothpaste with antibacterial mouthwash formula for fresh breath")
                .price(new BigDecimal("85.00"))
                .quantity(30)
                .category(catPersonalCare)
                .brand("Closeup")
                .image("https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.PERSONAL_CARE)
                .shelfLifeDays(720)
                .unit("150g Tube")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Nivea Body Lotion")
                .description("Deep moisture nourishing body lotion enriched with natural almond oil")
                .price(new BigDecimal("240.00"))
                .quantity(15)
                .category(catPersonalCare)
                .brand("Nivea")
                .image("https://images.unsplash.com/photo-1556228720-195a672e8a03?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.PERSONAL_CARE)
                .shelfLifeDays(720)
                .unit("200ml Bottle")
                .minStockThreshold(5)
                .build(),
            Product.builder()
                .name("Himalaya Face Wash")
                .description("Purifying neem face wash that cleanses skin and prevents acne pimples")
                .price(new BigDecimal("130.00"))
                .quantity(25)
                .category(catPersonalCare)
                .brand("Himalaya")
                .image("https://images.unsplash.com/photo-1556228722-d0b5d012e8b2?w=500&auto=format&fit=crop&q=60")
                .productType(ProductType.PERSONAL_CARE)
                .shelfLifeDays(720)
                .unit("100ml Tube")
                .minStockThreshold(5)
                .build()
        );

        for (Product p : products) {
            p.updateStockStatus();
            productRepository.save(p);
        }

        // 4. Seed Discounts & Coupons
        discountRepository.save(Discount.builder()
                .name("Save 10% on Everything")
                .code("SAVE10")
                .type(DiscountType.COUPON)
                .value(new BigDecimal("10.00"))
                .isPercentage(true)
                .minSpend(new BigDecimal("200.00"))
                .active(true)
                .description("10% instant discount on orders above ₹200")
                .build());

        discountRepository.save(Discount.builder()
                .name("Grand Festival Mega Saver")
                .code("FESTIVAL20")
                .type(DiscountType.FESTIVAL)
                .value(new BigDecimal("20.00"))
                .isPercentage(true)
                .minSpend(new BigDecimal("500.00"))
                .active(true)
                .description("20% off festive special celebration discount")
                .build());

        discountRepository.save(Discount.builder()
                .name("Welcome Bonus ₹50 Off")
                .code("WELCOME50")
                .type(DiscountType.COUPON)
                .value(new BigDecimal("50.00"))
                .isPercentage(false)
                .minSpend(new BigDecimal("150.00"))
                .active(true)
                .description("Flat ₹50 rebate on your cart order")
                .build());

        // 5. Seed Initial Observer Notification
        notificationRepository.save(Notification.builder()
                .message("[SYSTEM ONLINE] Smart Retail Management System started successfully. All 23 GoF & 6 Application patterns active.")
                .type("SYSTEM_BOOT")
                .readStatus(false)
                .createdAt(LocalDateTime.now())
                .build());

        log.info("Database successfully seeded with {} products across {} categories!", products.size(), 6);
    }
}
