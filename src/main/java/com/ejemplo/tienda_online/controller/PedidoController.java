package com.ejemplo.tienda_online.controller;

import com.ejemplo.tienda_online.model.Pedido;
import com.ejemplo.tienda_online.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de pedidos.
 * Expone endpoints para la creación, consulta y administración de pedidos.
 *
 * @author Sistema Tienda Online
 * @version 2.0
 */
@Slf4j
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    /**
     * Obtiene todos los pedidos del sistema.
     * Solo disponible para administradores.
     *
     * @return Lista completa de pedidos
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pedido>> getAll() {
        log.info("Administrador solicitando todos los pedidos");
        List<Pedido> pedidos = pedidoService.getAll();
        log.info("Devolviendo {} pedidos al administrador", pedidos.size());
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Obtiene los pedidos del usuario autenticado.
     * Solo disponible para clientes.
     *
     * @return Lista de pedidos del usuario actual
     */
    @GetMapping("/mios")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<Pedido>> getMisPedidos() {
        log.info("Cliente solicitando sus pedidos");
        List<Pedido> pedidos = pedidoService.getPedidosUsuarioActual();
        log.info("Cliente tiene {} pedidos", pedidos.size());
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Busca un pedido específico por ID.
     * Administradores pueden ver cualquier pedido, clientes solo los suyos.
     *
     * @param id ID del pedido
     * @return El pedido encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable String id) {
        log.info("Solicitando pedido con ID: {}", id);
        Pedido pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(pedido);
    }

    /**
     * Crea un nuevo pedido.
     * Solo disponible para clientes autenticados.
     *
     * @param pedido Datos del pedido a crear
     * @return El pedido creado
     */    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido) {
        log.info("Cliente creando nuevo pedido con {} productos",
                 pedido.getProductos() != null ? pedido.getProductos().size() : 0);

        Pedido pedidoCreado = pedidoService.crearPedido(pedido);

        log.info("Pedido creado exitosamente con ID: {} por un total de ${}",
                 pedidoCreado.getId(), pedidoCreado.getTotal());

        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoCreado);
    }

    /**
     * Cambia el estado de un pedido.
     * Solo disponible para administradores.
     *
     * @param id ID del pedido
     * @param estado Nuevo estado para el pedido
     * @return El pedido con estado actualizado
     */
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pedido> cambiarEstado(@PathVariable String id, @RequestParam String estado) {
        log.info("Administrador cambiando estado del pedido {} a {}", id, estado);
        Pedido pedidoActualizado = pedidoService.cambiarEstado(id, estado);
        log.info("Estado del pedido {} cambiado exitosamente a {}", id, estado);
        return ResponseEntity.ok(pedidoActualizado);
    }

    /**
     * Cancela un pedido y restaura el stock.
     * Administradores pueden cancelar cualquier pedido, clientes solo los suyos.
     *
     * @param id ID del pedido a cancelar
     * @return El pedido cancelado
     */
    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    public ResponseEntity<Pedido> cancelarPedido(@PathVariable String id) {
        log.info("Solicitud de cancelación para el pedido: {}", id);
        Pedido pedidoCancelado = pedidoService.cancelarPedido(id);
        log.info("Pedido {} cancelado exitosamente", id);
        return ResponseEntity.ok(pedidoCancelado);
    }
}
