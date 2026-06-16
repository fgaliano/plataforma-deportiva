import React, { useState } from 'react';
import '../../../styles/login.css'; // Subimos 3 niveles de carpetas para buscar los estilos

function Login({ onNavigate }) {
  // ESTADOS DEL FORMULARIO:
  // En React, no buscamos el valor del HTML con un ID.
  // Creamos variables en la memoria de JavaScript y las unimos a los inputs.
  const [usuario, setUsuario] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  // Esta función se ejecuta cuando el usuario pulsa el botón de enviar (Submit)
  const handleSubmit = (e) => {
    e.preventDefault(); // Evita que la página se recargue por defecto como en un HTML clásico

    // Validación en el cliente: si están vacíos, ponemos un texto en nuestro estado 'error'
    if (usuario === '' || password === '') {
      setError('Por favor, rellena todos los campos');
      return;
    }

    setError(''); // Limpiamos el error si todo está correcto
    console.log('Enviando datos...', { usuario, password });
    // Aquí meteremos el fetch AJAX hacia Java en el futuro
  };

  return (
    <div className="login-container">
      <h2>Iniciar Sesión</h2>
      
      {/* RENDERIZADO CONDICIONAL: Si la variable error tiene texto, React dibuja este div rojo automáticamente */}
      {error && <div className="error-msg">{error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <label>Usuario</label>
          <input 
            type="text" 
            placeholder="Introduce tu usuario" 
            value={usuario} // El input muestra lo que vale la variable 'usuario'
            onChange={(e) => setUsuario(e.target.value)} // Al teclear, guardamos la letra en la variable 'usuario'
          />
        </div>

        <div className="input-group">
          <label>Contraseña</label>
          <input 
            type="password" 
            placeholder="••••••••" 
            value={password} // Unido a la variable 'password'
            onChange={(e) => setPassword(e.target.value)} // Guarda la contraseña en tiempo real
          />
        </div>

        <button type="submit" className="btn-login">Entrar</button>
      </form>

      {/* Enlace para volver a la pantalla de inicio */}
      <p style={{ marginTop: '1rem', fontSize: '0.9rem', color: '#a1a1aa' }}>
        <span style={{ color: '#10b981', cursor: 'pointer', textDecoration: 'underline' }} onClick={() => onNavigate('home')}>
          Volver al Inicio
        </span>
      </p>
    </div>
  );
}

export default Login;