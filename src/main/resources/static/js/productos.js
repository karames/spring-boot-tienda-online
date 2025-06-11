let productos = [];
let carrito = [];

document.addEventListener('DOMContentLoaded', async () => {
    const jwt = localStorage.getItem('jwt');
    const role = localStorage.getItem('role');
    const path = window.location.pathname;
    // Si no está autenticado, redirigir a login
    if (!jwt || !role) {
        localStorage.clear();
        window.location.replace('login.html');
        return;
    }
    if (role.toUpperCase() === 'ADMIN') {
        mostrarFormularioAdmin();
        await cargarProductos(); // Mostrar productos para gestión
    } else if (role.toUpperCase() === 'CLIENTE') {
        // Si estamos en carrito.html, no mostrar productos, solo el carrito
        if (path.endsWith('carrito.html')) {
            // Solo cargar productos si no están cargados (para mostrar nombres y precios)
            if (!productos || productos.length === 0) {
                await cargarProductos(true); // true = no renderizar productos
            }
            mostrarCarritoPagina();
        } else {
            await cargarProductos(); // Mostrar productos con opción de carrito
        }
    } else {
        // Rol desconocido, limpiar y redirigir
        localStorage.clear();
        window.location.replace('login.html');
    }
});

async function cargarProductos(soloDatos = false) {
    // Permite cargar productos sin autenticación
    let headers = { 'Accept': 'application/json' };
    const jwt = localStorage.getItem('jwt');
    if (jwt) headers['Authorization'] = 'Bearer ' + jwt;

    try {
        const res = await fetch('/api/productos', { headers });
        if (res.ok) {
            productos = await res.json();
            if (!soloDatos) mostrarProductos();
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

    // Aplicar grid de 3 columnas
    div.className = 'productos-grid-cards';

    // Ordenar productos por nombre
    let productosOrdenados = productos.slice().sort((a, b) => {
        if (!a.nombre && !b.nombre) return 0;
        if (!a.nombre) return 1;
        if (!b.nombre) return -1;
        return a.nombre.localeCompare(b.nombre, 'es', { sensitivity: 'base' });
    });

    // Botón para agregar productos (solo admin)
    let botonAgregar = '';
    if (role === 'ADMIN') {
        botonAgregar = `<div class="producto-card" style="display:flex;align-items:center;justify-content:center;min-height:220px;">
            <button class="btn btn-primary" style="width:100%;height:100%;font-size:1.25em;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:0.5em;padding:2em 0;" onclick="mostrarFormularioAdmin()">
                <span style='font-size:2em;'>➕</span>
                Agregar Producto
            </button>
        </div>`;
    }

    // Mostrar siempre tarjetas, tanto para admin como para cliente
    div.innerHTML = botonAgregar + productosOrdenados.map(p => `
        <div class="producto-card">
            <div class="producto-header" style="flex-direction:column;align-items:flex-start;">
                <h3 style="text-align:left;width:100%;margin-bottom:0.2em;">${p.nombre}</h3>
                <span class="badge-stock ${p.stock > 0 ? 'stock-disponible' : 'stock-agotado'}" style="margin-top:0.3em;">
                    Stock: ${p.stock}
                </span>
            </div>
            <div class="producto-descripcion">${p.descripcion || 'Sin descripción'}</div>
            <div class="producto-precio" style="text-align:center;width:100%;">${p.precioFormateado ? p.precioFormateado : formatearPrecio(p.precio)} €</div>
            <div class="producto-acciones">
                <button onclick="editarProducto('${p.id}')" class="btn btn-editar-producto" title="Editar ${p.nombre}">✏️</button>
                <button onclick="eliminarProducto('${p.id}')" class="btn btn-eliminar-producto" title="Eliminar ${p.nombre}">🗑️</button>
                ${role === 'CLIENTE' && p.stock > 0 ? `
                    <button onclick="agregarAlCarrito('${p.id}')" class="btn btn-primary" title="Agregar ${p.nombre} al Carrito" style="width:100%;display:block;">
                        Agregar al Carrito
                    </button>
                ` : ''}
            </div>
        </div>
    `).join('');
    // No mostrar el formulario de alta
    const formContainer = document.querySelector('.admin-form-container');
    if (formContainer) formContainer.style.display = 'none';
}

// Inicializar carrito desde localStorage
try {
    const carritoGuardado = localStorage.getItem('carrito');
    if (carritoGuardado) {
        carrito = JSON.parse(carritoGuardado);
    }
} catch { }

function guardarCarrito() {
    localStorage.setItem('carrito', JSON.stringify(carrito));
}

function agregarAlCarrito(productoId) {
    const producto = productos.find(p => p.id === productoId);
    if (!producto || producto.stock < 1) return;
    let item = carrito.find(i => i.productoId === productoId);
    if (item) {
        if (item.cantidad < producto.stock) {
            item.cantidad++;
        } else {
            mostrarMensaje('No hay más stock disponible', 'error');
            return;
        }
    } else {
        carrito.push({ productoId, cantidad: 1 });
    }
    guardarCarrito();
    mostrarMensaje(`${producto.nombre} agregado al carrito`, 'success');
}

function removerDelCarrito(productoId) {
    carrito = carrito.filter(item => item.productoId !== productoId);
    guardarCarrito();
    mostrarCarritoPagina();
}

function cambiarCantidad(productoId, nuevaCantidad) {
    if (nuevaCantidad < 1) {
        removerDelCarrito(productoId);
        return;
    }
    const item = carrito.find(i => i.productoId === productoId);
    if (item) {
        item.cantidad = nuevaCantidad;
    }
    guardarCarrito();
    mostrarCarritoPagina();
}

function mostrarCarritoPagina() {
    const role = localStorage.getItem('role');
    if (role !== 'CLIENTE') return;
    let carritoDiv = document.getElementById('carrito-list');
    if (!carritoDiv) return;
    if (carrito.length === 0) {
        carritoDiv.innerHTML = '<div class="info info-carrito-vacio" style="background:#ffeaea;border-radius:1em;padding:2em 1em;margin:2em auto;text-align:center;box-shadow:0 2px 12px 0 rgba(255,0,0,0.06);color:#c62828;font-size:1.2em;max-width:400px;display:flex;flex-direction:column;align-items:center;gap:0.7em;font-weight:bold;">'
            + 'Tu carrito está vacío'
            + '</div>';
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
                    <span class="item-precio">${formatearPrecio(producto.precio)} € c/u</span>
                </div>
                <div class="item-cantidad">
                    <button onclick="cambiarCantidad('${item.productoId}', ${item.cantidad - 1})" class="btn-cantidad">-</button>
                    <span>${item.cantidad}</span>
                    <button onclick="cambiarCantidad('${item.productoId}', ${item.cantidad + 1})" class="btn-cantidad">+</button>
                </div>
                <div class="item-subtotal">${formatearPrecio(subtotal)} €</div>
                <button onclick="removerDelCarrito('${item.productoId}')" class="btn-remover">×</button>
            </div>
        `;
    }).join('');
    carritoDiv.innerHTML = `
        <div class="carrito">
            <h3>Carrito de Compras</h3>
            <div class="carrito-items">${itemsHTML}</div>
            <div class="carrito-total"><strong>Total: ${formatearPrecio(total)} €</strong></div>
            <div class="carrito-acciones">
                <button onclick="realizarPedido()" class="btn btn-primary">Realizar Pedido</button>
                <button onclick="vaciarCarrito()" class="btn btn-secondary">Vaciar Carrito</button>
            </div>
        </div>
    `;
}

function vaciarCarrito(skipConfirm = false) {
    if (!skipConfirm) {
        const confirmar = confirm('¿Estás seguro de que deseas vaciar el carrito?');
        if (!confirmar) return;
    }
    carrito = [];
    guardarCarrito();
    mostrarCarritoPagina();
}

async function realizarPedido() {
    if (carrito.length === 0) {
        mostrarMensaje('El carrito está vacío', 'error');
        return;
    }
    // Confirmación antes de realizar el pedido
    const confirmar = confirm('¿Estás seguro de que deseas realizar el pedido?');
    if (!confirmar) return;
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
            vaciarCarrito(true); // No pedir confirmación aquí
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

function mostrarFormularioAdmin(producto = null) {
    const container = document.querySelector('.productos-grid') || document.getElementById('productos-list');
    // Eliminar cualquier formulario previo
    const formPrevio = document.querySelector('.admin-form-container');
    if (formPrevio) formPrevio.remove();

    const isEdit = !!producto;
    const formHTML = `
        <div class="admin-form-container">
            <h3>${isEdit ? 'Editar Producto' : 'Agregar Nuevo Producto'}</h3>
            <form id="form-producto" class="producto-form">
                <input type="hidden" id="producto-id" value="${isEdit ? producto.id : ''}">
                <div class="form-group">
                    <label for="producto-nombre">Nombre:</label>
                    <input type="text" id="producto-nombre" required value="${isEdit ? producto.nombre : ''}">
                </div>
                <div class="form-group">
                    <label for="producto-descripcion">Descripción:</label>
                    <textarea id="producto-descripcion">${isEdit ? (producto.descripcion || '') : ''}</textarea>
                </div>
                <div class="form-group">
                    <label for="producto-precio">Precio:</label>
                    <input type="number" id="producto-precio" step="0.01" min="0" required value="${isEdit ? producto.precio : ''}">
                </div>
                <div class="form-group">
                    <label for="producto-stock">Stock:</label>
                    <input type="number" id="producto-stock" min="0" required value="${isEdit ? producto.stock : ''}">
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">${isEdit ? 'Guardar Cambios' : 'Guardar Producto'}</button>
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
    mostrarFormularioAdmin(producto);
}

function cancelarEdicion() {
    // Elimina el formulario de agregar/editar producto si existe
    const form = document.querySelector('.admin-form-container');
    if (form) form.remove();
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

    // Notificación destacada para éxito en carrito
    let icon = '';
    let customClass = '';
    if (tipo === 'success') {
        icon = '🛒';
        customClass = 'cart-success';
    } else if (tipo === 'error') {
        icon = '❌';
        customClass = 'cart-error';
    } else if (tipo === 'info') {
        icon = 'ℹ️';
        customClass = 'cart-info';
    }

    messageDiv.innerHTML = `<div class="alert alert-${tipo} ${customClass}"><span class="msg-icon">${icon}</span> <span class="msg-text">${mensaje}</span></div>`;
    messageDiv.style.display = 'block';
    messageDiv.style.position = 'fixed';
    messageDiv.style.top = '2.5em';
    messageDiv.style.left = '50%';
    messageDiv.style.transform = 'translateX(-50%)';
    messageDiv.style.zIndex = '9999';
    messageDiv.style.width = 'auto';
    messageDiv.style.maxWidth = '90vw';
    messageDiv.style.pointerEvents = 'none';

    // Animación de entrada
    messageDiv.firstChild.classList.add('msg-fade-in');
    setTimeout(() => {
        if (messageDiv.firstChild) messageDiv.firstChild.classList.remove('msg-fade-in');
    }, 400);

    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 2500);
}

function formatearPrecio(precio) {
    // Asegura que el precio sea un número y lo formatea en formato español
    const num = Number(precio);
    if (isNaN(num)) return precio;
    return num.toLocaleString('es-ES', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}
