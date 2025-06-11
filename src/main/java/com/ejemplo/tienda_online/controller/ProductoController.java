package com.ejemplo.tienda_online.controller;

import com.ejemplo.tienda_online.dto.ProductoResponse;
import com.ejemplo.tienda_online.model.Producto;
import com.ejemplo.tienda_online.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de productos.
 * Expone endpoints para operaciones CRUD de productos.
 *
 * @author Sistema Tienda Online
 * @version 2.0
 */
@Slf4j
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Obtiene todos los productos disponibles.
     *
     * @return Lista de todos los productos
     */
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> getAll() {
        log.info("Solicitando lista de todos los productos");
        List<Producto> productos = productoService.getAll();
        log.info("Devolviendo {} productos", productos.size());
        // Convertir a ProductoResponse y devolver precio formateado
        List<ProductoResponse> response = productos.stream()
            .map(p -> ProductoResponse.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precio(java.math.BigDecimal.valueOf(p.getPrecio()))
                .stock(p.getStock())
                .categoria(p.getCategoria())
                .imagenUrl(p.getImagenUrl())
                .fechaCreacion(p.getFechaCreacion())
                .fechaActualizacion(p.getFechaActualizacion())
                .disponible(p.estaDisponible())
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Busca productos por nombre.
     *
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de productos que coinciden con la búsqueda
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
        log.info("Buscando productos por nombre: {}", nombre);
        List<Producto> productos = productoService.findByNombre(nombre);
        log.info("Encontrados {} productos para la búsqueda: {}", productos.size(), nombre);
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtiene productos con stock disponible.
     *
     * @return Lista de productos en stock
     */
    @GetMapping("/en-stock")
    public ResponseEntity<List<Producto>> getProductosEnStock() {
        log.info("Solicitando productos en stock");
        List<Producto> productos = productoService.getProductosEnStock();
        log.info("Devolviendo {} productos en stock", productos.size());
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtiene un producto específico por su ID.
     *
     * @param id ID del producto
     * @return El producto encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable String id) {
        log.info("Solicitando producto con ID: {}", id);
        Producto producto = productoService.getById(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * Crea un nuevo producto.
     * Solo disponible para administradores.
     *
     * @param producto Datos del producto a crear
     * @return El producto creado
     */    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> create(@RequestBody Producto producto) {
        log.info("Creando nuevo producto: {}", producto.getNombre());
        Producto productoCreado = productoService.create(producto);
        log.info("Producto creado exitosamente con ID: {}", productoCreado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);
    }

    /**
     * Actualiza un producto existente.
     * Solo disponible para administradores.
     *
     * @param id ID del producto a actualizar
     * @param producto Nuevos datos del producto
     * @return El producto actualizado
     */    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> update(@PathVariable String id, @RequestBody Producto producto) {
        log.info("Actualizando producto con ID: {}", id);
        Producto productoActualizado = productoService.update(id, producto);
        log.info("Producto {} actualizado exitosamente", id);
        return ResponseEntity.ok(productoActualizado);
    }

    /**
     * Elimina un producto.
     * Solo disponible para administradores.
     *
     * @param id ID del producto a eliminar
     * @return Respuesta vacía de confirmación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("Eliminando producto con ID: {}", id);
        productoService.delete(id);
        log.info("Producto {} eliminado exitosamente", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza el stock de un producto.
     * Solo disponible para administradores.
     *
     * @param id ID del producto
     * @param cantidad Nueva cantidad de stock
     * @return El producto con stock actualizado
     */
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> updateStock(@PathVariable String id, @RequestParam Integer cantidad) {
        log.info("Actualizando stock del producto {} a {}", id, cantidad);
        Producto productoActualizado = productoService.updateStock(id, cantidad);
        log.info("Stock del producto {} actualizado a {}", id, cantidad);
        return ResponseEntity.ok(productoActualizado);
    }

    /**
     * Reduce el stock de un producto.
     * Endpoint interno para operaciones de pedidos.
     *
     * @param id ID del producto
     * @param cantidad Cantidad a reducir
     * @return El producto con stock reducido
     */
    @PutMapping("/{id}/reducir-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    public ResponseEntity<Producto> reduceStock(@PathVariable String id, @RequestParam Integer cantidad) {
        log.info("Reduciendo stock del producto {} en {}", id, cantidad);
        Producto productoActualizado = productoService.reduceStock(id, cantidad);
        log.info("Stock del producto {} reducido en {}", id, cantidad);
        return ResponseEntity.ok(productoActualizado);
    }
}
