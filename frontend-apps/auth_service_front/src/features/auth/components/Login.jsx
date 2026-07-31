import React, { useState } from 'react';
import '../../../styles/login.css'; // Subimos 3 niveles de carpetas para buscar los estilos
import '../../../styles/forms.css'; // Subimos 3 niveles de carpetas para buscar los estilos
// Importamos las herramientas globales
import PantallaBloqueo from '../../../components/PantallaBloqueo';
import { pantallaBloqueo } from '../../../hooks/pantallaBloqueo';

function Login({ onNavigate, onLoginSuccess }) {
  // ESTADOS DEL FORMULARIO:
  // En React, no buscamos el valor del HTML con un ID.
  // Creamos variables en la memoria de JavaScript y las unimos a los inputs.
  const [usuario, setUsuario] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  // Estado para controlar qué campos tienen errores de validación
  const [camposConErrores, setCamposConErrores] = useState([]); 

  // Estado para controlar la carga del formulario
  // Traemos la lógica de bloqueo global de un plumazo
  const { cargando, textoCargando, setBloqueoPantalla } = pantallaBloqueo();

  // Esta función se ejecuta cuando el usuario pulsa el botón de enviar (Submit)
  const handleSubmit = (e) => {
    e.preventDefault(); // Evita que la página se recargue por defecto como en un HTML clásico

    setBloqueoPantalla(true, "Iniciando sesión... por favor espera"); // Mostramos la pantalla de bloqueo con un mensaje personalizado

    // Creamos una lista temporal para apuntar los fallos de esta ejecución
    const erroresActuales = [];

    // Creamos el empaquetador FormData para la foto del usuario (aunque no lo usemos todavía, lo dejamos preparado)
    const formData = new FormData();    

    // Validamos cada campo y si está vacío, añadimos su nombre al array de errores
    if (!usuario) erroresActuales.push('usuario');
    if (!password) erroresActuales.push('password');  
    
    // Si la lista tiene algo dentro, significa que hay campos vacíos
    if (erroresActuales.length > 0) {
      setBloqueoPantalla(false, ""); // Mostramos la pantalla de bloqueo con un mensaje personalizado
      setError('Todos los campos son obligatorios');
      setCamposConErrores(erroresActuales); // ◄── Guardamos la lista de culpables en el estado
      return; // Frenamos el envío
    }  

    // Si llegamos aquí, significa que todos los campos están llenos, así que limpiamos los errores
    setCamposConErrores([]);    
    setError(''); // Limpiamos el error si todo está correcto

    // CONSTRUIMOS EL JSON EXACTO
    const datosFinales = {
      usuario: usuario,   // ◄── Mapeamos tu estado 'usuario' a la propiedad 'usuario' que exige tu back
      password: password // ◄── Mapeamos tu estado 'password' a la propiedad 'password' que exige tu back
    }; 

    // Creamos un objeto para enviar al back
    formData.append('usuario', new Blob([JSON.stringify(datosFinales)], { type: 'application/json' }));
    console.log("JSON listo para enviar al back:", datosFinales);

    // Enviamos la petición al backend con el FormData
    fetch('http://localhost:8081/auth/login', {
      method: 'POST',
      body: formData // ◄── Enviamos el formData (React añade el Content-Type automáticamente)
    })
    .then(async (response) => {
      setBloqueoPantalla(false, ""); // Ocultamos la pantalla de bloqueo
      if (response.ok) {

      // 1. Leemos los datos que nos devuelve tu backend (Spring Boot)
      const data = await response.json();

      // Para ver exactamente qué te está devolviendo tu backend, mete este console.log:
      console.log("Datos del usuario devueltos por el Back:", data);

      // Enviamos el token, nombre y perfil al "jefe" (App.jsx)
      onLoginSuccess({ 
          usuario:  data.usuario,
          perfil:   data.usuarioPerfil,
          token:    data.token,
          rutaFoto: data.rutaFoto // ◄── Añadimos la ruta de la foto devuelta por el backend
      });

      // Aquí puedes redirigir o limpiar el formulario
      onNavigate('userPanel');   

      } else {
        
        // Si quieres, puedes leer el mensaje de error que devuelve tu backend y mostrarlo en la pantalla
        const mensajeErrorJava = await response.text();

        setError("Error al iniciar sesión: " + mensajeErrorJava);

        // Creamos una lista temporal para identificar qué campo del formulario marcar en rojo
        const culpablesBack = [];

        if (mensajeErrorJava.includes("usuario")) {
          culpablesBack.push("usuario");
        }

        if (mensajeErrorJava.includes("password")) {
          culpablesBack.push("password");
        }        

        // Actualizamos el estado para que React redibuje los bordes en rojo
        setCamposConErrores(culpablesBack);
        

      }
    })
    .catch(err => console.error("Error en la petición:", err));

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
            className={camposConErrores.includes('usuario') ? 'input-error' : ''}
          />
        </div>

        {/*<img src="../../../uploads/Eldarion.png" alt="Icono de usuario" />*/}

        <div className="input-group">
          <label>Contraseña</label>
          <input 
            type="password" 
            placeholder="••••••••" 
            value={password} // Unido a la variable 'password'
            onChange={(e) => setPassword(e.target.value)} // Guarda la contraseña en tiempo real
            className={camposConErrores.includes('password') ? 'input-error' : ''}
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

      {/* Renderizamos la pantalla de bloqueo */}
      <PantallaBloqueo cargando={cargando} textoCargando={textoCargando} />

    </div>
  );
}

export default Login;