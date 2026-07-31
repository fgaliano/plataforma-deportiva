import React from 'react';
import '../styles/header.css'; 
import AvatarUsuario from './AvatarUsuario'; // Asegúrate de que la ruta a tu carpeta AvatarUsuario esté bien

/**
 * Componente de encabezado que muestra el logo y las opciones de autenticación
 * @param {Object} usuarioLogueado - Información del usuario logueado
 * @param {Function} onNavigate - Función para navegar a diferentes rutas
 * @param {Function} onLogout - Función para cerrar sesión
 */
export default function Header({ usuarioLogueado, onNavigate, onLogout }) {
  return (
// 1. Esta clase aplica los estilos de la barra negra (.main-header)
    <header className="main-header">
      
      {/* 2. ESTA ES LA CLAVE: El div contenedor que empuja todo a la derecha */}
      <div className="header-session-info">
        
        {usuarioLogueado ? (
          /* Si el usuario está conectado, se pinta esto dentro del contenedor derecho */
          <div className="logged-container">
                         
             <AvatarUsuario 
                 nombreFoto={usuarioLogueado.rutaFoto}
                 token={usuarioLogueado.token}
                 className="panel-avatar"
             />
                        
            <span className="session-text">
              Sesión iniciada por <strong>{usuarioLogueado.usuario}</strong> con perfil <em>{usuarioLogueado.perfil}</em>
            </span>
            <button className="btn-logout" onClick={onLogout}>
              Cerrar Sesión
            </button>
          </div>
        ) : (
          /* Si no está conectado, el botón de Iniciar Sesión también saldrá a la derecha */
          <button className="btn-login-nav" onClick={() => onNavigate('login')}>
            Iniciar Sesión
          </button>
        )}

      </div>
    </header>
  );
}