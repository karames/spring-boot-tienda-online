document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('login-form');
    const errorDiv = document.getElementById('login-error');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const res = await fetch('/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            if (res.ok) {
                const data = await res.json();
                localStorage.setItem('jwt', data.token);
                localStorage.setItem('role', data.role);
                localStorage.setItem('username', data.username || document.getElementById('username').value);
                errorDiv.style.display = 'none';
                if (data.role === 'ADMIN') {
                    window.location.href = 'admin.html';
                } else if (data.role === 'CLIENTE') {
                    window.location.href = 'productos.html';
                } else {
                    window.location.href = '/';
                }
            } else {
                errorDiv.textContent = 'Usuario o contraseña incorrectos';
                errorDiv.style.display = '';
            }
        });
    }
    // Cambia el botón de volver al inicio para que apunte a '/'
    const volverBtn = document.querySelector('button[onclick*="index.html"]');
    if (volverBtn) {
        volverBtn.onclick = () => window.location.href = '/';
    }
});
