# Tienda Online - Spring Boot + MongoDB + JWT

## Descripción

Tienda Online es una aplicación web desarrollada con Spring Boot que permite la gestión de productos y pedidos, soportando autenticación de usuarios con roles diferenciados (CLIENTE, ADMIN). El frontend es moderno, minimalista y unificado, y consume una API REST protegida mediante JWT. Los datos se almacenan en MongoDB.

## Funcionalidad principal

- **Gestión de productos:** Visualización, alta, edición y eliminación de productos (solo ADMIN).
- **Gestión de pedidos:** Los usuarios CLIENTE pueden crear y consultar sus pedidos. Los ADMIN pueden ver todos los pedidos.
- **Autenticación y roles:** Registro y login de usuarios. Acceso diferenciado según rol (CLIENTE o ADMIN) mediante JWT.
- **Frontend minimalista:** Interfaz limpia, responsiva y unificada en todas las páginas principales.
- **API REST segura:** Todas las operaciones sensibles requieren autenticación JWT.
- **Persistencia en MongoDB:** Productos, usuarios y pedidos se almacenan en una base de datos MongoDB.

## Estructura principal del proyecto

- `src/main/resources/static/` - Archivos frontend esenciales (HTML, CSS, JS)
- `src/main/java/com/ejemplo/tienda_online/` - Lógica backend (controladores, servicios, modelos)
- `src/main/resources/application.properties` - Configuración de la aplicación
- `pom.xml` - Dependencias y configuración de Maven

## Cómo ejecutar

1. Asegúrate de tener MongoDB en ejecución.
2. Compila el proyecto:
   ```powershell
   .\mvnw.cmd clean compile
   ```
3. Ejecuta la aplicación:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
4. Accede a la tienda en [http://localhost:8080](http://localhost:8080)

## Roles y credenciales de ejemplo

- **ADMIN:** Acceso total a productos, pedidos y usuarios.
- **CLIENTE:** Puede ver productos y gestionar sus propios pedidos.

## Documentación adicional

Consulta la página `documentacion.html` en el frontend para más detalles técnicos y de uso.

---

Proyecto realizado en 2025. Diseño y código unificados, limpios y listos para producción.
