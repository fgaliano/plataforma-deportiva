import React, { useState } from 'react';
import '../../../styles/login.css'; // Subimos 3 niveles de carpetas para buscar los estilos
import '../../../styles/forms.css'; // Subimos 3 niveles de carpetas para buscar los estilos


function UserPanel({ onNavigate }) {
return (

        <div className="user-panel">

            <h2>Panel de Usuario</h2>
            <p>Bienvenido al panel de usuario.</p>

            {/* Enlace para volver a la pantalla de inicio */}
            <p style={{ marginTop: '1rem', fontSize: '0.9rem', color: '#a1a1aa' }}>
            <span style={{ color: '#10b981', cursor: 'pointer', textDecoration: 'underline' }} onClick={() => onNavigate('home')}>
                Volver al Inicio
            </span>
            </p>


        </div>

    );
}

export default UserPanel;