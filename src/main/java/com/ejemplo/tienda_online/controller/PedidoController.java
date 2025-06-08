package com.ejemplo.tienda_online.controller;

import com.ejemplo.tienda_online.model.Pedido;
import com.ejemplo.tienda_online.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pedido>> getAll() {
        return ResponseEntity.ok(pedidoService.getAll());
    }

    @GetMapping("/mios")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<Pedido>> getMisPedidos() {
        return ResponseEntity.ok(pedidoService.getPedidosUsuarioActual());
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido) {
        if (pedido.getProductos() == null || pedido.getProductos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido debe contener al menos un producto");
        }
        for (Pedido.ItemPedido item : pedido.getProductos()) {
            if (item.getProductoId() == null || item.getCantidad() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cada item debe tener un ID de producto válido y una cantidad positiva");
            }
        }
        return ResponseEntity.ok(pedidoService.crearPedido(pedido));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pedido> cambiarEstado(@PathVariable String id, @RequestParam String estado) {
        try {
            return ResponseEntity.ok(pedidoService.cambiarEstado(id, estado));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
