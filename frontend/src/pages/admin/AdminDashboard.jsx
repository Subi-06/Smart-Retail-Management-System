import React, { useState, useEffect } from 'react';
import { adminAPI } from '../../services/api';
import {
  Package,
  Users,
  ShoppingBag,
  IndianRupee,
  AlertTriangle,
  Clock,
  ArrowUpRight,
  TrendingUp,
  Receipt
} from 'lucide-react';
import { Link } from 'react-router-dom';

export const AdminDashboard = ({ onOpenBillModal }) => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminAPI.getDashboard()
      .then(res => setStats(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '60px' }}>Loading admin dashboard telemetry...</div>;
  }

  const kpis = [
    { label: 'Total Products', value: stats?.totalProducts || 0, icon: Package, color: '#4f46e5', bg: '#eef2ff' },
    { label: 'Total Customers', value: stats?.totalCustomers || 0, icon: Users, color: '#0ea5e9', bg: '#f0f9ff' },
    { label: "Today's Orders", value: stats?.todaysOrders || 0, icon: ShoppingBag, color: '#10b981', bg: '#ecfdf5' },
    { label: "Today's Sales", value: `₹${Number(stats?.todaysSales || 0).toFixed(2)}`, icon: IndianRupee, color: '#059669', bg: '#ecfdf5' },
    { label: 'Low Stock Items', value: stats?.lowStockCount || 0, icon: AlertTriangle, color: '#f59e0b', bg: '#fffbeb' },
    { label: 'Total Revenue', value: `₹${Number(stats?.totalSales || 0).toFixed(2)}`, icon: TrendingUp, color: '#8b5cf6', bg: '#f5f3ff' },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '28px' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', fontWeight: '800', color: 'var(--secondary)' }}>
            Admin Dashboard
          </h1>
          <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>
            Overview of store telemetry, active inventory locks, and customer sales.
          </p>
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '18px', marginBottom: '32px' }}>
        {kpis.map((kpi, idx) => {
          const Icon = kpi.icon;
          return (
            <div key={idx} className="card" style={{ padding: '20px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '12px' }}>
                <span style={{ fontSize: '0.8rem', fontWeight: '600', color: 'var(--text-muted)' }}>{kpi.label}</span>
                <div style={{ padding: '8px', backgroundColor: kpi.bg, color: kpi.color, borderRadius: '10px' }}>
                  <Icon size={18} />
                </div>
              </div>
              <div style={{ fontSize: '1.5rem', fontWeight: '800', color: 'var(--secondary)' }}>{kpi.value}</div>
            </div>
          );
        })}
      </div>

      {/* Low Stock Alerts */}
      {stats?.lowStockProducts && stats.lowStockProducts.length > 0 && (
        <div className="card" style={{ padding: '20px', marginBottom: '32px', borderLeft: '4px solid var(--warning)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '12px' }}>
            <AlertTriangle size={20} color="var(--warning)" />
            <h3 style={{ fontSize: '1.1rem', fontWeight: '800', color: '#92400e' }}>
              Low Stock Alerts
            </h3>
          </div>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '14px' }}>
            The following products have fallen below the 5-unit inventory threshold and alerted store management:
          </p>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '12px' }}>
            {stats.lowStockProducts.map(p => (
              <div key={p.id} style={{ padding: '12px', backgroundColor: '#fffbeb', borderRadius: '8px', border: '1px solid #fde68a' }}>
                <div style={{ fontWeight: '700', fontSize: '0.9rem' }}>{p.name}</div>
                <div style={{ fontSize: '0.8rem', color: '#92400e', marginTop: '2px' }}>
                  Stock remaining: <strong>{p.quantity} units</strong> ({p.categoryName})
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Recent Orders Section */}
      <div className="table-container">
        <div style={{ padding: '18px 24px', borderBottom: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: '800' }}>Recent Store Orders</h3>
          <Link to="/admin/orders" style={{ fontSize: '0.85rem', color: 'var(--primary)', fontWeight: '700', textDecoration: 'none' }}>
            View All Orders →
          </Link>
        </div>

        <table className="data-table">
          <thead>
            <tr>
              <th>Order ID</th>
              <th>Customer</th>
              <th>Date</th>
              <th>Payment</th>
              <th>Amount</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {stats?.recentOrders?.map(order => (
              <tr key={order.id}>
                <td><strong>#{order.id}</strong></td>
                <td>{order.customerName}</td>
                <td>{new Date(order.createdAt).toLocaleDateString()}</td>
                <td>{order.paymentMethod || 'UPI'}</td>
                <td style={{ fontWeight: '700' }}>₹{Number(order.finalAmount).toFixed(2)}</td>
                <td>
                  <span className={`badge ${order.status === 'COMPLETED' ? 'badge-green' : order.status === 'CANCELLED' ? 'badge-red' : 'badge-primary'}`}>
                    {order.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
