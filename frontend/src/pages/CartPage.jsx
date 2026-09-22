import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { cartAPI, discountAPI } from '../services/api';
import {
  Trash2,
  Plus,
  Minus,
  RotateCcw,
  ArrowRight,
  ShoppingBag,
  Tag,
  ShieldCheck,
  Percent
} from 'lucide-react';

export const CartPage = ({ onTriggerToast }) => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [couponInput, setCouponInput] = useState('');
  const [couponError, setCouponError] = useState('');
  const [appliedCoupon, setAppliedCoupon] = useState(null);

  useEffect(() => {
    if (user?.userId) {
      fetchCart();
    }
  }, [user]);

  const fetchCart = async () => {
    try {
      const res = await cartAPI.get(user.userId);
      setCart(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateQuantity = async (cartItemId, newQty) => {
    if (!user?.userId) return;
    try {
      const res = await cartAPI.updateQuantity(cartItemId, newQty, user.userId);
      setCart(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleRemoveItem = async (cartItemId) => {
    if (!user?.userId) return;
    try {
      const res = await cartAPI.removeItem(cartItemId, user.userId);
      setCart(res.data);
      onTriggerToast && onTriggerToast('Item removed from cart');
    } catch (err) {
      console.error(err);
    }
  };

  const handleClearCart = async () => {
    if (!user?.userId) return;
    try {
      const res = await cartAPI.clear(user.userId);
      setCart(res.data);
      onTriggerToast && onTriggerToast(
        'Cart cleared! Click UNDO to restore items',
        true, // isUndoAction
        handleUndoClear
      );
    } catch (err) {
      console.error(err);
    }
  };

  const handleUndoClear = async () => {
    if (!user?.userId) return;
    try {
      const res = await cartAPI.undo(user.userId);
      setCart(res.data);
      onTriggerToast && onTriggerToast('Cart restored successfully!');
    } catch (err) {
      console.error(err);
    }
  };

  const handleApplyCoupon = async (e) => {
    e.preventDefault();
    setCouponError('');
    if (!couponInput.trim()) return;

    try {
      const res = await discountAPI.getByCode(couponInput.trim().toUpperCase());
      const discount = res.data;

      if (discount.minSpend && cart.subtotal < discount.minSpend) {
        setCouponError(`Coupon requires minimum spend of ₹${discount.minSpend}`);
        return;
      }

      setAppliedCoupon(discount);
      onTriggerToast && onTriggerToast(`Coupon ${discount.code} applied!`);
    } catch (err) {
      setCouponError('Invalid coupon code. Try SAVE10, FESTIVAL20, or WELCOME50');
    }
  };

  const handleRemoveCoupon = () => {
    setAppliedCoupon(null);
    setCouponInput('');
    setCouponError('');
  };

  // Re-calculate totals when coupon is applied
  const subtotal = cart?.subtotal || 0;
  let couponDiscount = 0;
  if (appliedCoupon) {
    if (appliedCoupon.isPercentage) {
      couponDiscount = (subtotal * appliedCoupon.value) / 100;
    } else {
      couponDiscount = Math.min(subtotal, appliedCoupon.value);
    }
  }

  const membershipDiscount = cart?.discount || 0;
  const totalDiscount = Number(membershipDiscount) + Number(couponDiscount);
  const taxable = Math.max(0, subtotal - totalDiscount);
  const tax = taxable * 0.05; // 5% GST
  const grandTotal = taxable + tax;

  if (loading) {
    return <div className="content-wrapper" style={{ textAlign: 'center', padding: '80px 0' }}>Loading your cart...</div>;
  }

  const items = cart?.items || [];

  return (
    <div className="content-wrapper">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', fontWeight: '800', color: 'var(--secondary)' }}>Shopping Cart</h1>
          <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>
            Review your selected items, apply discounts, and proceed to checkout.
          </p>
        </div>

        {/* Undo button if available */}
        {cart?.undoAvailable && (
          <button
            onClick={handleUndoClear}
            className="btn btn-secondary btn-sm"
            style={{ borderColor: 'var(--primary)', color: 'var(--primary)', fontWeight: '700', gap: '6px' }}
          >
            <RotateCcw size={15} />
            [ UNDO ]
          </button>
        )}
      </div>

      {items.length === 0 ? (
        <div className="card" style={{ padding: '60px 20px', textAlign: 'center' }}>
          <div style={{
            width: '64px',
            height: '64px',
            margin: '0 auto 16px auto',
            backgroundColor: '#f1f5f9',
            color: 'var(--text-light)',
            borderRadius: '50%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}>
            <ShoppingBag size={28} />
          </div>
          <h3 style={{ fontSize: '1.25rem', fontWeight: '800', marginBottom: '8px' }}>Your cart is empty</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '24px' }}>
            Looks like you haven't added any groceries or electronics to your basket yet.
          </p>

          <div style={{ display: 'flex', justifyContent: 'center', gap: '12px' }}>
            <Link to="/products" className="btn btn-primary">
              Browse Products
            </Link>
            {cart?.undoAvailable && (
              <button onClick={handleUndoClear} className="btn btn-secondary">
                <RotateCcw size={16} />
                Restore Previous Cart
              </button>
            )}
          </div>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 360px', gap: '28px', alignItems: 'start' }}>
          {/* Items List Table */}
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Product</th>
                  <th>Price</th>
                  <th style={{ textAlign: 'center' }}>Quantity</th>
                  <th style={{ textAlign: 'right' }}>Subtotal</th>
                  <th style={{ textAlign: 'center' }}>Action</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.id}>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
                        <img
                          src={item.productImage || 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=100'}
                          alt={item.productName}
                          style={{ width: '48px', height: '48px', borderRadius: '8px', objectFit: 'cover' }}
                        />
                        <div>
                          <div style={{ fontWeight: '700', fontSize: '0.92rem' }}>{item.productName}</div>
                          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                            {item.productBrand} • {item.productUnit}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td>₹{Number(item.price).toFixed(2)}</td>
                    <td style={{ textAlign: 'center' }}>
                      <div style={{
                        display: 'inline-flex',
                        alignItems: 'center',
                        border: '1px solid var(--border-color)',
                        borderRadius: 'var(--radius-sm)',
                        backgroundColor: '#ffffff'
                      }}>
                        <button
                          onClick={() => handleUpdateQuantity(item.id, item.quantity - 1)}
                          style={{ padding: '4px 8px', border: 'none', background: 'none', cursor: 'pointer' }}
                        >
                          <Minus size={13} />
                        </button>
                        <span style={{ padding: '0 8px', fontSize: '0.88rem', fontWeight: '700' }}>{item.quantity}</span>
                        <button
                          onClick={() => handleUpdateQuantity(item.id, item.quantity + 1)}
                          style={{ padding: '4px 8px', border: 'none', background: 'none', cursor: 'pointer' }}
                        >
                          <Plus size={13} />
                        </button>
                      </div>
                    </td>
                    <td style={{ textAlign: 'right', fontWeight: '700' }}>
                      ₹{Number(item.subtotal).toFixed(2)}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <button
                        onClick={() => handleRemoveItem(item.id)}
                        className="btn btn-secondary btn-sm"
                        style={{ color: 'var(--danger)', borderColor: '#fecaca', padding: '6px' }}
                        title="Remove item"
                      >
                        <Trash2 size={15} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div style={{ padding: '16px 20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#f8fafc', borderTop: '1px solid var(--border-color)' }}>
              <Link to="/products" className="btn btn-secondary btn-sm">
                Continue Shopping
              </Link>
              <button
                onClick={handleClearCart}
                className="btn btn-danger btn-sm"
              >
                <Trash2 size={15} />
                Clear Cart
              </button>
            </div>
          </div>

          {/* Cart Summary Card */}
          <div className="card" style={{ padding: '24px' }}>
            <h3 style={{ fontSize: '1.15rem', fontWeight: '800', marginBottom: '16px', borderBottom: '1px solid var(--border-color)', paddingBottom: '12px' }}>
              Order Summary
            </h3>

            {/* Promo Code Input */}
            <form onSubmit={handleApplyCoupon} style={{ marginBottom: '16px' }}>
              <div className="form-label" style={{ fontSize: '0.82rem' }}>Promotional Voucher</div>
              <div style={{ display: 'flex', gap: '8px' }}>
                <input
                  type="text"
                  className="form-input"
                  placeholder="SAVE10, FESTIVAL20"
                  value={couponInput}
                  onChange={(e) => setCouponInput(e.target.value.toUpperCase())}
                  style={{ textTransform: 'uppercase' }}
                />
                <button type="submit" className="btn btn-secondary btn-sm">
                  Apply
                </button>
              </div>
              {couponError && <div style={{ color: 'var(--danger)', fontSize: '0.78rem', marginTop: '4px' }}>{couponError}</div>}
              {appliedCoupon && (
                <div style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  backgroundColor: '#ecfdf5',
                  color: '#065f46',
                  padding: '6px 10px',
                  borderRadius: '6px',
                  marginTop: '8px',
                  fontSize: '0.82rem'
                }}>
                  <span><strong>{appliedCoupon.code}</strong> applied!</span>
                  <button onClick={handleRemoveCoupon} style={{ border: 'none', background: 'none', cursor: 'pointer', color: '#991b1b', fontWeight: '700' }}>×</button>
                </div>
              )}
            </form>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', fontSize: '0.9rem', marginBottom: '20px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--text-muted)' }}>Subtotal ({items.length} items)</span>
                <span>₹{Number(subtotal).toFixed(2)}</span>
              </div>

              {Number(membershipDiscount) > 0 && (
                <div style={{ display: 'flex', justifyContent: 'space-between', color: '#059669' }}>
                  <span>{user?.membershipType} Member Discount</span>
                  <span>-₹{Number(membershipDiscount).toFixed(2)}</span>
                </div>
              )}

              {couponDiscount > 0 && (
                <div style={{ display: 'flex', justifyContent: 'space-between', color: '#059669' }}>
                  <span>Promo ({appliedCoupon.code})</span>
                  <span>-₹{Number(couponDiscount).toFixed(2)}</span>
                </div>
              )}

              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--text-muted)' }}>Estimated GST (5%)</span>
                <span>₹{Number(tax).toFixed(2)}</span>
              </div>

              <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                fontWeight: '800',
                fontSize: '1.2rem',
                borderTop: '1px solid var(--border-color)',
                paddingTop: '12px',
                marginTop: '6px',
                color: 'var(--secondary)'
              }}>
                <span>Total Payable</span>
                <span>₹{Number(grandTotal).toFixed(2)}</span>
              </div>
            </div>

            <button
              onClick={() => navigate('/checkout', { state: { appliedCoupon: appliedCoupon?.code } })}
              className="btn btn-primary btn-lg"
              style={{ width: '100%' }}
            >
              <span>Proceed to Checkout</span>
              <ArrowRight size={18} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
