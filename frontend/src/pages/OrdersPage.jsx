import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { orderAPI, billAPI } from '../services/api';
import {
  Package,
  Receipt,
  RotateCcw,
  CheckCircle,
  Clock,
  Truck,
  Check,
  XCircle,
  AlertCircle
} from 'lucide-react';

export const OrdersPage = ({ onOpenBillModal }) => {
  const { user, isAdmin } = useAuth();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOrders();
  }, [user]);

  const fetchOrders = async () => {
    try {
      let res;
      if (isAdmin) {
        res = await orderAPI.getAll();
      } else if (user?.userId) {
        res = await orderAPI.getByUser(user.userId);
      }
      setOrders(res ? res.data : []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAdvanceStatus = async (orderId) => {
    try {
      // STATE PATTERN: Progresses order status
      await orderAPI.transitionStatus(orderId);
      fetchOrders();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to transition state');
    }
  };

  const handleCancelOrder = async (orderId) => {
    if (!window.confirm('Are you sure you want to cancel this order?')) return;
    try {
      // STATE & COMMAND PATTERN: Cancel and restock
      await orderAPI.cancel(orderId);
      fetchOrders();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to cancel order');
    }
  };

  const handleViewBill = async (orderId) => {
    try {
      const res = await billAPI.getByOrder(orderId);
      onOpenBillModal && onOpenBillModal(res.data);
    } catch (err) {
      alert('Bill not found for this order.');
    }
  };

  const steps = [
    { key: 'ORDER_PLACED', label: 'Order Placed' },
    { key: 'PAID', label: 'Paid' },
    { key: 'PROCESSING', label: 'Processing' },
    { key: 'COMPLETED', label: 'Completed' },
  ];

  const getStepIndex = (status) => {
    switch (status) {
      case 'ORDER_PLACED': return 0;
      case 'PAYMENT_PENDING': return 0;
      case 'PAID': return 1;
      case 'PROCESSING': return 2;
      case 'COMPLETED': return 3;
      case 'CANCELLED': return -1;
      default: return 0;
    }
  };

  if (loading) {
    return <div className="content-wrapper" style={{ textAlign: 'center', padding: '80px 0' }}>Loading orders...</div>;
  }

  return (
    <div className="content-wrapper">
      <div style={{ marginBottom: '28px' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: '800', color: 'var(--secondary)' }}>
          {isAdmin ? "Store Customer Orders" : "My Purchase Orders"}
        </h1>
        <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>
          Track real-time fulfillment and delivery status for your purchases.
        </p>
      </div>

      {orders.length === 0 ? (
        <div className="card" style={{ padding: '60px 20px', textAlign: 'center' }}>
          <Package size={36} color="var(--text-light)" style={{ margin: '0 auto 12px auto' }} />
          <h3 style={{ fontSize: '1.2rem', fontWeight: '700' }}>No orders placed yet</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem', marginTop: '6px' }}>
            When you complete checkout, your order history will appear here with live delivery tracking.
          </p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '22px' }}>
          {orders.map((order) => {
            const currentStepIdx = getStepIndex(order.status);
            const isCancelled = order.status === 'CANCELLED';

            return (
              <div key={order.id} className="card" style={{ padding: '24px' }}>
                {/* Order Top Bar */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '14px', borderBottom: '1px solid var(--border-color)', paddingBottom: '14px', marginBottom: '20px' }}>
                  <div>
                    <span style={{ fontWeight: '800', fontSize: '1.1rem', color: 'var(--secondary)' }}>
                      Order #{order.id}
                    </span>
                    <span style={{ color: 'var(--text-muted)', fontSize: '0.82rem', marginLeft: '12px' }}>
                      Placed on {new Date(order.createdAt).toLocaleDateString()} at {new Date(order.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </span>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <span className={`badge ${isCancelled ? 'badge-red' : order.status === 'COMPLETED' ? 'badge-green' : 'badge-primary'}`}>
                      {order.status}
                    </span>
                    <button
                      onClick={() => handleViewBill(order.id)}
                      className="btn btn-secondary btn-sm"
                      style={{ gap: '6px' }}
                    >
                      <Receipt size={14} />
                      View Bill
                    </button>
                    {isAdmin && order.status !== 'COMPLETED' && order.status !== 'CANCELLED' && (
                      <button
                        onClick={() => handleAdvanceStatus(order.id)}
                        className="btn btn-primary btn-sm"
                        title="Advance Status"
                      >
                        Advance Status →
                      </button>
                    )}
                    {order.status === 'ORDER_PLACED' && (
                      <button
                        onClick={() => handleCancelOrder(order.id)}
                        className="btn btn-secondary btn-sm"
                        style={{ color: 'var(--danger)', borderColor: '#fecaca' }}
                      >
                        Cancel
                      </button>
                    )}
                  </div>
                </div>

                {/* Status Stepper */}
                {!isCancelled ? (
                  <div style={{ marginBottom: '24px', padding: '16px 20px', backgroundColor: '#f8fafc', borderRadius: 'var(--radius-md)', border: '1px solid #e2e8f0' }}>
                    <div style={{ fontSize: '0.72rem', fontWeight: '800', textTransform: 'uppercase', color: 'var(--primary)', marginBottom: '14px' }}>
                      Order Tracking Status
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', position: 'relative' }}>
                      {steps.map((step, idx) => {
                        const isCompleted = idx <= currentStepIdx;
                        const isCurrent = idx === currentStepIdx;

                        return (
                          <div key={step.key} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', zIndex: 2, width: '90px' }}>
                            <div style={{
                              width: '32px',
                              height: '32px',
                              borderRadius: '50%',
                              backgroundColor: isCompleted ? 'var(--primary)' : '#e2e8f0',
                              color: isCompleted ? '#ffffff' : 'var(--text-light)',
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                              fontWeight: '800',
                              fontSize: '0.85rem',
                              boxShadow: isCurrent ? '0 0 0 4px rgba(79, 70, 229, 0.2)' : 'none',
                              transition: 'all 0.3s'
                            }}>
                              {isCompleted ? <Check size={16} /> : idx + 1}
                            </div>
                            <span style={{
                              fontSize: '0.75rem',
                              fontWeight: isCompleted ? '700' : '500',
                              color: isCompleted ? 'var(--text-main)' : 'var(--text-light)',
                              marginTop: '6px',
                              textAlign: 'center'
                            }}>
                              {step.label}
                            </span>
                          </div>
                        );
                      })}

                      {/* Stepper Connecting Background Line */}
                      <div style={{
                        position: 'absolute',
                        top: '16px',
                        left: '45px',
                        right: '45px',
                        height: '2px',
                        backgroundColor: '#e2e8f0',
                        zIndex: 1
                      }}>
                        <div style={{
                          height: '100%',
                          backgroundColor: 'var(--primary)',
                          width: `${(currentStepIdx / (steps.length - 1)) * 100}%`,
                          transition: 'width 0.4s ease'
                        }} />
                      </div>
                    </div>
                  </div>
                ) : (
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '12px 16px', backgroundColor: '#fef2f2', borderRadius: '8px', color: '#991b1b', fontSize: '0.85rem', marginBottom: '16px' }}>
                    <XCircle size={18} />
                    <span>This order was cancelled and its items were returned to store stock.</span>
                  </div>
                )}

                {/* Purchased Items List */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  {order.items?.map((item) => (
                    <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.88rem' }}>
                      <span><strong>{item.quantity}×</strong> {item.productName} ({item.productBrand})</span>
                      <span style={{ fontWeight: '700' }}>₹{Number(item.subtotal).toFixed(2)}</span>
                    </div>
                  ))}
                </div>

                {/* Financial Summary Breakdown */}
                <div style={{ display: 'flex', justifyContent: 'flex-end', borderTop: '1px solid #f1f5f9', paddingTop: '14px', marginTop: '14px', gap: '24px', fontSize: '0.88rem' }}>
                  <div>Subtotal: <strong>₹{Number(order.totalAmount).toFixed(2)}</strong></div>
                  {Number(order.discountAmount) > 0 && (
                    <div style={{ color: '#059669' }}>Discount: <strong>-₹{Number(order.discountAmount).toFixed(2)}</strong></div>
                  )}
                  <div>GST Tax: <strong>₹{Number(order.taxAmount).toFixed(2)}</strong></div>
                  <div style={{ fontSize: '1.05rem', fontWeight: '800', color: 'var(--secondary)' }}>
                    Total: ₹{Number(order.finalAmount).toFixed(2)}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
