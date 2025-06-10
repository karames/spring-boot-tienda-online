package com.ejemplo.tienda_online.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para solicitudes de creación y actualización de productos.
 *
 * <p>Esta clase contiene los campos necesarios para crear o actualizar
 * un producto en el sistema, con validaciones apropiadas.</p>
 *
 * @author Sistema Tienda Online
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequest {

    /**
     * Nombre del producto.
     */
    private String nombre;

    /**
     * Descripción del producto.
     */
    private String descripcion;

    /**
     * Precio del producto.
     */
    private BigDecimal precio;

    /**
     * Stock inicial del producto.
     */
    private Integer stock;

    /**
     * Categoría del producto.
     */
    private String categoria;

    /**
     * Peso del producto en gramos.
     */
    private Integer pesoGramos;

    /**
     * URL de la imagen del producto.
     */
    private String imagenUrl;

    /**
     * Estado activo del producto.
     */
    private Boolean activo;
}
