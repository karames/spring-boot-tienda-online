package com.ejemplo.tienda_online.repository;

import com.ejemplo.tienda_online.model.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de productos en MongoDB
 */
@Repository
public interface ProductoRepository extends MongoRepository<Producto, String> {

    /**
     * Busca productos por nombre (case insensitive)
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca un producto por nombre exacto (case insensitive)
     */
    Optional<Producto> findByNombreIgnoreCase(String nombre);

    /**
     * Obtiene productos con stock mayor al especificado
     */
    List<Producto> findByStockGreaterThan(Integer stock);

    /**
     * Obtiene productos con stock menor al especificado
     */
    List<Producto> findByStockLessThan(Integer stock);

    /**
     * Obtiene productos en un rango de precios
     */
    List<Producto> findByPrecioBetween(Double minPrecio, Double maxPrecio);

    /**
     * Busca productos ordenados por precio
     */
    List<Producto> findAllByOrderByPrecioAsc();

    /**
     * Busca productos ordenados por nombre
     */
    List<Producto> findAllByOrderByNombreAsc();

    /**
     * Cuenta productos con stock mayor a cero
     */
    @Query("{ 'stock' : { $gt : 0 } }")
    long countProductosEnStock();
}
