import React, { useState, useEffect } from 'react';
import { productAPI, categoryAPI } from '../../services/api';
import {
  Plus,
  Copy,
  Trash2,
  Edit3,
  Search,
  CheckCircle,
  FileCheck,
  X,
  RefreshCw
} from 'lucide-react';

export const AdminProducts = () => {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  // Modals
  const [showFactoryModal, setShowFactoryModal] = useState(false);
  const [showCloneModal, setShowCloneModal] = useState(false);
  const [showAuditModal, setShowAuditModal] = useState(false);
  const [auditResult, setAuditResult] = useState(null);

  // Form states
  const [targetProduct, setTargetProduct] = useState(null);
  const [factoryFormData, setFactoryFormData] = useState({
    name: '',
    price: '',
    quantity: '',
    productType: 'GROCERY',
    categoryId: '',
    brand: '',
    unit: '1 Unit',
    shelfLifeDays: '',
    warrantyMonths: '',
    description: '',
    image: '',
  });

  const [cloneFormData, setCloneFormData] = useState({
    name: '',
    price: '',
    quantity: '',
    unit: '',
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [prodRes, catRes] = await Promise.all([
        productAPI.getAll(false),
        categoryAPI.getAll(),
      ]);
      setProducts(prodRes.data);
      setCategories(catRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenFactoryModal = () => {
    setFactoryFormData({
      name: '',
      price: '120.00',
      quantity: '20',
      productType: 'BEVERAGE',
      categoryId: categories[2]?.id || '',
      brand: 'Organic Brew',
      unit: '500ml',
      shelfLifeDays: '90',
      warrantyMonths: '0',
      description: 'Factory-created organic retail product item.',
      image: 'https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=500',
    });
    setShowFactoryModal(true);
  };

  const handleCreateWithFactory = async (e) => {
    e.preventDefault();
    try {
      // FACTORY METHOD PATTERN: Invokes backend Factory for Food, Grocery, Beverage, or Electronics
      await productAPI.create({
        ...factoryFormData,
        price: parseFloat(factoryFormData.price),
        quantity: parseInt(factoryFormData.quantity),
        shelfLifeDays: factoryFormData.shelfLifeDays ? parseInt(factoryFormData.shelfLifeDays) : null,
        warrantyMonths: factoryFormData.warrantyMonths ? parseInt(factoryFormData.warrantyMonths) : null,
      });
      setShowFactoryModal(false);
      loadData();
      alert(`Product created successfully!`);
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to create product');
    }
  };

  const handleOpenCloneModal = (prod) => {
    setTargetProduct(prod);
    setCloneFormData({
      name: `${prod.name} (Large Pack)`,
      price: (Number(prod.price) * 1.8).toFixed(2),
      quantity: prod.quantity,
      unit: prod.unit ? `2x ${prod.unit}` : 'Double Pack',
    });
    setShowCloneModal(true);
  };

  const handleCloneSubmit = async (e) => {
    e.preventDefault();
    try {
      // PROTOTYPE PATTERN: Product.cloneProduct()
      await productAPI.clone(targetProduct.id, {
        name: cloneFormData.name,
        price: parseFloat(cloneFormData.price),
        quantity: parseInt(cloneFormData.quantity),
        unit: cloneFormData.unit,
      });
      setShowCloneModal(false);
      loadData();
      alert(`Product variation '${cloneFormData.name}' successfully created!`);
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to create variation');
    }
  };

  const handleDeleteProduct = async (id) => {
    if (!window.confirm('Are you sure you want to delete this product?')) return;
    try {
      // PROXY PATTERN: Protection proxy verifies ROLE_ADMIN
      await productAPI.delete(id);
      loadData();
      alert('Product deleted successfully.');
    } catch (err) {
      alert(err.response?.data?.message || 'Access Denied: Admin role required.');
    }
  };

  const handleAuditVisitor = async (id) => {
    try {
      // VISITOR PATTERN: TaxVisitor & InventoryAuditVisitor
      const res = await productAPI.auditVisitor(id);
      setAuditResult(res.data);
      setShowAuditModal(true);
    } catch (err) {
      alert('Audit failed');
    }
  };

  const filtered = products.filter(p =>
    p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    p.brand?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '14px' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', fontWeight: '800', color: 'var(--secondary)' }}>Product Management</h1>
          <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>
            Manage store catalog, prices, inventory levels, and product specifications.
          </p>
        </div>

        <button onClick={handleOpenFactoryModal} className="btn btn-primary" style={{ gap: '8px' }}>
          <Plus size={18} />
          <span>Add Product</span>
        </button>
      </div>

      {/* Search Bar */}
      <div style={{ marginBottom: '20px', position: 'relative', maxWidth: '400px' }}>
        <Search size={16} className="search-icon" />
        <input
          type="text"
          className="search-input"
          placeholder="Filter products..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      {/* Products Table */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Item</th>
              <th>Category</th>
              <th>Type</th>
              <th>Price</th>
              <th>Stock</th>
              <th>Status</th>
              <th style={{ textAlign: 'center' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map(p => (
              <tr key={p.id}>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                    <img
                      src={p.image || 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=100'}
                      alt={p.name}
                      style={{ width: '40px', height: '40px', borderRadius: '6px', objectFit: 'cover' }}
                    />
                    <div>
                      <div style={{ fontWeight: '700' }}>{p.name}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{p.brand} • {p.unit}</div>
                    </div>
                  </div>
                </td>
                <td>{p.categoryName}</td>
                <td>
                  <span className="badge badge-primary" style={{ fontSize: '0.68rem' }}>
                    {p.productType}
                  </span>
                </td>
                <td style={{ fontWeight: '700' }}>₹{Number(p.price).toFixed(2)}</td>
                <td>{p.quantity}</td>
                <td>
                  <span className={`badge ${p.quantity <= 0 ? 'badge-red' : p.quantity <= 5 ? 'badge-yellow' : 'badge-green'}`}>
                    {p.status}
                  </span>
                </td>
                <td style={{ textAlign: 'center' }}>
                  <div style={{ display: 'inline-flex', gap: '6px' }}>
                    {/* Clone Button */}
                    <button
                      onClick={() => handleOpenCloneModal(p)}
                      className="btn btn-secondary btn-sm"
                      title="Duplicate / Create Variation"
                      style={{ gap: '4px' }}
                    >
                      <Copy size={13} />
                      Clone
                    </button>

                    {/* Audit Button */}
                    <button
                      onClick={() => handleAuditVisitor(p.id)}
                      className="btn btn-secondary btn-sm"
                      title="Audit GST & Inventory"
                      style={{ gap: '4px' }}
                    >
                      <FileCheck size={13} />
                      Audit
                    </button>

                    {/* Delete Button */}
                    <button
                      onClick={() => handleDeleteProduct(p.id)}
                      className="btn btn-secondary btn-sm"
                      title="Delete Product"
                      style={{ color: 'var(--danger)', borderColor: '#fecaca' }}
                    >
                      <Trash2 size={13} />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* ADD PRODUCT MODAL */}
      {showFactoryModal && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <div>
                <h3 style={{ fontSize: '1.15rem', fontWeight: '800' }}>Add New Product</h3>
                <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                  Configure product specifications, tax category, and initial inventory
                </div>
              </div>
              <button onClick={() => setShowFactoryModal(false)} style={{ border: 'none', background: 'none', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleCreateWithFactory}>
              <div className="modal-body" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                <div className="form-group" style={{ gridColumn: 'span 2' }}>
                  <label className="form-label">Tax & Department Category</label>
                  <select
                    className="form-select"
                    value={factoryFormData.productType}
                    onChange={(e) => setFactoryFormData({ ...factoryFormData, productType: e.target.value })}
                  >
                    <option value="FOOD">Food & Dairy (5% GST, Perishable)</option>
                    <option value="GROCERY">Grocery & Staples (0% GST Exempt)</option>
                    <option value="BEVERAGE">Beverages (12% GST)</option>
                    <option value="ELECTRONICS">Electronics (18% GST, Warranty)</option>
                  </select>
                </div>

                <div className="form-group" style={{ gridColumn: 'span 2' }}>
                  <label className="form-label">Product Name</label>
                  <input
                    type="text"
                    className="form-input"
                    value={factoryFormData.name}
                    onChange={(e) => setFactoryFormData({ ...factoryFormData, name: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Category</label>
                  <select
                    className="form-select"
                    value={factoryFormData.categoryId}
                    onChange={(e) => setFactoryFormData({ ...factoryFormData, categoryId: e.target.value })}
                  >
                    {categories.map(c => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Brand</label>
                  <input
                    type="text"
                    className="form-input"
                    value={factoryFormData.brand}
                    onChange={(e) => setFactoryFormData({ ...factoryFormData, brand: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Price (₹)</label>
                  <input
                    type="number"
                    step="0.01"
                    className="form-input"
                    value={factoryFormData.price}
                    onChange={(e) => setFactoryFormData({ ...factoryFormData, price: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Stock Quantity</label>
                  <input
                    type="number"
                    className="form-input"
                    value={factoryFormData.quantity}
                    onChange={(e) => setFactoryFormData({ ...factoryFormData, quantity: e.target.value })}
                    required
                  />
                </div>

                {/* Type specific inputs */}
                {factoryFormData.productType === 'ELECTRONICS' ? (
                  <div className="form-group" style={{ gridColumn: 'span 2' }}>
                    <label className="form-label">Warranty (Months)</label>
                    <input
                      type="number"
                      className="form-input"
                      value={factoryFormData.warrantyMonths}
                      onChange={(e) => setFactoryFormData({ ...factoryFormData, warrantyMonths: e.target.value })}
                    />
                  </div>
                ) : (
                  <div className="form-group" style={{ gridColumn: 'span 2' }}>
                    <label className="form-label">Shelf Life (Days)</label>
                    <input
                      type="number"
                      className="form-input"
                      value={factoryFormData.shelfLifeDays}
                      onChange={(e) => setFactoryFormData({ ...factoryFormData, shelfLifeDays: e.target.value })}
                    />
                  </div>
                )}
              </div>

              <div className="modal-footer">
                <button type="button" onClick={() => setShowFactoryModal(false)} className="btn btn-secondary btn-sm">
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary btn-sm">
                  Save Product
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* CLONE MODAL */}
      {showCloneModal && targetProduct && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ maxWidth: '480px' }}>
            <div className="modal-header">
              <div>
                <h3 style={{ fontSize: '1.15rem', fontWeight: '800' }}>Duplicate / Pack Variation</h3>
                <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                  Creating variation for: "{targetProduct.name}"
                </div>
              </div>
              <button onClick={() => setShowCloneModal(false)} style={{ border: 'none', background: 'none', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleCloneSubmit}>
              <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
                <div className="form-group">
                  <label className="form-label">Variation Name</label>
                  <input
                    type="text"
                    className="form-input"
                    value={cloneFormData.name}
                    onChange={(e) => setCloneFormData({ ...cloneFormData, name: e.target.value })}
                    required
                  />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                  <div className="form-group">
                    <label className="form-label">Adjusted Price (₹)</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-input"
                      value={cloneFormData.price}
                      onChange={(e) => setCloneFormData({ ...cloneFormData, price: e.target.value })}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Quantity</label>
                    <input
                      type="number"
                      className="form-input"
                      value={cloneFormData.quantity}
                      onChange={(e) => setCloneFormData({ ...cloneFormData, quantity: e.target.value })}
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Size / Unit Variation</label>
                  <input
                    type="text"
                    className="form-input"
                    value={cloneFormData.unit}
                    onChange={(e) => setCloneFormData({ ...cloneFormData, unit: e.target.value })}
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" onClick={() => setShowCloneModal(false)} className="btn btn-secondary btn-sm">
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary btn-sm">
                  Save Variation
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* AUDIT REPORT MODAL */}
      {showAuditModal && auditResult && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ maxWidth: '540px' }}>
            <div className="modal-header">
              <div>
                <h3 style={{ fontSize: '1.15rem', fontWeight: '800' }}>GST & Inventory Compliance Audit</h3>
                <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                  Inspecting: {auditResult.productName} ({auditResult.productType})
                </div>
              </div>
              <button onClick={() => setShowAuditModal(false)} style={{ border: 'none', background: 'none', cursor: 'pointer' }}>
                <X size={20} />
              </button>
            </div>

            <div className="modal-body">
              <div style={{ marginBottom: '16px', padding: '14px', backgroundColor: '#f0fdf4', borderRadius: '8px', border: '1px solid #bbf7d0' }}>
                <div style={{ fontWeight: '800', color: '#166534', marginBottom: '4px' }}>GST Tax Assessment:</div>
                <div style={{ fontSize: '0.88rem' }}>
                  {auditResult.taxBreakdown?.map((item, idx) => (
                    <div key={idx}>✔ {item}</div>
                  ))}
                </div>
              </div>

              <div style={{ padding: '14px', backgroundColor: '#eff6ff', borderRadius: '8px', border: '1px solid #bfdbfe' }}>
                <div style={{ fontWeight: '800', color: '#1e40af', marginBottom: '4px' }}>Inventory Audit:</div>
                <div style={{ fontSize: '0.88rem' }}>
                  {auditResult.auditLogs?.map((item, idx) => (
                    <div key={idx}>✔ {item}</div>
                  ))}
                </div>
              </div>
            </div>

            <div className="modal-footer">
              <button onClick={() => setShowAuditModal(false)} className="btn btn-primary btn-sm">
                Done
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
