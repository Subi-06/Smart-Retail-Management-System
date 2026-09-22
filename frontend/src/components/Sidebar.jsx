import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Package,
  FolderTree,
  Warehouse,
  ShoppingBag,
  Receipt,
  Percent,
  Bell,
  Layers,
  Settings,
  ArrowLeft
} from 'lucide-react';

export const Sidebar = ({ onOpenPatternModal }) => {
  const links = [
    { to: '/admin/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
    { to: '/admin/products', icon: Package, label: 'Products' },
    { to: '/admin/categories', icon: FolderTree, label: 'Categories' },
    { to: '/admin/inventory', icon: Warehouse, label: 'Inventory' },
    { to: '/admin/orders', icon: ShoppingBag, label: 'Orders' },
    { to: '/admin/bills', icon: Receipt, label: 'Bills & Invoices' },
    { to: '/admin/discounts', icon: Percent, label: 'Discounts' },
    { to: '/admin/notifications', icon: Bell, label: 'Notifications' },
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-heading">Admin Management</div>

      {links.map((link) => {
        const Icon = link.icon;
        return (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
          >
            <Icon size={18} />
            <span>{link.label}</span>
          </NavLink>
        );
      })}

      <div style={{ marginTop: 'auto', paddingTop: '20px', borderTop: '1px solid var(--border-color)', display: 'flex', flexDirection: 'column', gap: '8px' }}>
        <NavLink
          to="/products"
          className="sidebar-link"
          style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}
        >
          <ArrowLeft size={16} />
          <span>Exit to Customer Store</span>
        </NavLink>
      </div>
    </aside>
  );
};
