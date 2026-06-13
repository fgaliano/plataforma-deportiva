/* ==========================================================================
   MOTOR CENTRAL SPA - ARQUITECTURA MODERNA (js/app.js)
   ========================================================================== */

async function cargarVista(nombreVista) {
    const contenedor = document.getElementById("main-content");
    
    try {
        // 1. 📁 CARGA DEL COMPONENTE VISUAL (HTML)
        const responseHTML = await fetch(`componentes/${nombreVista}.html`);
        if (!responseHTML.ok) throw new Error(`No existe el HTML: ${nombreVista}`);
        
        const htmlContenido = await responseHTML.text();
        contenedor.innerHTML = htmlContenido;
        
        // 2. ⚡ INICIALIZACIÓN DE LA LÓGICA (JS) BAJO DEMANDA
        // Buscamos el archivo JS gemelo y arrancamos su fontanería interna
        if (nombreVista === 'login') {
            const modulo = await import('./login.js');
            modulo.inicializarLogin(); 
        } 
        else if (nombreVista === 'registro') {
            const modulo = await import('./registro.js');
            modulo.inicializarRegistro();
        }
        
    } catch (error) {
        console.error("Error en el enrutador:", error);
        contenedor.innerHTML = `
            <p class="error-msg">
                <strong>Error de carga:</strong> No se ha podido cargar la sección "${nombreVista}".
            </p>
        `;
    }
}

// Vinculamos SOLO el enrutador al entorno global para que el Navbar pueda usarlo
window.cargarVista = cargarVista;

// Arranque inicial con la pantalla 'home'
window.addEventListener("DOMContentLoaded", () => {
    cargarVista('home'); 
});