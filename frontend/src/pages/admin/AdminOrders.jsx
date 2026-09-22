import React, { useState, useEffect } from 'react';
import { orderAPI, billAPI } from '../../services/api';
import { BillReceiptModal } from '../../components/BillReceiptModal';
import {
  ShoppingBag,
  ArrowRightCircle,
  XCircle,
  Receipt,
  Search,
  Filter,
  RefreshCw,
  Clock,
  CheckCircle2,
  Truck,
  Package
} from 'lucide-react';

export const AdminOrders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedBill, setSelectedBill] = useState(null);
  const [isBillModalOpen, setIsBillModalOpen] = useState(false);
  const [actionLoading, setActionLoading] = useState(null);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const res = await orderAPI.getAll();
      setOrders(res.data);
    } catch (err) {
      console.error('Error fetching orders:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAdvanceStatus = async (orderId) => {
    try {
      setActionLoading(orderId);
      const res = await orderAPI.transitionStatus(orderId);
      setOrders((prev) => prev.map((o) => (o.id === orderId ? res.data : o)));
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to transition order status.');
    } finally {
      setActionLoading(null);
    }
  };

  const handleCancelOrder = async (orderId) => {
    if (!window.confirm('Are you sure you want to cancel this order? This will restock the inventory items.')) {
      return;
    }
    try {
      setActionLoading(orderId);
      const res = await orderAPI.cancel(orderId);
      setOrders((prev) => prev.map((o) => (o.id === orderId ? res.data : o)));
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to cancel order.');
    } finally {
      setActionLoading(null);
    }
  };

  const handleViewBill = async (orderId) => {
    try {
      const res = await billAPI.getByOrder(orderId);
      setSelectedBill(res.data);
      setIsBillModalOpen(true);
    } catch (err) {
      alert('Invoice receipt could not be retrieved for this order.');
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'PENDING':
        return <span className="badge badge-warning"><Clock size={12} /> Pending</span>;
      case 'PAID':
        return <span className="badge badge-primary"><CheckCircle2 size={12} /> Paid</span>;
      case 'PROCESSING':
        return <span className="badge badge-info"><Package size={12} /> Processing</span>;
      case 'SHIPPED':
        return <span className="badge badge-purple" style={{ backgroundColor: '#f3e8ff', color: '#7e22ce' }}><Truck size={12} /> Shipped</span>;
      case 'DELIVERED':
      case 'COMPLETED':
        return <span className="badge badge-success"><CheckCircle2 size={12} /> Completed</span>;
      case 'CANCELLED':
        return <span className="badge badge-danger"><XCircle size={12} /> Cancelled</span>;
      default:
        return <span className="badge badge-secondary">{status}</span>;
    }
  };

  const filteredOrders = orders.filter((order) => {
    const matchesFilter = statusFilter === 'ALL' || order.status === statusFilter;
    const matchesSearch =
      order.orderNumber?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      order.userName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      order.userEmail?.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesFilter && matchesSearch;
  });

  return (
    <div className="admin-content">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: '800', color: 'var(--secondary)' }}>Order Operations</h1>
          <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>
            Manage customer orders and track order fulfillment.
          </p>
        </div>

        <button onClick={fetchOrders} className="btn btn-secondary btn-sm" style={{ gap: '6px' }}>
          <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
          Refresh
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="card" style={{ padding: '16px 20px', marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '14px' }}>
        <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
          {['ALL', 'PAID', 'PROCESSING', 'SHIPPED', 'COMPLETED', 'CANCELLED'].map((st) => (
            <button
              key={st}
              onClick={() => setStatusFilter(st)}
              className={`btn btn-sm ${statusFilter === st ? 'btn-primary' : 'btn-secondary'}`}
              style={{ fontSize: '0.78rem', padding: '5px 12px' }}
            >
              {st}
            </button>
          ))}
        </div>

        <div style={{ position: 'relative', width: '280px' }}>
          <Search size={16} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
          <input
            type="text"
            className="form-input"
            placeholder="Search order # or customer..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{ paddingLeft: '36px', height: '36px', fontSize: '0.82rem' }}
          />
        </div>
      </div>

      {/* Orders Table */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Order Number</th>
              <th>Customer</th>
              <th>Date</th>
              <th>Items</th>
              <th>Total Amount</th>
              <th>Status</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="7" style={{ textAlign: 'center', padding: '40px' }}>
                  Loading order records...
                </td>
              </tr>
            ) : filteredOrders.length === 0 ? (
              <tr>
                <td colSpan="7" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                  No orders found matching your criteria.
                </td>
              </tr>
            ) : (
              filteredOrders.map((order) => {
                const canAdvance = order.status !== 'DELIVERED' && order.status !== 'COMPLETED' && order.status !== 'CANCELLED';
                const canCancel = order.status !== 'DELIVERED' && order.status !== 'COMPLETED' && order.status !== 'CANCELLED';

                return (
                  <tr key={order.id}>
                    <td>
                      <div style={{ fontWeight: '700', color: 'var(--secondary)' }}>{order.orderNumber}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Payment: {order.paymentMethod || 'CARD'}</div>
                    </td>
                    <td>
                      <div style={{ fontWeight: '600' }}>{order.userName}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{order.userEmail}</div>
                    </td>
                    <td style={{ fontSize: '0.82rem' }}>
                      {order.orderDate ? new Date(order.orderDate).toLocaleString() : 'Recent'}
                    </td>
                    <td>
                      <div style={{ fontSize: '0.85rem' }}>
                        {order.items?.length || 0} item(s)
                      </div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        {order.items?.map((i) => `${i.productName} (x${i.quantity})`).slice(0, 2).join(', ')}
                        {order.items?.length > 2 && '...'}
                      </div>
                    </td>
                    <td>
                      <div style={{ fontWeight: '800', color: 'var(--secondary)' }}>
                        ₹{Number(order.totalAmount || 0).toFixed(2)}
                      </div>
                      {order.discountAmount > 0 && (
                        <div style={{ fontSize: '0.72rem', color: '#059669' }}>
                          Saved ₹{Number(order.discountAmount).toFixed(2)}
                        </div>
                      )}
                    </td>
                    <td>{getStatusBadge(order.status)}</td>
                    <td>
                      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '6px' }}>
                        {/* Advance State Pattern */}
                        {canAdvance && (
                          <button
                            onClick={() => handleAdvanceStatus(order.id)}
                            disabled={actionLoading === order.id}
                            className="btn btn-primary btn-sm"
                            title="Advance Order Status"
                            style={{ fontSize: '0.78rem', padding: '5px 10px', gap: '4px' }}
                          >
                            <ArrowRightCircle size={14} />
                            Next Step
                          </button>
                        )}

                        {/* View Thermal Receipt */}
                        <button
                          onClick={() => handleViewBill(order.id)}
                          className="btn btn-secondary btn-sm"
                          title="Print / View Invoice Bill"
                          style={{ padding: '5px 8px' }}
                        >
                          <Receipt size={14} />
                        </button>

                        {/* Cancel & Restock */}
                        {canCancel && (
                          <button
                            onClick={() => handleCancelOrder(order.id)}
                            disabled={actionLoading === order.id}
                            className="btn btn-secondary btn-sm"
                            title="Cancel Order and Restock"
                            style={{ color: 'var(--danger)', borderColor: '#fecaca', padding: '5px 8px' }}
                          >
                            <XCircle size={14} />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      {/* Bill Receipt Modal */}
      {isBillModalOpen && selectedBill && (
        <BillReceiptModal bill={selectedBill} onClose={() => setIsBillModalOpen(false)} />
      )}
    </div>
  );
};
