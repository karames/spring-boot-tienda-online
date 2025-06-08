package com.ejemplo.tienda_online.repository;

import com.ejemplo.tienda_online.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);

    @Query("{ 'roles' : ?0 }")
    List<Usuario> findByRoles(String role);

    @Query(value = "{ 'roles' : ?0 }", count = true)
    long countByRoles(Usuario.RolUsuario rol);
}
