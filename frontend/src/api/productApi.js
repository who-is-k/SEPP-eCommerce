import { PRODUCT_API } from '../config.js';

/**
 * GET /api/products
 * Returns all available products from Product Service.
 */
export async function getAllProducts() {
  const res = await fetch(`${PRODUCT_API}/api/products`);
  if (!res.ok) throw new Error(`Unable to load products (${res.status}). Please try again.`);
  return res.json(); // Array of ProductResponse
}

/**
 * GET /api/products/{productId}
 * Returns a single product by its String UUID productId.
 */
export async function getProductById(productId) {
  const res = await fetch(`${PRODUCT_API}/api/products/${productId}`);
  if (!res.ok) throw new Error(`Product not found (${res.status}).`);
  return res.json();
}

/**
 * GET /api/products/search?keyword=...
 * Returns products matching the keyword.
 */
export async function searchProducts(keyword) {
  const res = await fetch(`${PRODUCT_API}/api/products/search?keyword=${encodeURIComponent(keyword)}`);
  if (!res.ok) throw new Error(`Search failed (${res.status}). Please try again.`);
  return res.json();
}
