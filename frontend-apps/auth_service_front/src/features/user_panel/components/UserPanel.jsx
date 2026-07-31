import React, { useState } from 'react';
import AvatarUsuario from '../../../components/AvatarUsuario';
import '../../../styles/login.css'; // Subimos 3 niveles de carpetas para buscar los estilos
import '../../../styles/forms.css'; // Subimos 3 niveles de carpetas para buscar los estilos
import '../../../styles/user_panel/user_panel.css'; 

function UserPanel({ usuarioLogueado, onNavigate }) {
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


            {/* Enlace para volver a la pantalla de inicio */}
            <p className="back-to-home-container">
            <span className="back-to-home-link" onClick={() => onNavigate('home')}>
                Volver al Inicio
            </span>
            </p>


        </div>

    );
}

export default UserPanel;