const API_BASE_URL = "http://localhost:8081"; 

// 1. Lógica interna del formulario (Privada)
async function ejecutarRegistro(event) {
    event.preventDefault(); 
    
    const formulario = event.target;
    const botonSubmit = formulario.querySelector("button[type='submit']");
    const errorTxt = document.getElementById("reg-error");
    const successTxt = document.getElementById("reg-success");
    
    const username = document.getElementById("reg-username").value;
    const email = document.getElementById("reg-email").value;
    const password = document.getElementById("reg-password").value;
    
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
        formulario.reset(); 

    } catch (error) {
        errorTxt.innerText = error.message;
        errorTxt.style.display = "block";
    } finally {
        botonSubmit.innerText = "Crear Cuenta";
        botonSubmit.classList.remove("cargando");
    }
}

// 2. 🚀 Función de enganche (Pública)
export function inicializarRegistro() {
    const formulario = document.getElementById("form-register");
    if (formulario) {
        formulario.addEventListener("submit", ejecutarRegistro);
    }
}