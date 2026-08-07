import React, { useState, useEffect, useRef } from 'react';
import '../../../styles/register.css'; // Subimos 3 niveles de carpetas para buscar los estilos
import '../../../styles/forms.css'; // Subimos 3 niveles de carpetas para buscar los estilos
// Importamos las herramientas globales
import PantallaBloqueo from '../../../components/PantallaBloqueo';
import { pantallaBloqueo } from '../../../hooks/pantallaBloqueo';

function Registro({ onNavigate }) {
 
const [error, setError] = useState('');




/**************************************************/
/************ VARIABLES DEL FORMULARIO ************/
/**************************************************/
// Declaracion de variables de estado para los campos del formulario
const [nombre, setNombre] = useState('');
const [apellidos, setApellidos] = useState('');
const [mail, setMail] = useState('');
const [usuario, setUsuario] = useState('');
const [password, setPassword] = useState('');
const [perfil, setPerfilId] = useState('');
const [foto, setFoto] = useState(null);

// 1. Creas la referencia para el input file
const inputFileRefFoto = useRef(null);

// Estado para controlar qué campos tienen errores de validación
const [camposConErrores, setCamposConErrores] = useState([]); 

// Estado para controlar la carga del formulario
// Traemos la lógica de bloqueo global de un plumazo
const { cargando, textoCargando, setBloqueoPantalla } = pantallaBloqueo();

// Guarda el array de perfiles que traeremos de la base de datos (arranca vacío)
const [listaPerfiles, setListaPerfiles] = useState([]);

// Se carga la lista de perfiles desde el backend al montar el componente
const cargarPerfiles = () => {
    fetch('http://localhost:8081/perfil/listarPerfiles')
      .then(response => response.json())
      .then(datos => {
        setListaPerfiles(datos);
      })
      .catch(err => console.error('Error al cargar perfiles:', err));
  };

  // Función para limpiar el formulario y resetear los estados
  const restablecerFormulario = () => {

      setNombre('');
      setApellidos('');
      setMail('');
      setUsuario('');
      setPassword('');
      setPerfilId('0'); // Vuelve automáticamente a "Seleccione perfil de usuario"
      setFoto(null); // Limpias el estado de React
      inputFileRefFoto.current.value = ""; // Limpias el valor del input HTML

  };  

  // 3. EL DISPARADOR (useEffect)
  // Ahora se queda como un mero "oyente" que ejecuta la función al arrancar
  useEffect(() => {
    cargarPerfiles(); // ◄── Equivale al método init() o al arranque del Action de Struts
  }, []); // Se ejecuta solo al montar el componente


/************************************************************************/
/************ FUNCION QUE SE EJECUTA AL ENVIAR EL FORMULARIO ************/
/************************************************************************/
// Esta función se ejecuta cuando el usuario pulsa el botón de enviar (Submit)
const handleSubmit = (e) => {
  e.preventDefault(); // Evita que la página se recargue por defecto como en un HTML clásico

  setBloqueoPantalla(true, "Guardando el nuevo usuario en la plataforma..."); // Mostramos la pantalla de bloqueo con un mensaje personalizado

  // Creamos una lista temporal para apuntar los fallos de esta ejecución
  const erroresActuales = [];
  
  // Creamos el empaquetador FormData para la foto del usuario (aunque no lo usemos todavía, lo dejamos preparado)
  const formData = new FormData();

  // Validamos cada campo y si está vacío, añadimos su nombre al array de errores
  if (!nombre) erroresActuales.push('nombre');
  if (!apellidos) erroresActuales.push('apellidos');
  if (!mail) erroresActuales.push('mail');
  if (!usuario) erroresActuales.push('usuario');
  if (!password) erroresActuales.push('password');
  if (!foto) erroresActuales.push('foto');

  if(perfil === '' || perfil === 0 ){
    erroresActuales.push('perfil');
  }

  // Si la lista tiene algo dentro, significa que hay campos vacíos
  if (erroresActuales.length > 0) {
    setBloqueoPantalla(false, ""); // Mostramos la pantalla de bloqueo con un mensaje personalizado
    setError('Todos los campos son obligatorios');
    setCamposConErrores(erroresActuales); // ◄── Guardamos la lista de culpables en el estado
    return; // Frenamos el envío
  }  

  // Si llegamos aquí, significa que todos los campos están llenos, así que limpiamos los errores
  setCamposConErrores([]);

  // CONSTRUIMOS EL JSON EXACTO
  const datosFinales = {
    nombre: nombre,
    apellidos: apellidos,
    mail: mail,       // ◄── Mapeamos tu estado 'mail' a la propiedad 'mail' que exige tu back
    usuario: usuario,   // ◄── Añadimos el estado 'usuario' de tu formulario
    password: password,
    perfil: {
      id_perfil: perfil // ◄── Metemos tu estado 'perfilId' dentro de la propiedad 'id_perfil'
    }    
  };  

  // Creamos un objeto para enviar al back
  formData.append('usuario', new Blob([JSON.stringify(datosFinales)], { type: 'application/json' }));

  // Si hay una foto seleccionada, la añadimos al FormData
  if (foto) {
    formData.append('foto', foto); // Añadimos el archivo físico de la foto
  }

  setError(''); // Limpiamos el error si todo está correcto
  console.log("JSON listo para enviar al back:", datosFinales);
 // Aquí irá el fetch POST definitivo hacia tu auth_service

  // Enviamos la petición al backend con el FormData
  fetch('http://localhost:8081/auth/register', {
    method: 'POST',
    body: formData // ◄── Enviamos el formData (React añade el Content-Type automáticamente)
  })
  .then(async (response) => {
    setBloqueoPantalla(false, ""); // Ocultamos la pantalla de bloqueo
    if (response.ok) {
      alert("Usuario registrado con éxito");
      
      // Aquí puedes redirigir o limpiar el formulario
      restablecerFormulario(); // Llamamos a la función que limpia el formulario y resetea los estados
      
      // Limpiamos también el input físico de tipo archivo de la pantalla
      const inputFoto = document.querySelector('input[type="file"]');
      if (inputFoto) inputFoto.value = '';      
    } else {
      
      // Si quieres, puedes leer el mensaje de error que devuelve tu backend y mostrarlo en la pantalla
      const mensajeErrorJava = await response.text();

      setError("Error al registrar el usuario: " + mensajeErrorJava);

      // Creamos una lista temporal para identificar qué campo del formulario marcar en rojo
      const culpablesBack = [];

      if (mensajeErrorJava.includes("contraseña") || mensajeErrorJava.includes("longitud")) {
        culpablesBack.push("password");
      }

      if (mensajeErrorJava.includes("nombre") || mensajeErrorJava.includes("nombre")) {
        culpablesBack.push("nombre");
      }     
      
      if (mensajeErrorJava.includes("apellidos") || mensajeErrorJava.includes("apellidos")) {
        culpablesBack.push("apellidos");
      }       

      // Actualizamos el estado para que React redibuje los bordes en rojo
      setCamposConErrores(culpablesBack);
      

    }
  })
  .catch(err => console.error("Error en la petición:", err));

};

  return (
    <div className="register-container">
      <h2>Registrar Nuevo Usuario</h2>
      
      {
        /*********************************************************************************************************/
        /* RENDERIZADO CONDICIONAL: Si la variable error tiene texto, React dibuja este div rojo automáticamente */
        /*********************************************************************************************************/      
      }
      {error && <div className="error-msg">{error}</div>}

      {/*************** FORMULARIO REGISTRO NUEVO USUARIO ****************/}
      {/* handleSubmit -> funcion que se ejecuta al enviar el formulario */}
      <form onSubmit={handleSubmit}>
      
        {/* Nombre del usuario */}
        <div className="input-group">
          <label>Nombre</label>
          <input 
            type="text" 
            placeholder="Introduce tu nombre" 
            value={nombre} // El input muestra lo que vale la variable 'nombre'
            onChange={(e) => setNombre(e.target.value)} // Al teclear, guardamos la letra en la variable 'nombre'
            className={camposConErrores.includes('nombre') ? 'input-error' : ''}
          />
        </div>

        {/* Apellidos del usuario */}
        <div className="input-group">
          <label>Apellidos</label>
          <input 
            type="text" 
            placeholder="Introduce tus apellidos" 
            value={apellidos} // El input muestra lo que vale la variable 'apellidos'
            onChange={(e) => setApellidos(e.target.value)} // Al teclear, guardamos la letra en la variable 'apellidos'
            className={camposConErrores.includes('apellidos') ? 'input-error' : ''}
          />
        </div>  

        {/* email del usuario */}
        <div className="input-group">
          <label>email</label>
          <input 
            type="text" 
            placeholder="Introduce tu email" 
            value={mail} // El input muestra lo que vale la variable 'mail'
            onChange={(e) => setMail(e.target.value)} // Al teclear, guardamos la letra en la variable 'mail'
            className={camposConErrores.includes('mail') ? 'input-error' : ''}
          />
        </div>

        {/* nombre del usuario */}
        <div className="input-group">
          <label>Usuario</label>
          <input 
            type="text" 
            placeholder="Introduce tu nombre de usuario" 
            value={usuario} // Unido a la variable 'username'
            onChange={(e) => setUsuario(e.target.value)} // Guarda el nombre de usuario en tiempo real
            className={camposConErrores.includes('usuario') ? 'input-error' : ''}
          />
        </div>          

        {/* contraseña del usuario */}
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

        {/* perfil del usuario */}
        <div className="input-group">
          <label>Perfil de Usuario</label>

        {/* Vinculamos el valor a tu estado 'perfil' */}
        <select
          value={perfil}
          onChange={(e) => setPerfilId(Number(e.target.value))}
          className={camposConErrores.includes('perfil') ? 'input-error' : ''}>
          {/* 1. Opción fija por defecto */}
          <option value="0">Seleccione perfil de usuario</option>
          {/* Recorremos tu estado 'listaPerfiles' */}
          {listaPerfiles.map((perf) => (
            <option key={perf.id_perfil} value={perf.id_perfil}>
              {perf.descripcion}
            </option>
          ))}
        </select>

      {/* Foto de Perfil: Guarda el archivo real, no el texto */}
      <div className="input-group">
        <label>Foto de Perfil</label>
        <input 
          ref={inputFileRefFoto} // Vinculas la referencia aquí
          type="file" 
          accept="image/*" // Solo permite imágenes (.png, .jpg, etc.)
          onChange={(e) => setFoto(e.target.files[0])} // Guarda el archivo real, no el texto
          className={camposConErrores.includes('foto') ? 'input-error' : ''}
        />
      </div>

        </div>           

        <div className="input-group">
         <button type="submit" className="btn-guardar" disabled={cargando}>
            GUARDAR USUARIO
          </button>
          <button type="button" className="btn-guardar" onClick={restablecerFormulario}>
            LIMPIAR FORMULARIO
          </button>          
        </div> 
 
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

export default Registro;