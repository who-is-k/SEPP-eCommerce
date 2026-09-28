-- Insert Categories
INSERT INTO categories (category_id, name, description, parent_category_id) VALUES
('cat-001', 'Electronics', 'Electronic devices and accessories', NULL),
('cat-002', 'Wearables', 'Smartwatches, fitness trackers', NULL),
('cat-003', 'Accessories', 'Phone cases, cables, chargers', NULL);

-- Insert Products (stock_status: 0=IN_STOCK, 1=LOW_STOCK, 2=OUT_OF_STOCK)
INSERT INTO products (
    product_id, name, description, price_amount, price_currency,
    stock_quantity, stock_status, category_id, image_url, sku, active, created_at, updated_at
) VALUES
('prod-001', 'Wireless Bluetooth Headphones Pro',
 'High-quality wireless headphones with noise cancellation, 30-hour battery life.',
 129.99, 'MYR', 100, 0, 'cat-001',
 'https://example.com/images/headphones-pro.jpg', 'HP-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('prod-002', 'Smart Fitness Tracker X1',
 'Monitor your health with heart rate tracking, GPS, sleep analysis.',
 79.99, 'MYR', 50, 0, 'cat-002',
 'https://example.com/images/tracker-x1.jpg', 'FT-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('prod-003', 'Premium USB-C Charging Cable',
 'Durable braided USB-C cable, supports fast charging and data transfer.',
 19.99, 'MYR', 200, 0, 'cat-003',
 'https://example.com/images/usb-c-cable.jpg', 'CB-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('prod-004', '4K Action Camera Ultra',
 'Capture stunning 4K video with waterproof housing and image stabilization.',
 299.99, 'MYR', 30, 0, 'cat-001',
 'https://example.com/images/action-camera.jpg', 'AC-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('prod-005', 'Noise Canceling Earbuds',
 'True wireless earbuds with active noise cancellation and touch controls.',
 89.99, 'MYR', 45, 1, 'cat-001',
 'https://example.com/images/earbuds.jpg', 'EB-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('prod-006', 'Smart LED Desk Lamp',
 'Dimmable LED desk lamp with touch controls and USB charging port.',
 34.99, 'MYR', 60, 0, 'cat-001',
 'https://example.com/images/desk-lamp.jpg', 'DL-001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);