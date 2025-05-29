package com.ejemplo.tienda_online.repository;

import com.ejemplo.tienda_online.model.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends MongoRepository<Producto, String> {
    // Métodos personalizados si se requieren
}
