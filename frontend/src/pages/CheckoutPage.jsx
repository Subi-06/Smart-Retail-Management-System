import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { cartAPI, checkoutAPI, billAPI } from '../services/api';
import {
  ShieldCheck,
  CreditCard,
  QrCode,
  Banknote,
  CheckCircle2,
  Receipt,
  Layers,
  ArrowRight,
  RefreshCw,
  Lock
} from 'lucide-react';

export const CheckoutPage = ({ onOpenBillModal }) => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  // Form State
  const [shippingAddress, setShippingAddress] = useState(user?.address || '42 Green Meadow Lane, Bangalore');
  const [paymentMethod, setPaymentMethod] = useState('UPI'); // UPI, CARD, CASH
  const [upiVpa, setUpiVpa] = useState('subiksha@okaxis');
  const [cardNumber, setCardNumber] = useState('4532 1198 4432 9012');
  const [cardCvv, setCardCvv] = useState('892');
  const [couponCode, setCouponCode] = useState(location.state?.appliedCoupon || 'SAVE10');

  // Checkout Result State
  const [checkoutResult, setCheckoutResult] = useState(null);

  useEffect(() => {
    if (user?.userId) {
      cartAPI.get(user.userId)
        .then(res => {
          if (!res.data.items || res.data.items.length === 0) {
            navigate('/cart');
          } else {
            setCart(res.data);
          }
        })
        .catch(() => navigate('/cart'))
        .finally(() => setLoading(false));
    }
  }, [user]);

  const handlePlaceOrder = async (e) => {
    e.preventDefault();
    setSubmitting(true);

    const payload = {
      userId: user.userId,
      paymentMethod: paymentMethod,
      couponCode: couponCode.trim(),
      shippingAddress: shippingAddress,
      upiVpa: paymentMethod === 'UPI' ? upiVpa : null,
      cardNumber: paymentMethod === 'CARD' ? cardNumber : null,
      cardCvv: paymentMethod === 'CARD' ? cardCvv : null,
    };

    try {
      // FACADE PATTERN: Frontend calls checkoutFacade.checkout()
      const res = await checkoutAPI.checkout(payload);
      setCheckoutResult(res.data);
    } catch (err) {
      alert(err.response?.data?.message || 'Checkout failed. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleViewBill = async () => {
    if (!checkoutResult?.billId) return;
    try {
      const res = await billAPI.getById(checkoutResult.billId);
      onOpenBillModal && onOpenBillModal(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) {
    return <div className="content-wrapper" style={{ textAlign: 'center', padding: '80px 0' }}>Loading checkout data...</div>;
  }

  // If order is successfully placed, show success confirmation screen!
  if (checkoutResult) {
    return (
      <div className="content-wrapper" style={{ maxWidth: '780px' }}>
        <div className="card" style={{ padding: '36px', textAlign: 'center' }}>
          <div style={{
            width: '68px',
            height: '68px',
            backgroundColor: 'var(--success-bg)',
            color: 'var(--success)',
            borderRadius: '50%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 16px auto'
          }}>
            <CheckCircle2 size={36} />
          </div>

          <h2 style={{ fontSize: '1.8rem', fontWeight: '800', color: 'var(--secondary)', marginBottom: '6px' }}>
            ✓ Order Placed Successfully!
          </h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem', marginBottom: '24px' }}>
            Your order has been confirmed and is being prepared for express delivery.
          </p>

          <div style={{
            backgroundColor: '#f8fafc',
            border: '1px solid var(--border-color)',
            borderRadius: 'var(--radius-lg)',
            padding: '20px',
            maxWidth: '480px',
            margin: '0 auto 28px auto',
            textAlign: 'left',
            display: 'flex',
            flexDirection: 'column',
            gap: '10px'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-muted)' }}>Order Reference:</span>
              <strong>#{checkoutResult.orderId}</strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-muted)' }}>Invoice Number:</span>
              <strong>{checkoutResult.billNumber}</strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-muted)' }}>Payment Channel:</span>
              <strong>{paymentMethod} ({checkoutResult.paymentStatus})</strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-muted)' }}>Transaction Reference:</span>
              <strong style={{ fontFamily: 'monospace' }}>{checkoutResult.transactionId}</strong>
            </div>
            <div style={{
              display: 'flex',
              justifyContent: 'space-between',
              borderTop: '1px solid var(--border-color)',
              paddingTop: '8px',
              fontSize: '1.1rem',
              fontWeight: '800',
              color: 'var(--secondary)'
            }}>
              <span>Amount Paid:</span>
              <span>₹{Number(checkoutResult.finalAmount).toFixed(2)}</span>
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'center', gap: '14px' }}>
            <button onClick={handleViewBill} className="btn btn-primary btn-lg">
              <Receipt size={18} />
              [ View Bill ]
            </button>
            <button onClick={() => navigate('/orders')} className="btn btn-secondary btn-lg">
              View Order Status
            </button>
          </div>
        </div>
      </div>
    );
  }

  const items = cart?.items || [];

  return (
    <div className="content-wrapper">
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: '800', color: 'var(--secondary)' }}>Checkout</h1>
        <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>
          Complete your delivery details and choose your preferred payment option.
        </p>
      </div>

      <form onSubmit={handlePlaceOrder} style={{ display: 'grid', gridTemplateColumns: '1fr 380px', gap: '28px' }}>
        {/* Left Column: Customer Info & Payment Options */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          {/* Customer Information */}
          <div className="card" style={{ padding: '24px' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: '800', marginBottom: '16px' }}>1. Customer & Shipping Address</h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px', marginBottom: '14px' }}>
              <div>
                <label className="form-label">Full Name</label>
                <input type="text" className="form-input" value={user?.name || ''} disabled />
              </div>
              <div>
                <label className="form-label">Email Address</label>
                <input type="email" className="form-input" value={user?.email || ''} disabled />
              </div>
            </div>
            <div>
              <label className="form-label">Delivery Address</label>
              <textarea
                rows="2"
                className="form-textarea"
                value={shippingAddress}
                onChange={(e) => setShippingAddress(e.target.value)}
                required
              />
            </div>
          </div>

          {/* Payment Method Selector */}
          <div className="card" style={{ padding: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <h3 style={{ fontSize: '1.1rem', fontWeight: '800' }}>2. Payment Method</h3>
            </div>

            {/* Tabs for UPI, Card, Cash */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '12px', marginBottom: '20px' }}>
              <button
                type="button"
                onClick={() => setPaymentMethod('UPI')}
                style={{
                  padding: '14px 10px',
                  borderRadius: 'var(--radius-md)',
                  border: paymentMethod === 'UPI' ? '2px solid var(--primary)' : '1px solid var(--border-color)',
                  backgroundColor: paymentMethod === 'UPI' ? 'var(--primary-light)' : '#ffffff',
                  color: paymentMethod === 'UPI' ? 'var(--primary)' : 'var(--text-main)',
                  fontWeight: '700',
                  cursor: 'pointer',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  gap: '6px'
                }}
              >
                <QrCode size={22} />
                <span>UPI Instant</span>
              </button>

              <button
                type="button"
                onClick={() => setPaymentMethod('CARD')}
                style={{
                  padding: '14px 10px',
                  borderRadius: 'var(--radius-md)',
                  border: paymentMethod === 'CARD' ? '2px solid var(--primary)' : '1px solid var(--border-color)',
                  backgroundColor: paymentMethod === 'CARD' ? 'var(--primary-light)' : '#ffffff',
                  color: paymentMethod === 'CARD' ? 'var(--primary)' : 'var(--text-main)',
                  fontWeight: '700',
                  cursor: 'pointer',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  gap: '6px'
                }}
              >
                <CreditCard size={22} />
                <span>Credit / Debit</span>
              </button>

              <button
                type="button"
                onClick={() => setPaymentMethod('CASH')}
                style={{
                  padding: '14px 10px',
                  borderRadius: 'var(--radius-md)',
                  border: paymentMethod === 'CASH' ? '2px solid var(--primary)' : '1px solid var(--border-color)',
                  backgroundColor: paymentMethod === 'CASH' ? 'var(--primary-light)' : '#ffffff',
                  color: paymentMethod === 'CASH' ? 'var(--primary)' : 'var(--text-main)',
                  fontWeight: '700',
                  cursor: 'pointer',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  gap: '6px'
                }}
              >
                <Banknote size={22} />
                <span>Cash on Counter</span>
              </button>
            </div>

            {/* Dynamic Inputs Based on Payment Channel */}
            {paymentMethod === 'UPI' && (
              <div>
                <label className="form-label">Virtual Payment Address (VPA)</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. yourname@oksbi"
                  value={upiVpa}
                  onChange={(e) => setUpiVpa(e.target.value)}
                  required
                />
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                  Instant zero-fee payment via Google Pay, PhonePe, Paytm, or BHIM UPI.
                </div>
              </div>
            )}

            {paymentMethod === 'CARD' && (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                <div>
                  <label className="form-label">Card Number</label>
                  <input
                    type="text"
                    className="form-input"
                    value={cardNumber}
                    onChange={(e) => setCardNumber(e.target.value)}
                    required
                  />
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                  <div>
                    <label className="form-label">Expiry (MM/YY)</label>
                    <input type="text" className="form-input" defaultValue="12/28" />
                  </div>
                  <div>
                    <label className="form-label">CVV</label>
                    <input
                      type="password"
                      maxLength="3"
                      className="form-input"
                      value={cardCvv}
                      onChange={(e) => setCardCvv(e.target.value)}
                      required
                    />
                  </div>
                </div>
              </div>
            )}

            {paymentMethod === 'CASH' && (
              <div style={{ padding: '14px', backgroundColor: '#f8fafc', borderRadius: '8px', fontSize: '0.85rem' }}>
                <p>Pay with cash upon delivery or at the store checkout counter.</p>
              </div>
            )}
          </div>
        </div>

        {/* Right Column: Order Summary & Placement */}
        <div className="card" style={{ padding: '24px', height: 'fit-content' }}>
          <h3 style={{ fontSize: '1.15rem', fontWeight: '800', marginBottom: '16px', borderBottom: '1px solid var(--border-color)', paddingBottom: '12px' }}>
            Checkout Summary
          </h3>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', maxHeight: '200px', overflowY: 'auto', marginBottom: '16px' }}>
            {items.map(item => (
              <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem' }}>
                <span style={{ color: 'var(--text-main)' }}>{item.productName} × {item.quantity}</span>
                <span style={{ fontWeight: '700' }}>₹{Number(item.subtotal).toFixed(2)}</span>
              </div>
            ))}
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', fontSize: '0.88rem', borderTop: '1px solid var(--border-color)', paddingTop: '14px', marginBottom: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-muted)' }}>Subtotal:</span>
              <span>₹{Number(cart.subtotal).toFixed(2)}</span>
            </div>

            {couponCode && (
              <div style={{ display: 'flex', justifyContent: 'space-between', color: '#059669' }}>
                <span>Coupon ({couponCode}):</span>
                <span>Applied</span>
              </div>
            )}

            {user?.membershipType && user.membershipType !== 'REGULAR' && (
              <div style={{ display: 'flex', justifyContent: 'space-between', color: '#059669' }}>
                <span>{user.membershipType} Tier Discount:</span>
                <span>Active</span>
              </div>
            )}

            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-muted)' }}>Estimated GST (5%):</span>
              <span>₹{Number(cart.tax).toFixed(2)}</span>
            </div>

            <div style={{
              display: 'flex',
              justifyContent: 'space-between',
              fontSize: '1.25rem',
              fontWeight: '800',
              borderTop: '2px solid var(--border-color)',
              paddingTop: '12px',
              marginTop: '4px',
              color: 'var(--secondary)'
            }}>
              <span>Final Total:</span>
              <span>₹{Number(cart.total).toFixed(2)}</span>
            </div>
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="btn btn-primary btn-lg"
            style={{ width: '100%', gap: '10px' }}
          >
            {submitting ? (
              <>
                <RefreshCw size={18} className="spin" />
                <span>Processing Order...</span>
              </>
            ) : (
              <>
                <Lock size={18} />
                <span>PLACE ORDER</span>
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};
