document.addEventListener('DOMContentLoaded', async () => {
    const jwt = localStorage.getItem('jwt');
    if (!jwt) {
        // No mostrar nada si no hay sesión
        return;
    }
    const res = await fetch('/api/pedidos/mios', {
        headers: { 'Authorization': 'Bearer ' + jwt }
    });
    if (res.ok) {
        const pedidos = await res.json();
        const div = document.getElementById('pedidos-list');
        div.innerHTML = pedidos.map(p => `
            <div class="pedido">
                <b>Pedido:</b> ${p.id} | <b>Estado:</b> ${p.estado}<br>
                <b>Fecha:</b> ${new Date(p.fecha).toLocaleString()}<br>
                <b>Productos:</b>
                <ul>${p.productos.map(item => `<li>${item.productoId} x${item.cantidad}</li>`).join('')}</ul>
            </div>
        `).join('');
    } else {
        document.getElementById('pedidos-list').textContent = 'No se pudieron cargar los pedidos.';
    }
});
