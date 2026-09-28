import { CUSTOMER_API } from '../config.js';

/**
 * Register a new customer.
 * POST /api/customers/register
 * Body: { fullName, email, password, phoneNumber, street, city, postcode }
 * Returns: CustomerResponse
 */
export async function register(data) {
  const res = await fetch(`${CUSTOMER_API}/api/customers/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  const body = await res.json().catch(() => ({}));
  if (!res.ok) {
    const msg = body.message || body.error || `Registration failed (${res.status})`;
    throw new Error(msg);
  }
  return body;
}

/**
 * Login a customer.
 * POST /api/customers/login
 * Body: { email, password }
 * Returns: LoginResponse { customerId, fullName, email, token }
 */
export async function login(email, password) {
  const res = await fetch(`${CUSTOMER_API}/api/customers/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });
  const body = await res.json().catch(() => ({}));
  if (!res.ok) {
    const msg = body.message || body.error || 'Invalid email or password.';
    throw new Error(msg);
  }
  return body; // { customerId, fullName, email, token }
}
