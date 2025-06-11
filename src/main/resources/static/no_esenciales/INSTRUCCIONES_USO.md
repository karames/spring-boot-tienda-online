# 🚀 Tienda Online - Instrucciones de Uso

## ✅ Estado del Proyecto: COMPLETADO

La aplicación está **completamente funcional** y ejecutándose en:
**🌐 http://localhost:8080**

---

## 📋 Checklist Final - Todas las Funcionalidades Implementadas

### ✅ Backend (Spring Boot)

- [x] **Spring Security** configurado con JWT
- [x] **MongoDB** como base de datos NoSQL
- [x] **CRUD completo** para Productos y Pedidos
- [x] **Control de roles** ADMIN/CLIENTE por endpoint
- [x] **API REST** completamente funcional
- [x] **Datos de prueba** automáticos (usuarios + productos)

### ✅ Frontend (HTML/CSS/JS)

- [x] **Interfaz responsive** y moderna
- [x] **Carrito de compras** funcional
- [x] **Panel de administración** completo
- [x] **Gestión de pedidos** diferenciada por rol
- [x] **Autenticación JWT** en frontend
- [x] **Notificaciones** y mensajes de usuario

### ✅ Seguridad

- [x] **Endpoints protegidos** por rol
- [x] **JWT tokens** seguros
- [x] **Validación** de permisos
- [x] **Manejo de errores** apropiado

---

## 🔐 Usuarios de Prueba

### 👨‍💼 Administrador

```
Usuario: admin
Contraseña: admin
Rol: ADMIN
URL: http://localhost:8080/admin.html
```

### 👤 Cliente

```
Usuario: cliente
Contraseña: cliente
Rol: CLIENTE
URL: http://localhost:8080/productos.html
```

---

## 🛍️ Guía de Uso Paso a Paso

### Para Clientes:

1. **Acceder** → http://localhost:8080
2. **Login** → Usar credenciales: `cliente` / `cliente`
3. **Ver Productos** → Explorar catálogo de 10 productos
4. **Añadir al Carrito** → Seleccionar productos y cantidades
5. **Crear Pedido** → Finalizar compra desde el carrito
6. **Ver Pedidos** → Revisar historial en "Mis Pedidos"

### Para Administradores:

1. **Acceder** → http://localhost:8080
2. **Login** → Usar credenciales: `admin` / `admin`
3. **Dashboard** → Ver estadísticas en `/admin.html`
4. **Gestionar Productos** → CRUD completo desde `/productos.html`
5. **Gestionar Pedidos** → Cambiar estados en `/pedidos.html`
6. **Ver Usuarios** → Lista de usuarios registrados

---

## 🔧 Ejemplos de API

### Autenticación

```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# Respuesta: {"token":"JWT_TOKEN_HERE","role":"ADMIN"}
```

### Productos (requiere autenticación)

```bash
# Obtener productos
curl -X GET http://localhost:8080/api/productos \
  -H "Authorization: Bearer JWT_TOKEN"

# Crear producto (solo ADMIN)
curl -X POST http://localhost:8080/api/productos \
  -H "Authorization: Bearer JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Nuevo Producto","descripcion":"Descripción","precio":99.99,"stock":10}'
```

### Pedidos (requiere autenticación)

```bash
# Ver mis pedidos (CLIENTE)
curl -X GET http://localhost:8080/api/pedidos/mios \
  -H "Authorization: Bearer JWT_TOKEN"

# Ver todos los pedidos (ADMIN)
curl -X GET http://localhost:8080/api/pedidos \
  -H "Authorization: Bearer JWT_TOKEN"
```

---

## 📊 Productos Incluidos

La aplicación incluye 10 productos de prueba:

| Producto                 | Precio    | Stock |
| ------------------------ | --------- | ----- |
| 💻 Laptop Gaming         | $1,299.99 | 15    |
| 📱 Smartphone Android    | $599.99   | 25    |
| 🎧 Auriculares Bluetooth | $199.99   | 40    |
| 📱 Tablet 10 pulgadas    | $299.99   | 20    |
| ⌨️ Teclado Mecánico      | $89.99    | 30    |
| 🖱️ Mouse Gaming          | $59.99    | 35    |
| 🖥️ Monitor 4K            | $399.99   | 12    |
| 📹 Webcam HD             | $79.99    | 18    |
| 🔌 Cargador Inalámbrico  | $29.99    | 50    |
| 💾 Disco SSD 1TB         | $99.99    | 25    |

---

## 🎯 Funcionalidades Destacadas

### 🛒 Carrito de Compras

- Carrito flotante con contador
- Persistencia durante la sesión
- Validación de stock automática
- Cálculo de totales en tiempo real

### 📋 Gestión de Pedidos

- Estados: PENDIENTE → ENVIADO
- Vista diferenciada por rol
- Filtros por estado
- Historial completo

### 🛡️ Seguridad Robusta

- JWT con expiración
- Control granular por endpoint
- Validación de roles
- Manejo de errores específicos

### 📱 Diseño Moderno

- Responsive design
- Iconos y emojis
- Colores y tipografía consistentes
- Experiencia de usuario optimizada

---

## 🔄 Comandos de Desarrollo

```bash
# Compilar proyecto
./mvnw.cmd clean compile

# Ejecutar aplicación
./mvnw.cmd clean spring-boot:run

# Verificar que funciona
curl http://localhost:8080
```

---

## 📚 Archivos de Documentación

- `README.md` - Información técnica del proyecto
- `GUIA_COMPLETA.md` - Guía detallada de funcionalidades
- `documentacion.html` - Documentación web integrada
- Este archivo - Instrucciones de uso inmediato

---

## 🎉 Proyecto Completado

✅ **La aplicación está lista para:**

- Demostración completa
- Desarrollo adicional
- Testing de funcionalidades
- Despliegue en producción (con configuraciones adicionales)

✅ **Todas las especificaciones cumplidas:**

- Backend Spring Boot ✅
- MongoDB NoSQL ✅
- JWT Authentication ✅
- Control de roles ✅
- Frontend funcional ✅
- CRUD completo ✅
- Datos de prueba ✅

🚀 **¡Listo para usar! Accede a http://localhost:8080 y explora todas las funcionalidades!**
