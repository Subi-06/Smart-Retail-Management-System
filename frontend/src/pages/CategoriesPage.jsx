import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { categoryAPI } from '../services/api';
import { FolderTree, ShoppingBag, Layers, ArrowRight, IndianRupee, Package } from 'lucide-react';

export const CategoriesPage = () => {
  const [categories, setCategories] = useState([]);
  const [compositeData, setCompositeData] = useState(null);
  const [showHierarchy, setShowHierarchy] = useState(false);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([
      categoryAPI.getAll(),
      categoryAPI.getCompositeHierarchy(),
    ]).then(([catRes, compRes]) => {
      setCategories(catRes.data);
      setCompositeData(compRes.data);
      setLoading(false);
    }).catch(() => setLoading(false));
  }, []);

  return (
    <div className="content-wrapper">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '28px', flexWrap: 'wrap', gap: '14px' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', fontWeight: '800', color: 'var(--secondary)', marginBottom: '6px' }}>
            Store Categories
          </h1>
          <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
            Browse our complete store catalog departments and inventory.
          </p>
        </div>

        <button
          onClick={() => setShowHierarchy(!showHierarchy)}
          className="btn btn-secondary btn-sm"
          style={{ borderColor: 'var(--primary)', color: 'var(--primary)', gap: '6px' }}
        >
          <FolderTree size={16} />
          {showHierarchy ? "Hide Category Structure" : "View Category Structure"}
        </button>
      </div>

      {/* Composite Pattern Valuation Card */}
      {compositeData && (
        <div className="card" style={{ padding: '20px', marginBottom: '32px', backgroundColor: '#f8fafc', borderLeft: '4px solid var(--primary)' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>
            <div>
              <div style={{ fontSize: '0.75rem', fontWeight: '700', textTransform: 'uppercase', color: 'var(--primary)' }}>
                Store Inventory & Valuation
              </div>
              <h3 style={{ fontSize: '1.1rem', fontWeight: '800', marginTop: '2px' }}>{compositeData.catalogName}</h3>
            </div>
            <div style={{ display: 'flex', gap: '24px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <Package size={22} color="var(--primary)" />
                <div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Total Inventory</div>
                  <div style={{ fontSize: '1.1rem', fontWeight: '800' }}>{compositeData.totalInventoryItems} items</div>
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <IndianRupee size={22} color="#059669" />
                <div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Catalog Valuation</div>
                  <div style={{ fontSize: '1.1rem', fontWeight: '800', color: '#059669' }}>
                    ₹{Number(compositeData.totalStockValuation).toFixed(2)}
                  </div>
                </div>
              </div>
            </div>
          </div>

          {showHierarchy && (
            <div style={{ marginTop: '16px', paddingTop: '16px', borderTop: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: '0.8rem', fontWeight: '700', marginBottom: '8px' }}>Category Hierarchy:</div>
              <pre style={{
                backgroundColor: '#1e293b',
                color: '#38bdf8',
                padding: '14px',
                borderRadius: '8px',
                fontSize: '0.8rem',
                overflowX: 'auto',
                fontFamily: 'monospace'
              }}>
                {compositeData.hierarchyText}
              </pre>
            </div>
          )}
        </div>
      )}

      {/* Category Cards Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '22px' }}>
        {categories.map((category) => (
          <div
            key={category.id}
            className="card"
            style={{ padding: '24px', display: 'flex', flexDirection: 'column', height: '100%' }}
          >
            <div style={{
              width: '52px',
              height: '52px',
              backgroundColor: 'var(--primary-light)',
              color: 'var(--primary)',
              borderRadius: '14px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginBottom: '16px'
            }}>
              <ShoppingBag size={24} />
            </div>

            <h3 style={{ fontSize: '1.15rem', fontWeight: '800', marginBottom: '6px' }}>{category.name}</h3>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '20px', flex: '1' }}>
              {category.description || 'Essential retail items tailored for high quality and everyday convenience.'}
            </p>

            <button
              onClick={() => navigate(`/products?category=${category.id}`)}
              className="btn btn-secondary btn-sm"
              style={{ width: '100%', justifyContent: 'space-between' }}
            >
              <span>Explore Products</span>
              <ArrowRight size={15} />
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};
