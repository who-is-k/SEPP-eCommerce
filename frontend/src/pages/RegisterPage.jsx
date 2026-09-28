import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { register } from '../api/customerApi.js';

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: '', email: '', password: '',
    phoneNumber: '', street: '', city: '', postcode: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    if (!form.fullName || !form.email || !form.password) {
      setError('Full name, email and password are required.');
      return;
    }
    if (form.password.length < 8) {
      setError('Password must be at least 8 characters.');
      return;
    }
    setLoading(true);
    try {
      await register(form);
      setSuccess('Registration successful! You can now log in.');
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      setError(err.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>Create Account</h1>
        <p className="auth-subtitle">Join SEPPe-Commerce today</p>

        {error && <div className="alert alert-error" role="alert">{error}</div>}
        {success && <div className="alert alert-success" role="alert">{success}</div>}

        <form onSubmit={handleSubmit} id="register-form" noValidate>
          <div className="form-group">
            <label htmlFor="fullName">Full Name *</label>
            <input
              id="fullName" name="fullName" type="text"
              placeholder="John Smith"
              value={form.fullName} onChange={handleChange}
              required autoFocus
            />
          </div>
          <div className="form-group">
            <label htmlFor="email">Email Address *</label>
            <input
              id="email" name="email" type="email"
              placeholder="john@example.com"
              value={form.email} onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password * (min 8 characters)</label>
            <input
              id="password" name="password" type="password"
              placeholder="••••••••"
              value={form.password} onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="phoneNumber">Phone Number</label>
            <input
              id="phoneNumber" name="phoneNumber" type="tel"
              placeholder="0123456789"
              value={form.phoneNumber} onChange={handleChange}
            />
          </div>
          <div className="form-section-label">Delivery Address (optional)</div>
          <div className="form-group">
            <label htmlFor="street">Street</label>
            <input
              id="street" name="street" type="text"
              placeholder="123 Main Street"
              value={form.street} onChange={handleChange}
            />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="city">City</label>
              <input
                id="city" name="city" type="text"
                placeholder="Kuala Lumpur"
                value={form.city} onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label htmlFor="postcode">Postcode</label>
              <input
                id="postcode" name="postcode" type="text"
                placeholder="50000"
                value={form.postcode} onChange={handleChange}
              />
            </div>
          </div>

          <button
            id="register-btn" type="submit"
            className="btn-primary btn-full"
            disabled={loading}
          >
            {loading ? 'Creating account...' : 'Create Account'}
          </button>
        </form>

        <p className="auth-switch">
          Already have an account? <Link to="/login">Log in</Link>
        </p>
      </div>
    </div>
  );
}
