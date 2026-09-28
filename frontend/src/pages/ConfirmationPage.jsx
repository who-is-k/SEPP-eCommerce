import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getOrder } from '../api/orderApi.js';
import { getPaymentByOrderId } from '../api/paymentApi.js';

const POLL_INTERVAL_MS = 2000;  // poll every 2 seconds
const MAX_POLLS = 15;            // stop after 30 seconds

export default function ConfirmationPage() {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [payment, setPayment] = useState(null);
  const [error, setError] = useState('');
  const [pollCount, setPollCount] = useState(0);
  const [done, setDone] = useState(false);

  const isTerminalOrderStatus = (s) => s === 'PAID' || s === 'FAILED' || s === 'SHIPPED';
  const isTerminalPaymentStatus = (s) => s === 'SUCCESS' || s === 'FAILED';

  const fetchStatus = useCallback(async () => {
    try {
      const [orderData, paymentData] = await Promise.all([
        getOrder(orderId),
        getPaymentByOrderId(orderId),
      ]);
      setOrder(orderData);
      setPayment(paymentData);

      // Stop polling when we have a final state for both
      if (
        isTerminalOrderStatus(orderData?.status) &&
        (!paymentData || isTerminalPaymentStatus(paymentData?.paymentStatus))
      ) {
        setDone(true);
      }
    } catch (err) {
      setError(err.message || 'Unable to fetch order status.');
      setDone(true);
    }
  }, [orderId]);

  // Initial fetch + polling
  useEffect(() => {
    fetchStatus();
  }, [fetchStatus]);

  useEffect(() => {
    if (done) return;
    if (pollCount >= MAX_POLLS) { setDone(true); return; }
    const timer = setTimeout(async () => {
      await fetchStatus();
      setPollCount(c => c + 1);
    }, POLL_INTERVAL_MS);
    return () => clearTimeout(timer);
  }, [pollCount, done, fetchStatus]);

  const formatPrice = (price) => `RM ${Number(price ?? 0).toFixed(2)}`;
  const formatDate = (ts) => ts ? new Date(ts).toLocaleString() : '—';

  const orderStatusIcon = (s) => {
    if (s === 'PAID' || s === 'SHIPPED') return '✅';
    if (s === 'FAILED') return '❌';
    return '⏳';
  };

  const paymentStatusIcon = (s) => {
    if (s === 'SUCCESS') return '✅';
    if (s === 'FAILED') return '❌';
    return '⏳';
  };

  if (!order && !error) {
    return (
      <div className="confirmation-page">
        <div className="confirmation-card">
          <div className="spinner" />
          <p>Loading your order...</p>
        </div>
      </div>
    );
  }

  if (error && !order) {
    return (
      <div className="confirmation-page">
        <div className="confirmation-card">
          <div className="conf-icon error">❌</div>
          <h1>Something went wrong</h1>
          <div className="alert alert-error">{error}</div>
          <Link to="/orders" className="btn-primary">View My Orders</Link>
        </div>
      </div>
    );
  }

  const isPaid = order?.status === 'PAID' || order?.status === 'SHIPPED' || payment?.paymentStatus === 'SUCCESS';
  const isFailed = order?.status === 'FAILED' || payment?.paymentStatus === 'FAILED';
  const isPending = !isPaid && !isFailed;

  return (
    <div className="confirmation-page">
      <div className="confirmation-card">
        {/* Header */}
        <div className={`conf-icon ${isPaid ? 'success' : isFailed ? 'error' : 'pending'}`}>
          {isPaid ? '✅' : isFailed ? '❌' : '⏳'}
        </div>
        <h1 id="confirmation-heading">
          {isPaid ? 'Order & Payment Confirmed!' :
            isFailed ? 'Payment Failed' :
              'Processing Order...'}
        </h1>
        {isPending && !done && (
          <p className="processing-note">
            <span className="spinner-small" /> Your payment is being processed. Please wait...
          </p>
        )}
        {isPending && done && (
          <p className="processing-note">
            Payment is taking longer than usual. Check <Link to="/orders">My Orders</Link> for updates.
          </p>
        )}

        {/* Order Details */}
        <div className="conf-details">
          <div className="conf-row">
            <span>Order ID</span>
            <span id="conf-order-id"><strong>#{order?.id}</strong></span>
          </div>
          <div className="conf-row">
            <span>Order Status</span>
            <span id="conf-order-status">
              {orderStatusIcon(order?.status)} <strong>{order?.status}</strong>
            </span>
          </div>
          <div className="conf-row">
            <span>Payment Status</span>
            <span id="conf-payment-status">
              {payment
                ? <>{paymentStatusIcon(payment.paymentStatus)} <strong>{payment.paymentStatus}</strong></>
                : <span className="text-muted">⏳ Pending</span>
              }
            </span>
          </div>
          {payment?.paymentMethod && (
            <div className="conf-row">
              <span>Payment Method</span>
              <span>{payment.paymentMethod.replace('_', ' ')}</span>
            </div>
          )}
          {payment?.transactionId && (
            <div className="conf-row">
              <span>Transaction ID</span>
              <span className="mono">{payment.transactionId}</span>
            </div>
          )}
          <div className="conf-row">
            <span>Order Date</span>
            <span>{formatDate(order?.createdAt)}</span>
          </div>
          <div className="conf-row conf-total">
            <span>Total Amount</span>
            <span id="conf-total"><strong>{formatPrice(order?.totalAmount)}</strong></span>
          </div>
        </div>

        {/* Order Lines */}
        {order?.lines && order.lines.length > 0 && (
          <div className="conf-items">
            <h3>Items Ordered</h3>
            {order.lines.map(line => (
              <div key={line.id} className="conf-item">
                <span className="conf-item-id">Product {line.productId}</span>
                <span>× {line.quantity}</span>
                <span>{formatPrice(line.lineTotal)}</span>
              </div>
            ))}
          </div>
        )}

        {isPaid && (
          <p className="thank-you">Thank you for your purchase! 🎉</p>
        )}

        <div className="conf-actions">
          <Link to="/orders" className="btn-primary" id="view-orders-btn">View My Orders</Link>
          <Link to="/products" className="btn-ghost" id="continue-shopping-btn">Continue Shopping</Link>
        </div>
      </div>
    </div>
  );
}
