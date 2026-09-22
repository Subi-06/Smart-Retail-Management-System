import React from 'react';
import { X, Printer, Download, CheckCircle2 } from 'lucide-react';

export const BillReceiptModal = ({ bill, onClose }) => {
  if (!bill) return null;

  const handlePrint = () => {
    window.print();
  };

  const handleDownload = () => {
    const receiptContent = `
========================================
             SMART RETAIL               
========================================
Invoice No: ${bill.billNumber}
Date: ${new Date(bill.createdAt || Date.now()).toLocaleString()}
Customer: ${bill.customerName || 'Valued Customer'}
Email: ${bill.customerEmail || 'N/A'}
----------------------------------------
Item                     Qty     Total
----------------------------------------
${bill.items?.map(i => `${(i.productName || 'Item').padEnd(24)} ${String(i.quantity).padEnd(6)} ₹${Number(i.subtotal).toFixed(2)}`).join('\n') || 'Items attached to transaction'}
----------------------------------------
Subtotal:                        ₹${Number(bill.subtotal).toFixed(2)}
Discount:                       -₹${Number(bill.discount).toFixed(2)}
GST / Tax:                       ₹${Number(bill.tax).toFixed(2)}
----------------------------------------
GRAND TOTAL:                     ₹${Number(bill.total).toFixed(2)}
----------------------------------------
Payment Method: ${bill.paymentMethod || 'UPI'}
Payment Status: ${bill.paymentStatus || 'PAID'}

Thank you for choosing Smart Retail!
========================================
    `;

    const blob = new Blob([receiptContent], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${bill.billNumber || 'Invoice'}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card receipt-printable" style={{ maxWidth: '520px', fontFamily: "'Courier New', Courier, monospace" }}>
        <div className="modal-header no-print" style={{ fontFamily: 'var(--font-family)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <CheckCircle2 size={20} color="var(--success)" />
            <h3 style={{ fontSize: '1.1rem', fontWeight: '700' }}>Official Tax Invoice</h3>
          </div>
          <button onClick={onClose} style={{ border: 'none', background: 'none', cursor: 'pointer' }}>
            <X size={20} />
          </button>
        </div>

        <div className="modal-body" style={{ padding: '24px 30px', backgroundColor: '#fafafa', borderRadius: '8px', margin: '16px' }}>
          <div style={{ textAlign: 'center', marginBottom: '16px', borderBottom: '2px dashed #94a3b8', paddingBottom: '12px' }}>
            <h2 style={{ fontSize: '1.4rem', fontWeight: '900', letterSpacing: '2px', color: '#0f172a' }}>SMART RETAIL</h2>
            <div style={{ fontSize: '0.8rem', color: '#64748b' }}>Tax Invoice & Cash Receipt</div>
            <div style={{ fontSize: '0.85rem', fontWeight: '700', marginTop: '6px' }}>Bill No: {bill.billNumber}</div>
            <div style={{ fontSize: '0.75rem', color: '#64748b' }}>{new Date(bill.createdAt || Date.now()).toLocaleString()}</div>
          </div>

          <div style={{ fontSize: '0.85rem', marginBottom: '16px' }}>
            <div><strong>Customer:</strong> {bill.customerName || 'Valued Customer'}</div>
            {bill.customerEmail && <div><strong>Email:</strong> {bill.customerEmail}</div>}
          </div>

          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem', marginBottom: '16px' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid #cbd5e1' }}>
                <th style={{ textAlign: 'left', paddingBottom: '6px' }}>Product</th>
                <th style={{ textAlign: 'center', paddingBottom: '6px' }}>Qty</th>
                <th style={{ textAlign: 'right', paddingBottom: '6px' }}>Amount</th>
              </tr>
            </thead>
            <tbody>
              {bill.items && bill.items.map((item, idx) => (
                <tr key={idx} style={{ borderBottom: '1px dotted #e2e8f0' }}>
                  <td style={{ padding: '6px 0' }}>{item.productName}</td>
                  <td style={{ textAlign: 'center', padding: '6px 0' }}>{item.quantity}</td>
                  <td style={{ textAlign: 'right', padding: '6px 0' }}>₹{Number(item.subtotal).toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div style={{ borderTop: '1px dashed #94a3b8', paddingTop: '10px', fontSize: '0.88rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
              <span>Subtotal:</span>
              <span>₹{Number(bill.subtotal).toFixed(2)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px', color: '#059669' }}>
              <span>Discount:</span>
              <span>-₹{Number(bill.discount).toFixed(2)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
              <span>GST Tax:</span>
              <span>₹{Number(bill.tax).toFixed(2)}</span>
            </div>
            <div style={{
              display: 'flex',
              justifyContent: 'space-between',
              fontWeight: '900',
              fontSize: '1.1rem',
              borderTop: '2px solid #0f172a',
              borderBottom: '2px solid #0f172a',
              padding: '6px 0',
              marginTop: '6px'
            }}>
              <span>TOTAL:</span>
              <span>₹{Number(bill.total).toFixed(2)}</span>
            </div>
          </div>

          <div style={{ marginTop: '16px', fontSize: '0.8rem', textAlign: 'center', color: '#475569' }}>
            <div>Payment: <strong>{bill.paymentMethod || 'UPI'}</strong> | Status: <strong>{bill.paymentStatus || 'PAID'}</strong></div>
            <div style={{ marginTop: '8px', fontStyle: 'italic' }}>Thank you for shopping at Smart Retail!</div>
          </div>
        </div>

        <div className="modal-footer no-print" style={{ fontFamily: 'var(--font-family)' }}>
          <button onClick={handleDownload} className="btn btn-secondary btn-sm">
            <Download size={15} />
            Download TXT
          </button>
          <button onClick={handlePrint} className="btn btn-primary btn-sm">
            <Printer size={15} />
            Print Receipt
          </button>
          <button onClick={onClose} className="btn btn-secondary btn-sm">
            Close
          </button>
        </div>
      </div>
    </div>
  );
};
