import React, { useState, useEffect } from 'react';

export default function AvatarUsuario({ nombreFoto, token, className }) {
  const [imagenUrl, setImagenUrl] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(false);
  
  // 🌟 Estado para controlar si el modal desplegado está abierto o cerrado
  const [modalAbierto, setModalAbierto] = useState(false);

  useEffect(() => {
    if (!nombreFoto || !token) {
      setCargando(false);
      return;
    }

    const cargarFotoProtegida = async () => {
      try {
        setCargando(true);
        setError(false);

        // Hacemos el fetch al nuevo endpoint enviando el Token en el Authorization
        const response = await fetch(`http://localhost:8081/auth/foto/${nombreFoto}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}` // 👈 ¡Token JWT que autoriza la lectura!
          }
        });


        if (!response.ok) {
          throw new Error('No autorizado o imagen no encontrada');
        }

        const blob = await response.blob();
        const urlBinaria = URL.createObjectURL(blob);
        setImagenUrl(urlBinaria);
      } catch (err) {
        console.error("Error al obtener la imagen del servidor:", err);
        setError(true);
      } finally {
        setCargando(false);
      }
    };

    cargarFotoProtegida();

    return () => {
      if (imagenUrl) URL.revokeObjectURL(imagenUrl);
    };
  }, [nombreFoto, token]);

  if (cargando) return <span className="avatar-loading">Cargando foto...</span>;
  if (error || !imagenUrl) return <span className="avatar-error">👤</span>;

  return (
    <>
      {/* 🖼️ Imagen Normal (Al hacer clic, cambia modalAbierto a true) */}
      <img 
        src={imagenUrl} 
        alt="Foto de perfil" 
        className={className || "user-avatar"} 
        onClick={() => setModalAbierto(true)}
        style={{ cursor: 'pointer' }} // Indica al usuario que se puede clicar
        title="Haz clic para ampliar"
      />

      {/* 🔍 MODAL A PANTALLA COMPLETA */}
      {modalAbierto && (
        <div 
          style={styles.overlay} 
          onClick={() => setModalAbierto(false)} // Cierra al hacer clic en el fondo oscuro
        >
          <div style={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            {/* Botón de cerrar (X) */}
            <button 
              style={styles.closeBtn} 
              onClick={() => setModalAbierto(false)}
            >
              &times;
            </button>

            {/* Imagen ampliada */}
            <img 
              src={imagenUrl} 
              alt="Foto ampliada" 
              style={styles.expandedImg} 
            />
          </div>
        </div>
      )}
    </>
  );
}

// 🎨 Estilos Inline para el Modal (para que no tengas que crear CSS extra)
const styles = {
  overlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    width: '100vw',
    height: '100vh',
    backgroundColor: 'rgba(0, 0, 0, 0.85)', // Fondo semitransparente oscuro
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 9999, // Asegura que se dibuje por encima de todo
    backdropFilter: 'blur(4px)' // Efecto desenfoque de fondo
  },
  modalContent: {
    position: 'relative',
    maxWidth: '90%',
    maxHeight: '90%',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center'
  },
  expandedImg: {
    maxWidth: '80vw',
    maxHeight: '80vh',
    borderRadius: '8px',
    boxShadow: '0 8px 32px rgba(0,0,0,0.5)',
    objectFit: 'contain'
  },
  closeBtn: {
    position: 'absolute',
    top: '-40px',
    right: '0px',
    background: 'none',
    border: 'none',
    color: '#fff',
    fontSize: '36px',
    fontWeight: 'bold',
    cursor: 'pointer',
    lineHeight: '1'
  }
};