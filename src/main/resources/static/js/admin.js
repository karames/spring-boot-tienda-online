document.addEventListener('DOMContentLoaded', async () => {
    const jwt = localStorage.getItem('jwt');
    const role = localStorage.getItem('role');
    if (!jwt || role !== 'ADMIN') {
        window.location.href = 'login.html';
        return;
    }
    const res = await fetch('/api/pedidos', {
        headers: { 'Authorization': 'Bearer ' + jwt }
    });
    if (res.ok) {
        const pedidos = await res.json();
        const div = document.getElementById('admin-pedidos');
        div.innerHTML = pedidos.map(p => `
            <div class="pedido">
                <b>Pedido:</b> ${p.id} | <b>Usuario:</b> ${p.usuarioId} | <b>Estado:</b> ${p.estado}<br>
                <b>Fecha:</b> ${new Date(p.fecha).toLocaleString()}<br>
                <b>Productos:</b>
                <ul>${p.productos.map(item => `<li>${item.productoId} x${item.cantidad}</li>`).join('')}</ul>
                ${p.estado === 'PENDIENTE' ? `<button onclick="cambiarEstado('${p.id}')">Marcar como ENVIADO</button>` : ''}
            </div>
        `).join('');
    } else {
        document.getElementById('admin-pedidos').textContent = 'No se pudieron cargar los pedidos.';
    }
});

async function cambiarEstado(id) {
    const jwt = localStorage.getItem('jwt');
    await fetch(`/api/pedidos/${id}/estado?estado=ENVIADO`, {
        method: 'PUT',
        headers: { 'Authorization': 'Bearer ' + jwt }
    });
    location.reload();
}
