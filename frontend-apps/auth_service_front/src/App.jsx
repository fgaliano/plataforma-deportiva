import React, { useState } from 'react';
import Home from './components/Home';                           // Traemos la pantalla de inicio
import Login from './features/auth/components/Login';           // Traemos el formulario de login
import Registro from './features/register/components/Registro'; // Traemos el formulario de registro

function App() {
  // EXPLICACIÓN DEL MOTOR:
  // Creamos una variable llamada 'pantallaActual' que arranca valiendo 'home'.
  // 'setPantallaActual' es la FUNCIÓN que usaremos para cambiar ese valor.
  // Al usar useState, cuando ejecutamos setPantallaActual, React borra la pantalla vieja y pinta la nueva al instante.
  const [pantallaActual, setPantallaActual] = useState('home');

  // Esta función auxiliar recibe el nombre de la pantalla a la que queremos ir, 
  // y actualiza nuestro estado de arriba.
  const navegarA = (nombrePantalla) => {
    setPantallaActual(nombrePantalla);
  };

  // El 'return' es lo que se dibuja en el index.html
  return (
    <>
      {/* SI la pantalla actual es 'home', pintamos el componente de bienvenida */}
      {pantallaActual === 'home' && (
        <Home onNavigate={navegarA} />
      )}

      {/* SI la pantalla actual es 'login', pintamos el formulario de Login */}
      {pantallaActual === 'login' && (
        <Login onNavigate={navegarA} />
      )}
      
      {/* SI la pantalla actual es 'registro', pintamos el formulario de Registro */}
      {pantallaActual === 'registro' && (
        <Registro onNavigate={navegarA} />
      )}
    </>
  );
}

// Exportamos App para que main.jsx lo pueda leer e inyectar en el HTML
export default App;