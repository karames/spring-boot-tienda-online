package com.ejemplo.tienda_online.service;

import com.ejemplo.tienda_online.model.Pedido;
import com.ejemplo.tienda_online.model.Producto;
import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.repository.PedidoRepository;
import com.ejemplo.tienda_online.repository.ProductoRepository;
import com.ejemplo.tienda_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public List<Pedido> getAll() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> getPedidosUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return pedidoRepository.findByUsuarioId(usuario.getId());
    }

    public Pedido crearPedido(Pedido pedido) {
        pedido.setId(null); // Deja que Mongo genere el id

        double total = 0.0;

        // Validar stock disponible y calcular total
        for (Pedido.ItemPedido item : pedido.getProductos()) {
            Optional<Producto> productoOpt = productoRepository.findById(item.getProductoId());
            if (productoOpt.isEmpty()) {
                throw new RuntimeException("Producto no encontrado: " + item.getProductoId());
            }

            Producto producto = productoOpt.get();
            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() +
                    ". Stock disponible: " + producto.getStock() + ", solicitado: " + item.getCantidad());
            }

            // Establecer precio unitario y calcular subtotal
            item.setPrecioUnitario(producto.getPrecio());
            total += producto.getPrecio() * item.getCantidad();
        }

        // Obtiene el usuario actual y asigna su ID al pedido
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        pedido.setUsuarioId(usuario.getId());

        // Establece fecha actual, estado inicial y total
        pedido.setFecha(java.time.Instant.now());
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);
        pedido.setTotal(total);

        // Guardar el pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Actualizar stock de productos
        for (Pedido.ItemPedido item : pedido.getProductos()) {
            Producto producto = productoRepository.findById(item.getProductoId()).get();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }

        return pedidoGuardado;
    }

    public Pedido cambiarEstado(String id, String estado) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            try {
                Pedido.EstadoPedido nuevoEstado = Pedido.EstadoPedido.valueOf(estado.toUpperCase());
                pedido.setEstado(nuevoEstado);
                return pedidoRepository.save(pedido);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Estado de pedido no válido. Valores permitidos: PENDIENTE, ENVIADO, ENTREGADO, CANCELADO");
            }
        }
        throw new RuntimeException("Pedido no encontrado");
    }
}
