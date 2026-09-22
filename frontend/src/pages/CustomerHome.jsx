import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { productAPI, categoryAPI } from '../services/api';
import { ProductCard } from '../components/ProductCard';
import {
  ShoppingBag,
  Sparkles,
  ShieldCheck,
  TrendingUp,
  Tag,
  ArrowRight,
  Truck,
  Percent
} from 'lucide-react';

export const CustomerHome = ({ onAddToCart }) => {
  const [categories, setCategories] = useState([]);
  const [featuredProducts, setFeaturedProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([
      categoryAPI.getAll(),
      productAPI.getAll(false),
    ]).then(([catRes, prodRes]) => {
      setCategories(catRes.data);
      // Pick top 8 products
      setFeaturedProducts(prodRes.data.slice(0, 8));
      setLoading(false);
    }).catch(err => {
      console.error(err);
      setLoading(false);
    });
  }, []);

  return (
    <div className="content-wrapper">
      {/* Hero Banner */}
      <div style={{
        background: 'linear-gradient(135deg, #1e1b4b 0%, #312e81 50%, #4338ca 100%)',
        borderRadius: 'var(--radius-lg)',
        color: '#ffffff',
        padding: '48px 40px',
        marginBottom: '40px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        position: 'relative',
        overflow: 'hidden',
        boxShadow: 'var(--shadow-lg)'
      }}>
        <div style={{ maxWidth: '640px', zIndex: 1 }}>
          <div style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '6px',
            backgroundColor: 'rgba(255, 255, 255, 0.15)',
            padding: '6px 14px',
            borderRadius: '999px',
            fontSize: '0.82rem',
            fontWeight: '700',
            marginBottom: '16px',
            backdropFilter: 'blur(8px)'
          }}>
            <Sparkles size={15} color="#fbbf24" />
            <span>Grand Festive Sale • Up to 20% Instant Savings</span>
          </div>

          <h1 style={{ fontSize: '2.4rem', fontWeight: '800', lineHeight: 1.2, marginBottom: '14px', letterSpacing: '-0.8px' }}>
            Smart Retail Management
          </h1>
          <p style={{ fontSize: '1.05rem', color: '#e0e7ff', marginBottom: '24px', lineHeight: 1.6 }}>
            Experience intelligent supermarket shopping with real-time stock tracking, dynamic discount strategies, and seamless multi-channel checkout.
          </p>

          <div style={{ display: 'flex', gap: '14px', flexWrap: 'wrap' }}>
            <Link to="/products" className="btn btn-primary btn-lg" style={{ backgroundColor: '#ffffff', color: '#312e81', border: 'none' }}>
              <ShoppingBag size={18} />
              Browse All Products
            </Link>
            <Link to="/categories" className="btn btn-secondary btn-lg" style={{ backgroundColor: 'rgba(255, 255, 255, 0.15)', color: '#ffffff', border: '1px solid rgba(255,255,255,0.3)' }}>
              Explore Categories
            </Link>
          </div>
        </div>

        <div style={{ display: 'none', md: 'block', zIndex: 1 }}>
          <div style={{
            background: 'rgba(255, 255, 255, 0.1)',
            backdropFilter: 'blur(12px)',
            border: '1px solid rgba(255, 255, 255, 0.2)',
            borderRadius: 'var(--radius-lg)',
            padding: '24px',
            width: '280px'
          }}>
            <div style={{ fontWeight: '800', fontSize: '1.1rem', marginBottom: '12px' }}>Active Promo Codes</div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '0.85rem' }}>
              <div style={{ backgroundColor: 'rgba(0,0,0,0.2)', padding: '8px 12px', borderRadius: '6px' }}>
                <strong>SAVE10</strong>: 10% Off Cart &gt; ₹200
              </div>
              <div style={{ backgroundColor: 'rgba(0,0,0,0.2)', padding: '8px 12px', borderRadius: '6px' }}>
                <strong>FESTIVAL20</strong>: 20% Festive Special
              </div>
              <div style={{ backgroundColor: 'rgba(0,0,0,0.2)', padding: '8px 12px', borderRadius: '6px' }}>
                <strong>WELCOME50</strong>: Flat ₹50 Rebate
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Feature Highlights */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '20px', marginBottom: '40px' }}>
        <div className="card" style={{ padding: '20px', display: 'flex', alignItems: 'center', gap: '14px' }}>
          <div style={{ padding: '12px', backgroundColor: '#ecfdf5', color: '#059669', borderRadius: '12px' }}>
            <Truck size={24} />
          </div>
          <div>
            <div style={{ fontWeight: '700', fontSize: '0.95rem' }}>Express Dispatch</div>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Same day packaging and fulfillment</div>
          </div>
        </div>

        <div className="card" style={{ padding: '20px', display: 'flex', alignItems: 'center', gap: '14px' }}>
          <div style={{ padding: '12px', backgroundColor: '#eef2ff', color: 'var(--primary)', borderRadius: '12px' }}>
            <Percent size={24} />
          </div>
          <div>
            <div style={{ fontWeight: '700', fontSize: '0.95rem' }}>Stackable Discounts</div>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Membership & coupon promotional savings</div>
          </div>
        </div>

        <div className="card" style={{ padding: '20px', display: 'flex', alignItems: 'center', gap: '14px' }}>
          <div style={{ padding: '12px', backgroundColor: '#fef3c7', color: '#d97706', borderRadius: '12px' }}>
            <ShieldCheck size={24} />
          </div>
          <div>
            <div style={{ fontWeight: '700', fontSize: '0.95rem' }}>Verified Quality</div>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Stock threshold & shelf-life audit</div>
          </div>
        </div>
      </div>

      {/* Categories Grid */}
      <div style={{ marginBottom: '40px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '18px' }}>
          <div>
            <h2 style={{ fontSize: '1.4rem', fontWeight: '800', color: 'var(--secondary)' }}>Shop by Category</h2>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Explore our structured catalog hierarchy</p>
          </div>
          <Link to="/categories" style={{ display: 'flex', alignItems: 'center', gap: '4px', color: 'var(--primary)', fontWeight: '700', fontSize: '0.9rem', textDecoration: 'none' }}>
            View All <ArrowRight size={16} />
          </Link>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))', gap: '16px' }}>
          {categories.map((cat) => (
            <div
              key={cat.id}
              onClick={() => navigate(`/products?category=${cat.id}`)}
              className="card"
              style={{ padding: '18px', textAlign: 'center', cursor: 'pointer', transition: 'all 0.2s' }}
            >
              <div style={{
                width: '48px',
                height: '48px',
                margin: '0 auto 12px auto',
                backgroundColor: 'var(--primary-light)',
                color: 'var(--primary)',
                borderRadius: '14px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                <ShoppingBag size={22} />
              </div>
              <h4 style={{ fontSize: '0.95rem', fontWeight: '700', marginBottom: '4px' }}>{cat.name}</h4>
              <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{cat.description || 'View Products'}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Featured Products */}
      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '18px' }}>
          <div>
            <h2 style={{ fontSize: '1.4rem', fontWeight: '800', color: 'var(--secondary)' }}>Trending Essentials</h2>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Top picks in dairy, pantry, snacks, and personal care</p>
          </div>
          <Link to="/products" style={{ display: 'flex', alignItems: 'center', gap: '4px', color: 'var(--primary)', fontWeight: '700', fontSize: '0.9rem', textDecoration: 'none' }}>
            See All Items <ArrowRight size={16} />
          </Link>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px' }}>Loading products...</div>
        ) : (
          <div className="product-grid">
            {featuredProducts.map(product => (
              <ProductCard
                key={product.id}
                product={product}
                onAddToCart={onAddToCart}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
