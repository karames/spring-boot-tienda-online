# 🛒 Tienda Online - Guía Completa

## 📋 Resumen del Proyecto

Esta es una aplicación web completa de tienda online desarrollada con Spring Boot, que incluye:

### 🔧 Tecnologías Utilizadas
- **Backend**: Spring Boot 3.x con Spring Web, Spring Data MongoDB, Spring Security
- **Base de datos**: MongoDB (NoSQL)
- **Autenticación**: JWT (JSON Web Tokens)
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Seguridad**: Control de acceso basado en roles (RBAC)

### 👥 Tipos de Usuario
1. **CLIENTE**: Puede ver productos, crear pedidos y ver su historial
2. **ADMIN**: Gestión completa de productos, pedidos y visualización de estadísticas

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 17 o superior
- MongoDB en ejecución (local o remoto)
- Puerto 8080 disponible

### Ejecutar la Aplicación
```bash
# Usar el wrapper de Maven incluido
./mvnw.cmd clean spring-boot:run
```

### Acceder a la Aplicación
- **URL Principal**: http://localhost:8080
- **Panel de Admin**: http://localhost:8080/admin.html (requiere rol ADMIN)

## 👤 Usuarios Predefinidos

La aplicación crea automáticamente usuarios de prueba:

### Usuario Administrador
- **Usuario**: `admin`
- **Contraseña**: `admin`
- **Rol**: ADMIN
- **Capacidades**: Gestión completa de productos y pedidos

### Usuario Cliente
- **Usuario**: `cliente`
- **Contraseña**: `cliente`
- **Rol**: CLIENTE
- **Capacidades**: Ver productos, crear pedidos, ver historial

## 📦 Productos de Prueba

La aplicación incluye 10 productos de prueba:
- Laptop Gaming ($1,299.99)
- Smartphone Android ($599.99)
- Auriculares Bluetooth ($199.99)
- Tablet 10 pulgadas ($299.99)
- Teclado Mecánico ($89.99)
- Mouse Gaming ($59.99)
- Monitor 4K ($399.99)
- Webcam HD ($79.99)
- Cargador Inalámbrico ($29.99)
- Disco SSD 1TB ($99.99)

## 🔐 Funcionalidades por Rol

### 👨‍💼 Administrador (ADMIN)
1. **Dashboard Administrativo**
   - Estadísticas generales (productos, pedidos, usuarios)
   - Acciones rápidas

2. **Gestión de Productos**
   - Crear nuevos productos
   - Editar productos existentes
   - Eliminar productos
   - Gestionar stock

3. **Gestión de Pedidos**
   - Ver todos los pedidos del sistema
   - Cambiar estados (PENDIENTE → ENVIADO)
   - Filtrar por estado

4. **Vista de Usuarios**
   - Lista de todos los usuarios registrados

### 👨‍💻 Cliente (CLIENTE)
1. **Catálogo de Productos**
   - Ver todos los productos disponibles
   - Buscar productos
   - Ver detalles y stock

2. **Carrito de Compras**
   - Añadir productos al carrito
   - Modificar cantidades
   - Ver total del carrito

3. **Gestión de Pedidos**
   - Crear nuevos pedidos
   - Ver historial de pedidos propios
   - Seguimiento de estados

## 🛡️ Seguridad Implementada

### Autenticación JWT
- Tokens seguros con expiración
- Headers de autorización requeridos
- Refresh automático en el frontend

### Control de Acceso por Endpoints
```
GET /api/productos/* -> CLIENTE, ADMIN
POST /api/productos -> ADMIN únicamente
PUT /api/productos/* -> ADMIN únicamente
DELETE /api/productos/* -> ADMIN únicamente

GET /api/pedidos -> ADMIN (todos los pedidos)
GET /api/pedidos/mios -> CLIENTE (pedidos propios)
POST /api/pedidos -> CLIENTE únicamente
PUT /api/pedidos/* -> ADMIN únicamente
```

### Protección de Páginas
- `/admin.html` -> Solo ADMIN
- `/productos.html, /pedidos.html` -> CLIENTE y ADMIN
- Redirección automática si no autenticado

## 📱 Características del Frontend

### Diseño Responsive
- Adaptable a móviles, tablets y escritorio
- CSS Grid y Flexbox para layouts modernos

### Experiencia de Usuario
- Carrito flotante con contador
- Mensajes de notificación
- Formularios interactivos
- Filtros y búsquedas en tiempo real

### Estados Visuales
- Badges de estado para pedidos
- Indicadores de stock
- Botones con estados (loading, success, error)

## 🔧 Estructura del Proyecto

```
src/main/
├── java/com/ejemplo/tienda_online/
│   ├── config/          # Configuración (Security, CORS, Data Init)
│   ├── controller/      # REST Controllers
│   ├── model/          # Entidades (Usuario, Producto, Pedido)
│   ├── repository/     # Repositorios MongoDB
│   ├── security/       # JWT Filter, UserDetails
│   └── service/        # Lógica de negocio
├── resources/
│   ├── static/         # Frontend (HTML, CSS, JS)
│   └── application.properties
```

## 🎯 Casos de Uso Principales

### Flujo Cliente
1. Registro/Login → Ver productos → Añadir al carrito → Crear pedido → Ver historial

### Flujo Administrador
1. Login → Dashboard → Gestionar productos → Revisar pedidos → Cambiar estados

## 🔍 Testing y Debugging

### Endpoints de Desarrollo
- `/diagnostico.html` - Información del sistema
- Logs detallados en consola
- Manejo de errores con mensajes descriptivos

### Verificación de Funcionamiento
```bash
# Verificar que la app responde
curl http://localhost:8080

# Probar autenticación
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

## 📚 Documentación Adicional

- **Documentación Completa**: `/documentacion.html`
- **README**: Información técnica del proyecto
- **Código**: Comentarios en español en todos los archivos

## 🎉 Próximos Pasos

La aplicación está completamente funcional y lista para:
- ✅ Desarrollo local
- ✅ Testing de funcionalidades
- ✅ Demostración de capacidades
- ✅ Base para extensiones futuras

¡Disfruta explorando la tienda online! 🛍️
