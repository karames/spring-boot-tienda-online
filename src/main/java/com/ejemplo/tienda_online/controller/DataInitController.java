package com.ejemplo.tienda_online.controller;

import com.ejemplo.tienda_online.model.Producto;
import com.ejemplo.tienda_online.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/admin/init")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DataInitController {

    private final ProductoRepository productoRepository;

    @PostMapping("/productos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> initProductos() {
        // Verificar si ya hay productos
        if (productoRepository.count() > 0) {
            return ResponseEntity.ok("Ya existen productos en la base de datos");
        }        List<Producto> productos = Arrays.asList(
            Producto.builder()
                .nombre("Laptop Gaming")
                .descripcion("Laptop para gaming de alta gama con procesador Intel i7")
                .precio(1299.99)
                .stock(10)
                .build(),
            Producto.builder()
                .nombre("Mouse Inalámbrico")
                .descripcion("Mouse inalámbrico ergonómico con sensor óptico")
                .precio(29.99)
                .stock(50)
                .build(),
            Producto.builder()
                .nombre("Teclado Mecánico")
                .descripcion("Teclado mecánico con retroiluminación RGB")
                .precio(89.99)
                .stock(25)
                .build(),
            Producto.builder()
                .nombre("Monitor 4K")
                .descripcion("Monitor 4K de 27 pulgadas con tecnología IPS")
                .precio(399.99)
                .stock(15)
                .build(),
            Producto.builder()
                .nombre("Auriculares Gaming")
                .descripcion("Auriculares gaming con sonido surround 7.1")
                .precio(159.99)
                .stock(30)
                .build()
        );

        productoRepository.saveAll(productos);
        return ResponseEntity.ok("Productos de prueba creados exitosamente: " + productos.size() + " productos");
    }

    @DeleteMapping("/productos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> clearProductos() {
        long count = productoRepository.count();
        productoRepository.deleteAll();
        return ResponseEntity.ok("Eliminados " + count + " productos");
    }
}
