import React, { useState, useEffect } from 'react';
import { notificationAPI } from '../../services/api';
import { Link } from 'react-router-dom';
import {
  Bell,
  AlertTriangle,
  Package,
  ShoppingBag,
  CheckCheck,
  RefreshCw,
  ExternalLink,
  ShieldCheck,
  Clock
} from 'lucide-react';

export const AdminNotifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const res = await notificationAPI.getAdminAlerts();
      setNotifications(res.data);
    } catch (err) {
      console.error('Error fetching notifications:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (id) => {
    try {
      await notificationAPI.markAsRead(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, readStatus: true } : n))
      );
    } catch (err) {
      console.error(err);
    }
  };

  const filtered = notifications.filter((item) => {
    if (filter === 'UNREAD') return !item.readStatus;
    if (filter === 'LOW_STOCK') return item.type.includes('LOW_STOCK');
    if (filter === 'ORDER') return item.type.includes('ORDER');
    return true;
  });

  const unreadCount = notifications.filter((n) => !n.readStatus).length;

  return (
    <div className="admin-content">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '14px' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: '800', color: 'var(--secondary)' }}>
            System Alerts & Notifications
          </h1>
          <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>
            Real-time inventory alerts, order status updates, and system events.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '10px' }}>
          <button onClick={fetchNotifications} className="btn btn-secondary btn-sm" style={{ gap: '6px' }}>
            <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
            Refresh
          </button>
        </div>
      </div>

      {/* Filter Tabs */}
      <div style={{ display: 'flex', gap: '8px', marginBottom: '20px' }}>
        <button
          onClick={() => setFilter('ALL')}
          className={`btn btn-sm ${filter === 'ALL' ? 'btn-primary' : 'btn-secondary'}`}
        >
          All ({notifications.length})
        </button>
        <button
          onClick={() => setFilter('UNREAD')}
          className={`btn btn-sm ${filter === 'UNREAD' ? 'btn-primary' : 'btn-secondary'}`}
        >
          Unread ({unreadCount})
        </button>
        <button
          onClick={() => setFilter('LOW_STOCK')}
          className={`btn btn-sm ${filter === 'LOW_STOCK' ? 'btn-primary' : 'btn-secondary'}`}
        >
          Low Stock Alerts
        </button>
        <button
          onClick={() => setFilter('ORDER')}
          className={`btn btn-sm ${filter === 'ORDER' ? 'btn-primary' : 'btn-secondary'}`}
        >
          Order Events
        </button>
      </div>

      {/* Notification Stream */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
        {loading ? (
          <div className="card" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-muted)' }}>
            Loading notification stream...
          </div>
        ) : filtered.length === 0 ? (
          <div className="card" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-muted)' }}>
            No notifications found under this category.
          </div>
        ) : (
          filtered.map((item) => {
            const isLowStock = item.type.includes('LOW_STOCK');
            const isOrder = item.type.includes('ORDER');

            return (
              <div
                key={item.id}
                className="card"
                style={{
                  padding: '16px 20px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  gap: '16px',
                  backgroundColor: item.readStatus ? '#ffffff' : '#f8fafc',
                  borderLeft: isLowStock ? '4px solid var(--danger)' : isOrder ? '4px solid var(--primary)' : '4px solid #10b981',
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '14px', flex: 1 }}>
                  <div
                    style={{
                      width: '42px',
                      height: '42px',
                      borderRadius: '10px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      backgroundColor: isLowStock ? '#fee2e2' : isOrder ? 'var(--primary-light)' : '#d1fae5',
                      color: isLowStock ? 'var(--danger)' : isOrder ? 'var(--primary)' : '#059669',
                      flexShrink: 0,
                    }}
                  >
                    {isLowStock ? <AlertTriangle size={20} /> : isOrder ? <ShoppingBag size={20} /> : <Bell size={20} />}
                  </div>

                  <div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <span
                        style={{
                          fontSize: '0.72rem',
                          fontWeight: '800',
                          padding: '2px 8px',
                          borderRadius: '4px',
                          backgroundColor: isLowStock ? '#fee2e2' : 'var(--primary-light)',
                          color: isLowStock ? 'var(--danger)' : 'var(--primary)',
                        }}
                      >
                        {item.type}
                      </span>
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <Clock size={12} />
                        {item.createdAt ? new Date(item.createdAt).toLocaleString() : 'Just now'}
                      </span>
                      {!item.readStatus && (
                        <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: 'var(--primary)' }}></span>
                      )}
                    </div>

                    <div style={{ fontWeight: item.readStatus ? '500' : '700', fontSize: '0.92rem', marginTop: '4px', color: 'var(--secondary)' }}>
                      {item.message}
                    </div>
                  </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flexShrink: 0 }}>
                  {isLowStock && (
                    <Link to="/admin/inventory" className="btn btn-secondary btn-sm" style={{ gap: '4px', fontSize: '0.75rem' }}>
                      <span>Restock</span>
                      <ExternalLink size={12} />
                    </Link>
                  )}

                  {!item.readStatus && (
                    <button
                      onClick={() => handleMarkAsRead(item.id)}
                      className="btn btn-secondary btn-sm"
                      title="Mark as read"
                      style={{ padding: '6px' }}
                    >
                      <CheckCheck size={14} color="#059669" />
                    </button>
                  )}
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
