package com.ejemplo.tienda_online.config;

import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.model.Producto;
import com.ejemplo.tienda_online.model.Rol;
import com.ejemplo.tienda_online.model.Pedido;
import com.ejemplo.tienda_online.repository.UsuarioRepository;
import com.ejemplo.tienda_online.repository.ProductoRepository;
import com.ejemplo.tienda_online.repository.RolRepository;
import com.ejemplo.tienda_online.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.security.SecureRandom;

@Configuration
@RequiredArgsConstructor
public class DataInitConfig {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final RolRepository rolRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            System.out.println("Iniciando la creación de datos por defecto (roles, productos, usuarios y pedidos)...");
            try {
                poblarRolesSiNoExisten();
                crearProductosDePruebaSiNoExisten();
                crearUsuarioSiNoExiste("admin", "admin@ejemplo.com", "admin", List.of(Usuario.RolUsuario.ADMIN));
                crearUsuarioSiNoExiste("cliente", "cliente@ejemplo.com", "cliente", Collections.singletonList(Usuario.RolUsuario.CLIENTE));
                poblarPedidosDePrueba();
            } catch (Exception e) {
                System.err.println("ERROR al crear datos por defecto: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    private void poblarRolesSiNoExisten() {
        if (rolRepository.count() > 0) {
            rolRepository.deleteAll();
            System.out.println("Todos los roles eliminados");
        }
        Rol rolAdmin = new Rol("ADMIN", "ROLE_ADMIN");
        Rol rolCliente = new Rol("CLIENTE", "ROLE_CLIENTE");
        rolRepository.save(rolAdmin);
        System.out.println("Rol ADMIN creado");
        rolRepository.save(rolCliente);
        System.out.println("Rol CLIENTE creado");
    }

    private void crearUsuarioSiNoExiste(String username, String email, String password, List<Usuario.RolUsuario> roles) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            System.out.println("El usuario '" + username + "' ya existe, no se crea de nuevo.");
            return;
        }
        String hashedPwd = passwordEncoder.encode(password);
        Usuario usuario = Usuario.builder()
                .username(username)
                .email(email)
                .password(hashedPwd)
                .roles(roles)
                .build();
        Usuario saved = usuarioRepository.save(usuario);
        System.out.printf("Usuario %s creado con éxito! ID: %s | Username: %s | Password: %s (hasheada)\n", username, saved.getId(), username, password);
        boolean matches = passwordEncoder.matches(password, saved.getPassword());
        System.out.println("Verificación de contraseña: " + (matches ? "CORRECTA" : "INCORRECTA"));
    }

    private void crearProductosDePruebaSiNoExisten() {
        if (productoRepository.count() > 0) {
            productoRepository.deleteAll();
            System.out.println("Todos los productos eliminados");
        }
        System.out.println("Creando productos de prueba...");
        List<Producto> productos = List.of(
            Producto.builder().nombre("Laptop Gaming").descripcion("Laptop para juegos con tarjeta gráfica dedicada RTX 4060").precio(1299.99).stock(15).build(),
            Producto.builder().nombre("Smartphone Android").descripcion("Teléfono inteligente con 128GB de almacenamiento y cámara de 48MP").precio(599.99).stock(25).build(),
            Producto.builder().nombre("Auriculares Bluetooth").descripcion("Auriculares inalámbricos con cancelación de ruido").precio(199.99).stock(40).build(),
            Producto.builder().nombre("Tablet 10 pulgadas").descripcion("Tablet con pantalla táctil de 10 pulgadas y 64GB de almacenamiento").precio(299.99).stock(20).build(),
            Producto.builder().nombre("Teclado Mecánico").descripcion("Teclado mecánico RGB para gaming con switches azules").precio(89.99).stock(30).build(),
            Producto.builder().nombre("Mouse Gaming").descripcion("Mouse óptico para gaming con 16000 DPI y RGB").precio(59.99).stock(35).build(),
            Producto.builder().nombre("Monitor 4K").descripcion("Monitor 4K UHD de 27 pulgadas con HDR").precio(399.99).stock(12).build(),
            Producto.builder().nombre("Webcam HD").descripcion("Cámara web Full HD 1080p con micrófono integrado").precio(79.99).stock(18).build(),
            Producto.builder().nombre("Cargador Inalámbrico").descripcion("Cargador inalámbrico rápido compatible con iPhone y Android").precio(29.99).stock(50).build(),
            Producto.builder().nombre("Disco SSD 1TB").descripcion("Disco sólido SSD de 1TB con conexión SATA III").precio(99.99).stock(25).build()
        );
        List<Producto> productosCreados = productoRepository.saveAll(productos);
        System.out.println("Se han creado " + productosCreados.size() + " productos de prueba con éxito!");
        productosCreados.forEach(producto ->
            System.out.println("Producto: " + producto.getNombre() + " - Precio: $" + producto.getPrecio() + " - Stock: " + producto.getStock())
        );
    }

    private void poblarPedidosDePrueba() {
        if (pedidoRepository.count() > 0) {
            pedidoRepository.deleteAll();
            System.out.println("Todos los pedidos eliminados");
        }
        System.out.println("Creando pedidos de prueba...");
        var clienteOpt = usuarioRepository.findByUsername("cliente");
        if (clienteOpt.isEmpty()) {
            System.out.println("No se encontró el usuario cliente para asociar pedidos");
            return;
        }
        var cliente = clienteOpt.get();
        var productos = productoRepository.findAll();
        if (productos.isEmpty()) {
            System.out.println("No hay productos para asociar a los pedidos");
            return;
        }
        SecureRandom random = new SecureRandom();
        for (int i = 1; i <= 5; i++) {
            int numProductos = 1 + random.nextInt(5); // entre 1 y 5 productos
            List<Pedido.ItemPedido> items = new java.util.ArrayList<>();
            java.util.Collections.shuffle(productos, random);
            for (int j = 0; j < numProductos; j++) {
                var prod = productos.get(j);
                int cantidad = 1 + random.nextInt(3); // cantidad entre 1 y 3
                items.add(Pedido.ItemPedido.builder()
                    .productoId(prod.getId())
                    .nombreProducto(prod.getNombre())
                    .precioUnitario(prod.getPrecio() != null ? prod.getPrecio() : 0.0)
                    .cantidad(cantidad)
                    .build());
            }
            Pedido.EstadoPedido estado = random.nextBoolean() ? Pedido.EstadoPedido.PENDIENTE : Pedido.EstadoPedido.ENVIADO;
            Pedido pedido = Pedido.builder()
                    .usuarioId(cliente.getId())
                    .productos(items)
                    .fecha(java.time.Instant.now())
                    .estado(estado)
                    .build();
            pedidoRepository.save(pedido);
            System.out.println("Pedido de prueba " + i + " creado con estado " + estado);
        }
    }
}
