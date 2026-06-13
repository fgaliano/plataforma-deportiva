const API_BASE_URL = "http://localhost:8081"; 

// 1. Lógica interna del formulario (Privada)
async function ejecutarLogin(event) {
    event.preventDefault(); // Evitamos que la página se recargue
    
    const formulario = event.target;
    const botonSubmit = formulario.querySelector("button[type='submit']");
    const errorTxt = document.getElementById("login-error");
    
    const usuario  = document.getElementById("login-username").value;
    const password = document.getElementById("login-password").value;
    
    errorTxt.style.display = "none"; 
    botonSubmit.innerText = "Procesando acceso... ⏳";
    botonSubmit.classList.add("cargando");

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ usuario, password })
        });

        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "Usuario o contraseña incorrectos");

        localStorage.setItem("token_deportivo", data.token);
        alert("¡Login correcto!");
        
        window.cargarVista('home'); 

    } catch (error) {
        errorTxt.innerText = error.message;
        errorTxt.style.display = "block";
    } finally {
        botonSubmit.innerText = "Entrar";
        botonSubmit.classList.remove("cargando");
    }
}

// 2. 🚀 Función de enganche (Pública)
export function inicializarLogin() {
    const formulario = document.getElementById("login-form");
    if (formulario) {
        formulario.addEventListener("submit", ejecutarLogin);
    }
}