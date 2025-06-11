let pedidos = [];
let productos = [];

document.addEventListener('DOMContentLoaded', async () => {
    // Espera a que localStorage esté disponible y estable
    await new Promise(resolve => setTimeout(resolve, 150));

    const jwt = localStorage.getItem('jwt');
    const role = localStorage.getItem('role');
    console.log('[PEDIDOS] JWT presente:', !!jwt, '| Role:', role);

    if (!jwt || !role) {
        localStorage.clear();
        window.location.replace('login.html');
        return;
    }
    if (role.toUpperCase() === 'ADMIN') {
        await cargarTodosLosPedidos();
        mostrarVistaAdmin();
    } else if (role.toUpperCase() === 'CLIENTE') {
        await cargarMisPedidos();
        mostrarVistaCliente();
    }
    // Cargar productos para que los nombres y totales estén disponibles en la vista
    await cargarProductos();
});

async function cargarProductos() {
    const jwt = localStorage.getItem('jwt');

    try {
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
        console.error('Error al cargar productos:', error);
    }
}

async function cargarMisPedidos() {
    const jwt = localStorage.getItem('jwt');

    try {
        const res = await fetch('/api/pedidos/mios', {
            headers: {
                'Authorization': 'Bearer ' + jwt,
                'Accept': 'application/json'
            }
        });

        if (res.ok) {
            pedidos = await res.json();
            mostrarPedidos();
        } else {
            const error = await res.json();
            mostrarMensaje(error.message || 'No se pudieron cargar los pedidos', 'error');
        }
    } catch (error) {
        mostrarMensaje('Error de conexión: ' + error.message, 'error');
    }
}

async function cargarTodosLosPedidos() {
    const jwt = localStorage.getItem('jwt');

    try {
        const res = await fetch('/api/pedidos', {
            headers: {
                'Authorization': 'Bearer ' + jwt,
                'Accept': 'application/json'
            }
        });

        if (res.ok) {
            pedidos = await res.json();
            mostrarPedidos();
        } else {
            const error = await res.json();
            mostrarMensaje(error.message || 'No se pudieron cargar los pedidos', 'error');
        }
    } catch (error) {
        mostrarMensaje('Error de conexión: ' + error.message, 'error');
    }
}

function mostrarVistaCliente() {
    const container = document.querySelector('.container');
    const title = container.querySelector('h2');
    title.textContent = 'Mis Pedidos';
}

function mostrarVistaAdmin() {
    const container = document.querySelector('.container');
    const title = container.querySelector('h2');
    title.textContent = 'Gestión de Pedidos - Vista de Administrador';

    // Agregar filtros para admin
    const filtrosHTML = `
        <div class="filtros-admin">
            <label for="filtro-estado">Filtrar por estado:</label>
            <select id="filtro-estado" onchange="filtrarPedidos()">
                <option value="TODOS">Todos</option>
                <option value="PENDIENTE">Pendientes</option>
                <option value="ENVIADO">Enviados</option>
            </select>
        </div>
    `;

    title.insertAdjacentHTML('afterend', filtrosHTML);
}

function filtrarPedidos() {
    const filtro = document.getElementById('filtro-estado').value;
    let pedidosFiltrados = pedidos;

    if (filtro !== 'TODOS') {
        pedidosFiltrados = pedidos.filter(p => p.estado === filtro);
    }

    mostrarPedidos(pedidosFiltrados);
}

