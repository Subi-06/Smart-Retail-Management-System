import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { productAPI, categoryAPI } from '../services/api';
import { ProductCard } from '../components/ProductCard';
import { Search, Filter, ArrowUpDown, Check, RefreshCw } from 'lucide-react';

export const ProductCatalog = ({ onAddToCart }) => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  const [selectedCategory, setSelectedCategory] = useState(searchParams.get('category') || 'ALL');
  const [searchQuery, setSearchQuery] = useState(searchParams.get('search') || '');
  const [inStockOnly, setInStockOnly] = useState(false);
  const [sortBy, setSortBy] = useState('featured');

  useEffect(() => {
    categoryAPI.getAll().then(res => setCategories(res.data)).catch(() => {});
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [selectedCategory, inStockOnly]);

  useEffect(() => {
    const urlQuery = searchParams.get('search');
    const urlCat = searchParams.get('category');
    if (urlQuery !== null) setSearchQuery(urlQuery);
    if (urlCat !== null) setSelectedCategory(urlCat);
  }, [searchParams]);

  const fetchProducts = async () => {
    setLoading(true);
    try {
      let res;
      if (selectedCategory && selectedCategory !== 'ALL') {
        res = await productAPI.getByCategory(selectedCategory);
      } else {
        // Calls backend endpoint utilizing InStockProductIterator when inStockOnly is checked!
        res = await productAPI.getAll(inStockOnly);
      }
      setProducts(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      setLoading(true);
      productAPI.search(searchQuery.trim())
        .then(res => setProducts(res.data))
        .finally(() => setLoading(false));
    } else {
      fetchProducts();
    }
  };

  // Filter & Sort
  const filteredAndSortedProducts = [...products]
    .filter(p => {
      if (!searchQuery.trim()) return true;
      const q = searchQuery.toLowerCase();
      return p.name.toLowerCase().includes(q) ||
             (p.brand && p.brand.toLowerCase().includes(q)) ||
             (p.description && p.description.toLowerCase().includes(q));
    })
    .sort((a, b) => {
      if (sortBy === 'price-low') return Number(a.price) - Number(b.price);
      if (sortBy === 'price-high') return Number(b.price) - Number(a.price);
      if (sortBy === 'name') return a.name.localeCompare(b.name);
      return 0; // featured/default
    });

  return (
    <div className="content-wrapper">
      {/* Header & Controls */}
      <div style={{ marginBottom: '28px' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: '800', color: 'var(--secondary)', marginBottom: '6px' }}>
          Product Catalog
        </h1>
        <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
          Browse fresh groceries, dairy, pantry staples, beverages, and electronics.
        </p>

        {/* Search & Filters Toolbar */}
        <div style={{
          display: 'flex',
          gap: '14px',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          marginTop: '20px',
          padding: '16px',
          backgroundColor: '#ffffff',
          borderRadius: 'var(--radius-lg)',
          border: '1px solid var(--border-color)',
          boxShadow: 'var(--shadow-sm)'
        }}>
          {/* Search */}
          <form onSubmit={handleSearchSubmit} style={{ flex: '1', minWidth: '280px', position: 'relative' }}>
            <Search size={16} className="search-icon" />
            <input
              type="text"
              className="search-input"
              placeholder="Filter by product name, brand, or ingredients..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </form>

          {/* Controls: In-Stock Toggle & Sort */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '14px', flexWrap: 'wrap' }}>
            {/* Iterator Pattern Demonstration Toggle */}
            <label style={{
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              fontSize: '0.85rem',
              fontWeight: '600',
              cursor: 'pointer',
              userSelect: 'none',
              backgroundColor: inStockOnly ? 'var(--primary-light)' : '#f8fafc',
              color: inStockOnly ? 'var(--primary)' : 'var(--text-main)',
              padding: '8px 14px',
              borderRadius: 'var(--radius-md)',
              border: '1px solid var(--border-color)'
            }}>
              <input
                type="checkbox"
                checked={inStockOnly}
                onChange={(e) => setInStockOnly(e.target.checked)}
                style={{ accentColor: 'var(--primary)' }}
              />
              <span>In Stock Only</span>
            </label>

            {/* Sort Dropdown */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <ArrowUpDown size={16} color="var(--text-muted)" />
              <select
                className="form-select"
                style={{ padding: '8px 12px', width: 'auto' }}
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
              >
                <option value="featured">Sort: Featured</option>
                <option value="price-low">Price: Low to High</option>
                <option value="price-high">Price: High to Low</option>
                <option value="name">Name: A to Z</option>
              </select>
            </div>
          </div>
        </div>

        {/* Category Filter Pills */}
        <div style={{ display: 'flex', gap: '8px', overflowX: 'auto', padding: '14px 0 6px 0' }}>
          <button
            onClick={() => setSelectedCategory('ALL')}
            className={`btn btn-sm ${selectedCategory === 'ALL' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ borderRadius: '20px', padding: '6px 16px' }}
          >
            All Items ({products.length})
          </button>
          {categories.map(cat => (
            <button
              key={cat.id}
              onClick={() => setSelectedCategory(String(cat.id))}
              className={`btn btn-sm ${selectedCategory === String(cat.id) ? 'btn-primary' : 'btn-secondary'}`}
              style={{ borderRadius: '20px', padding: '6px 16px' }}
            >
              {cat.name}
            </button>
          ))}
        </div>
      </div>

      {/* Product Grid */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px 0', color: 'var(--text-muted)' }}>
          <RefreshCw className="spin" size={24} style={{ marginBottom: '8px' }} />
          <div>Loading products...</div>
        </div>
      ) : filteredAndSortedProducts.length === 0 ? (
        <div className="card" style={{ padding: '60px 20px', textAlign: 'center' }}>
          <h3 style={{ fontSize: '1.2rem', fontWeight: '700', marginBottom: '8px' }}>No products found</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '16px' }}>
            Try adjusting your search criteria or resetting category filters.
          </p>
          <button onClick={() => { setSelectedCategory('ALL'); setSearchQuery(''); setInStockOnly(false); }} className="btn btn-secondary btn-sm">
            Reset Filters
          </button>
        </div>
      ) : (
        <div className="product-grid">
          {filteredAndSortedProducts.map(product => (
            <ProductCard
              key={product.id}
              product={product}
              onAddToCart={onAddToCart}
            />
          ))}
        </div>
      )}
    </div>
  );
};
