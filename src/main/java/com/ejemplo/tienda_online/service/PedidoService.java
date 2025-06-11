package com.ejemplo.tienda_online.service;

import com.ejemplo.tienda_online.dto.PedidoResponse;
import com.ejemplo.tienda_online.exception.BusinessException;
import com.ejemplo.tienda_online.exception.ResourceNotFoundException;
import com.ejemplo.tienda_online.model.Pedido;
import com.ejemplo.tienda_online.model.Producto;
import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.repository.PedidoRepository;
import com.ejemplo.tienda_online.repository.ProductoRepository;
import com.ejemplo.tienda_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Servicio para la gestión de pedidos.
 * Maneja la creación, consulta y actualización de pedidos.
 *
 * @author Sistema Tienda Online
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    // Constantes para validación de negocio
    private static final int MAX_CANTIDAD_POR_ITEM = 100;
    private static final int MAX_ITEMS_POR_PEDIDO = 50;

    /**
     * Obtiene todos los pedidos del sistema.
     * Solo disponible para administradores.
     *
     * @return Lista completa de pedidos
     */
    public List<Pedido> getAll() {
        log.info("Obteniendo todos los pedidos del sistema");
        List<Pedido> pedidos = pedidoRepository.findAll();
        log.info("Se encontraron {} pedidos en total", pedidos.size());
        return pedidos;
    }

    /**
     * Obtiene los pedidos del usuario autenticado actualmente.
     *
     * @return Lista de pedidos del usuario actual
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public List<Pedido> getPedidosUsuarioActual() {
        log.info("Obteniendo pedidos del usuario autenticado");
        Usuario usuario = obtenerUsuarioActual();
        List<Pedido> pedidos = pedidoRepository.findByUsuarioId(usuario.getId());
        log.info("Usuario {} tiene {} pedidos", usuario.getUsername(), pedidos.size());
        return pedidos;
    }

    /**
     * Crea un nuevo pedido validando stock y calculando totales.
     *
     * @param pedido El pedido a crear
     * @return El pedido creado con ID asignado
     * @throws BusinessException si hay problemas de validación
     * @throws ResourceNotFoundException si algún producto no existe
     */
    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        log.info("Iniciando creación de nuevo pedido");

        // Validaciones iniciales
        validarPedidoBasico(pedido);

        pedido.setId(null); // Deja que MongoDB genere el ID

        // Validar stock y calcular total (solo para validación, no guardar el total)
        validarStockYCalcularTotal(pedido);

        // Asignar usuario actual
        Usuario usuario = obtenerUsuarioActual();
        pedido.setUsuarioId(usuario.getId());

        // Establecer metadatos del pedido
        pedido.setFecha(Instant.now());
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);

        // Guardar el pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        log.info("Pedido creado exitosamente con ID: {}", pedidoGuardado.getId());

        // Actualizar stock de productos
        actualizarStockProductos(pedido);

        log.info("Pedido {} procesado completamente.", pedidoGuardado.getId());

        return pedidoGuardado;
    }

    /**
     * Cambia el estado de un pedido existente.
     *
     * @param id ID del pedido
     * @param estado Nuevo estado del pedido
     * @return El pedido actualizado
     * @throws ResourceNotFoundException si el pedido no existe
     * @throws BusinessException si el estado no es válido
     */
    @Transactional
    public Pedido cambiarEstado(String id, String estado) {
        log.info("Cambiando estado del pedido {} a {}", id, estado);

        if (id == null || id.trim().isEmpty()) {
            throw new BusinessException("El ID del pedido es requerido");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));

        Pedido.EstadoPedido estadoAnterior = pedido.getEstado();
        Pedido.EstadoPedido nuevoEstado = validarYConvertirEstado(estado);

        // Validar transición de estado
        validarTransicionEstado(estadoAnterior, nuevoEstado);

        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        log.info("Estado del pedido {} cambiado de {} a {}",
                 id, estadoAnterior, nuevoEstado);

        return pedidoActualizado;
    }

    /**
     * Busca un pedido por ID.
     *
     * @param id ID del pedido
     * @return El pedido encontrado
     * @throws ResourceNotFoundException si el pedido no existe
     */
    public Pedido buscarPorId(String id) {
        log.info("Buscando pedido por ID: {}", id);

        if (id == null || id.trim().isEmpty()) {
            throw new BusinessException("El ID del pedido es requerido");
        }

        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
    }

    /**
     * Cancela un pedido y restaura el stock.
     *
     * @param id ID del pedido a cancelar
     * @return El pedido cancelado
     * @throws ResourceNotFoundException si el pedido no existe
     * @throws BusinessException si el pedido no se puede cancelar
     */
    @Transactional
    public Pedido cancelarPedido(String id) {
        log.info("Cancelando pedido: {}", id);

        Pedido pedido = buscarPorId(id);

        if (pedido.getEstado() == Pedido.EstadoPedido.ENTREGADO) {
            throw new BusinessException("No se puede cancelar un pedido ya entregado");
        }

        if (pedido.getEstado() == Pedido.EstadoPedido.CANCELADO) {
            throw new BusinessException("El pedido ya está cancelado");
        }

        // Restaurar stock
        restaurarStockProductos(pedido);

        // Cambiar estado
        pedido.setEstado(Pedido.EstadoPedido.CANCELADO);
        Pedido pedidoCancelado = pedidoRepository.save(pedido);

        log.info("Pedido {} cancelado exitosamente", id);
        return pedidoCancelado;
    }

    /**
     * Obtiene todos los pedidos del sistema con información del usuario.
     *
     * @return Lista de pedidos con información de usuario
     */
    public List<PedidoResponse> getAllWithUserInfo() {
        log.info("Obteniendo todos los pedidos del sistema (con info de usuario)");
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream().map(pedido -> {
            Usuario usuario = usuarioRepository.findById(pedido.getUsuarioId()).orElse(null);
            return PedidoResponse.builder()
                .id(pedido.getId())
                .usuarioId(pedido.getUsuarioId())
                .nombreUsuario(usuario != null ? usuario.getUsername() : "-")
                .emailUsuario(usuario != null ? usuario.getEmail() : "-")
                .productos(pedido.getProductos().stream().map(item -> PedidoResponse.ItemPedidoResponse.builder()
                    .productoId(item.getProductoId())
                    .nombreProducto(item.getNombreProducto())
                    .cantidad(item.getCantidad())
                    .precioUnitario(item.getPrecioUnitario())
                    .subtotal(item.getPrecioUnitario() != null && item.getCantidad() != null ? item.getPrecioUnitario() * item.getCantidad() : 0.0)
                    // No es necesario calcular aquí el formateado, lo hace el DTO
                    .build()).collect(java.util.stream.Collectors.toList()))
                .fecha(pedido.getFecha())
                .estado(pedido.getEstado().name())
                .estadoDescripcion(pedido.getEstado().toString())
                .fechaActualizacion(pedido.getFechaActualizacion())
                .direccionEnvio(pedido.getDireccionEnvio())
                .notas(pedido.getNotas())
                .numeroSeguimiento(pedido.getNumeroSeguimiento())
                .fechaEstimadaEntrega(pedido.getFechaEstimadaEntrega())
                .fechaEntrega(pedido.getFechaEntrega())
                .motivoCancelacion(pedido.getMotivoCancelacion())
                .build();
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Obtiene los pedidos del usuario autenticado actualmente como PedidoResponse,
     * con precios formateados.
     *
     * @return Lista de pedidos del usuario actual con información formateada
     */
    public List<PedidoResponse> getPedidosUsuarioActualConFormato() {
        Usuario usuario = obtenerUsuarioActual();
        List<Pedido> pedidos = pedidoRepository.findByUsuarioId(usuario.getId());
        return pedidos.stream().map(pedido -> PedidoResponse.builder()
            .id(pedido.getId())
            .usuarioId(pedido.getUsuarioId())
            .nombreUsuario(usuario.getUsername())
            .emailUsuario(usuario.getEmail())
            .productos(pedido.getProductos().stream().map(item -> PedidoResponse.ItemPedidoResponse.builder()
                .productoId(item.getProductoId())
                .nombreProducto(item.getNombreProducto())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getPrecioUnitario() != null && item.getCantidad() != null ? item.getPrecioUnitario() * item.getCantidad() : 0.0)
                .build()).collect(java.util.stream.Collectors.toList()))
            .fecha(pedido.getFecha())
            .estado(pedido.getEstado().name())
            .estadoDescripcion(pedido.getEstado().toString())
            .fechaActualizacion(pedido.getFechaActualizacion())
            .direccionEnvio(pedido.getDireccionEnvio())
            .notas(pedido.getNotas())
            .numeroSeguimiento(pedido.getNumeroSeguimiento())
            .fechaEstimadaEntrega(pedido.getFechaEstimadaEntrega())
            .fechaEntrega(pedido.getFechaEntrega())
            .motivoCancelacion(pedido.getMotivoCancelacion())
            .build()
        ).collect(java.util.stream.Collectors.toList());
    }

    // MÉTODOS PRIVADOS DE APOYO

    /**
     * Obtiene el usuario autenticado actual.
     */
    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new BusinessException("No hay usuario autenticado");
        }

        String username = auth.getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    /**
     * Valida que el pedido cumpla con las reglas básicas de negocio.
     */
    private void validarPedidoBasico(Pedido pedido) {
        if (pedido == null) {
            throw new BusinessException("El pedido no puede ser nulo");
        }

        if (pedido.getProductos() == null || pedido.getProductos().isEmpty()) {
            throw new BusinessException("El pedido debe contener al menos un producto");
        }

        if (pedido.getProductos().size() > MAX_ITEMS_POR_PEDIDO) {
            throw new BusinessException("El pedido no puede tener más de " + MAX_ITEMS_POR_PEDIDO + " items");
        }

        // Validar cada item del pedido
        for (Pedido.ItemPedido item : pedido.getProductos()) {
            validarItemPedido(item);
        }
    }

    /**
     * Valida un item individual del pedido.
     */
    private void validarItemPedido(Pedido.ItemPedido item) {
        if (item == null) {
            throw new BusinessException("Los items del pedido no pueden ser nulos");
        }

        if (item.getProductoId() == null || item.getProductoId().trim().isEmpty()) {
            throw new BusinessException("Todo item debe tener un ID de producto válido");
        }
          if (item.getCantidad() <= 0) {
            throw new BusinessException("La cantidad de cada item debe ser mayor a cero");
        }

        if (item.getCantidad() > MAX_CANTIDAD_POR_ITEM) {
            throw new BusinessException("La cantidad por item no puede exceder " + MAX_CANTIDAD_POR_ITEM + " unidades");
        }
    }

    /**
     * Valida el stock disponible y calcula el total del pedido.
     */
    private void validarStockYCalcularTotal(Pedido pedido) {
        for (Pedido.ItemPedido item : pedido.getProductos()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + item.getProductoId()));

            // Validar stock disponible
            if (producto.getStock() < item.getCantidad()) {
                throw new BusinessException(
                    String.format("Stock insuficiente para el producto '%s'. Disponible: %d, solicitado: %d",
                        producto.getNombre(), producto.getStock(), item.getCantidad()));
            }
            // Establecer precio unitario y nombre del producto al momento de la compra
            Double precioUnitario = producto.getPrecio();
            item.setPrecioUnitario(precioUnitario);
            item.setNombreProducto(producto.getNombre());
        }
    }

    /**
     * Actualiza el stock de los productos después de crear un pedido.
     */
    private void actualizarStockProductos(Pedido pedido) {
        for (Pedido.ItemPedido item : pedido.getProductos()) {
            Producto producto = productoRepository.findById(item.getProductoId()).orElseThrow();
            int nuevoStock = producto.getStock() - item.getCantidad();
            producto.setStock(nuevoStock);
            productoRepository.save(producto);

            log.debug("Stock actualizado para producto {}: {} -> {}",
                     producto.getNombre(), producto.getStock() + item.getCantidad(), nuevoStock);
        }
    }

    /**
     * Restaura el stock de los productos al cancelar un pedido.
     */
    private void restaurarStockProductos(Pedido pedido) {
        for (Pedido.ItemPedido item : pedido.getProductos()) {
            Producto producto = productoRepository.findById(item.getProductoId()).orElseThrow();
            int stockRestaurado = producto.getStock() + item.getCantidad();
            producto.setStock(stockRestaurado);
            productoRepository.save(producto);

            log.debug("Stock restaurado para producto {}: {} -> {}",
                     producto.getNombre(), producto.getStock() - item.getCantidad(), stockRestaurado);
        }
    }

    /**
     * Valida y convierte el string de estado a enum.
     */
    private Pedido.EstadoPedido validarYConvertirEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new BusinessException("El estado del pedido es requerido");
        }

        try {
            return Pedido.EstadoPedido.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado de pedido no válido. Valores permitidos: PENDIENTE, ENVIADO, ENTREGADO, CANCELADO");
        }
    }

    /**
     * Valida que la transición de estado sea permitida.
     */
    private void validarTransicionEstado(Pedido.EstadoPedido estadoActual, Pedido.EstadoPedido nuevoEstado) {        // Reglas de transición de estados
        switch (estadoActual) {
            case PENDIENTE:
                // Desde PENDIENTE se puede ir a cualquier estado
                break;
            case CONFIRMADO:
                // Desde CONFIRMADO se puede ir a cualquier estado excepto a PENDIENTE
                if (nuevoEstado == Pedido.EstadoPedido.PENDIENTE) {
                    throw new BusinessException("No se puede cambiar un pedido confirmado a pendiente");
                }
                break;
            case ENVIADO:
                if (nuevoEstado == Pedido.EstadoPedido.PENDIENTE) {
                    throw new BusinessException("No se puede cambiar un pedido enviado a pendiente");
                }
                break;
            case ENTREGADO:
                throw new BusinessException("No se puede cambiar el estado de un pedido ya entregado");
            case CANCELADO:
                throw new BusinessException("No se puede cambiar el estado de un pedido cancelado");
        }
    }
}
