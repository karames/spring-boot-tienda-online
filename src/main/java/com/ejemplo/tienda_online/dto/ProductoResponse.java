package com.ejemplo.tienda_online.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO para respuestas que contienen información de productos.
 *
 * <p>Esta clase representa la información de un producto que se envía
 * como respuesta en las operaciones de consulta.</p>
 *
 * @author Sistema Tienda Online
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

    /**
     * ID único del producto.
     */
    private String id;

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
     * Stock disponible del producto.
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
     * Indica si el producto está activo.
     */
    private Boolean activo;

    /**
     * Fecha de creación del producto.
     */
    private Instant fechaCreacion;

    /**
     * Fecha de última actualización.
     */
    private Instant fechaActualizacion;

    /**
     * Indica si el producto está disponible para la venta.
     */
    private Boolean disponible;
}
