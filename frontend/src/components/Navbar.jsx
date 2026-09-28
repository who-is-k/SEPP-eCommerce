import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { useCart } from '../context/CartContext.jsx';

export default function Navbar() {
  const { auth, logout } = useAuth();
  const { itemCount } = useCart();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar" id="main-nav">
      <div className="navbar-brand">
        <Link to="/products" id="nav-brand-link">🛒 SEPPe-Commerce</Link>
      </div>
      <div className="navbar-links">
        <Link to="/products" id="nav-products">Products</Link>
        {auth && (
          <>
            <Link to="/cart" id="nav-cart" className="cart-link">
              Cart
              {itemCount > 0 && (
                <span className="cart-badge">{itemCount}</span>
              )}
            </Link>
            <Link to="/orders" id="nav-orders">My Orders</Link>
            <span className="nav-username">Hi, {auth.fullName?.split(' ')[0]}</span>
            <button id="nav-logout" className="btn-logout" onClick={handleLogout}>Logout</button>
          </>
        )}
        {!auth && (
          <>
            <Link to="/login" id="nav-login">Login</Link>
            <Link to="/register" id="nav-register" className="btn-primary-small">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}
