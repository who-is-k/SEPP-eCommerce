import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { getOrdersByCustomer } from '../api/orderApi.js';
import { getPaymentsByCustomer } from '../api/paymentApi.js';

export default function OrdersPage() {
  const { auth } = useAuth();
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!auth) { navigate('/login'); return; }
    loadOrders();
  }, [auth]);

  const loadOrders = async () => {
    setLoading(true);
    setError('');
    try {
      const [ordersData, paymentsData] = await Promise.all([
        getOrdersByCustomer(auth.customerId),
        getPaymentsByCustomer(auth.customerId),
      ]);
      // Sort newest first
      ordersData.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      setOrders(ordersData);
      setPayments(paymentsData);
    } catch (err) {
      setError(err.message || 'Unable to load orders. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // Map orderId -> payment status
  const paymentMap = payments.reduce((acc, p) => {
    acc[p.orderId] = p;
    return acc;
  }, {});

  const formatPrice = (price) => `RM ${Number(price ?? 0).toFixed(2)}`;
  const formatDate  = (ts) => ts ? new Date(ts).toLocaleString() : '—';

  const statusClass = (status) => {
    if (status === 'PAID' || status === 'SHIPPED') return 'status-paid';
    if (status === 'FAILED') return 'status-failed';
    return 'status-pending';
  };

  const paymentStatusClass = (status) => {
    if (status === 'SUCCESS') return 'status-paid';
    if (status === 'FAILED') return 'status-failed';
    return 'status-pending';
  };

  if (loading) {
    return (
      <div className="orders-page">
        <h1>My Orders</h1>
        <div className="loading-list">
          {[1,2,3].map(i => <div key={i} className="order-card skeleton" />)}
        </div>
      </div>
    );
  }

  return (
    <div className="orders-page">
      <div className="page-header">
        <h1>My Orders</h1>
        <button className="btn-ghost" onClick={loadOrders} id="refresh-orders-btn">↻ Refresh</button>
      </div>

      {error && (
        <div className="alert alert-error" role="alert">
          {error}
          <button className="btn-ghost btn-sm" onClick={loadOrders}>Retry</button>
        </div>
      )}

      {!error && orders.length === 0 && (
        <div className="empty-state">
          <div className="empty-icon">📋</div>
          <h2>No orders yet</h2>
          <p>Start shopping to see your orders here.</p>
          <Link to="/products" className="btn-primary">Shop Now</Link>
        </div>
      )}

      <div className="orders-list">
        {orders.map(order => {
          const pmt = paymentMap[order.id];
          return (
            <div key={order.id} className="order-card" id={`order-${order.id}`}>
              <div className="order-card-header">
                <div>
                  <span className="order-id">Order #{order.id}</span>
                  <span className="order-date">{formatDate(order.createdAt)}</span>
                </div>
                <div className="order-card-statuses">
                  <span className={`status-badge ${statusClass(order.status)}`}>
                    Order: {order.status}
                  </span>
                  {pmt && (
                    <span className={`status-badge ${paymentStatusClass(pmt.paymentStatus)}`}>
                      Payment: {pmt.paymentStatus}
                    </span>
                  )}
                </div>
              </div>

              <div className="order-lines">
                {order.lines?.map(line => (
                  <div key={line.id} className="order-line">
                    <span>Product {line.productId}</span>
                    <span>× {line.quantity}</span>
                    <span>{formatPrice(line.lineTotal)}</span>
                  </div>
                ))}
              </div>

              <div className="order-card-footer">
                <span className="order-total">
                  Total: <strong>{formatPrice(order.totalAmount)}</strong>
                </span>
                <Link
                  to={`/confirmation/${order.id}`}
                  className="btn-ghost btn-sm"
                  id={`view-order-${order.id}`}
                >
                  View Details →
                </Link>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
