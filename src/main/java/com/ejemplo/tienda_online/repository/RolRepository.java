package com.ejemplo.tienda_online.repository;

import com.ejemplo.tienda_online.model.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RolRepository extends MongoRepository<Rol, String> {
    boolean existsById(String id);
}
