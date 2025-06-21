import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';

// // new : 
// // At the very top of index.js or wherever your app is initialized
// const observerError = 'ResizeObserver loop completed with undelivered notifications';
// const suppressedErrors = [observerError];

// const originalError = window.onerror;
// window.onerror = function (msg, ...args) {
//   if (suppressedErrors.some(e => msg.includes(e))) {
//     return true; // suppress it
//   }
//   if (originalError) return originalError(msg, ...args);
// };



const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
reportWebVitals();
