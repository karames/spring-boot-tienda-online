document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('login-form');
    const errorDiv = document.getElementById('login-error');

    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const submitBtn = form.querySelector('button[type="submit"]');
            const originalText = submitBtn.textContent;
            submitBtn.textContent = '🔄 Iniciando sesión...';
            submitBtn.disabled = true;

            if (errorDiv) {
                errorDiv.style.display = 'none';
            }

            try {
                const username = document.getElementById('username').value;
                const password = document.getElementById('password').value;

                // CORRECCIÓN: endpoint correcto para login
                const res = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, password })
                });

                if (res.ok) {
                    const data = await res.json();
                    console.log('Login exitoso:', data);
                    console.log('Guardando en localStorage: token:', data.token, '| role:', data.role, '| username:', data.username || username);

                    // Limpiar localStorage primero
                    localStorage.clear();

                    // Guardar datos de sesión
                    console.log('[auth.js] Guardando en localStorage:', { token: data.token, role: data.role, username: data.username || username });
                    localStorage.setItem('jwt', data.token);
                    localStorage.setItem('role', data.role);
                    localStorage.setItem('username', data.username || username);

                    // Mensaje de éxito
                    submitBtn.textContent = '✅ ¡Éxito! Redirigiendo...';
                    submitBtn.style.background = '#28a745';

                    // Pequeño delay para asegurar que localStorage se escriba
                    setTimeout(() => {
                        // Redirigir según el rol (insensible a mayúsculas)
                        const role = (data.role || '').toUpperCase();
                        console.log('[auth.js] Redirigiendo según rol:', role);
                        if (role === 'ADMIN') {
                            window.location.replace('admin.html');
                        } else if (role === 'CLIENTE') {
                            window.location.replace('productos.html');
                        } else {
                            window.location.replace('index.html');
                        }
                    }, 350); // Aumentado a 350ms para máxima robustez

                } else {
                    let msg = 'Usuario o contraseña incorrectos';
                    try {
                        const err = await res.json();
                        if (err && err.message) {
                            msg = err.message;
                        }
                    } catch (e) {
                        console.error('Error parsing error response:', e);
                    }

                    if (errorDiv) {
                        errorDiv.textContent = '❌ ' + msg;
                        errorDiv.style.display = 'block';
                    }

                    submitBtn.textContent = originalText;
                    submitBtn.disabled = false;
                    submitBtn.style.background = '';
                }

            } catch (error) {
                console.error('Error durante el login:', error);

                if (errorDiv) {
                    errorDiv.textContent = '❌ Error de conexión. Intenta nuevamente.';
                    errorDiv.style.display = 'block';
                }

                submitBtn.textContent = originalText;
                submitBtn.disabled = false;
                submitBtn.style.background = '';
            }
        });
    }
});

// Funciones auxiliares simples
function logout() {
    localStorage.clear();
    window.location.href = 'login.html';
}

function isAuthenticated() {
    return localStorage.getItem('jwt') && localStorage.getItem('role');
}

function getUserRole() {
    return localStorage.getItem('role');
}

function getUsername() {
    return localStorage.getItem('username');
}
