// src/components/PantallaBloqueo.jsx
import React from 'react';
// Aquí importas el CSS donde tengas guardados los estilos de '.pantalla-bloqueo' y '.spinner-grande'
import '../styles/forms.css'; // Subimos 3 niveles de carpetas para buscar los estilos

function PantallaBloqueo({ cargando, textoCargando }) {
  // Si 'cargando' es false, no renderizamos nada
if (!cargando) return null;

  // Si es true, renderiza el bloque exacto que tenías antes
    return (
    <div className="pantalla-bloqueo">
        <div className="bloqueo-contenido">
            <div className="spinner-grande"></div>
            <p>{textoCargando}</p>
        </div>
    </div>
    );
}

export default PantallaBloqueo;


