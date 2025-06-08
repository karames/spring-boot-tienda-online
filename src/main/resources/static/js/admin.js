// Variables globales
let productos = [];
let pedidos = [];
let usuarios = [];
let currentSection = 'dashboard';

// Inicialización simple
document.addEventListener('DOMContentLoaded', async () => {
    // Pequeño delay para asegurar que localStorage esté disponible
    await new Promise(resolve => setTimeout(resolve, 50));

    const jwt = localStorage.getItem('jwt');
    const role = localStorage.getItem('role');

    console.log('Verificando acceso admin - JWT:', !!jwt, 'Role:', role);

    // Verificación simple: si no hay JWT o no es ADMIN, redirigir
    if (!jwt || role !== 'ADMIN') {
        console.log('Acceso denegado, redirigiendo a login');
        alert('Acceso denegado. Se requieren permisos de administrador.');
        localStorage.clear(); // Limpiar datos corruptos
        window.location.replace('login.html');
        return;
    }

    // Si llega aquí, el usuario es admin válido
    console.log('Acceso de admin válido');

    // Inicializar navegación
    initAdminNavigation();

    // Cargar datos iniciales
    await loadDashboard();
});

// Navegación entre secciones
function initAdminNavigation() {
    const tabs = document.querySelectorAll('.admin-tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const section = tab.getAttribute('data-section');
            switchSection(section);
        });
    });
}

function switchSection(sectionName) {
    // Actualizar tabs activos
    document.querySelectorAll('.admin-tab').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelector(`[data-section="${sectionName}"]`).classList.add('active');

    // Mostrar/ocultar secciones
    document.querySelectorAll('.admin-section').forEach(section => {
        section.style.display = 'none';
    });
    document.getElementById(`${sectionName}-section`).style.display = 'block';

    currentSection = sectionName;

    // Cargar datos según la sección
    switch (sectionName) {
        case 'dashboard':
            loadDashboard();
            break;
        case 'pedidos':
            loadPedidos();
            break;
        case 'usuarios':
            loadUsuarios();
            break;
    }
}

// Dashboard con estadísticas
async function loadDashboard() {
    try {
        const jwt = localStorage.getItem('jwt');
        const headers = { 'Authorization': 'Bearer ' + jwt };

        // Cargar productos
        const productosRes = await fetch('/api/productos', { headers });
        if (productosRes.ok) {
            productos = await productosRes.json();
            document.getElementById('total-productos').textContent = productos.length;
        }

        // Cargar pedidos
        const pedidosRes = await fetch('/api/pedidos', { headers });
        if (pedidosRes.ok) {
            pedidos = await pedidosRes.json();
            document.getElementById('total-pedidos').textContent = pedidos.length;

            const pendientes = pedidos.filter(p => p.estado === 'PENDIENTE').length;
            document.getElementById('pedidos-pendientes').textContent = pendientes;
        }

        // Simular usuarios (no hay endpoint específico)
        document.getElementById('total-usuarios').textContent = '2+';

        showNotification('Dashboard actualizado correctamente', 'success');

    } catch (error) {
        console.error('Error cargando dashboard:', error);
        showNotification('Error al cargar el dashboard', 'error');
    }
}

