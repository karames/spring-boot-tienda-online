package com.ejemplo.tienda_online.service;

import com.ejemplo.tienda_online.exception.BusinessException;
import com.ejemplo.tienda_online.exception.ResourceNotFoundException;
import com.ejemplo.tienda_online.model.Producto;
import com.ejemplo.tienda_online.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de productos
 * Incluye validaciones de negocio y operaciones CRUD
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;

    private static final BigDecimal MIN_PRICE = BigDecimal.valueOf(0.01);
    private static final BigDecimal MAX_PRICE = BigDecimal.valueOf(999999.99);
    private static final int MIN_STOCK = 0;
    private static final int MAX_STOCK = 999999;

    /**
     * Obtiene todos los productos
     * @return lista de productos
     */
    public List<Producto> getAll() {
        log.info("Obteniendo todos los productos");
        List<Producto> productos = productoRepository.findAll();
        boolean huboCorregidos = false;
        for (Producto p : productos) {
            boolean modificado = false;
            // Corrige precio nulo o no numérico
            if (p.getPrecio() == null) {
                p.setPrecio(BigDecimal.ZERO);
                modificado = true;
            }
            // Corrige stock nulo o no numérico
            if (p.getStock() == null) {
                p.setStock(0);
                modificado = true;
            }
            if (modificado) {
                productoRepository.save(p);
                huboCorregidos = true;
                log.warn("Producto corregido automáticamente: {} (precio: {}, stock: {})", p.getNombre(), p.getPrecio(), p.getStock());
            }
        }
        if (huboCorregidos) {
            log.warn("Se corrigieron productos con datos nulos en precio o stock antes de enviar al frontend.");
        }
        // Devuelve solo productos válidos
        return productos.stream()
            .filter(p -> p.getPrecio() != null && p.getStock() != null)
            .toList();
    }

    /**
     * Obtiene un producto por ID
     * @param id identificador del producto
     * @return producto encontrado
     * @throws ResourceNotFoundException si el producto no existe
     */
    public Producto getById(String id) {
        log.info("Buscando producto con ID: {}", id);
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
    }

    /**
     * Busca productos por nombre
     * @param nombre nombre o parte del nombre del producto
     * @return lista de productos que coinciden
     */
    public List<Producto> findByNombre(String nombre) {
        if (!StringUtils.hasText(nombre)) {
            return getAll();
        }
        log.info("Buscando productos por nombre: {}", nombre);
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtiene productos con stock disponible
     * @return lista de productos en stock
     */
    public List<Producto> getProductosEnStock() {
        log.info("Obteniendo productos en stock");
        return productoRepository.findByStockGreaterThan(0);
    }

    /**
     * Crea un nuevo producto
     * @param producto datos del producto
     * @return producto creado
     * @throws BusinessException si los datos son inválidos
     */
    public Producto create(Producto producto) {
        log.info("Creando nuevo producto: {}", producto.getNombre());

        validateProducto(producto);
        checkDuplicateProduct(producto.getNombre(), null);

        Producto savedProduct = productoRepository.save(producto);
        log.info("Producto creado con ID: {}", savedProduct.getId());

        return savedProduct;
    }

    /**
     * Actualiza un producto existente
     * @param id identificador del producto
     * @param producto nuevos datos del producto
     * @return producto actualizado
     * @throws ResourceNotFoundException si el producto no existe
     * @throws BusinessException si los datos son inválidos
     */    public Producto update(String id, Producto producto) {
        log.info("Actualizando producto con ID: {}", id);

        // Verificar que el producto existe (lanza excepción si no existe)
        getById(id);
        validateProducto(producto);
        checkDuplicateProduct(producto.getNombre(), id);

        producto.setId(id);
        Producto updatedProduct = productoRepository.save(producto);

        log.info("Producto actualizado: {}", updatedProduct.getNombre());
        return updatedProduct;
    }

    /**
     * Elimina un producto
     * @param id identificador del producto
     * @throws ResourceNotFoundException si el producto no existe
     */
    public void delete(String id) {
        log.info("Eliminando producto con ID: {}", id);

        Producto producto = getById(id);
        productoRepository.deleteById(id);

        log.info("Producto eliminado: {}", producto.getNombre());
    }

    /**
     * Actualiza el stock de un producto
     * @param id identificador del producto
     * @param cantidad nueva cantidad en stock
     * @return producto actualizado
     */
    public Producto updateStock(String id, Integer cantidad) {
        log.info("Actualizando stock del producto {} a {}", id, cantidad);

        Producto producto = getById(id);
        validateStock(cantidad);

        producto.setStock(cantidad);
        return productoRepository.save(producto);
    }

    /**
     * Reduce el stock de un producto
     * @param id identificador del producto
     * @param cantidad cantidad a reducir
     * @return producto actualizado
     * @throws BusinessException si no hay stock suficiente
     */
    public Producto reduceStock(String id, Integer cantidad) {
        log.info("Reduciendo stock del producto {} en {}", id, cantidad);

        Producto producto = getById(id);

        if (producto.getStock() < cantidad) {
            throw new BusinessException("Stock insuficiente. Disponible: " + producto.getStock() +
                                      ", Solicitado: " + cantidad);
        }

        producto.setStock(producto.getStock() - cantidad);
        return productoRepository.save(producto);
    }

    private void validateProducto(Producto producto) {
        if (!StringUtils.hasText(producto.getNombre())) {
            throw new BusinessException("El nombre del producto es obligatorio");
        }

        if (producto.getNombre().length() > 100) {
            throw new BusinessException("El nombre del producto no puede exceder 100 caracteres");
        }

        if (!StringUtils.hasText(producto.getDescripcion())) {
            throw new BusinessException("La descripción del producto es obligatoria");
        }

        if (producto.getDescripcion().length() > 500) {
            throw new BusinessException("La descripción no puede exceder 500 caracteres");
        }

        validatePrice(producto.getPrecio());
        validateStock(producto.getStock());
    }
      private void validatePrice(BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio debe ser mayor a 0");
        }

        if (precio.compareTo(MIN_PRICE) < 0 || precio.compareTo(MAX_PRICE) > 0) {
            throw new BusinessException("El precio debe estar entre " + MIN_PRICE + " y " + MAX_PRICE);
        }
    }

    private void validateStock(Integer stock) {
        if (stock == null || stock < MIN_STOCK || stock > MAX_STOCK) {
            throw new BusinessException("El stock debe estar entre " + MIN_STOCK + " y " + MAX_STOCK);
        }
    }

    private void checkDuplicateProduct(String nombre, String excludeId) {
        Optional<Producto> existing = productoRepository.findByNombreIgnoreCase(nombre);
        if (existing.isPresent() && !existing.get().getId().equals(excludeId)) {
            throw new BusinessException("Ya existe un producto con el nombre: " + nombre);
        }
    }
}
