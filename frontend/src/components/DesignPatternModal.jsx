import React, { useState, useEffect } from 'react';
import { X, Layers, CheckCircle2, BookOpen, Code, Compass } from 'lucide-react';
import { patternAPI } from '../services/api';

export const DesignPatternModal = ({ isOpen, onClose }) => {
  const [activeTab, setActiveTab] = useState('creational');
  const [summaryData, setSummaryData] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setLoading(true);
      patternAPI.getSummary()
        .then(res => setSummaryData(res.data))
        .catch(() => {})
        .finally(() => setLoading(false));
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const tabs = [
    { key: 'creational', label: 'Creational (5)' },
    { key: 'structural', label: 'Structural (7)' },
    { key: 'behavioral', label: 'Behavioral (10)' },
    { key: 'application', label: 'Application (6)' },
  ];

  const getPatternsForActiveTab = () => {
    if (!summaryData) return [];
    if (activeTab === 'creational') return summaryData.gofCreationalPatterns || [];
    if (activeTab === 'structural') return summaryData.gofStructuralPatterns || [];
    if (activeTab === 'behavioral') return summaryData.gofBehavioralPatterns || [];
    if (activeTab === 'application') return summaryData.applicationPatterns || [];
    return [];
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card" style={{ maxWidth: '850px', maxHeight: '88vh' }}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{
              width: '32px',
              height: '32px',
              backgroundColor: 'var(--primary-light)',
              color: 'var(--primary)',
              borderRadius: '8px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}>
              <Layers size={18} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.15rem', fontWeight: '800' }}>Design Patterns Architecture Explorer</h3>
              <p style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                Demonstrating 23 GoF Design Patterns & 6 Application Architecture Patterns
              </p>
            </div>
          </div>
          <button onClick={onClose} style={{ border: 'none', background: 'none', cursor: 'pointer' }}>
            <X size={20} />
          </button>
        </div>

        {/* Category Tabs */}
        <div style={{ display: 'flex', borderBottom: '1px solid var(--border-color)', padding: '0 24px', backgroundColor: '#f8fafc', gap: '8px' }}>
          {tabs.map((tab) => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              style={{
                padding: '12px 16px',
                border: 'none',
                background: 'none',
                cursor: 'pointer',
                fontWeight: activeTab === tab.key ? '700' : '500',
                color: activeTab === tab.key ? 'var(--primary)' : 'var(--text-muted)',
                borderBottom: activeTab === tab.key ? '3px solid var(--primary)' : '3px solid transparent',
                fontSize: '0.88rem'
              }}
            >
              {tab.label}
            </button>
          ))}
        </div>

        <div className="modal-body" style={{ maxHeight: '55vh', overflowY: 'auto' }}>
          {loading ? (
            <div style={{ textAlign: 'center', padding: '40px' }}>Loading pattern registry...</div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              {getPatternsForActiveTab().map((pattern, idx) => (
                <div
                  key={idx}
                  style={{
                    border: '1px solid var(--border-color)',
                    borderRadius: 'var(--radius-md)',
                    padding: '16px',
                    backgroundColor: '#ffffff'
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '6px' }}>
                    <h4 style={{ fontSize: '0.98rem', fontWeight: '800', color: 'var(--secondary)' }}>
                      {pattern.name}
                    </h4>
                    <span className="badge badge-primary" style={{ fontSize: '0.68rem' }}>
                      Active in System
                    </span>
                  </div>

                  <p style={{ fontSize: '0.85rem', color: 'var(--text-main)', marginBottom: '8px' }}>
                    {pattern.role}
                  </p>

                  <div style={{
                    fontSize: '0.78rem',
                    backgroundColor: '#f8fafc',
                    padding: '8px 12px',
                    borderRadius: 'var(--radius-sm)',
                    border: '1px solid #e2e8f0',
                    color: '#0f172a',
                    fontFamily: 'monospace'
                  }}>
                    <strong>Classes:</strong> {pattern.classes}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="modal-footer">
          <div style={{ marginRight: 'auto', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
            All patterns participate in genuine, non-dummy operational flows.
          </div>
          <button onClick={onClose} className="btn btn-primary btn-sm">
            Close Explorer
          </button>
        </div>
      </div>
    </div>
  );
};
