const API_BASE_URL = "http://localhost:8081/api"; 

export async function ejecutarLogin(event) {
    event.preventDefault(); 
    
    // 1. Capturamos el botón del formulario y el texto de error
    const formulario = event.target;
    const botonSubmit = formulario.querySelector("button[type='submit']");
    const errorTxt = document.getElementById("login-error");
    
    const username = document.getElementById("login-username").value;
    const password = document.getElementById("login-password").value;
    
    // 2. ACTIVAMOS EL ESTADO "CARGANDO"
    errorTxt.style.display = "none"; 
    botonSubmit.innerText = "Procesando acceso... ⏳";
    botonSubmit.classList.add("cargando");

    try {
        // Simulamos un pelín de espera para que aprecies el efecto visual (opcional)
        // await new Promise(resolve => setTimeout(resolve, 800));

        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Usuario o contraseña incorrectos");
        }

        localStorage.setItem("token_deportivo", data.token);
        alert("¡Login correcto! Token guardado.");
        
        window.cargarVista('home'); 

    } catch (error) {
        errorTxt.innerText = error.message;
        errorTxt.style.display = "block";
    } finally {
        // 3. DESACTIVAMOS EL ESTADO "CARGANDO" (Se ejecuta SIEMPRE, vaya bien o mal)
        botonSubmit.innerText = "Entrar";
        botonSubmit.classList.remove("cargando");
    }
}