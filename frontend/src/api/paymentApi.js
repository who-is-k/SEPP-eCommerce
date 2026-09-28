import { PAYMENT_API } from '../config.js';

/**
 * GET /api/payments/order/{orderId}
 * Returns the payment record for a given order.
 * PaymentResponse: { paymentId, orderId, customerId, amount, paymentMethod, paymentStatus, transactionId, paymentDate }
 */
export async function getPaymentByOrderId(orderId) {
  const res = await fetch(`${PAYMENT_API}/api/payments/order/${orderId}`);
  if (!res.ok) {
    if (res.status === 404) return null; // Payment not yet created (timing)
    throw new Error(`Unable to load payment status (${res.status}).`);
  }
  return res.json();
}

/**
 * GET /api/payments/customer/{customerId}
 * Returns all payments for a customer.
 */
export async function getPaymentsByCustomer(customerId) {
  const res = await fetch(`${PAYMENT_API}/api/payments/customer/${customerId}`);
  if (!res.ok) return []; // gracefully return empty
  return res.json();
}
