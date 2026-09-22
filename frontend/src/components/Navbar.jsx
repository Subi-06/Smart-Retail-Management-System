import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { cartAPI, notificationAPI } from '../services/api';
import {
  ShoppingBag,
  ShoppingCart,
  Search,
  Bell,
  User,
  ShieldCheck,
  Layers,
  LogOut,
  RefreshCw
} from 'lucide-react';

export const Navbar = ({ onOpenPatternModal }) => {
  const { user, isAdmin, logout, switchToAdmin, switchToCustomer } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [searchQuery, setSearchQuery] = useState('');
  const [cartCount, setCartCount] = useState(0);
  const [unreadAlertsCount, setUnreadAlertsCount] = useState(0);
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const [alerts, setAlerts] = useState([]);

  // Fetch Cart Items Count
  useEffect(() => {
    if (user?.userId) {
      cartAPI.get(user.userId)
        .then(res => {
          const count = res.data.items?.reduce((sum, item) => sum + item.quantity, 0) || 0;
          setCartCount(count);
        })
        .catch(() => setCartCount(0));
    }
  }, [user, location.pathname]);

  // Fetch Notifications
  useEffect(() => {
    if (isAdmin) {
      notificationAPI.getAdminAlerts()
        .then(res => {
          setAlerts(res.data);
          const unread = res.data.filter(a => !a.readStatus).length;
          setUnreadAlertsCount(unread);
        })
        .catch(() => {});
    } else if (user?.userId) {
      notificationAPI.getUserAlerts(user.userId)
        .then(res => {
          setAlerts(res.data);
          const unread = res.data.filter(a => !a.readStatus).length;
          setUnreadAlertsCount(unread);
        })
        .catch(() => {});
    }
  }, [user, isAdmin, location.pathname]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/products?search=${encodeURIComponent(searchQuery.trim())}`);
    }
  };

  const markAllRead = () => {
    if (user?.userId) {
      notificationAPI.markAllRead(user.userId).then(() => {
        setUnreadAlertsCount(0);
        setAlerts(prev => prev.map(a => ({ ...a, readStatus: true })));
      });
    }
  };

  return (
    <header className="navbar">
      <div className="navbar-inner">
        {/* Brand Logo */}
        <Link to="/" className="brand-logo">
          <span>SMART RETAIL</span>
        </Link>

        {/* Global Search Bar */}
        <form className="search-bar" onSubmit={handleSearch}>
          <Search size={17} className="search-icon" />
          <input
            type="text"
            className="search-input"
            placeholder="Search Amul Milk, Aashirvaad Atta, Tata Salt, KitKat..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </form>

        {/* Navigation Links */}
        <nav className="nav-links">
          <Link
            to="/products"
            className={`nav-link ${location.pathname === '/products' ? 'active' : ''}`}
          >
            Products
          </Link>

          <Link
            to="/categories"
            className={`nav-link ${location.pathname === '/categories' ? 'active' : ''}`}
          >
            Categories
          </Link>

          {!isAdmin && (
            <>
              <Link
                to="/cart"
                className={`nav-link ${location.pathname === '/cart' ? 'active' : ''}`}
                style={{ position: 'relative' }}
              >
                <ShoppingCart size={18} />
                Cart
                {cartCount > 0 && (
                  <span style={{
                    position: 'absolute',
                    top: '2px',
                    right: '-4px',
                    backgroundColor: 'var(--primary)',
                    color: '#fff',
                    borderRadius: '999px',
                    padding: '1px 6px',
                    fontSize: '0.72rem',
                    fontWeight: '800'
                  }}>
                    {cartCount}
                  </span>
                )}
              </Link>

              <Link
                to="/orders"
                className={`nav-link ${location.pathname === '/orders' ? 'active' : ''}`}
              >
                Orders
              </Link>
            </>
          )}

          {isAdmin && (
            <Link
              to="/admin/dashboard"
              className={`nav-link ${location.pathname.startsWith('/admin') ? 'active' : ''}`}
              style={{ color: 'var(--primary)', fontWeight: '700' }}
            >
              <ShieldCheck size={18} />
              Admin Portal
            </Link>
          )}

          {/* Notification Bell */}
          <div style={{ position: 'relative' }}>
            <button
              onClick={() => setNotificationsOpen(!notificationsOpen)}
              className="btn btn-secondary btn-sm"
              style={{ padding: '8px 10px', position: 'relative' }}
              title="Notifications"
            >
              <Bell size={18} />
              {unreadAlertsCount > 0 && (
                <span style={{
                  position: 'absolute',
                  top: '-4px',
                  right: '-4px',
                  backgroundColor: 'var(--danger)',
                  color: '#fff',
                  borderRadius: '999px',
                  padding: '1px 5px',
                  fontSize: '0.68rem',
                  fontWeight: '800'
                }}>
                  {unreadAlertsCount}
                </span>
              )}
            </button>

            {/* Notification Drawer Popover */}
            {notificationsOpen && (
              <div style={{
                position: 'absolute',
                top: '44px',
                right: '0',
                width: '360px',
                backgroundColor: '#ffffff',
                border: '1px solid var(--border-color)',
                borderRadius: 'var(--radius-lg)',
                boxShadow: 'var(--shadow-lg)',
                zIndex: 100,
                padding: '14px',
                maxHeight: '400px',
                overflowY: 'auto'
              }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px', borderBottom: '1px solid #f1f5f9', paddingBottom: '8px' }}>
                  <div style={{ fontWeight: '700', fontSize: '0.9rem' }}>Notifications</div>
                  <button onClick={markAllRead} style={{ fontSize: '0.75rem', color: 'var(--primary)', border: 'none', background: 'none', cursor: 'pointer', fontWeight: '600' }}>
                    Mark all read
                  </button>
                </div>

                {alerts.length === 0 ? (
                  <div style={{ padding: '20px 0', textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.85rem' }}>
                    No alerts at this time.
                  </div>
                ) : (
                  alerts.slice(0, 10).map((alert) => (
                    <div
                      key={alert.id}
                      style={{
                        padding: '10px',
                        marginBottom: '8px',
                        borderRadius: 'var(--radius-sm)',
                        backgroundColor: alert.readStatus ? '#f8fafc' : '#eff6ff',
                        borderLeft: alert.type.includes('LOW_STOCK') ? '4px solid var(--danger)' : '4px solid var(--primary)',
                        fontSize: '0.82rem'
                      }}
                    >
                      <div style={{ fontWeight: '700', fontSize: '0.72rem', color: alert.type.includes('LOW_STOCK') ? 'var(--danger)' : 'var(--primary)' }}>
                        {alert.type}
                      </div>
                      <div style={{ color: 'var(--text-main)', marginTop: '2px' }}>{alert.message}</div>
                    </div>
                  ))
                )}
              </div>
            )}
          </div>

          {/* User Profile & Role Switcher */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginLeft: '6px' }}>
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                padding: '6px 12px',
                backgroundColor: '#f8fafc',
                border: '1px solid var(--border-color)',
                borderRadius: 'var(--radius-md)',
                fontSize: '0.85rem'
              }}
            >
              <User size={15} color="var(--primary)" />
              <div style={{ display: 'flex', flexDirection: 'column' }}>
                <span style={{ fontWeight: '700', fontSize: '0.82rem' }}>{user?.name || 'Guest'}</span>
                <span style={{ fontSize: '0.68rem', color: 'var(--text-muted)' }}>
                  {isAdmin ? 'ADMIN' : `${user?.membershipType || 'REGULAR'} Member`}
                </span>
              </div>
            </div>

            {/* Instant Role Switcher Toggle */}
            <button
              onClick={isAdmin ? switchToCustomer : switchToAdmin}
              className="btn btn-secondary btn-sm"
              style={{ fontSize: '0.75rem', padding: '6px 10px', gap: '4px' }}
              title={isAdmin ? "Switch to Customer role for shopping demo" : "Switch to Admin role for dashboard demo"}
            >
              <RefreshCw size={13} />
              {isAdmin ? "Shop as Customer" : "Admin Panel"}
            </button>
          </div>
        </nav>
      </div>
    </header>
  );
};
