package com.ejemplo.tienda_online.service;

import com.ejemplo.tienda_online.model.Producto;
import com.ejemplo.tienda_online.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;

    public List<Producto> getAll() {
        return productoRepository.findAll();
    }

    public Producto create(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto update(String id, Producto producto) {
        producto.setId(id);
        return productoRepository.save(producto);
    }

    public void delete(String id) {
        productoRepository.deleteById(id);
    }
}
