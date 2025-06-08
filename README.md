# 🛍️ Tienda Online - Spring Boot

Una aplicación web completa de tienda online desarrollada con **Spring Boot** y **MongoDB**, que incluye autenticación JWT, gestión de productos, carrito de compras y sistema de pedidos.

## 🚀 Características Principales

- ✅ **Autenticación y Autorización**: Sistema JWT con roles (ADMIN/CLIENTE)
- ✅ **Gestión de Productos**: CRUD completo para administradores
- ✅ **Carrito de Compras**: Carrito flotante interactivo
- ✅ **Sistema de Pedidos**: Creación y gestión de pedidos con estados
- ✅ **Panel de Administración**: Dashboard con estadísticas y gestión avanzada
- ✅ **API REST**: Endpoints protegidos con control de roles granular
- ✅ **Interfaz Responsiva**: Diseño moderno y adaptable a dispositivos móviles
- ✅ **Base de Datos NoSQL**: MongoDB para almacenamiento flexible

## 🛠️ Tecnologías Utilizadas

### Backend

- **Spring Boot 3.2.x**
- **Spring Security** (JWT Authentication)
- **Spring Data MongoDB**
- **Spring Web**
- **Lombok**
- **Maven**

### Frontend

- **HTML5**
- **CSS3** (Grid, Flexbox, Gradients)
- **JavaScript** (Vanilla ES6+)
- **Font Awesome** (iconos)

### Base de Datos

- **MongoDB** (NoSQL)

## 📋 Requisitos Previos

- ☕ **Java 17** o superior
- 📦 **Maven 3.6** o superior
- 🍃 **MongoDB 4.4** o superior
- 🌐 **Navegador web moderno**

## 🚀 Instalación y Ejecución

### 1. Clonar el Repositorio

```bash
git clone <url-del-repositorio>
cd tienda-online
```

### 2. Configurar MongoDB

Asegúrate de que MongoDB esté ejecutándose en `localhost:27017` (configuración por defecto).

### 3. Ejecutar la Aplicación

```bash
mvn clean spring-boot:run
```

### 4. Acceder a la Aplicación

Abre tu navegador web y ve a: **http://localhost:8080**

## 👥 Usuarios de Prueba

La aplicación incluye usuarios de prueba precargados:

### 🛡️ Administrador

- **Usuario**: `admin`
- **Contraseña**: `admin`
- **Permisos**: Gestión completa de productos y pedidos

### 🛒 Cliente

- **Usuario**: `cliente`
- **Contraseña**: `cliente`
- **Permisos**: Comprar productos y gestionar pedidos propios

## 📦 Productos de Prueba

El sistema incluye 10 productos de tecnología precargados:

1. **Laptop Gaming** - $1,299.99 (15 unidades)
2. **Smartphone Android** - $599.99 (25 unidades)
3. **Auriculares Bluetooth** - $199.99 (40 unidades)
4. **Tablet 10 pulgadas** - $299.99 (20 unidades)
5. **Teclado Mecánico** - $89.99 (30 unidades)
6. **Mouse Gaming** - $59.99 (35 unidades)
7. **Monitor 4K** - $399.99 (12 unidades)
8. **Webcam HD** - $79.99 (18 unidades)
9. **Cargador Inalámbrico** - $29.99 (50 unidades)
10. **Disco SSD 1TB** - $99.99 (25 unidades)

## 🔌 API REST Endpoints

### 🔐 Autenticación

- `POST /api/auth/register` - Registro de usuarios
- `POST /api/auth/login` - Inicio de sesión (retorna JWT)

### 📦 Productos

- `GET /api/productos` - Listar productos (público)
- `GET /api/productos/{id}` - Obtener producto específico (público)
- `POST /api/productos` - Crear producto (ADMIN)
- `PUT /api/productos/{id}` - Actualizar producto (ADMIN)
- `DELETE /api/productos/{id}` - Eliminar producto (ADMIN)

### 📋 Pedidos

- `GET /api/pedidos` - Listar pedidos (según rol)
- `POST /api/pedidos` - Crear pedido (CLIENTE)
- `PUT /api/pedidos/{id}/estado` - Cambiar estado (ADMIN)

## 📱 Funcionalidades por Rol

### 👨‍💼 Administrador (ADMIN)

- ✅ Dashboard con estadísticas
- ✅ CRUD completo de productos
- ✅ Ver todos los pedidos del sistema
- ✅ Cambiar estados de pedidos (PENDIENTE ↔ ENVIADO)
- ✅ Gestión de inventario y stock

### 🛒 Cliente (CLIENTE)

- ✅ Ver catálogo de productos
- ✅ Añadir productos al carrito
- ✅ Realizar pedidos
- ✅ Ver historial de pedidos propios
- ✅ Seguimiento de estado de pedidos

## 🏗️ Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/ejemplo/tienda_online/
│   │   ├── config/          # Configuraciones (Security, Data Init)
│   │   ├── controller/      # Controladores REST
│   │   ├── model/          # Entidades (Usuario, Producto, Pedido)
│   │   ├── repository/     # Repositorios MongoDB
│   │   ├── service/        # Lógica de negocio
│   │   ├── security/       # Configuración de seguridad
│   │   └── util/           # Utilidades (JWT)
│   └── resources/
│       ├── static/         # Frontend (HTML, CSS, JS)
│       └── application.properties
└── test/                   # Tests unitarios
```

## 🎨 Capturas de Pantalla

### 🏠 Página Principal

- Dashboard de bienvenida con características principales
- Enlaces rápidos a todas las secciones
- Información de usuarios de prueba

### 📦 Catálogo de Productos

- Vista en grid responsiva
- Carrito flotante interactivo
- Formularios de administración (ADMIN)

### 📋 Gestión de Pedidos

- Vista diferenciada por rol
- Estados visuales con badges
- Filtros y búsqueda

### ⚙️ Panel de Administración

- Dashboard con estadísticas en tiempo real
- Navegación por pestañas
- Gestión completa del sistema

## 🔧 Configuración

### Base de Datos

El archivo `application.properties` contiene la configuración de MongoDB:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/tienda_online
spring.data.mongodb.database=tienda_online
```

### Seguridad JWT

- Los tokens JWT tienen una validez configurable
- Las contraseñas se encriptan con BCrypt
- Control de acceso granular por endpoint y método HTTP

## 🚧 Desarrollo

### Ejecutar en Modo Desarrollo

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Ejecutar Tests

```bash
mvn test
```

### Construir para Producción

```bash
mvn clean package
java -jar target/tienda-online-0.0.1-SNAPSHOT.jar
```

## 📝 Notas Adicionales

- **Datos de Prueba**: Se cargan automáticamente al iniciar la aplicación
- **Seguridad**: Todos los endpoints críticos están protegidos
- **Responsive**: La interfaz se adapta a dispositivos móviles
- **Escalabilidad**: Arquitectura preparada para crecimiento
- **Mantenimiento**: Código bien documentado y organizado

## 🤝 Contribuciones

Este proyecto está desarrollado con fines educativos. Las mejoras y sugerencias son bienvenidas.

## 📄 Licencia

Este proyecto es de uso educativo y demostrativo.

---

**Desarrollado con ❤️ usando Spring Boot y MongoDB**
