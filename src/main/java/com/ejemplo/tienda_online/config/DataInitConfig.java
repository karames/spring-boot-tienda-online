package com.ejemplo.tienda_online.config;

import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.model.Producto;
import com.ejemplo.tienda_online.repository.UsuarioRepository;
import com.ejemplo.tienda_online.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitConfig {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            System.out.println("Iniciando la creación de datos por defecto (usuarios y productos)...");

            try {
                // Crear usuario administrador si no existe
                if (usuarioRepository.findByUsername("admin").isEmpty()) {
                    String adminPwd = "admin";
                    String hashedPwd = passwordEncoder.encode(adminPwd);

                    Usuario admin = Usuario.builder()
                            .username("admin")
                            .email("admin@ejemplo.com")
                            .password(hashedPwd)
                            .roles(List.of(Usuario.RolUsuario.ADMIN))
                            .build();

                    Usuario savedAdmin = usuarioRepository.save(admin);
                    System.out.println("Usuario administrador creado con éxito!");
                    System.out.println("ID: " + savedAdmin.getId());
                    System.out.println("Username: admin");
                    System.out.println("Password: " + adminPwd + " (hasheada como: " + hashedPwd + ")");

                    // Verificamos que la contraseña se haya guardado correctamente
                    boolean matches = passwordEncoder.matches(adminPwd, savedAdmin.getPassword());
                    System.out.println("Verificación de contraseña: " + (matches ? "CORRECTA" : "INCORRECTA"));
                } else {
                    System.out.println("El usuario 'admin' ya existe. No se ha creado de nuevo.");
                }

                // Crear usuario cliente si no existe
                if (usuarioRepository.findByUsername("cliente").isEmpty()) {
                    String clientePwd = "cliente";
                    String hashedPwd = passwordEncoder.encode(clientePwd);

                    Usuario cliente = Usuario.builder()
                            .username("cliente")
                            .email("cliente@ejemplo.com")
                            .password(hashedPwd)
                            .roles(Collections.singletonList(Usuario.RolUsuario.CLIENTE))
                            .build();

                    Usuario savedCliente = usuarioRepository.save(cliente);
                    System.out.println("Usuario cliente creado con éxito!");
                    System.out.println("ID: " + savedCliente.getId());
                    System.out.println("Username: cliente");
                    System.out.println("Password: " + clientePwd + " (hasheada como: " + hashedPwd + ")");

                    // Verificamos que la contraseña se haya guardado correctamente
                    boolean matches = passwordEncoder.matches(clientePwd, savedCliente.getPassword());
                    System.out.println("Verificación de contraseña: " + (matches ? "CORRECTA" : "INCORRECTA"));
                } else {
                    System.out.println("El usuario 'cliente' ya existe. No se ha creado de nuevo.");
                }

                // Crear productos de prueba si no existen
                if (productoRepository.count() == 0) {
                    System.out.println("Creando productos de prueba...");

                    List<Producto> productos = List.of(
                        Producto.builder()
                            .nombre("Laptop Gaming")
                            .descripcion("Laptop para juegos con tarjeta gráfica dedicada RTX 4060")
                            .precio(1299.99)
                            .stock(15)
                            .build(),

                        Producto.builder()
                            .nombre("Smartphone Android")
                            .descripcion("Teléfono inteligente con 128GB de almacenamiento y cámara de 48MP")
                            .precio(599.99)
                            .stock(25)
                            .build(),

                        Producto.builder()
                            .nombre("Auriculares Bluetooth")
                            .descripcion("Auriculares inalámbricos con cancelación de ruido")
                            .precio(199.99)
                            .stock(40)
                            .build(),

                        Producto.builder()
                            .nombre("Tablet 10 pulgadas")
                            .descripcion("Tablet con pantalla táctil de 10 pulgadas y 64GB de almacenamiento")
                            .precio(299.99)
                            .stock(20)
                            .build(),

                        Producto.builder()
                            .nombre("Teclado Mecánico")
                            .descripcion("Teclado mecánico RGB para gaming con switches azules")
                            .precio(89.99)
                            .stock(30)
                            .build(),

                        Producto.builder()
                            .nombre("Mouse Gaming")
                            .descripcion("Mouse óptico para gaming con 16000 DPI y RGB")
                            .precio(59.99)
                            .stock(35)
                            .build(),

                        Producto.builder()
                            .nombre("Monitor 4K")
                            .descripcion("Monitor 4K UHD de 27 pulgadas con HDR")
                            .precio(399.99)
                            .stock(12)
                            .build(),

                        Producto.builder()
                            .nombre("Webcam HD")
                            .descripcion("Cámara web Full HD 1080p con micrófono integrado")
                            .precio(79.99)
                            .stock(18)
                            .build(),

                        Producto.builder()
                            .nombre("Cargador Inalámbrico")
                            .descripcion("Cargador inalámbrico rápido compatible con iPhone y Android")
                            .precio(29.99)
                            .stock(50)
                            .build(),

                        Producto.builder()
                            .nombre("Disco SSD 1TB")
                            .descripcion("Disco sólido SSD de 1TB con conexión SATA III")
                            .precio(99.99)
                            .stock(25)
                            .build()
                    );

                    List<Producto> productosCreados = productoRepository.saveAll(productos);
                    System.out.println("Se han creado " + productosCreados.size() + " productos de prueba con éxito!");

                    productosCreados.forEach(producto ->
                        System.out.println("Producto: " + producto.getNombre() + " - Precio: $" + producto.getPrecio() + " - Stock: " + producto.getStock())
                    );
                } else {
                    System.out.println("Ya existen " + productoRepository.count() + " productos en la base de datos.");
                }

            } catch (Exception e) {
                System.err.println("ERROR al crear datos por defecto: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