function mostrarPedidos(pedidosAMostrar = pedidos) {
    const role = localStorage.getItem('role');
    const div = document.getElementById('pedidos-list');

    if (pedidosAMostrar.length === 0) {
        div.innerHTML = '<div class="info">No hay pedidos para mostrar.</div>';
        return;
    }

    div.innerHTML = pedidosAMostrar.map(pedido => {
        const fecha = new Date(pedido.fecha).toLocaleString('es-ES');
        const productosHTML = pedido.productos.map(item => {
            const producto = productos.find(p => p.id === item.productoId);
            const nombreProducto = producto ? producto.nombre : `Producto ${item.productoId}`;
            const precio = producto ? producto.precio : 0;
            const subtotal = precio * item.cantidad;

            return `
                <div class="pedido-item">
                    <span class="item-nombre">${nombreProducto}</span>
                    <span class="item-cantidad">Cantidad: ${item.cantidad}</span>
                    <span class="item-precio">€${precio.toFixed(2)} c/u</span>
                    <span class="item-subtotal">Subtotal: €${subtotal.toFixed(2)}</span>
                </div>
            `;
        }).join('');

        const total = pedido.productos.reduce((sum, item) => {
            const producto = productos.find(p => p.id === item.productoId);
            return sum + (producto ? producto.precio * item.cantidad : 0);
        }, 0);

        return `
            <div class="pedido ${pedido.estado.toLowerCase()}" id="pedido-${pedido.id}">
                <div class="pedido-header">
                    <div class="pedido-info">
                        <h3>Pedido #${pedido.id}</h3>
                        <span class="pedido-fecha">${fecha}</span>
                    </div>
                    <div class="pedido-estado">
                        <span class="badge badge-${pedido.estado.toLowerCase()}">${pedido.estado}</span>
                    </div>
                </div>

                ${role === 'ADMIN' ? `
                    <div class="pedido-usuario">
                        <strong>Usuario:</strong> ${pedido.usuarioId}
                    </div>
                ` : ''}

                <div class="pedido-productos">
                    <h4>Productos:</h4>
                    ${productosHTML}
                </div>

                <div class="pedido-total">
                    <strong>Total: €${total.toFixed(2)}</strong>
                </div>

                ${role === 'ADMIN' && pedido.estado === 'PENDIENTE' ? `
                    <div class="pedido-acciones">
                        <button onclick="cambiarEstadoPedido('${pedido.id}', 'ENVIADO')" class="btn btn-primary">
                            Marcar como Enviado
                        </button>
                    </div>
                ` : ''}
            </div>
        `;
    }).join('');
}

async function cambiarEstadoPedido(pedidoId, nuevoEstado) {
    const jwt = localStorage.getItem('jwt');

    try {
        const res = await fetch(`/api/pedidos/${pedidoId}/estado?estado=${nuevoEstado}`, {
            method: 'PUT',
            headers: {
                'Authorization': 'Bearer ' + jwt
            }
        });

        if (res.ok) {
            mostrarMensaje(`Pedido marcado como ${nuevoEstado.toLowerCase()}`, 'success');

            // Actualizar el pedido en la lista local
            const pedido = pedidos.find(p => p.id === pedidoId);
            if (pedido) {
                pedido.estado = nuevoEstado;
            }

            // Refrescar la vista
            mostrarPedidos();
        } else {
            const error = await res.json();
            mostrarMensaje(error.message || 'Error al cambiar el estado del pedido', 'error');
        }
    } catch (error) {
        mostrarMensaje('Error de conexión: ' + error.message, 'error');
    }
}

function mostrarMensaje(mensaje, tipo) {
    let messageDiv = document.getElementById('message-container');
    if (!messageDiv) {
        messageDiv = document.createElement('div');
        messageDiv.id = 'message-container';
        messageDiv.className = 'message-container';
        document.body.insertBefore(messageDiv, document.body.firstChild);
    }

    messageDiv.innerHTML = `<div class="alert alert-${tipo}">${mensaje}</div>`;
    messageDiv.style.display = 'block';

    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 5000);
}

async function realizarPedido(pedido) {
    const jwt = localStorage.getItem('jwt');

    try {
        const res = await fetch('/api/pedidos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + jwt,
                'Accept': 'application/json'
            },
            body: JSON.stringify(pedido)
        });

        if (res.ok) {
            const nuevoPedido = await res.json();
            mostrarMensaje('Pedido realizado con éxito. ID: ' + nuevoPedido.id, 'success');

            // Agregar el nuevo pedido a la lista y mostrarlo
            pedidos.push(nuevoPedido);
            mostrarPedidos();
        } else {
            const error = await res.json();
            mostrarMensaje(error.message || 'Error al realizar el pedido', 'error');
        }
    } catch (error) {
        mostrarMensaje('Error de conexión: ' + error.message, 'error');
    }
}