// Gestión de pedidos
async function loadPedidos() {
    try {
        const jwt = localStorage.getItem('jwt');
        const estadoFilter = document.getElementById('estado-filter')?.value || '';

        const res = await fetch('/api/pedidos', {
            headers: { 'Authorization': 'Bearer ' + jwt }
        });

        if (!res.ok) {
            throw new Error('Error al cargar pedidos');
        }

        let pedidos = await res.json();

        // Aplicar filtro si existe
        if (estadoFilter) {
            pedidos = pedidos.filter(p => p.estado === estadoFilter);
        }

        // Cargar nombres de productos
        await loadProductosInfo();

        const div = document.getElementById('admin-pedidos');

        if (pedidos.length === 0) {
            div.innerHTML = '<div class="empty-state">📋 No hay pedidos que mostrar</div>';
            return;
        }

        div.innerHTML = pedidos.map(pedido => `
            <div class="pedido-card">
                <div class="pedido-header">
                    <div class="pedido-id">
                        <strong>Pedido #${pedido.id.slice(-8)}</strong>
                        <span class="badge badge-${pedido.estado.toLowerCase()}">${pedido.estado}</span>
                    </div>
                    <div class="pedido-fecha">
                        📅 ${new Date(pedido.fecha).toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        })}
                    </div>
                </div>

                <div class="pedido-info">
                    <div class="pedido-usuario">
                        <strong>👤 Usuario:</strong> ${pedido.usuarioId.slice(-8)}...
                    </div>
                    <div class="pedido-total">
                        <strong>💰 Total:</strong> $${pedido.total ? pedido.total.toFixed(2) : 'N/A'}
                    </div>
                </div>

                <div class="pedido-productos">
                    <strong>📦 Productos:</strong>
                    <ul>
                        ${pedido.productos.map(item => {
            const producto = productos.find(p => p.id === item.productoId);
            const nombre = producto ? producto.nombre : `Producto ${item.productoId.slice(-6)}`;
            const precio = producto ? producto.precio : 0;
            return `
                                <li>
                                    ${nombre} × ${item.cantidad}
                                    <span class="item-precio">($${(precio * item.cantidad).toFixed(2)})</span>
                                </li>
                            `;
        }).join('')}
                    </ul>
                </div>

                <div class="pedido-actions">
                    ${pedido.estado === 'PENDIENTE' ?
                `<button onclick="cambiarEstado('${pedido.id}', 'ENVIADO')" class="btn btn-success">
                            📦 Marcar como Enviado
                        </button>` :
                pedido.estado === 'ENVIADO' ?
                    `<button onclick="cambiarEstado('${pedido.id}', 'PENDIENTE')" class="btn btn-warning">
                            ⏪ Volver a Pendiente
                        </button>` : ''
            }
                </div>
            </div>
        `).join('');

    } catch (error) {
        console.error('Error cargando pedidos:', error);
        document.getElementById('admin-pedidos').innerHTML =
            '<div class="error">❌ Error al cargar los pedidos</div>';
    }
}

// Cargar información de productos para mostrar nombres
async function loadProductosInfo() {
    if (productos.length === 0) {
        try {
            const jwt = localStorage.getItem('jwt');
            const res = await fetch('/api/productos', {
                headers: { 'Authorization': 'Bearer ' + jwt }
            });
            if (res.ok) {
                productos = await res.json();
            }
        } catch (error) {
            console.error('Error cargando productos:', error);
        }
    }
}

// Cambiar estado de pedido
async function cambiarEstado(pedidoId, nuevoEstado) {
    try {
        const jwt = localStorage.getItem('jwt');
        const res = await fetch(`/api/pedidos/${pedidoId}/estado?estado=${nuevoEstado}`, {
            method: 'PUT',
            headers: { 'Authorization': 'Bearer ' + jwt }
        });

        if (res.ok) {
            showNotification(`Pedido actualizado a ${nuevoEstado}`, 'success');
            await loadPedidos();
            await loadDashboard(); // Actualizar stats
        } else {
            throw new Error('Error al cambiar estado del pedido');
        }
    } catch (error) {
        console.error('Error cambiando estado:', error);
        showNotification('Error al cambiar el estado del pedido', 'error');
    }
}

// Gestión de usuarios
async function loadUsuarios() {
    if (typeof adminDataManager !== 'undefined') {
        await adminDataManager.loadAllUsers();
    } else {
        try {
            const jwt = localStorage.getItem('jwt');
            const response = await fetch('/api/admin/users', {
                headers: { 'Authorization': 'Bearer ' + jwt }
            });

            if (response.ok) {
                const users = await response.json();
                displayUsersSimple(users);
            } else {
                showNotification('Error al cargar usuarios', 'error');
            }
        } catch (error) {
            showNotification('Error de conexión: ' + error.message, 'error');
        }
    }
}

function displayUsersSimple(users) {
    const container = document.getElementById('users-list');
    if (!container) return;

    if (users.length === 0) {
        container.innerHTML = '<p>No hay usuarios registrados</p>';
        return;
    }

    const html = users.map(user => `
        <div class="user-card">
            <h4>${user.username}</h4>
            <p><strong>Email:</strong> ${user.email}</p>
            <p><strong>Roles:</strong> ${user.roles.join(', ')}</p>
            <p><strong>ID:</strong> ${user.id}</p>
        </div>
    `).join('');

    container.innerHTML = html;
}

// Sistema de notificaciones
function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification notification-${type}`;
    notification.style.display = 'block';

    // Auto-ocultar después de 3 segundos
    setTimeout(() => {
        notification.style.display = 'none';
    }, 3000);
}

// Función de logout (debe existir en auth.js)
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
