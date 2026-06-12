/* ==========================================================================
   PLATAFORMA DEPORTIVA - MOTOR CENTRAL (js/app.js)
   Arquitectura: SPA (Single Page Application) con Carga Dinámica de Módulos
   ========================================================================== */

/**
 * Enrutador Asíncrono Inteligente
 * Se encarga de descargar el HTML de la vista solicitada y, de forma dinámica,
 * importa el archivo JavaScript de lógica asociado únicamente cuando se necesita.
 * * @param {string} nombreVista - El nombre del componente a cargar (ej: 'home', 'login')
 */
async function cargarVista(nombreVista) {
    const contenedor = document.getElementById("main-content");
    
    try {
        // 1. 📁 CARGA DEL COMPONENTE VISUAL (HTML)
        // Hacemos un fetch local para traernos el fragmento de código de la subcarpeta
        const responseHTML = await fetch(`componentes/${nombreVista}.html`);
        
        // Si el archivo no existe en la carpeta (error 404), lanzamos un fallo
        if (!responseHTML.ok) {
            throw new Error(`No se encontró el archivo visual: componentes/${nombreVista}.html`);
        }
        
        // Convertimos el archivo descargado en texto plano e inyectamos en el lienzo
        const htmlContenido = await responseHTML.text();
        contenedor.innerHTML = htmlContenido;
        
        // 2. ⚡ CARGA DINÁMICA DE LA LÓGICA (JS) bajo demanda
        // Evaluamos qué pantalla se ha cargado e importamos su script gemelo al vuelo
        if (nombreVista === 'login') {
            // Importamos el módulo de forma asíncrona
            const modulo = await import('./login.js');
            // Colgamos la función exportada en el objeto global 'window' para el formulario
            window.ejecutarLogin = modulo.ejecutarLogin;
        } 
        else if (nombreVista === 'registro') {
            const modulo = await import('./registro.js');
            window.ejecutarRegistro = modulo.ejecutarRegistro;
        }
        
    } catch (error) {
        // Registramos el error en la consola de desarrollo (F12) para poder debuguear
        console.error("Error en el enrutador:", error);
        
        // Pintamos una alerta visual roja para que la pantalla no se quede colgada en blanco
        contenedor.innerHTML = `
            <p class="error-msg">
                <strong>Error de carga:</strong> No se ha podido cargar la sección "${nombreVista}". 
                Por favor, verifica que los archivos existan en las carpetas correspondientes.
            </p>
        `;
    }
}

/* ==========================================================================
   VINCULACIÓN AL ENTORNO GLOBAL (window)
   ========================================================================== */
/* Al usar scripts de tipo módulo (type="module"), las funciones son totalmente 
   privadas. Para que los botones del 'index.html' (como el Navbar fijo) puedan 
   ejecutar la función 'cargarVista' al hacer clic, necesitamos registrarla 
   explícitamente en el objeto global del navegador (window). */
window.cargarVista = cargarVista;

/* ==========================================================================
   ARRANQUE AUTOMÁTICO DE LA APLICACIÓN
   ========================================================================== */
/* Escuchamos al navegador. En cuanto la estructura básica del 'index.html' está 
   lista en memoria, disparamos la carga de la pantalla de bienvenida ('home') */
window.addEventListener("DOMContentLoaded", () => {
    cargarVista('home'); 
});