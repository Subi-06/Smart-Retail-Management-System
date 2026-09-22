import React, { useState, useEffect } from 'react';
import { inventoryAPI } from '../../services/api';
import {
  Warehouse,
  Plus,
  Minus,
  AlertTriangle,
  CheckCircle,
  XCircle,
  RefreshCw,
  Sliders,
  ShieldCheck
} from 'lucide-react';

export const AdminInventory = () => {
  const [items, setItems] = useState([]);
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadInventory();
  }, []);

  const loadInventory = async () => {
    try {
      const [invRes, metricRes] = await Promise.all([
        inventoryAPI.getStatus(),
        inventoryAPI.getMetrics(),
      ]);
      setItems(invRes.data);
      setMetrics(metricRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAdjustStock = async (productId, delta) => {
    try {
      // SINGLETON PATTERN: Thread-safe stock adjustment with locks
      // OBSERVER PATTERN: LowStockObserver triggered if stock drops <= 5
      await inventoryAPI.adjustStock(productId, delta);
      loadInventory();
    } catch (err) {
      alert(err.response?.data?.message || 'Stock adjustment failed');
    }
  };

  const getStatusBadge = (quantity, threshold = 5) => {
    if (quantity <= 0) {
      return (
        <span className="badge badge-red">
          <XCircle size={12} /> Out of Stock
        </span>
      );
    }
    if (quantity <= threshold) {
      return (
        <span className="badge badge-yellow">
          <AlertTriangle size={12} /> Low Stock
        </span>
      );
    }
    return (
      <span className="badge badge-green">
        <CheckCircle size={12} /> In Stock
      </span>
    );
  };

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '60px' }}>Loading inventory telemetry...</div>;
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '14px' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', fontWeight: '800', color: 'var(--secondary)' }}>
            Inventory Management
          </h1>
          <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>
            Real-time warehouse stock tracking, replenishment, and reorder levels.
          </p>
        </div>

        <button onClick={loadInventory} className="btn btn-secondary btn-sm" style={{ gap: '6px' }}>
          <RefreshCw size={14} />
          Refresh Stock
        </button>
      </div>

      {/* Metrics Cards */}
      {metrics && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '18px', marginBottom: '28px' }}>
          <div className="card" style={{ padding: '18px' }}>
            <div style={{ fontSize: '0.78rem', fontWeight: '600', color: 'var(--text-muted)' }}>Total Stock Units</div>
            <div style={{ fontSize: '1.6rem', fontWeight: '800', color: 'var(--secondary)' }}>{metrics.totalStockUnits}</div>
          </div>
          <div className="card" style={{ padding: '18px' }}>
            <div style={{ fontSize: '0.78rem', fontWeight: '600', color: '#059669' }}>In Stock</div>
            <div style={{ fontSize: '1.6rem', fontWeight: '800', color: '#059669' }}>{metrics.inStockProductsCount}</div>
          </div>
          <div className="card" style={{ padding: '18px' }}>
            <div style={{ fontSize: '0.78rem', fontWeight: '600', color: '#d97706' }}>Low Stock (&le; 5)</div>
            <div style={{ fontSize: '1.6rem', fontWeight: '800', color: '#d97706' }}>{metrics.lowStockProductsCount}</div>
          </div>
          <div className="card" style={{ padding: '18px' }}>
            <div style={{ fontSize: '0.78rem', fontWeight: '600', color: '#dc2626' }}>Out of Stock</div>
            <div style={{ fontSize: '1.6rem', fontWeight: '800', color: '#dc2626' }}>{metrics.outOfStockProductsCount}</div>
          </div>
        </div>
      )}

      {/* Inventory Table */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Product</th>
              <th>Category</th>
              <th>Current Stock</th>
              <th>Min Threshold</th>
              <th>Status</th>
              <th style={{ textAlign: 'center' }}>Stock Adjustment</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                    <img
                      src={item.image || 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=100'}
                      alt={item.name}
                      style={{ width: '40px', height: '40px', borderRadius: '6px', objectFit: 'cover' }}
                    />
                    <div>
                      <div style={{ fontWeight: '700' }}>{item.name}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{item.brand} • {item.unit}</div>
                    </div>
                  </div>
                </td>
                <td>{item.categoryName}</td>
                <td style={{ fontWeight: '800', fontSize: '1.05rem' }}>{item.quantity}</td>
                <td style={{ color: 'var(--text-muted)' }}>{item.minStockThreshold || 5} units</td>
                <td>{getStatusBadge(item.quantity, item.minStockThreshold)}</td>
                <td style={{ textAlign: 'center' }}>
                  <div style={{ display: 'inline-flex', alignItems: 'center', gap: '8px' }}>
                    <button
                      onClick={() => handleAdjustStock(item.id, -1)}
                      disabled={item.quantity <= 0}
                      className="btn btn-secondary btn-sm"
                      title="Reduce stock by 1"
                      style={{ padding: '4px 10px' }}
                    >
                      <Minus size={14} />
                    </button>
                    <button
                      onClick={() => handleAdjustStock(item.id, +5)}
                      className="btn btn-primary btn-sm"
                      title="Restock +5 units"
                      style={{ padding: '4px 10px' }}
                    >
                      <Plus size={14} />
                      +5
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
