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
    const navCenter = document.querySelector('.nav-center');
    const inicioLink = document.getElementById('inicio-link');
    const loginLink = document.getElementById('login-link');
    const registerLink = document.getElementById('register-link');
    // Mostrar/ocultar enlace de productos según autenticación
    if (productosLink) productosLink.style.display = jwt ? '' : 'none';
    // Si es CLIENTE, reordenar: productos primero, luego pedidos
    if (role && role.toUpperCase() === 'CLIENTE' && productosLink && pedidosLink) {
        const navCenter = productosLink.parentElement;
        if (navCenter && navCenter.contains(productosLink) && navCenter.contains(pedidosLink)) {
            navCenter.insertBefore(productosLink, pedidosLink);
        }
        // Resaltar el enlace activo según la página
        const path = window.location.pathname;
        productosLink.classList.remove('active');
        pedidosLink.classList.remove('active');
        if (path.endsWith('productos.html')) {
            productosLink.classList.add('active');
        } else if (path.endsWith('pedidos.html')) {
            pedidosLink.classList.add('active');
        }
    }
    // Centrar las opciones de navegación
    if (navCenter) {
        navCenter.style.display = 'flex';
        navCenter.style.justifyContent = 'center';
        navCenter.style.alignItems = 'center';
        navCenter.style.width = '100%';
        navCenter.style.gap = '1.2em';
    }
    // Si el usuario está logueado, mostrar el menú de usuario a la derecha de las opciones
    if (jwt && userMenu && navCenter) {
        userMenu.style.display = '';
        userMenu.style.marginLeft = '1.5em';
        navCenter.appendChild(userMenu); // Lo coloca justo después de los enlaces

        // Eliminar bloque de usuario en el dropdown, solo dejar el botón de cerrar sesión
        const userMenuDropdown = document.getElementById('user-menu-dropdown');
        if (userMenuDropdown) {
            const prevInfo = userMenuDropdown.querySelector('.user-menu-info');
            if (prevInfo) prevInfo.remove();
        }
    }
    // Por defecto, oculta enlaces privados
    if (pedidosLink) pedidosLink.style.display = 'none';
    if (adminLink) adminLink.style.display = 'none';
    if (userMenu) userMenu.style.display = 'none';
    // Ocultar Inicio y Documentación si hay sesión
    if (inicioLink) inicioLink.style.display = jwt ? 'none' : '';
    const docsLink = document.getElementById('docs-link');
    if (docsLink) docsLink.style.display = jwt ? 'none' : '';
    // Si hay sesión, muestra enlaces privados y menú usuario
    if (jwt && userMenu && userInfo) {
        userInfo.textContent = `${username || ''} (${role || ''})`;
        userMenu.style.display = '';
        if (pedidosLink) {
            pedidosLink.style.display = '';
            // Ajustar el texto según el rol
            if (role && role.toUpperCase() === 'ADMIN') {
                pedidosLink.textContent = '📋 Pedidos';
            } else {
                pedidosLink.textContent = '📋 Mis pedidos';
            }
        }
        if (adminLink) adminLink.style.display = (role && role.toUpperCase() === 'ADMIN') ? '' : 'none';

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

    // --- LIMPIEZA Y CONTROL DE ENLACES SEGÚN ROL ---
    // Ocultar todos los enlaces privados por defecto
    if (adminLink) adminLink.style.display = 'none';
    if (productosLink) productosLink.style.display = 'none';
    if (pedidosLink) pedidosLink.style.display = 'none';
    const carritoLink = document.getElementById('carrito-link');
    if (carritoLink) carritoLink.style.display = 'none';
    // Mostrar solo los enlaces permitidos por rol
    if (jwt && role) {
        if (role.toUpperCase() === 'ADMIN') {
            if (adminLink) adminLink.style.display = '';
            if (productosLink) productosLink.style.display = '';
            if (pedidosLink) {
                pedidosLink.style.display = '';
                pedidosLink.textContent = '📋 Pedidos';
            }
            if (carritoLink) carritoLink.style.display = 'none';
        } else if (role.toUpperCase() === 'CLIENTE') {
            if (productosLink) productosLink.style.display = '';
            if (pedidosLink) {
                pedidosLink.style.display = '';
                pedidosLink.textContent = '📋 Mis pedidos';
            }
            if (carritoLink) carritoLink.style.display = '';
        }
        // Menú usuario: icono, nombre, espacio, rol, submenú para cerrar sesión
        if (userMenu) {
            userMenu.style.display = '';
            userMenu.innerHTML = `
                <button id="user-menu-btn" class="user-menu-btn">
                    👤 ${username} <span style="margin-left:0.5em;" class="user-role-simple">(${role.toUpperCase() === 'ADMIN' ? 'Admin' : 'Cliente'})</span> ▼
                </button>
                <div id="user-menu-dropdown" class="user-menu-dropdown" style="display:none;min-width:120px;">
                    <button id="logout-btn" class="logout-btn">Cerrar sesión</button>
                </div>
            `;
            const userMenuBtn = document.getElementById('user-menu-btn');
            const userMenuDropdown = document.getElementById('user-menu-dropdown');
            const logoutBtn = document.getElementById('logout-btn');
            if (logoutBtn) logoutBtn.onclick = () => { if (typeof logout === 'function') logout(); };
            if (userMenuBtn && userMenuDropdown) {
                userMenuBtn.onclick = (e) => {
                    e.stopPropagation();
                    userMenuDropdown.style.display = userMenuDropdown.style.display === 'block' ? 'none' : 'block';
                };
                document.addEventListener('click', (e) => {
                    if (!userMenu.contains(e.target)) {
                        userMenuDropdown.style.display = 'none';
                    }
                });
            }
        }
        // Ocultar login/register/inicio/docs
        if (loginLink) loginLink.style.display = 'none';
        if (registerLink) registerLink.style.display = 'none';
        if (inicioLink) inicioLink.style.display = 'none';
        const docsLink = document.getElementById('docs-link');
        if (docsLink) docsLink.style.display = 'none';
    } else {
        // No autenticado: ocultar enlaces privados y mostrar login/register
        if (userMenu) userMenu.style.display = 'none';
        if (adminLink) adminLink.style.display = 'none';
        if (productosLink) productosLink.style.display = 'none';
        if (pedidosLink) pedidosLink.style.display = 'none';
        if (loginLink) loginLink.style.display = '';
        if (registerLink) registerLink.style.display = '';
        if (inicioLink) inicioLink.style.display = '';
        const docsLink = document.getElementById('docs-link');
        if (docsLink) docsLink.style.display = '';
    }
    // --- FIN CONTROL DE ENLACES ---
    // Reordenar y resaltar enlaces para CLIENTE
    if (role && role.toUpperCase() === 'CLIENTE' && productosLink && pedidosLink) {
        const navCenter = productosLink.parentElement;
        if (navCenter && navCenter.contains(productosLink) && navCenter.contains(pedidosLink)) {
            navCenter.insertBefore(productosLink, pedidosLink);
        }
        // Resaltar el enlace activo según la página
        const path = window.location.pathname;
        productosLink.classList.remove('active');
        pedidosLink.classList.remove('active');
        if (path.endsWith('productos.html')) {
            productosLink.classList.add('active');
        } else if (path.endsWith('pedidos.html')) {
            pedidosLink.classList.add('active');
        }
    }
    // Resaltar el enlace activo del carrito si corresponde
    if (carritoLink) {
        const path = window.location.pathname;
        carritoLink.classList.remove('active');
        if (path.endsWith('carrito.html')) {
            carritoLink.classList.add('active');
        }
    }
});

function formatearPrecio(precio) {
    // Formato: 1.234,56 (mostrar punto para miles y coma para decimales)
    const partes = precio.toFixed(2).split('.');
    let miles = partes[0];
    let resultado = '';
    while (miles.length > 3) {
        resultado = '.' + miles.slice(-3) + resultado;
        miles = miles.slice(0, -3);
    }
    resultado = miles + resultado + ',' + partes[1];
    return resultado;
}
