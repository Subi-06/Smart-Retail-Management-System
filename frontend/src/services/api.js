import axios from 'axios';

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

export const authAPI = {
  login: (credentials) => API.post('/auth/login', credentials),
  register: (data) => API.post('/auth/register', data),
  me: () => API.get('/auth/me'),
};

export const productAPI = {
  getAll: (inStockOnly = false) => API.get(`/products${inStockOnly ? '?inStockOnly=true' : ''}`),
  getById: (id) => API.get(`/products/${id}`),
  search: (query) => API.get(`/products/search?query=${encodeURIComponent(query)}`),
  getByCategory: (categoryId) => API.get(`/products/category/${categoryId}`),
  create: (data) => API.post('/products', data), // Factory Method
  clone: (id, overrides) => API.post(`/products/${id}/clone`, overrides), // Prototype
  update: (id, data) => API.put(`/products/${id}`, data),
  delete: (id) => API.delete(`/products/${id}`), // Proxy Protected
  updateStock: (id, quantity) => API.put(`/products/${id}/stock?quantity=${quantity}`),
  changePrice: (id, price) => API.put(`/products/${id}/price?price=${price}`),
  auditVisitor: (id) => API.get(`/products/${id}/audit`), // Visitor
  flyweightStats: () => API.get('/products/flyweight/stats'), // Flyweight
};

export const categoryAPI = {
  getAll: () => API.get('/categories'),
  getById: (id) => API.get(`/categories/${id}`),
  create: (data) => API.post('/categories', data),
  getCompositeHierarchy: () => API.get('/categories/composite-hierarchy'), // Composite
};

export const cartAPI = {
  get: (userId) => API.get(`/cart/${userId}`),
  addItem: (data) => API.post('/cart/items', data), // Command
  updateQuantity: (id, quantity, userId) => API.put(`/cart/items/${id}?quantity=${quantity}&userId=${userId}`), // Command
  removeItem: (id, userId) => API.delete(`/cart/items/${id}?userId=${userId}`), // Command
  clear: (userId) => API.post(`/cart/clear/${userId}`), // Command + Memento
  undo: (userId) => API.post(`/cart/undo/${userId}`), // Memento Restore
};

export const checkoutAPI = {
  checkout: (data) => API.post('/checkout', data), // Facade
};

export const orderAPI = {
  getAll: () => API.get('/orders'),
  getByUser: (userId) => API.get(`/orders/user/${userId}`),
  getById: (id) => API.get(`/orders/${id}`),
  transitionStatus: (id) => API.put(`/orders/${id}/status`), // State
  cancel: (id) => API.post(`/orders/${id}/cancel`), // State & Command
};

export const inventoryAPI = {
  getStatus: () => API.get('/inventory'),
  getLowStock: () => API.get('/inventory/low-stock'),
  adjustStock: (id, delta) => API.put(`/inventory/${id}?delta=${delta}`), // Singleton & Observer
  getMetrics: () => API.get('/inventory/metrics'),
};

export const billAPI = {
  getById: (id) => API.get(`/bills/${id}`),
  getByOrder: (orderId) => API.get(`/bills/order/${orderId}`),
  getByNumber: (billNumber) => API.get(`/bills/number/${billNumber}`),
};

export const discountAPI = {
  getAll: () => API.get('/discounts'),
  getByCode: (code) => API.get(`/discounts/code/${code}`),
  create: (data) => API.post('/discounts', data),
  toggleStatus: (id, active) => API.put(`/discounts/${id}/status?active=${active}`),
};

export const notificationAPI = {
  getAdminAlerts: () => API.get('/notifications'),
  getUserAlerts: (userId) => API.get(`/notifications/user/${userId}`),
  markAsRead: (id) => API.put(`/notifications/${id}/read`),
  markAllRead: (userId) => API.put(`/notifications/user/${userId}/read-all`),
};

export const adminAPI = {
  getDashboard: () => API.get('/admin/dashboard'),
};

export const patternAPI = {
  getSummary: () => API.get('/patterns/summary'),
};

export default API;
