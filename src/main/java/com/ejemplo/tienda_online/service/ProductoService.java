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

    public Producto getById(String id) {
        return productoRepository.findById(id).orElse(null);
    }

    public Producto create(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto update(String id, Producto producto) {
        if (!productoRepository.existsById(id)) {
            return null;
        }
        producto.setId(id);
        return productoRepository.save(producto);
    }

    public boolean delete(String id) {
        if (!productoRepository.existsById(id)) {
            return false;
        }
        productoRepository.deleteById(id);
        return true;
    }
}
