# Tienda Online - Spring Boot + MongoDB + JWT

## Descripción

Tienda Online es una aplicación web moderna y responsiva desarrollada con Spring Boot, que permite la gestión de productos, pedidos y usuarios, soportando autenticación y autorización mediante JWT. El frontend está completamente unificado y minimalista, y consume una API REST protegida. Los datos se almacenan en MongoDB.

## Funcionalidad principal

- **Gestión de productos:** Visualización, alta, edición y eliminación de productos (solo ADMIN).
- **Gestión de pedidos:** Los usuarios CLIENTE pueden crear y consultar sus pedidos. Los ADMIN pueden ver y gestionar todos los pedidos.
- **Autenticación y roles:** Registro y login de usuarios. Acceso diferenciado según rol (CLIENTE o ADMIN) mediante JWT.
- **Panel de administración:** Dashboard con estadísticas dinámicas y acceso a gestión avanzada (solo ADMIN).
- **Frontend unificado:** Interfaz limpia, moderna y responsiva en todas las páginas principales, con navegación adaptada al rol.
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
  - Usuario: `admin`
  - Contraseña: `admin`
- **CLIENTE:** Puede ver productos y gestionar sus propios pedidos.
  - Usuario: `cliente`
  - Contraseña: `cliente`

## Navegación y experiencia de usuario

- El enlace "Admin" aparece como la primera opción en la barra de navegación para usuarios administradores y su visibilidad se controla dinámicamente por JavaScript.
- Tras el login como admin, el usuario es redirigido automáticamente al panel de administración.
- El dashboard del panel admin muestra estadísticas relevantes de la base de datos, cargadas dinámicamente.
- La experiencia de usuario y las opciones de navegación se adaptan automáticamente según el rol (admin o cliente).

## Documentación adicional

Consulta la página `documentacion.html` en el frontend para más detalles técnicos y de uso.

- **Stack tecnológico:**
  - Spring Boot 3.x
  - Spring Security con JWT
  - JPA/Hibernate
  - MongoDB
  - Frontend HTML5/CSS3/JS

---

Proyecto realizado en 2025. Diseño y código unificados, limpios y listos para producción.
