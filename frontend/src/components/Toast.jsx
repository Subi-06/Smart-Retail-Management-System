import React, { createContext, useContext, useState, useCallback } from 'react';
import { CheckCircle, AlertCircle, RotateCcw, X } from 'lucide-react';

const ToastContext = createContext(null);

export const useToast = () => useContext(ToastContext);

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const addToast = useCallback((message, isUndoAction = false, onUndoClick = null, type = 'info') => {
    const id = Date.now() + Math.random();
    const newToast = { id, message, isUndoAction, onUndoClick, type };

    setToasts((prev) => [...prev, newToast]);

    // Auto-remove after 6 seconds unless it's an undo action (which lasts 8 seconds)
    const timeout = isUndoAction ? 8000 : 5000;
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, timeout);
  }, []);

  const removeToast = (id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  return (
    <ToastContext.Provider value={{ showToast: addToast }}>
      {children}
      <div
        style={{
          position: 'fixed',
          bottom: '24px',
          right: '24px',
          zIndex: 9999,
          display: 'flex',
          flexDirection: 'column',
          gap: '12px',
          maxWidth: '420px',
          width: 'calc(100% - 48px)',
        }}
      >
        {toasts.map((toast) => (
          <div
            key={toast.id}
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              gap: '12px',
              padding: '14px 18px',
              backgroundColor: '#0f172a',
              color: '#ffffff',
              borderRadius: '12px',
              boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.3), 0 8px 10px -6px rgba(0, 0, 0, 0.3)',
              fontSize: '0.88rem',
              fontWeight: '500',
              border: toast.isUndoAction ? '2px solid #818cf8' : '1px solid #334155',
              animation: 'slideIn 0.3s cubic-bezier(0.16, 1, 0.3, 1)',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flex: 1 }}>
              {toast.isUndoAction ? (
                <RotateCcw size={18} color="#818cf8" style={{ flexShrink: 0 }} />
              ) : toast.type === 'error' ? (
                <AlertCircle size={18} color="#f87171" style={{ flexShrink: 0 }} />
              ) : (
                <CheckCircle size={18} color="#34d399" style={{ flexShrink: 0 }} />
              )}
              <span>{toast.message}</span>
            </div>

            {toast.isUndoAction && toast.onUndoClick && (
              <button
                onClick={() => {
                  toast.onUndoClick();
                  removeToast(toast.id);
                }}
                className="btn btn-sm"
                style={{
                  backgroundColor: '#4f46e5',
                  color: '#ffffff',
                  fontWeight: '800',
                  padding: '6px 14px',
                  borderRadius: '6px',
                  border: 'none',
                  cursor: 'pointer',
                  flexShrink: 0,
                  fontSize: '0.8rem',
                }}
              >
                [ UNDO ]
              </button>
            )}

            <button
              onClick={() => removeToast(toast.id)}
              style={{
                background: 'transparent',
                border: 'none',
                color: '#94a3b8',
                cursor: 'pointer',
                padding: '2px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <X size={16} />
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};
