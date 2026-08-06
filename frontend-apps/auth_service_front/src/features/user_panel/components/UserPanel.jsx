import React, { useState } from 'react';
import AvatarUsuario from '../../../components/AvatarUsuario';
import ListadoUsers from './ListadoUsers'; // Importamos el componente hijo
import '../../../styles/login.css'; // Subimos 3 niveles de carpetas para buscar los estilos
import '../../../styles/forms.css'; // Subimos 3 niveles de carpetas para buscar los estilos
import '../../../styles/user_panel/user_panel.css'; 

function UserPanel({ usuarioLogueado, onNavigate }) {

    // Estado local para mostrar u ocultar el div (como toggle)
    const [mostrarListado, setMostrarListado] = useState(false);

    return (

        <div className="user-panel">

            <h2>Panel de Usuario</h2>
            <p>Bienvenido al panel de usuari@. {usuarioLogueado.usuario}</p>
            <p>{usuarioLogueado.perfil}</p>

            {usuarioLogueado && (
            <AvatarUsuario 
                nombreFoto={usuarioLogueado.rutaFoto}
                token={usuarioLogueado.token}
                className="perfil-usuario"
            />
            )}

            {/* Enlaces */}
            <p className="back-to-home-container">
                <span 
                className="back-to-home-link" 
                onClick={() => setMostrarListado(!mostrarListado)} 
                style={{ cursor: 'pointer' }}
                >
                {mostrarListado ? 'Ocultar usuarios' : 'Listar usuarios'}
                </span> 
                {' | '}
                <span className="back-to-home-link" onClick={() => onNavigate('home')} style={{ cursor: 'pointer' }}>
                    Volver al Inicio
                </span>
            </p>

            {/* 🌟 DIV CONTENEDOR (Equivalente al div objetivo de AJAX) */}
            <div className="resultado-ajax-container">
                {mostrarListado && (
                    <ListadoUsers token={usuarioLogueado.token} />
                )}
            </div>    

        </div>

    );
}

export default UserPanel;