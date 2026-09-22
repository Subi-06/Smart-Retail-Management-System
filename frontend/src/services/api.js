import axios from 'axios';
import { DEMO_CATEGORIES, DEMO_PRODUCTS, DEMO_DISCOUNTS } from './demoData';

const API_BASE = import.meta.env.VITE_API_BASE_URL
  ? `${import.meta.env.VITE_API_BASE_URL.replace(/\/$/, '')}/api`
  : '/api';

const API = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Attach JWT token to all outbound requests
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('smart_retail_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => Promise.reject(error));

// Interceptor to reject HTML responses (which occur on Vercel SPA rewrites when backend is offline)
API.interceptors.response.use(
  (response) => {
    if (typeof response.data === 'string' && response.data.trim().startsWith('<')) {
      return Promise.reject(new Error('HTML_RESPONSE_OFFLINE'));
    }
    return response;
  },
  (error) => Promise.reject(error)
);

// LocalStorage helpers for seamless offline/standalone demo mode
const getLocalCart = (userId = 2) => {
  const saved = localStorage.getItem(`smart_retail_cart_${userId}`);
  if (saved) {
    try { return JSON.parse(saved); } catch (e) { /* ignore */ }
  }
  return { id: 1, userId, items: [], subtotal: 0 };
};

const saveLocalCart = (userId, cart) => {
  localStorage.setItem(`smart_retail_cart_${userId}`, JSON.stringify(cart));
  return cart;
};

export const authAPI = {
  login: (credentials) => API.post('/auth/login', credentials).catch(() => ({
    data: {
      userId: credentials.email.includes('admin') ? 1 : 2,
      name: credentials.email.includes('admin') ? 'Store Administrator' : 'Subiksha',
      email: credentials.email,
      role: credentials.email.includes('admin') ? 'ROLE_ADMIN' : 'ROLE_CUSTOMER',
      token: 'demo-jwt-token',
      membershipType: 'PLATINUM'
    }
  })),
  register: (data) => API.post('/auth/register', data).catch(() => ({
    data: { ...data, userId: 3, role: 'ROLE_CUSTOMER', token: 'demo-jwt-token', membershipType: 'REGULAR' }
  })),
  me: () => API.get('/auth/me'),
};

export const productAPI = {
  getAll: (inStockOnly = false) => API.get(`/products${inStockOnly ? '?inStockOnly=true' : ''}`)
    .then(res => ({ data: Array.isArray(res.data) ? res.data : DEMO_PRODUCTS }))
    .catch(() => ({ data: DEMO_PRODUCTS })),

  getById: (id) => API.get(`/products/${id}`)
    .catch(() => ({ data: DEMO_PRODUCTS.find(p => p.id === Number(id)) || DEMO_PRODUCTS[0] })),

  search: (query) => API.get(`/products/search?query=${encodeURIComponent(query)}`)
    .catch(() => ({
      data: DEMO_PRODUCTS.filter(p =>
        p.name.toLowerCase().includes(query.toLowerCase()) ||
        p.brand?.toLowerCase().includes(query.toLowerCase()) ||
        p.description?.toLowerCase().includes(query.toLowerCase())
      )
    })),

  getByCategory: (categoryId) => API.get(`/products/category/${categoryId}`)
    .catch(() => ({
      data: DEMO_PRODUCTS.filter(p => p.category?.id === Number(categoryId))
    })),

  create: (data) => API.post('/products', data).catch(() => ({ data: { ...data, id: Date.now() } })),
  clone: (id, overrides) => API.post(`/products/${id}/clone`, overrides).catch(() => {
    const src = DEMO_PRODUCTS.find(p => p.id === Number(id)) || DEMO_PRODUCTS[0];
    return { data: { ...src, ...overrides, name: `${src.name} (Copy)`, id: Date.now() } };
  }),
  update: (id, data) => API.put(`/products/${id}`, data).catch(() => ({ data: { ...data, id } })),
  delete: (id) => API.delete(`/products/${id}`).catch(() => ({ data: { success: true } })),
  updateStock: (id, quantity) => API.put(`/products/${id}/stock?quantity=${quantity}`).catch(() => ({ data: { id, quantity } })),
  changePrice: (id, price) => API.put(`/products/${id}/price?price=${price}`).catch(() => ({ data: { id, price } })),
  auditVisitor: (id) => API.get(`/products/${id}/audit`).catch(() => ({
    data: { taxRate: 5.0, gstAmount: 15.0, passedAudit: true, message: "Standard GST Compliance Audit Passed" }
  })),
  flyweightStats: () => API.get('/products/flyweight/stats').catch(() => ({
    data: { distinctCategories: 6, distinctBrands: 24, totalProducts: 48, memoryOptimizationRatio: "76% Saved" }
  })),
};

export const categoryAPI = {
  getAll: () => API.get('/categories')
    .then(res => ({ data: Array.isArray(res.data) ? res.data : DEMO_CATEGORIES }))
    .catch(() => ({ data: DEMO_CATEGORIES })),

  getById: (id) => API.get(`/categories/${id}`)
    .catch(() => ({ data: DEMO_CATEGORIES.find(c => c.id === Number(id)) || DEMO_CATEGORIES[0] })),

  create: (data) => API.post('/categories', data).catch(() => ({ data: { ...data, id: Date.now() } })),
  getCompositeHierarchy: () => API.get('/categories/composite-hierarchy')
    .catch(() => ({ data: DEMO_CATEGORIES.map(c => ({ ...c, subcategories: [] })) })),
};

export const cartAPI = {
  get: (userId) => API.get(`/cart/${userId}`)
    .then(res => ({ data: res.data && typeof res.data === 'object' && Array.isArray(res.data.items) ? res.data : getLocalCart(userId) }))
    .catch(() => ({ data: getLocalCart(userId) })),

  addItem: (data) => API.post('/cart/items', data).catch(() => {
    const cart = getLocalCart(data.userId || 2);
    const prod = DEMO_PRODUCTS.find(p => p.id === Number(data.productId)) || DEMO_PRODUCTS[0];
    const existing = cart.items.find(i => i.product?.id === prod.id);
    if (existing) {
      existing.quantity += (data.quantity || 1);
      existing.subtotal = existing.quantity * existing.price;
    } else {
      cart.items.push({
        id: Date.now(),
        product: prod,
        quantity: data.quantity || 1,
        price: prod.price,
        subtotal: (data.quantity || 1) * prod.price
      });
    }
    cart.subtotal = cart.items.reduce((s, i) => s + i.subtotal, 0);
    return { data: saveLocalCart(data.userId || 2, cart) };
  }),

  updateQuantity: (id, quantity, userId) => API.put(`/cart/items/${id}?quantity=${quantity}&userId=${userId}`).catch(() => {
    const cart = getLocalCart(userId || 2);
    const item = cart.items.find(i => i.id === Number(id));
    if (item) {
      item.quantity = Math.max(1, quantity);
      item.subtotal = item.quantity * item.price;
    }
    cart.subtotal = cart.items.reduce((s, i) => s + i.subtotal, 0);
    return { data: saveLocalCart(userId || 2, cart) };
  }),

  removeItem: (id, userId) => API.delete(`/cart/items/${id}?userId=${userId}`).catch(() => {
    const cart = getLocalCart(userId || 2);
    cart.items = cart.items.filter(i => i.id !== Number(id));
    cart.subtotal = cart.items.reduce((s, i) => s + i.subtotal, 0);
    return { data: saveLocalCart(userId || 2, cart) };
  }),

  clear: (userId) => API.post(`/cart/clear/${userId}`).catch(() => {
    const cart = getLocalCart(userId || 2);
    localStorage.setItem(`smart_retail_cart_memento_${userId}`, JSON.stringify(cart));
    const emptyCart = { id: 1, userId, items: [], subtotal: 0 };
    return { data: saveLocalCart(userId || 2, emptyCart) };
  }),

  undo: (userId) => API.post(`/cart/undo/${userId}`).catch(() => {
    const saved = localStorage.getItem(`smart_retail_cart_memento_${userId}`);
    if (saved) {
      const restored = JSON.parse(saved);
      return { data: saveLocalCart(userId || 2, restored) };
    }
    return { data: getLocalCart(userId || 2) };
  }),
};

export const checkoutAPI = {
  checkout: (data) => API.post('/checkout', data).catch(() => {
    const subtotal = data.items?.reduce((s, i) => s + (i.price * i.quantity), 0) || 500;
    const discount = data.couponCode === 'FESTIVAL20' ? subtotal * 0.2 : (data.couponCode === 'SAVE10' ? subtotal * 0.1 : 0);
    const tax = (subtotal - discount) * 0.05;
    const total = subtotal - discount + tax;
    const billNumber = `INV-${new Date().toISOString().slice(0,10).replace(/-/g,'')}-${Math.floor(1000 + Math.random()*9000)}`;

    const bill = {
      id: Date.now(),
      billNumber,
      customerName: "Subiksha",
      customerEmail: "subiksha@gmail.com",
      subtotal,
      discount,
      tax,
      total,
      paymentMethod: data.paymentMethod || "UPI",
      paymentStatus: "PAID",
      createdAt: new Date().toISOString(),
      order: {
        id: Math.floor(100 + Math.random()*900),
        status: "PAID",
        orderNumber: `ORD-${Date.now().toString().slice(-6)}`,
        items: data.items || []
      }
    };

    // Clear local cart
    saveLocalCart(data.userId || 2, { id: 1, userId: data.userId || 2, items: [], subtotal: 0 });

    // Store in demo orders
    const orders = JSON.parse(localStorage.getItem('smart_retail_demo_orders') || '[]');
    orders.unshift(bill.order);
    localStorage.setItem('smart_retail_demo_orders', JSON.stringify(orders));

    return { data: bill };
  }),
};

export const orderAPI = {
  getAll: () => API.get('/orders').catch(() => ({
    data: JSON.parse(localStorage.getItem('smart_retail_demo_orders') || '[]')
  })),
  getByUser: (userId) => API.get(`/orders/user/${userId}`).catch(() => ({
    data: JSON.parse(localStorage.getItem('smart_retail_demo_orders') || '[]')
  })),
  getById: (id) => API.get(`/orders/${id}`).catch(() => ({ data: { id, status: "PAID" } })),
  transitionStatus: (id) => API.put(`/orders/${id}/status`).catch(() => ({ data: { id, status: "PROCESSING" } })),
  cancel: (id) => API.post(`/orders/${id}/cancel`).catch(() => ({ data: { id, status: "CANCELLED" } })),
};

export const inventoryAPI = {
  getStatus: () => API.get('/inventory').catch(() => ({ data: DEMO_PRODUCTS })),
  getLowStock: () => API.get('/inventory/low-stock').catch(() => ({ data: DEMO_PRODUCTS.filter(p => p.quantity <= 5) })),
  adjustStock: (id, delta) => API.put(`/inventory/${id}?delta=${delta}`).catch(() => ({ data: { id, delta } })),
  getMetrics: () => API.get('/inventory/metrics').catch(() => ({
    data: { totalSkus: 48, lowStockCount: 2, totalValuation: "₹45,280.00" }
  })),
};

export const billAPI = {
  getById: (id) => API.get(`/bills/${id}`).catch(() => ({ data: {} })),
  getByOrder: (orderId) => API.get(`/bills/order/${orderId}`).catch(() => ({ data: {} })),
  getByNumber: (billNumber) => API.get(`/bills/number/${billNumber}`).catch(() => ({ data: {} })),
};

export const discountAPI = {
  getAll: () => API.get('/discounts').catch(() => ({ data: DEMO_DISCOUNTS })),
  getByCode: (code) => API.get(`/discounts/code/${code}`).catch(() => ({
    data: DEMO_DISCOUNTS.find(d => d.code.toUpperCase() === code.toUpperCase())
  })),
  create: (data) => API.post('/discounts', data).catch(() => ({ data: { ...data, id: Date.now() } })),
  toggleStatus: (id, active) => API.put(`/discounts/${id}/status?active=${active}`).catch(() => ({ data: { id, active } })),
};

export const notificationAPI = {
  getAdminAlerts: () => API.get('/notifications').catch(() => ({
    data: [
      { id: 1, title: "Low Stock Alert", message: "Fresh Bun has only 4 units remaining in inventory", readStatus: false, createdAt: new Date().toISOString() }
    ]
  })),
  getUserAlerts: (userId) => API.get(`/notifications/user/${userId}`).catch(() => ({
    data: [
      { id: 2, title: "Welcome to Smart Retail", message: "Welcome Subiksha! Use code FESTIVAL20 for 20% off on your first grocery order.", readStatus: false, createdAt: new Date().toISOString() }
    ]
  })),
  markAsRead: (id) => API.put(`/notifications/${id}/read`).catch(() => ({ data: { success: true } })),
  markAllRead: (userId) => API.put(`/notifications/user/${userId}/read-all`).catch(() => ({ data: { success: true } })),
};

export const adminAPI = {
  getDashboard: () => API.get('/admin/dashboard').catch(() => ({
    data: {
      totalRevenue: "₹1,24,500.00",
      totalOrders: 64,
      totalProducts: 48,
      activeDiscounts: 3,
      recentOrders: []
    }
  })),
};

export const patternAPI = {
  getSummary: () => API.get('/patterns/summary').catch(() => ({ data: [] })),
};

export default API;
