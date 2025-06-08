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

        // Ocultar enlaces de autenticación cuando ya está logueado
        if (loginLink) loginLink.style.display = 'none';
        if (registerLink) registerLink.style.display = 'none';

        // Submenú desplegable
        if (userMenuBtn && userMenuDropdown) {
            userMenuBtn.onclick = (e) => {
                e.stopPropagation();
                const isOpen = userMenuDropdown.style.display === 'block';

                if (!isOpen) {
                    // Mostrar el menú
                    userMenuDropdown.style.display = 'block';

                    // Verificar si el menú se sale del viewport
                    setTimeout(() => {
                        const dropdownRect = userMenuDropdown.getBoundingClientRect();
                        const viewportWidth = window.innerWidth;

                        // Si el menú se sale por el lado derecho
                        if (dropdownRect.right > viewportWidth - 10) {
                            userMenuDropdown.classList.add('align-left');
                        } else {
                            userMenuDropdown.classList.remove('align-left');
                        }
                    }, 10);
                } else {
                    // Ocultar el menú
                    userMenuDropdown.style.display = 'none';
                    userMenuDropdown.classList.remove('align-left');
                }

                userMenuBtn.classList.toggle('active', !isOpen);
            };

            // Cerrar el menú si se hace clic fuera
            document.addEventListener('click', (e) => {
                if (!userMenu.contains(e.target)) {
                    userMenuDropdown.style.display = 'none';
                    userMenuBtn.classList.remove('active');
                    userMenuDropdown.classList.remove('align-left');
                }
            });

            // Reposicionar el menú al redimensionar la ventana
            window.addEventListener('resize', () => {
                if (userMenuDropdown.style.display === 'block') {
                    const dropdownRect = userMenuDropdown.getBoundingClientRect();
                    const viewportWidth = window.innerWidth;

                    if (dropdownRect.right > viewportWidth - 10) {
                        userMenuDropdown.classList.add('align-left');
                    } else {
                        userMenuDropdown.classList.remove('align-left');
                    }
                }
            });
        }
    } else {
        // Si no hay sesión, mostrar enlaces de autenticación
        if (loginLink) loginLink.style.display = '';
        if (registerLink) registerLink.style.display = '';
    }
});

// Función para mostrar notificaciones globales
function showGlobalNotification(message, type = 'success', duration = 4000) {
    // Crear o reutilizar el contenedor de notificación
    let notification = document.getElementById('global-notification');
    if (!notification) {
        notification = document.createElement('div');
        notification.id = 'global-notification';
        document.body.appendChild(notification);
    }

    // Configurar la notificación
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.display = 'block';

    // Pequeño delay para activar la animación
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
        notification.style.opacity = '1';
    }, 50);

    // Auto-ocultar después del tiempo especificado
    setTimeout(() => {
        notification.style.transform = 'translateX(100%)';
        notification.style.opacity = '0';

        // Ocultar completamente después de la animación
        setTimeout(() => {
            notification.style.display = 'none';
        }, 300);
    }, duration);
}

// Mostrar mensaje de cierre de sesión si existe
if (window.location.pathname === '/' || window.location.pathname.endsWith('index.html')) {
    document.addEventListener('DOMContentLoaded', () => {
        const msg = localStorage.getItem('logoutMessage');
        if (msg) {
            // Usar la nueva función de notificación
            showGlobalNotification(msg, 'success', 4000);
            localStorage.removeItem('logoutMessage');
        }
    });
}

function logout() {
    // Limpiar datos de sesión
    localStorage.removeItem('jwt');
    localStorage.removeItem('role');
    localStorage.removeItem('username');

    // Guardar mensaje de logout exitoso
    localStorage.setItem('logoutMessage', '✅ Sesión cerrada exitosamente. ¡Hasta pronto!');

    // Redirigir a la página de inicio
    window.location.href = '/';
}
