document.addEventListener('DOMContentLoaded', async () => {
    const jwt = localStorage.getItem('jwt');
    if (!jwt) {
        window.location.href = '/';
        return;
    }
    const res = await fetch('/api/productos', {
        headers: { 'Authorization': 'Bearer ' + jwt }
    });
    if (res.ok) {
        const productos = await res.json();
        const div = document.getElementById('productos-list');
        div.innerHTML = productos.map(p => `
            <div class="producto">
                <div style="display:flex;justify-content:space-between;align-items:center;">
                    <b>${p.nombre}</b>
                    <span class="badge-stock">Stock: ${p.stock}</span>
                </div>
                <div style="color:#555;margin:0.5em 0 0.7em 0;">${p.descripcion}</div>
                <div style="font-size:1.1em;color:#2a4d7a;font-weight:bold;">${p.precio} €</div>
            </div>
        `).join('');
    } else {
        document.getElementById('productos-list').innerHTML = '<div class="error">No se pudieron cargar los productos.</div>';
    }
});
