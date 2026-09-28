import { createContext, useContext, useState, useEffect } from 'react';

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const [items, setItems] = useState(() => {
    try {
      const stored = localStorage.getItem('cart');
      return stored ? JSON.parse(stored) : [];
    } catch {
      return [];
    }
  });

  // Persist cart to localStorage on every change
  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(items));
  }, [items]);

  /** Add a product to cart (or increase qty if already present) */
  const addItem = (product) => {
    setItems(prev => {
      const existing = prev.find(i => i.productId === product.productId);
      if (existing) {
        return prev.map(i =>
          i.productId === product.productId
            ? { ...i, quantity: i.quantity + 1 }
            : i
        );
      }
      return [...prev, {
        productId: product.productId,
        name: product.name,
        price: product.price,
        quantity: 1,
      }];
    });
  };

  /** Remove an item entirely */
  const removeItem = (productId) => {
    setItems(prev => prev.filter(i => i.productId !== productId));
  };

  /** Update quantity (min 1) */
  const updateQty = (productId, qty) => {
    if (qty < 1) return;
    setItems(prev =>
      prev.map(i => i.productId === productId ? { ...i, quantity: qty } : i)
    );
  };

  /** Clear the entire cart */
  const clearCart = () => {
    setItems([]);
    localStorage.removeItem('cart');
  };

  const total = items.reduce((sum, i) => sum + i.price * i.quantity, 0);
  const itemCount = items.reduce((sum, i) => sum + i.quantity, 0);

  return (
    <CartContext.Provider value={{ items, addItem, removeItem, updateQty, clearCart, total, itemCount }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}
