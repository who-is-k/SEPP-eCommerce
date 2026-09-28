import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext.jsx';
import { useAuth } from '../context/AuthContext.jsx';

export default function CartPage() {
  const { items, removeItem, updateQty, total, clearCart } = useCart();
  const { auth } = useAuth();
  const navigate = useNavigate();

  const formatPrice = (price) => `RM ${Number(price).toFixed(2)}`;

  if (items.length === 0) {
    return (
      <div className="cart-page">
        <h1>Shopping Cart</h1>
        <div className="empty-state">
          <div className="empty-icon">🛒</div>
          <h2>Your cart is empty</h2>
          <p>Add some products to get started.</p>
          <button className="btn-primary" onClick={() => navigate('/products')}>
            Browse Products
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="cart-page">
      <h1>Shopping Cart</h1>

      <div className="cart-layout">
        <div className="cart-items">
          {items.map(item => (
            <div key={item.productId} className="cart-item" id={`cart-item-${item.productId}`}>
              <div className="cart-item-info">
                <div className="cart-item-name">{item.name}</div>
                <div className="cart-item-price">
                  {formatPrice(item.price)} each
                </div>
              </div>
              <div className="cart-item-controls">
                <button
                  id={`qty-dec-${item.productId}`}
                  className="qty-btn"
                  onClick={() => updateQty(item.productId, item.quantity - 1)}
                  disabled={item.quantity <= 1}
                >−</button>
                <span className="qty-value" id={`qty-${item.productId}`}>{item.quantity}</span>
                <button
                  id={`qty-inc-${item.productId}`}
                  className="qty-btn"
                  onClick={() => updateQty(item.productId, item.quantity + 1)}
                >+</button>
              </div>
              <div className="cart-item-subtotal">
                {formatPrice(item.price * item.quantity)}
              </div>
              <button
                id={`remove-${item.productId}`}
                className="btn-remove"
                onClick={() => removeItem(item.productId)}
                title="Remove item"
              >✕</button>
            </div>
          ))}
        </div>

        <div className="cart-summary">
          <h2>Order Summary</h2>
          <div className="summary-rows">
            {items.map(item => (
              <div key={item.productId} className="summary-row">
                <span>{item.name} × {item.quantity}</span>
                <span>{formatPrice(item.price * item.quantity)}</span>
              </div>
            ))}
          </div>
          <div className="summary-divider" />
          <div className="summary-total">
            <span>Total</span>
            <span id="cart-total">{formatPrice(total)}</span>
          </div>

          <button
            id="checkout-btn"
            className="btn-primary btn-full"
            onClick={() => {
              if (!auth) { navigate('/login'); return; }
              navigate('/checkout');
            }}
          >
            Proceed to Checkout
          </button>
          <button className="btn-ghost btn-full" onClick={clearCart} id="clear-cart-btn">
            Clear Cart
          </button>
        </div>
      </div>
    </div>
  );
}
