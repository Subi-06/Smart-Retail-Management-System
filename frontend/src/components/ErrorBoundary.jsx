import React from 'react';

export class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('ErrorBoundary caught an error:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '24px',
          backgroundColor: '#0f172a',
          color: '#f8fafc',
          fontFamily: 'system-ui, -apple-system, sans-serif'
        }}>
          <div style={{
            maxWidth: '540px',
            backgroundColor: '#1e293b',
            borderRadius: '16px',
            padding: '32px',
            textAlign: 'center',
            border: '1px solid #334155',
            boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.5)'
          }}>
            <h2 style={{ fontSize: '1.5rem', fontWeight: '800', marginBottom: '12px', color: '#f8fafc' }}>
              SMART RETAIL
            </h2>
            <p style={{ color: '#94a3b8', fontSize: '0.95rem', marginBottom: '20px' }}>
              An error occurred while rendering the page.
            </p>
            <button
              onClick={() => {
                localStorage.clear();
                window.location.href = '/';
              }}
              style={{
                backgroundColor: '#4f46e5',
                color: '#ffffff',
                padding: '10px 24px',
                borderRadius: '8px',
                fontWeight: '600',
                border: 'none',
                cursor: 'pointer',
                fontSize: '0.9rem'
              }}
            >
              Reload Store
            </button>
          </div>
        </div>
      );
    }
    return this.props.children;
  }
}
