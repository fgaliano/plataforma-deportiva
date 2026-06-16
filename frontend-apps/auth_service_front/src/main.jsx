import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx' // Importamos el jefe de la aplicación
import './styles/index.css' // Importamos los estilos globales del fondo de la web

// Buscamos el elemento 'root' del index.html e inyectamos nuestro componente <App /> dentro
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)