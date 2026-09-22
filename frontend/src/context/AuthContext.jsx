import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  // Default to pre-seeded Platinum customer for instant browsing, or restore from localStorage
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('smart_retail_user');
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        if (parsed.name === 'John Doe') {
          parsed.name = 'Subiksha';
          parsed.email = 'subiksha@gmail.com';
        }
        return parsed;
      } catch (e) { return null; }
    }
    // Default demo customer
    return {
      userId: 2,
      name: 'Subiksha',
      email: 'subiksha@gmail.com',
      role: 'ROLE_CUSTOMER',
      membershipType: 'PLATINUM',
    };
  });

  const [token, setToken] = useState(() => localStorage.getItem('smart_retail_token') || 'demo-jwt-token');

  useEffect(() => {
    if (user) {
      localStorage.setItem('smart_retail_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('smart_retail_user');
    }
    if (token) {
      localStorage.setItem('smart_retail_token', token);
    } else {
      localStorage.removeItem('smart_retail_token');
    }
  }, [user, token]);

  const login = async (email, password) => {
    try {
      const res = await authAPI.login({ email, password });
      setUser(res.data);
      setToken(res.data.token);
      return { success: true };
    } catch (err) {
      return {
        success: false,
        message: err.response?.data?.message || 'Login failed. Please check credentials.',
      };
    }
  };

  const register = async (data) => {
    try {
      const res = await authAPI.register(data);
      setUser(res.data);
      setToken(res.data.token);
      return { success: true };
    } catch (err) {
      return {
        success: false,
        message: err.response?.data?.message || 'Registration failed.',
      };
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('smart_retail_user');
    localStorage.removeItem('smart_retail_token');
  };

  // Demo helper: instant switch between Admin and Customer
  const switchToAdmin = () => {
    setUser({
      userId: 1,
      name: 'Store Administrator',
      email: 'admin@smartretail.com',
      role: 'ROLE_ADMIN',
      membershipType: 'PLATINUM',
    });
  };

  const switchToCustomer = () => {
    setUser({
      userId: 2,
      name: 'Subiksha',
      email: 'subiksha@gmail.com',
      role: 'ROLE_CUSTOMER',
      membershipType: 'PLATINUM',
    });
  };

  const isAdmin = user?.role === 'ROLE_ADMIN';

  return (
    <AuthContext.Provider value={{
      user,
      token,
      login,
      register,
      logout,
      isAdmin,
      switchToAdmin,
      switchToCustomer,
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
