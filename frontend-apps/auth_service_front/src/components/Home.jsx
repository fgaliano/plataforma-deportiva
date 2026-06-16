import React from 'react';
import '../styles/login.css'; // Cargamos tus estilos oscuros

// Recibimos 'onNavigate' desde App.jsx (es la función navegarA del padre)
function Home({ onNavigate }) {
  return (
    <div className="login-container" style={{ textAlign: 'center' }}>
      <h2>Bienvenido al Sistema</h2>
      <p style={{ color: '#a1a1aa', marginBottom: '2rem' }}>
        Selecciona una opción para comenzar.
      </p>

      {/* Al hacer clic en este botón, ejecutamos onNavigate('login'), que le avisa a App.jsx que cambie a la pantalla de login */}
      <button className="btn-login" onClick={() => onNavigate('login')} style={{ marginBottom: '1rem' }}>
        Iniciar Sesión
      </button>

      {/* Al hacer clic aquí, le avisamos a App.jsx que cambie a la pantalla de 'registro' */}
      <button className="btn-login" onClick={() => onNavigate('registro')} style={{ backgroundColor: '#27272a', border: '1px solid #3f3f46' }}>
        Registrar Nuevo Usuario
      </button>
    </div>
  );
}

export default Home;