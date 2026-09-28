import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { createOrder } from '../api/orderApi.js';

export default function CheckoutPage() {
  const { items, total, clearCart } = useCart();
  const { auth } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('CREDIT_CARD');

  const formatPrice = (price) => `RM ${Number(price).toFixed(2)}`;

  if (!auth) {
    navigate('/login');
    return null;
  }

  if (items.length === 0) {
    navigate('/products');
    return null;
  }

  const handlePlaceOrder = async () => {
    setError('');
    setLoading(true);
    try {
      // POST /api/orders with customerId, lines[{ productId (String), quantity }], and paymentMethod
      const order = await createOrder(auth.customerId, items, paymentMethod);
      clearCart();
      navigate(`/confirmation/${order.id}`);
    } catch (err) {
      setError(err.message || 'Failed to place order. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="checkout-page">
      <h1>Checkout</h1>

      <div className="checkout-layout">
        {/* Order Items */}
        <div className="checkout-items">
          <h2>Order Items</h2>
          <div className="checkout-item-list">
            {items.map(item => (
              <div key={item.productId} className="checkout-item" id={`checkout-item-${item.productId}`}>
                <div className="checkout-item-name">{item.name}</div>
                <div className="checkout-item-meta">
                  <span>Qty: {item.quantity}</span>
                  <span>× {formatPrice(item.price)}</span>
                  <span className="checkout-item-subtotal">{formatPrice(item.price * item.quantity)}</span>
                </div>
              </div>
            ))}
          </div>

          <div className="checkout-total-row">
            <span>Order Total</span>
            <span id="checkout-total" className="checkout-total-amount">{formatPrice(total)}</span>
          </div>
        </div>

        {/* Customer & Payment Info */}
        <div className="checkout-sidebar">
          <div className="checkout-customer">
            <h2>Customer</h2>
            <div className="info-row"><span>Name</span><span>{auth.fullName}</span></div>
            <div className="info-row"><span>Email</span><span>{auth.email}</span></div>
          </div>

          <div className="checkout-payment-info">
            <h2>Payment</h2>
            <div className="form-group" style={{ marginBottom: '1rem' }}>
              <label htmlFor="paymentMethod" style={{ display: 'block', marginBottom: '0.5rem' }}>Select Payment Method</label>
              <select
                id="paymentMethod"
                value={paymentMethod}
                onChange={e => setPaymentMethod(e.target.value)}
                style={{ width: '100%', padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' }}
              >
                <option value="CREDIT_CARD">Credit Card</option>
                <option value="DEBIT_CARD">Debit Card</option>
                <option value="ONLINE_BANKING">Online Banking</option>
                <option value="E_WALLET">E-Wallet</option>
              </select>
            </div>
            <p className="payment-note">
              Payment will be processed automatically after order placement via our secure payment gateway.
            </p>
          </div>

          {error && <div className="alert alert-error" role="alert">{error}</div>}

          <button
            id="place-order-btn"
            className="btn-primary btn-full btn-large"
            onClick={handlePlaceOrder}
            disabled={loading}
          >
            {loading ? 'Placing Order...' : '✓ Place Order'}
          </button>
          <button
            className="btn-ghost btn-full"
            onClick={() => navigate('/cart')}
            disabled={loading}
            id="back-to-cart-btn"
          >
            ← Back to Cart
          </button>
        </div>
      </div>
    </div>
  );
}
