let productos = [];
let carrito = [];

document.addEventListener('DOMContentLoaded', async () => {
    // Página pública: no se requiere JWT ni rol
    const role = localStorage.getItem('role');
    if (role && role.toUpperCase() === 'ADMIN') {
        mostrarFormularioAdmin();
    } else {
        await cargarProductos();
    }
});

async function cargarProductos() {
    // Permite cargar productos sin autenticación
    let headers = { 'Accept': 'application/json' };
    const jwt = localStorage.getItem('jwt');
    if (jwt) headers['Authorization'] = 'Bearer ' + jwt;

    try {
        const res = await fetch('/api/productos', { headers });
        if (res.ok) {
            productos = await res.json();
            mostrarProductos();
            // Si no hay productos válidos y es admin, mostrar botón para inicializar
            const role = localStorage.getItem('role');
            if (productos.length === 0 && role && role.toUpperCase() === 'ADMIN') {
                const div = document.getElementById('productos-list');
                div.innerHTML = `<div class='info'>No hay productos en la base de datos.</div><button id='btn-init-productos' class='btn btn-primary'>Cargar productos de prueba</button>`;
                document.getElementById('btn-init-productos').onclick = async () => {
                    await inicializarProductosDePrueba();
                };
            }
        } else {
            let msg = 'No se pudieron cargar los productos.';
            try {
                const err = await res.json();
                if (err && err.message) msg = err.message;
            } catch { }
            document.getElementById('productos-list').innerHTML = `<div class="error">${msg}</div>`;
        }
    } catch (error) {
        document.getElementById('productos-list').innerHTML = `<div class="error">Error de conexión: ${error.message}</div>`;
    }
}

async function inicializarProductosDePrueba() {
    const jwt = localStorage.getItem('jwt');
    const btn = document.getElementById('btn-init-productos');
    if (btn) {
        btn.disabled = true;
        btn.textContent = 'Cargando...';
    }
    try {
        const res = await fetch('/api/admin/init/productos', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + jwt,
                'Accept': 'application/json'
            }
        });
        if (res.ok) {
            mostrarMensaje('Productos de prueba cargados correctamente', 'success');
            await cargarProductos();
        } else {
            mostrarMensaje('Error al cargar productos de prueba', 'error');
            if (btn) btn.disabled = false;
        }
    } catch (error) {
        mostrarMensaje('Error de conexión: ' + error.message, 'error');
        if (btn) btn.disabled = false;
    }
}

function mostrarProductos() {
    const role = localStorage.getItem('role');
    const div = document.getElementById('productos-list');

    if (productos.length === 0) {
        div.innerHTML = '<div class="info">No hay productos disponibles.</div>';
        return;
    }

    div.innerHTML = productos.map(p => `
        <div class="producto" id="producto-${p.id}">
            <div class="producto-header">
                <h3>${p.nombre}</h3>
                <span class="badge-stock ${p.stock > 0 ? 'stock-disponible' : 'stock-agotado'}">
                    Stock: ${p.stock}
                </span>
            </div>
            <div class="producto-descripcion">${p.descripcion || 'Sin descripción'}</div>
            <div class="producto-precio">€${p.precio.toFixed(2)}</div>

            ${role === 'CLIENTE' && p.stock > 0 ? `
                <div class="producto-acciones">
                    <button onclick="agregarAlCarrito('${p.id}')" class="btn btn-primary">
                        Agregar al Carrito
                    </button>
                </div>
            ` : ''}

            ${role === 'ADMIN' ? `
                <div class="producto-acciones">
                    <button onclick="editarProducto('${p.id}')" class="btn btn-secondary">
                        Editar
                    </button>
                    <button onclick="eliminarProducto('${p.id}')" class="btn btn-danger">
                        Eliminar
                    </button>
                </div>
            ` : ''}
        </div>
    `).join('');
}

