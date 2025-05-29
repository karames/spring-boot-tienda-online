document.addEventListener('DOMContentLoaded', () => {
    const jwt = localStorage.getItem('jwt');
    const role = localStorage.getItem('role');
    const username = localStorage.getItem('username');
    const userMenu = document.getElementById('user-menu');
    const userInfo = document.getElementById('user-info');
    const userMenuBtn = document.getElementById('user-menu-btn');
    const userMenuDropdown = document.getElementById('user-menu-dropdown');
    const adminLink = document.getElementById('admin-link');
    const productosLink = document.getElementById('productos-link');
    const pedidosLink = document.getElementById('pedidos-link');
    const inicioLink = document.getElementById('inicio-link');
    const loginLink = document.getElementById('login-link');
    const registerLink = document.getElementById('register-link');
    // Por defecto, oculta enlaces privados
    if (productosLink) productosLink.style.display = 'none';
    if (pedidosLink) pedidosLink.style.display = 'none';
    if (adminLink) adminLink.style.display = 'none';
    if (userMenu) userMenu.style.display = 'none';
    // Si hay sesión, muestra enlaces privados y menú usuario
    if (jwt && userMenu && userInfo) {
        userInfo.textContent = `${username || ''} (${role || ''})`;
        userMenu.style.display = '';
        if (productosLink) productosLink.style.display = '';
        if (pedidosLink) pedidosLink.style.display = '';
        if (adminLink) adminLink.style.display = (role === 'ADMIN') ? '' : 'none';
        // Submenú desplegable
        if (userMenuBtn && userMenuDropdown) {
            userMenuBtn.onclick = (e) => {
                e.stopPropagation();
                userMenuDropdown.style.display = userMenuDropdown.style.display === 'block' ? 'none' : 'block';
            };
            // Cerrar el menú si se hace clic fuera
            document.addEventListener('click', (e) => {
                if (!userMenu.contains(e.target)) {
                    userMenuDropdown.style.display = 'none';
                }
            });
        }
    }
});

// Mostrar mensaje de cierre de sesión si existe
if (window.location.pathname === '/' || window.location.pathname.endsWith('index.html')) {
    document.addEventListener('DOMContentLoaded', () => {
        const msg = localStorage.getItem('logoutMessage');
        if (msg) {
            const div = document.getElementById('global-message');
            if (div) {
                div.textContent = msg;
                div.style.display = '';
                setTimeout(() => {
                    div.style.display = 'none';
                    localStorage.removeItem('logoutMessage');
                }, 2500);
            } else {
                localStorage.removeItem('logoutMessage');
            }
        }
    });
}

function logout() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('role');
    localStorage.setItem('logoutMessage', 'Sesión cerrada correctamente.');
    window.location.href = '/';
}
