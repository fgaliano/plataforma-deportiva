const API_BASE_URL = "http://localhost:8081/api"; 

export async function ejecutarRegistro(event) {
    event.preventDefault(); 
    
    // 1. Capturamos el botón del formulario y los textos de alerta
    const formulario = event.target;
    const botonSubmit = formulario.querySelector("button[type='submit']");
    const errorTxt = document.getElementById("reg-error");
    const successTxt = document.getElementById("reg-success");
    
    const username = document.getElementById("reg-username").value;
    const email = document.getElementById("reg-email").value;
    const password = document.getElementById("reg-password").value;
    
    // 2. ACTIVAMOS EL ESTADO "CARGANDO"
    errorTxt.style.display = "none";
    successTxt.style.display = "none";
    botonSubmit.innerText = "Creando cuenta... ⏳";
    botonSubmit.classList.add("cargando");

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, email, password })
        });

        if (!response.ok) {
            const data = await response.json();
            throw new Error(data.message || "Error al registrar el usuario");
        }

        successTxt.innerText = "¡Usuario registrado con éxito! Ya puedes iniciar sesión.";
        successTxt.style.display = "block";
        formulario.reset(); // Resetea el formulario usando la variable limpia

    } catch (error) {
        errorTxt.innerText = error.message;
        errorTxt.style.display = "block";
    } finally {
        // 3. DESACTIVAMOS EL ESTADO "CARGANDO"
        botonSubmit.innerText = "Crear Cuenta";
        botonSubmit.classList.remove("cargando");
    }
}