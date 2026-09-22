import React from 'react';
import { ShoppingCart, Check, AlertTriangle, XCircle } from 'lucide-react';

export const ProductCard = ({ product, onAddToCart, onAuditVisitor, onCloneProduct, isAdmin }) => {
  const isOutOfStock = product.quantity <= 0 || product.status === 'OUT_OF_STOCK';
  const isLowStock = product.quantity > 0 && product.quantity <= (product.minStockThreshold || 5);

  const getBadgeClass = () => {
    if (isOutOfStock) return 'badge-out-stock';
    if (isLowStock) return 'badge-low-stock';
    return 'badge-in-stock';
  };

  const getBadgeText = () => {
    if (isOutOfStock) return 'Out of Stock';
    if (isLowStock) return `Low Stock (${product.quantity} left)`;
    return `In Stock (${product.quantity})`;
  };

  return (
    <div className="card product-card">
      <div className="product-img-wrapper">
        <img
          src={product.image || 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=500&auto=format&fit=crop&q=60'}
          alt={product.name}
          className="product-img"
          loading="lazy"
        />
        <div className="product-badge-overlay">
          <span className={`badge ${getBadgeClass()}`}>
            {isOutOfStock ? <XCircle size={12} /> : isLowStock ? <AlertTriangle size={12} /> : <Check size={12} />}
            {getBadgeText()}
          </span>
        </div>
      </div>

      <div className="product-body">
        <div className="product-category">{product.categoryName || 'General'}</div>
        <h3 className="product-title" title={product.name}>{product.name}</h3>

        <div className="product-meta">
          <span>Brand: <strong>{product.brand || 'Retail'}</strong></span>
          <span>•</span>
          <span>{product.unit || '1 Unit'}</span>
        </div>

        {/* Product Type specifics */}
        <div style={{ fontSize: '0.75rem', color: 'var(--text-light)', marginBottom: '10px' }}>
          {product.productType === 'FOOD' && product.shelfLifeDays && (
            <span>Shelf Life: {product.shelfLifeDays} days (Refrigerated)</span>
          )}
          {product.productType === 'ELECTRONICS' && product.warrantyMonths && (
            <span>Warranty: {product.warrantyMonths} Months Mfr</span>
          )}
          {product.productType === 'GROCERY' && (
            <span>0% GST Exempt Essential</span>
          )}
        </div>

        <div className="product-footer">
          <div>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>Price</div>
            <div className="product-price">₹{Number(product.price).toFixed(2)}</div>
          </div>

          <div style={{ display: 'flex', gap: '6px' }}>
            {isAdmin ? (
              <>
                <button
                  onClick={() => onCloneProduct && onCloneProduct(product)}
                  className="btn btn-secondary btn-sm"
                  title="Clone Product"
                >
                  Clone
                </button>
                <button
                  onClick={() => onAuditVisitor && onAuditVisitor(product.id)}
                  className="btn btn-secondary btn-sm"
                  title="Audit GST & Inventory"
                >
                  Audit
                </button>
              </>
            ) : (
              <button
                onClick={() => onAddToCart(product)}
                disabled={isOutOfStock}
                className="btn btn-primary btn-sm"
                style={{ opacity: isOutOfStock ? 0.6 : 1 }}
              >
                <ShoppingCart size={15} />
                {isOutOfStock ? 'Sold Out' : 'Add to Cart'}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
