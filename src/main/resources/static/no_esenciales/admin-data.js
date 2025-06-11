// Funciones de inicialización de datos para administradores
class AdminDataManager {
    constructor() {
        this.jwt = localStorage.getItem('jwt');
        this.baseHeaders = {
            'Authorization': 'Bearer ' + this.jwt,
            'Content-Type': 'application/json'
        };
    }

    // Inicializar productos de prueba
    async initProductos() {
        try {
            const response = await fetch('/api/admin/init/productos', {
                method: 'POST',
                headers: this.baseHeaders
            });

            if (response.ok) {
                const message = await response.text();
                this.showMessage(message, 'success');
                return true;
            } else {
                const error = await response.text();
                this.showMessage('Error: ' + error, 'error');
                return false;
            }
        } catch (error) {
            this.showMessage('Error de conexión: ' + error.message, 'error');
            return false;
        }
    }

    // Limpiar productos
    async clearProductos() {
        if (!confirm('¿Estás seguro de que quieres eliminar TODOS los productos?')) {
            return;
        }

        try {
            const response = await fetch('/api/admin/init/productos', {
                method: 'DELETE',
                headers: this.baseHeaders
            });

            if (response.ok) {
                const message = await response.text();
                this.showMessage(message, 'success');
                return true;
            } else {
                const error = await response.text();
                this.showMessage('Error: ' + error, 'error');
                return false;
            }
        } catch (error) {
            this.showMessage('Error de conexión: ' + error.message, 'error');
            return false;
        }
    }

    // Inicializar usuarios de prueba
    async initUsers() {
        try {
            const response = await fetch('/api/admin/users/init-users', {
                method: 'POST',
                headers: this.baseHeaders
            });

            if (response.ok) {
                const message = await response.text();
                this.showMessage(message, 'success');
                return true;
            } else {
                const error = await response.text();
                this.showMessage('Error: ' + error, 'error');
                return false;
            }
        } catch (error) {
            this.showMessage('Error de conexión: ' + error.message, 'error');
            return false;
        }
    }

    // Limpiar usuarios (excepto admin)
    async cleanupUsers() {
        if (!confirm('¿Estás seguro de que quieres eliminar TODOS los usuarios excepto admin?')) {
            return;
        }

        try {
            const response = await fetch('/api/admin/users/cleanup', {
                method: 'DELETE',
                headers: this.baseHeaders
            });

            if (response.ok) {
                const message = await response.text();
                this.showMessage(message, 'success');
                return true;
            } else {
                const error = await response.text();
                this.showMessage('Error: ' + error, 'error');
                return false;
            }
        } catch (error) {
            this.showMessage('Error de conexión: ' + error.message, 'error');
            return false;
        }
    }

    // Restablecer admin
    async resetAdmin() {
        if (!confirm('¿Restablecer usuario admin? (credenciales: admin/admin)')) {
            return;
        }

        try {
            const response = await fetch('/api/admin/users/reset-admin', {
                method: 'POST',
                headers: this.baseHeaders
            });

            if (response.ok) {
                const message = await response.text();
                this.showMessage(message + '\n¡Deberás volver a hacer login!', 'warning');

                // Logout automático después de 3 segundos
                setTimeout(() => {
                    localStorage.clear();
                    window.location.href = 'login.html';
                }, 3000);

                return true;
            } else {
                const error = await response.text();
                this.showMessage('Error: ' + error, 'error');
                return false;
            }
        } catch (error) {
            this.showMessage('Error de conexión: ' + error.message, 'error');
            return false;
        }
    }

    // Cargar todos los usuarios
    async loadAllUsers() {
        try {
            const response = await fetch('/api/admin/users', {
                headers: this.baseHeaders
            });

            if (response.ok) {
                const users = await response.json();
                this.displayUsers(users);
                return users;
            } else {
                this.showMessage('Error al cargar usuarios', 'error');
                return [];
            }
        } catch (error) {
            this.showMessage('Error de conexión: ' + error.message, 'error');
            return [];
        }
    }

    // Mostrar usuarios en la interfaz
    displayUsers(users) {
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

    // Mostrar mensaje
    showMessage(message, type = 'info') {
        // Buscar función de notificación existente
        if (typeof showNotification === 'function') {
            showNotification(message, type);
        } else if (typeof mostrarMensaje === 'function') {
            mostrarMensaje(message, type);
        } else {
            // Fallback a alert
            alert(message);
        }
    }

    // Configuración completa del sistema
    async setupCompleteSystem() {
        this.showMessage('Iniciando configuración completa del sistema...', 'info');

        let success = true;

        // 1. Inicializar usuarios
        const usersResult = await this.initUsers();
        if (!usersResult) success = false;

        // 2. Inicializar productos
        const productsResult = await this.initProductos();
        if (!productsResult) success = false;

        if (success) {
            this.showMessage('¡Sistema configurado completamente! Usuarios: admin/admin, cliente/cliente', 'success');
        } else {
            this.showMessage('Configuración completada con algunos errores', 'warning');
        }
    }
}

// Instancia global del gestor de datos de admin
const adminDataManager = new AdminDataManager();
