import { ORDER_API } from '../config.js';

/**
 * POST /api/orders
 * Create a new order.
 * Body: { customerId: Long, lines: [{ productId: String, quantity: Int }] }
 * Returns: OrderResponse { id, customerId, lines, totalAmount, status, createdAt }
 */
export async function createOrder(customerId, cartItems, paymentMethod = 'CREDIT_CARD') {
  const body = {
    customerId,
    paymentMethod,
    lines: cartItems.map(item => ({
      productId: item.productId,   // String UUID as returned by Product Service
      quantity: item.quantity,
    })),
  };

  const res = await fetch(`${ORDER_API}/api/orders`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    const msg = data.message || data.error || `Order creation failed (${res.status}).`;
    throw new Error(msg);
  }
  return data;
}

/**
 * GET /api/orders/{id}
 * Returns a single order by ID.
 */
export async function getOrder(orderId) {
  const res = await fetch(`${ORDER_API}/api/orders/${orderId}`);
  if (!res.ok) throw new Error(`Order not found (${res.status}).`);
  return res.json();
}

/**
 * GET /api/orders?customerId=X
 * Returns all orders for a customer.
 */
export async function getOrdersByCustomer(customerId) {
  const res = await fetch(`${ORDER_API}/api/orders?customerId=${customerId}`);
  if (!res.ok) throw new Error(`Unable to load orders (${res.status}). Please try again.`);
  return res.json();
}
