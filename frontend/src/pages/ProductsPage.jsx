import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllProducts, searchProducts } from '../api/productApi.js';
import { useCart } from '../context/CartContext.jsx';
import { useAuth } from '../context/AuthContext.jsx';

export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [addedId, setAddedId] = useState(null);
  const { addItem } = useCart();
  const { auth } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await getAllProducts();
      setProducts(data);
    } catch (err) {
      setError(err.message || 'Unable to load products. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!search.trim()) { loadProducts(); return; }
    setLoading(true);
    setError('');
    try {
      const data = await searchProducts(search.trim());
      setProducts(data);
    } catch (err) {
      setError(err.message || 'Search failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = (product) => {
    if (!auth) { navigate('/login'); return; }
    addItem(product);
    setAddedId(product.productId);
    setTimeout(() => setAddedId(null), 1500);
  };

  const formatPrice = (price) =>
    `RM ${Number(price).toFixed(2)}`;

  const getStockLabel = (product) => {
    if (product.stockStatus === 'OUT_OF_STOCK' || product.stockQuantity === 0)
      return { label: 'Out of Stock', cls: 'out-of-stock' };
    if (product.stockStatus === 'LOW_STOCK' || product.stockQuantity <= 10)
      return { label: `Low Stock (${product.stockQuantity} left)`, cls: 'low-stock' };
    return { label: 'In Stock', cls: 'in-stock' };
  };

  return (
    <div className="products-page">
      <div className="page-header">
        <h1>Product Catalogue</h1>
        <p>Find the perfect product for you</p>
      </div>

      {/* Search */}
      <form onSubmit={handleSearch} className="search-bar" id="search-form">
        <input
          id="search-input"
          type="search"
          placeholder="Search products..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
        <button id="search-btn" type="submit" className="btn-primary">Search</button>
        {search && (
          <button type="button" className="btn-ghost" onClick={() => { setSearch(''); loadProducts(); }}>
            Clear
          </button>
        )}
      </form>

      {error && (
        <div className="alert alert-error" role="alert">
          {error}
          <button className="btn-ghost btn-sm" onClick={loadProducts}>Retry</button>
        </div>
      )}

      {loading && (
        <div className="loading-grid">
          {[1, 2, 3, 4, 5, 6].map(i => (
            <div key={i} className="product-card skeleton" />
          ))}
        </div>
      )}

      {!loading && !error && products.length === 0 && (
        <div className="empty-state">
          <div className="empty-icon">📦</div>
          <h2>No products found</h2>
          <p>Try a different search term or check back later.</p>
          <button className="btn-primary" onClick={() => { setSearch(''); loadProducts(); }}>
            View All Products
          </button>
        </div>
      )}

      {!loading && products.length > 0 && (
        <div className="product-grid">
          {products.map(product => {
            const stock = getStockLabel(product);
            const outOfStock = stock.cls === 'out-of-stock';
            return (
              <div key={product.productId} className="product-card" id={`product-${product.productId}`}>
                <div className="product-image">
                  <span className="product-emoji">
                    {product.categoryName?.toLowerCase().includes('wearable') ? '⌚' :
                      product.categoryName?.toLowerCase().includes('accessory') || product.categoryName?.toLowerCase().includes('accessories') ? '🔌' :
                        product.name?.toLowerCase().includes('headphone') ? '🎧' :
                          product.name?.toLowerCase().includes('earbud') ? '🎵' :
                            product.name?.toLowerCase().includes('camera') ? '📷' :
                              product.name?.toLowerCase().includes('lamp') ? '💡' : '📱'}
                  </span>
                </div>
                <div className="product-info">
                  <div className="product-category">{product.categoryName}</div>
                  <h2 className="product-name">{product.name}</h2>
                  <p className="product-description">{product.description}</p>
                  <div className="product-footer">
                    <div className="product-price">{formatPrice(product.price)}</div>
                    <span className={`stock-badge ${stock.cls}`}>{stock.label}</span>
                  </div>
                  <button
                    id={`add-to-cart-${product.productId}`}
                    className={`btn-add-cart ${addedId === product.productId ? 'added' : ''}`}
                    onClick={() => handleAddToCart(product)}
                    disabled={outOfStock}
                  >
                    {outOfStock ? 'Out of Stock' :
                      addedId === product.productId ? '✓ Added!' : '+ Add to Cart'}
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
