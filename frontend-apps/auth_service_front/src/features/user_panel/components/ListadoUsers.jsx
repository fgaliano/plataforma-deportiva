import React, { useState, useEffect } from 'react';
import AvatarUsuario from '../../../components/AvatarUsuario';

export default function ListadoUsers({ token }) {
  const [usuarios, setUsuarios] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const pedirUsuarios = async () => {
      try {
        const response = await fetch('http://localhost:8081/auth/listado_usuarios', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        if (!response.ok) throw new Error('Error al cargar la lista');
        const data = await response.json();
        console.log('Datos recibidos:', data); // Depuración: mostrar los datos recibidos
        setUsuarios(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setCargando(false);
      }
    };

    pedirUsuarios();
  }, [token]);

  if (cargando) return <p>Cargando lista de usuarios...</p>;
  if (error) return <p style={{ color: 'red' }}>Error: {error}</p>;

  return (
    <div className="tabla-usuarios-container" style={{ marginTop: '20px' }}>
      <h3>Lista de Usuarios</h3>
      <table style={{ margin: '0 auto', borderCollapse: 'collapse', width: '90%' }}>
        <thead>
          <tr style={{ borderBottom: '2px solid #fff' }}>
            <th style={{ padding: '8px' }}>Avatar</th>
            <th style={{ padding: '8px' }}>Usuario</th>
            <th style={{ padding: '8px' }}>Perfil</th>
          </tr>
        </thead>
        <tbody>
          {usuarios.map((u, i) => (
            <tr key={u.id || i} style={{ borderBottom: '1px solid #444' }}>
              <td style={{ padding: '8px' }}>
                  <AvatarUsuario 
                      nombreFoto={u.rutaFoto}
                      token={` ${token}`}
                      className="perfil-usuario-listado"
                  />
              </td>
              <td style={{ padding: '8px' }}>{u.usuario}</td>
              <td style={{ padding: '8px' }}>{u.perfil.perfil}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