function agregarAlCarrito(productoId) {
    const producto = productos.find(p => p.id === productoId);
    if (!producto) return;

    const itemExistente = carrito.find(item => item.productoId === productoId);

    if (itemExistente) {
        if (itemExistente.cantidad < producto.stock) {
            itemExistente.cantidad++;
        } else {
            mostrarMensaje('No hay suficiente stock disponible', 'error');
            return;
        }
    } else {
        carrito.push({ productoId, cantidad: 1 });
    }

    mostrarCarrito();
    mostrarMensaje(`${producto.nombre} agregado al carrito`, 'success');
}

function removerDelCarrito(productoId) {
    carrito = carrito.filter(item => item.productoId !== productoId);
    mostrarCarrito();
}

function cambiarCantidad(productoId, nuevaCantidad) {
    const producto = productos.find(p => p.id === productoId);
    const item = carrito.find(item => item.productoId === productoId);

    if (!producto || !item) return;

    if (nuevaCantidad <= 0) {
        removerDelCarrito(productoId);
        return;
    }

    if (nuevaCantidad > producto.stock) {
        mostrarMensaje('No hay suficiente stock disponible', 'error');
        return;
    }

    item.cantidad = nuevaCantidad;
    mostrarCarrito();
}

function mostrarCarrito() {
    const role = localStorage.getItem('role');

    if (role !== 'CLIENTE') return;

    let carritoDiv = document.getElementById('carrito');
    if (!carritoDiv) {
        carritoDiv = document.createElement('div');
        carritoDiv.id = 'carrito';
        carritoDiv.className = 'carrito-container';
        document.body.appendChild(carritoDiv);
    }

    if (carrito.length === 0) {
        carritoDiv.innerHTML = `
            <div class="carrito">
                <h3>Carrito de Compras</h3>
                <p>Tu carrito está vacío</p>
            </div>
        `;
        return;
    }

    let total = 0;
    const itemsHTML = carrito.map(item => {
        const producto = productos.find(p => p.id === item.productoId);
        if (!producto) return '';

        const subtotal = producto.precio * item.cantidad;
        total += subtotal;

        return `
            <div class="carrito-item">
                <div class="item-info">
                    <span class="item-nombre">${producto.nombre}</span>
                    <span class="item-precio">€${producto.precio.toFixed(2)} c/u</span>
                </div>
                <div class="item-cantidad">
                    <button onclick="cambiarCantidad('${item.productoId}', ${item.cantidad - 1})" class="btn-cantidad">-</button>
                    <span>${item.cantidad}</span>
                    <button onclick="cambiarCantidad('${item.productoId}', ${item.cantidad + 1})" class="btn-cantidad">+</button>
                </div>
                <div class="item-subtotal">€${subtotal.toFixed(2)}</div>
                <button onclick="removerDelCarrito('${item.productoId}')" class="btn-remover">×</button>
            </div>
        `;
    }).join('');

    carritoDiv.innerHTML = `
        <div class="carrito">
            <h3>Carrito de Compras</h3>
            <div class="carrito-items">
                ${itemsHTML}
            </div>
            <div class="carrito-total">
                <strong>Total: €${total.toFixed(2)}</strong>
            </div>
            <div class="carrito-acciones">
                <button onclick="realizarPedido()" class="btn btn-primary">Realizar Pedido</button>
                <button onclick="vaciarCarrito()" class="btn btn-secondary">Vaciar Carrito</button>
            </div>
        </div>
    `;
}

function vaciarCarrito() {
    carrito = [];
    mostrarCarrito();
}

async function realizarPedido() {
    if (carrito.length === 0) {
        mostrarMensaje('El carrito está vacío', 'error');
        return;
    }

    const jwt = localStorage.getItem('jwt');
    const pedido = {
        productos: carrito.map(item => ({
            productoId: item.productoId,
            cantidad: item.cantidad
        }))
    };

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
            mostrarMensaje('Pedido realizado con éxito', 'success');
            vaciarCarrito();
            // Opcional: redirigir a página de pedidos
            setTimeout(() => {
                window.location.href = 'pedidos.html';
            }, 2000);
        } else {
            const error = await res.json();
            mostrarMensaje(error.message || 'Error al realizar el pedido', 'error');
        }
    } catch (error) {
        mostrarMensaje('Error de conexión: ' + error.message, 'error');
    }
}

