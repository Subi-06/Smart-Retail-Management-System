import React, { useState, useEffect } from 'react';
import { discountAPI } from '../../services/api';
import { Percent, Plus, Tag, Check, X, ShieldCheck } from 'lucide-react';

export const AdminDiscounts = () => {
  const [discounts, setDiscounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddModal, setShowAddModal] = useState(false);

  const [formData, setFormData] = useState({
    name: '',
    code: '',
    type: 'COUPON',
    value: '10.00',
    isPercentage: true,
    minSpend: '200.00',
    description: 'Instant shopping discount voucher',
  });

  useEffect(() => {
    loadDiscounts();
  }, []);

  const loadDiscounts = async () => {
    try {
      const res = await discountAPI.getAll();
      setDiscounts(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await discountAPI.create({
        ...formData,
        value: parseFloat(formData.value),
        minSpend: parseFloat(formData.minSpend),
        active: true,
      });
      setShowAddModal(false);
      loadDiscounts();
      alert('Discount created successfully!');
    } catch (err) {
      alert('Failed to create discount');
    }
  };

  const handleToggleActive = async (id, currentStatus) => {
    try {
      await discountAPI.toggleStatus(id, !currentStatus);
      loadDiscounts();
    } catch (err) {
      alert('Failed to toggle status');
    }
  };

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '60px' }}>Loading discounts...</div>;
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '14px' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', fontWeight: '800', color: 'var(--secondary)' }}>Discount & Promo Rules</h1>
          <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>
            Configure promotional coupon codes, member discounts, and minimum order rules.
          </p>
        </div>

        <button onClick={() => setShowAddModal(true)} className="btn btn-primary" style={{ gap: '8px' }}>
          <Plus size={18} />
          <span>New Discount Voucher</span>
        </button>
      </div>

      {/* Discounts Table */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Voucher Name</th>
              <th>Promo Code</th>
              <th>Rule Type</th>
              <th>Benefit</th>
              <th>Min Spend</th>
              <th>Active Status</th>
              <th style={{ textAlign: 'center' }}>Action</th>
            </tr>
          </thead>
          <tbody>
            {discounts.map(d => (
              <tr key={d.id}>
                <td>
                  <div style={{ fontWeight: '700' }}>{d.name}</div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{d.description}</div>
                </td>
                <td>
                  <span style={{ fontFamily: 'monospace', fontWeight: '800', backgroundColor: '#f1f5f9', padding: '4px 8px', borderRadius: '4px' }}>
                    {d.code}
                  </span>
                </td>
                <td>
                  <span className="badge badge-primary">{d.type}</span>
                </td>
                <td style={{ fontWeight: '800', color: '#059669' }}>
                  {d.isPercentage ? `${d.value}% Off` : `₹${d.value} Flat`}
                </td>
                <td>₹{Number(d.minSpend).toFixed(2)}</td>
                <td>
                  <span className={`badge ${d.active ? 'badge-green' : 'badge-red'}`}>
                    {d.active ? 'ACTIVE' : 'INACTIVE'}
                  </span>
                </td>
                <td style={{ textAlign: 'center' }}>
                  <button
                    onClick={() => handleToggleActive(d.id, d.active)}
                    className={`btn btn-sm ${d.active ? 'btn-secondary' : 'btn-primary'}`}
                  >
                    {d.active ? 'Deactivate' : 'Activate'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Add Discount Modal */}
      {showAddModal && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ maxWidth: '480px' }}>
            <div className="modal-header">
              <h3 style={{ fontSize: '1.15rem', fontWeight: '800' }}>Create Promotional Voucher</h3>
              <button onClick={() => setShowAddModal(false)} style={{ border: 'none', background: 'none', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleCreate}>
              <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
                <div className="form-group">
                  <label className="form-label">Voucher Title</label>
                  <input
                    type="text"
                    className="form-input"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Coupon Code (Uppercase)</label>
                  <input
                    type="text"
                    className="form-input"
                    placeholder="e.g. FESTIVAL30"
                    value={formData.code}
                    onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
                    required
                  />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                  <div className="form-group">
                    <label className="form-label">Discount Category</label>
                    <select
                      className="form-select"
                      value={formData.type}
                      onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                    >
                      <option value="COUPON">Coupon Voucher</option>
                      <option value="FESTIVAL">Festival Season</option>
                      <option value="BULK">Bulk Purchase</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label className="form-label">Discount Value (% or ₹)</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-input"
                      value={formData.value}
                      onChange={(e) => setFormData({ ...formData, value: e.target.value })}
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Minimum Spend Required (₹)</label>
                  <input
                    type="number"
                    step="0.01"
                    className="form-input"
                    value={formData.minSpend}
                    onChange={(e) => setFormData({ ...formData, minSpend: e.target.value })}
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" onClick={() => setShowAddModal(false)} className="btn btn-secondary btn-sm">
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary btn-sm">
                  Save Voucher
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
