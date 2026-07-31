import React, { useState } from 'react';
import Home from './components/Home';                               // Traemos la pantalla de inicio
import Login from './features/auth/components/Login';           // Traemos el formulario de login
import Registro from './features/register/components/Registro'; // Traemos el formulario de registro
import UserPanel from './features/user_panel/components/UserPanel'; //  Traemos el panel de usuario 
import Header from './components/Header'; //  Asegúrate de que la ruta a tu carpeta Header esté bien

function App() {
  const [pantallaActual, setPantallaActual] = useState('home');

  // Este estado empieza a 'null' porque al abrir la web nadie ha iniciado sesión todavía
  const [usuario, setUsuario] = useState(null); 

  const navegarA = (nombrePantalla) => {
    setPantallaActual(nombrePantalla);
  };

  // Añadimos esta función para que el Header pueda limpiar la sesión al darle a "Salir"
  const handleLogout = () => {
    setUsuario(null);
    navegarA('home');
  };

  return (
    <>
      {/* 1. Ponemos el Header aquí arriba fijo para que se vea en TODAS las pantallas */}
      <Header 
        usuarioLogueado={usuario} 
        onNavigate={navegarA} 
        onLogout={handleLogout} 
      />

      {/* SI la pantalla actual es 'home', pintamos el componente de bienvenida */}
      {pantallaActual === 'home' && (
        <Home onNavigate={navegarA} />
      )}
      
      {/* SI la pantalla actual es 'login', pintamos el formulario de Login */}
      {pantallaActual === 'login' && (
          /* Corregido onNavigate para que use navegarA */
          <Login 
            onNavigate={navegarA} 
            onLoginSuccess={(datosDelUsuario) => setUsuario(datosDelUsuario)} 
          />
        )}

      {/* SI la pantalla actual es 'registro', pintamos el formulario de Registro */}
      {pantallaActual === 'registro' && (
        <Registro onNavigate={navegarA} />
      )}

      {/* SI la pantalla actual es 'userPanel', pintamos el panel de usuario */}
      {pantallaActual === 'userPanel' && (
        <UserPanel onNavigate={navegarA} usuarioLogueado={usuario} />
      )}
    </>
  );
}

export default App;