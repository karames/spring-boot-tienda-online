// Variables globales
let productos = [];
let pedidos = [];
let usuarios = [];
let currentSection = 'dashboard';

// Inicialización simple
document.addEventListener('DOMContentLoaded', async () => {
    // Espera a que localStorage esté disponible y estable
    await new Promise(resolve => setTimeout(resolve, 150));

    const jwt = localStorage.getItem('jwt');
    const role = localStorage.getItem('role');
    console.log('[ADMIN] JWT presente:', !!jwt, '| Role:', role);

    // Validación estricta de rol ADMIN
    if (!jwt || !role || role.toUpperCase() !== 'ADMIN') {
        localStorage.clear();
        window.location.replace('login.html');
        return;
    }
    console.log('[ADMIN] Acceso concedido. JWT y rol válidos.');

    // Inicializar navegación
    initAdminNavigation();

    // Cargar datos iniciales
    await loadDashboard();
    // Cargar productos y pedidos para asegurar visibilidad inmediata
    await loadPedidos();
});

// Navegación entre secciones
function initAdminNavigation() {
    // Usar la clase correcta para los tabs
    const tabs = document.querySelectorAll('.admin-tab-simple');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const section = tab.getAttribute('data-section');
            switchSection(section);
        });
    });
}

function switchSection(sectionName) {
    // Actualizar tabs activos
    document.querySelectorAll('.admin-tab-simple').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelector(`[data-section="${sectionName}"]`).classList.add('active');

    // Mostrar/ocultar secciones
    document.querySelectorAll('.admin-section-simple').forEach(section => {
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
        case 'productos':
            // Si tienes función para productos, agrégala aquí
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

        // Mostrar información relevante de la base de datos
        mostrarInfoBaseDeDatos(productos, pedidos);

        showNotification('Dashboard actualizado correctamente', 'success');

    } catch (error) {
        console.error('Error cargando dashboard:', error);
        showNotification('Error al cargar el dashboard', 'error');
    }
}

function mostrarInfoBaseDeDatos(productos, pedidos) {
    const dbInfoList = document.getElementById('db-info-list');
    if (!dbInfoList) return;
    // Ejemplo de información relevante
    const totalStock = productos.reduce((sum, p) => sum + (p.stock || 0), 0);
    const productosSinStock = productos.filter(p => (p.stock || 0) === 0).length;
    const pedidosEnviados = pedidos.filter(p => p.estado === 'ENVIADO').length;
    const productosUnicosPedidos = new Set(pedidos.flatMap(p => p.productos.map(i => i.productoId))).size;
    dbInfoList.innerHTML = `
        <li>🗃️ <strong>Productos distintos en pedidos:</strong> ${productosUnicosPedidos}</li>
        <li>📦 <strong>Stock total disponible:</strong> ${totalStock}</li>
        <li>❌ <strong>Productos sin stock:</strong> ${productosSinStock}</li>
        <li>🚚 <strong>Pedidos enviados:</strong> ${pedidosEnviados}</li>
        <li>⏳ <strong>Pedidos pendientes:</strong> ${pedidos.filter(p => p.estado === 'PENDIENTE').length}</li>
        <li>🕒 <strong>Último pedido creado:</strong> ${pedidos.length > 0 ? new Date(Math.max(...pedidos.map(p => new Date(p.fecha)))).toLocaleString('es-ES') : 'N/A'}</li>
    `;
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
                headers: {
                    'Authorization': 'Bearer ' + jwt,
                    'Accept': 'application/json'
                }
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
