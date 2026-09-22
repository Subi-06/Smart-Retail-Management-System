import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ToastProvider, useToast } from './components/Toast';
import { Navbar } from './components/Navbar';
import { Sidebar } from './components/Sidebar';
import { DesignPatternModal } from './components/DesignPatternModal';
import { BillReceiptModal } from './components/BillReceiptModal';

// Customer Pages
import { CustomerHome } from './pages/CustomerHome';
import { ProductCatalog } from './pages/ProductCatalog';
import { CategoriesPage } from './pages/CategoriesPage';
import { CartPage } from './pages/CartPage';
import { CheckoutPage } from './pages/CheckoutPage';
import { OrdersPage } from './pages/OrdersPage';

// Admin Pages
import { AdminDashboard } from './pages/admin/AdminDashboard';
import { AdminProducts } from './pages/admin/AdminProducts';
import { AdminInventory } from './pages/admin/AdminInventory';
import { AdminOrders } from './pages/admin/AdminOrders';
import { AdminDiscounts } from './pages/admin/AdminDiscounts';
import { AdminNotifications } from './pages/admin/AdminNotifications';

import { cartAPI } from './services/api';
import { Heart, ShoppingBag, ShieldCheck, Layers } from 'lucide-react';

const AppContent = () => {
  const { user, isAdmin } = useAuth();
  const { showToast } = useToast();
  const location = useLocation();

  // Modals state
  const [isPatternModalOpen, setIsPatternModalOpen] = useState(false);
  const [activeBillReceipt, setActiveBillReceipt] = useState(null);
  const [isBillModalOpen, setIsBillModalOpen] = useState(false);

  const isAdminRoute = location.pathname.startsWith('/admin');

  const handleOpenPatternModal = () => {
    setIsPatternModalOpen(true);
  };

  const handleOpenBillModal = (bill) => {
    setActiveBillReceipt(bill);
    setIsBillModalOpen(true);
  };

  const handleAddToCart = async (product, quantity = 1) => {
    const userId = user?.userId || 2; // Default to Customer Subiksha
    try {
      await cartAPI.addItem({
        userId,
        productId: product.id,
        quantity,
      });
      showToast(`${product.name} added to your cart!`);
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to add item to cart', false, null, 'error');
    }
  };

  return (
    <div className="app-container">
      {/* Global Navbar */}
      <Navbar onOpenPatternModal={handleOpenPatternModal} />

      {/* Admin Layout vs Customer Layout */}
      {isAdminRoute ? (
        <div className="admin-layout">
          <Sidebar onOpenPatternModal={handleOpenPatternModal} />
          <Routes>
            <Route path="/admin" element={<Navigate to="/admin/dashboard" replace />} />
            <Route path="/admin/dashboard" element={<AdminDashboard onOpenBillModal={handleOpenBillModal} />} />
            <Route path="/admin/products" element={<AdminProducts />} />
            <Route path="/admin/categories" element={<div className="admin-content"><CategoriesPage /></div>} />
            <Route path="/admin/inventory" element={<AdminInventory />} />
            <Route path="/admin/orders" element={<AdminOrders />} />
            <Route path="/admin/bills" element={<AdminOrders />} />
            <Route path="/admin/discounts" element={<AdminDiscounts />} />
            <Route path="/admin/notifications" element={<AdminNotifications />} />
            <Route path="*" element={<Navigate to="/admin/dashboard" replace />} />
          </Routes>
        </div>
      ) : (
        <>
          <main style={{ flex: 1 }}>
            <Routes>
              <Route path="/" element={<CustomerHome onAddToCart={handleAddToCart} />} />
              <Route path="/products" element={<ProductCatalog onAddToCart={handleAddToCart} />} />
              <Route path="/categories" element={<CategoriesPage />} />
              <Route path="/cart" element={<CartPage onTriggerToast={showToast} />} />
              <Route path="/checkout" element={<CheckoutPage onOpenBillModal={handleOpenBillModal} />} />
              <Route path="/orders" element={<OrdersPage onOpenBillModal={handleOpenBillModal} />} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>

          {/* Customer Store Footer */}
          <footer style={{
            backgroundColor: '#0f172a',
            color: '#94a3b8',
            padding: '40px 20px 24px 20px',
            borderTop: '1px solid #1e293b',
            marginTop: 'auto'
          }}>
            <div style={{ maxWidth: '1320px', margin: '0 auto', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '32px', marginBottom: '32px' }}>
              <div>
                <div style={{ color: '#ffffff', fontWeight: '800', fontSize: '1.2rem', marginBottom: '12px' }}>
                  SMART RETAIL
                </div>
                <p style={{ fontSize: '0.82rem', lineHeight: '1.6' }}>
                  Your trusted neighborhood supermarket for everyday groceries, dairy, pantry essentials, beverages, snacks, and household care.
                </p>
              </div>

              <div>
                <h4 style={{ color: '#ffffff', fontSize: '0.9rem', fontWeight: '700', marginBottom: '12px' }}>Store Highlights</h4>
                <ul style={{ listStyle: 'none', padding: 0, fontSize: '0.8rem', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  <li>✓ 100% Genuine Quality Products</li>
                  <li>✓ Instant UPI & Card Payments</li>
                  <li>✓ Same-Day Express Delivery</li>
                  <li>✓ Daily Fresh Stock Guaranteed</li>
                </ul>
              </div>

              <div>
                <h4 style={{ color: '#ffffff', fontSize: '0.9rem', fontWeight: '700', marginBottom: '12px' }}>Support & Hours</h4>
                <p style={{ fontSize: '0.82rem', lineHeight: '1.6' }}>
                  Store Timings: 7:00 AM – 10:30 PM (All Days)<br />
                  Customer Support: care@smartretail.com
                </p>
              </div>
            </div>

            <div style={{
              maxWidth: '1320px',
              margin: '0 auto',
              paddingTop: '20px',
              borderTop: '1px solid #1e293b',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              flexWrap: 'wrap',
              gap: '12px',
              fontSize: '0.78rem'
            }}>
              <div>
                © 2026 <strong>SMART RETAIL</strong>. All rights reserved.
              </div>
              <div style={{ display: 'flex', gap: '16px' }}>
                <span>Secure Checkout</span>
                <span>•</span>
                <span>Fast Delivery</span>
              </div>
            </div>
          </footer>
        </>
      )}

      {/* Global Design Patterns Explorer Modal */}
      <DesignPatternModal
        isOpen={isPatternModalOpen}
        onClose={() => setIsPatternModalOpen(false)}
      />

      {/* Global Printable Bill Receipt Modal */}
      {isBillModalOpen && activeBillReceipt && (
        <BillReceiptModal
          bill={activeBillReceipt}
          onClose={() => {
            setIsBillModalOpen(false);
            setActiveBillReceipt(null);
          }}
        />
      )}
    </div>
  );
};

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastProvider>
          <AppContent />
        </ToastProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}
