package com.ejemplo.tienda_online.repository;

import com.ejemplo.tienda_online.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByNombre(Role.NombreRol nombre);
}