function mostrarFormularioAdmin() {
    const container = document.querySelector('.productos-grid') || document.getElementById('productos-list');
    const formHTML = `
        <div class="admin-form-container">
            <h3>Agregar Nuevo Producto</h3>
            <form id="form-producto" class="producto-form">
                <input type="hidden" id="producto-id" value="">
                <div class="form-group">
                    <label for="producto-nombre">Nombre:</label>
                    <input type="text" id="producto-nombre" required>
                </div>
                <div class="form-group">
                    <label for="producto-descripcion">Descripción:</label>
                    <textarea id="producto-descripcion"></textarea>
                </div>
                <div class="form-group">
                    <label for="producto-precio">Precio:</label>
                    <input type="number" id="producto-precio" step="0.01" min="0" required>
                </div>
                <div class="form-group">
                    <label for="producto-stock">Stock:</label>
                    <input type="number" id="producto-stock" min="0" required>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Guardar Producto</button>
                    <button type="button" onclick="cancelarEdicion()" class="btn btn-secondary">Cancelar</button>
                </div>
            </form>
        </div>
    `;

    container.insertAdjacentHTML('beforebegin', formHTML);

    document.getElementById('form-producto').addEventListener('submit', async (e) => {
        e.preventDefault();
        await guardarProducto();
    });
}

async function guardarProducto() {
    const jwt = localStorage.getItem('jwt');
    const id = document.getElementById('producto-id').value;
    const producto = {
        nombre: document.getElementById('producto-nombre').value,
        descripcion: document.getElementById('producto-descripcion').value,
        precio: parseFloat(document.getElementById('producto-precio').value),
        stock: parseInt(document.getElementById('producto-stock').value)
    };

    try {
        const url = id ? `/api/productos/${id}` : '/api/productos';
        const method = id ? 'PUT' : 'POST';

        const res = await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + jwt,
                'Accept': 'application/json'
            },
            body: JSON.stringify(producto)
        });

        if (res.ok) {
            mostrarMensaje(`Producto ${id ? 'actualizado' : 'creado'} con éxito`, 'success');
            cancelarEdicion();
            await cargarProductos();
        } else {
            const error = await res.json();
            mostrarMensaje(error.message || 'Error al guardar el producto', 'error');
        }
    } catch (error) {
        mostrarMensaje('Error de conexión: ' + error.message, 'error');
    }
}

function editarProducto(id) {
    const producto = productos.find(p => p.id === id);
    if (!producto) return;

    document.getElementById('producto-id').value = id;
    document.getElementById('producto-nombre').value = producto.nombre;
    document.getElementById('producto-descripcion').value = producto.descripcion || '';
    document.getElementById('producto-precio').value = producto.precio;
    document.getElementById('producto-stock').value = producto.stock;

    document.querySelector('.admin-form-container h3').textContent = 'Editar Producto';
}

function cancelarEdicion() {
    document.getElementById('form-producto').reset();
    document.getElementById('producto-id').value = '';
    document.querySelector('.admin-form-container h3').textContent = 'Agregar Nuevo Producto';
}

async function eliminarProducto(id) {
    if (!confirm('¿Estás seguro de que quieres eliminar este producto?')) return;

    const jwt = localStorage.getItem('jwt');

    try {
        const res = await fetch(`/api/productos/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + jwt,
                'Accept': 'application/json'
            }
        });

        if (res.ok) {
            mostrarMensaje('Producto eliminado con éxito', 'success');
            await cargarProductos();
        } else {
            const error = await res.json();
            mostrarMensaje(error.message || 'Error al eliminar el producto', 'error');
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
        document.body.appendChild(messageDiv);
    }

    messageDiv.innerHTML = `<div class="alert alert-${tipo}">${mensaje}</div>`;
    messageDiv.style.display = 'block';

    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 5000);
}
