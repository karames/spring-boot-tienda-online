package com.ejemplo.tienda_online.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entidad que representa un producto en la tienda online.
 *
 * <p>Esta clase encapsula toda la información relacionada con un producto,
 * incluyendo su información básica, precio, stock y metadatos de gestión.</p>
 *
 * @author Sistema Tienda Online
 * @version 1.0
 */
@Document(collection = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    /**
     * Identificador único del producto en la base de datos.
     */
    @Id
    private String id;

    /**
     * Nombre del producto. Debe ser único en el sistema.
     */
    @Indexed(unique = true)
    private String nombre;

    /**
     * Descripción detallada del producto.
     */
    private String descripcion;

    /**
     * Precio del producto usando BigDecimal para precisión monetaria.
     */
    @Builder.Default
    private BigDecimal precio = BigDecimal.ZERO;

    /**
     * Cantidad disponible en stock.
     */
    @Builder.Default
    private Integer stock = 0;

    /**
     * Categoría del producto para organización y filtrado.
     */
    private String categoria;

    /**
     * Indica si el producto está activo y disponible para la venta.
     */
    @Builder.Default
    private Boolean activo = true;

    /**
     * Fecha de creación del producto en el sistema.
     */
    @Builder.Default
    private Instant fechaCreacion = Instant.now();

    /**
     * Fecha de la última actualización del producto.
     */
    @Builder.Default
    private Instant fechaActualizacion = Instant.now();

    /**
     * Peso del producto en gramos para cálculo de envío.
     */
    @Builder.Default
    private Integer pesoGramos = 0;

    /**
     * URL de la imagen principal del producto.
     */
    private String imagenUrl;

    /**
     * Verifica si el producto está disponible para la venta.
     *
     * @return true si el producto está activo y tiene stock disponible
     */
    public boolean estaDisponible() {
        return Boolean.TRUE.equals(activo) && stock != null && stock > 0;
    }

    /**
     * Verifica si hay suficiente stock para una cantidad específica.
     *
     * @param cantidad cantidad solicitada
     * @return true si hay suficiente stock
     */
    public boolean tieneSuficienteStock(int cantidad) {
        return stock != null && stock >= cantidad;
    }

    /**
     * Reduce el stock del producto en la cantidad especificada.
     *
     * @param cantidad cantidad a reducir
     * @throws IllegalArgumentException si la cantidad es mayor al stock disponible
     */
    public void reducirStock(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        if (!tieneSuficienteStock(cantidad)) {
            throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + stock);
        }
        this.stock -= cantidad;
        this.fechaActualizacion = Instant.now();
    }

    /**
     * Aumenta el stock del producto en la cantidad especificada.
     *
     * @param cantidad cantidad a agregar
     * @throws IllegalArgumentException si la cantidad es negativa
     */
    public void aumentarStock(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        this.stock = (this.stock == null ? 0 : this.stock) + cantidad;
        this.fechaActualizacion = Instant.now();
    }

    /**
     * Actualiza la fecha de modificación del producto.
     */
    public void marcarComoActualizado() {
        this.fechaActualizacion = Instant.now();
    }

    /**
     * Activa el producto para que esté disponible para la venta.
     */
    public void activar() {
        this.activo = true;
        this.fechaActualizacion = Instant.now();
    }

    /**
     * Desactiva el producto para que no esté disponible para la venta.
     */
    public void desactivar() {
        this.activo = false;
        this.fechaActualizacion = Instant.now();
    }

    /**
     * Obtiene el precio como double para compatibilidad con código existente.
     * @deprecated Usar getPrecio() que retorna BigDecimal
     * @return precio como double
     */
    @Deprecated
    public double getPrecioAsDouble() {
        return precio != null ? precio.doubleValue() : 0.0;
    }

    /**
     * Establece el precio desde un valor double.
     * @deprecated Usar setPrecio(BigDecimal)
     * @param precio precio como double
     */
    @Deprecated
    public void setPrecioFromDouble(double precio) {
        this.precio = BigDecimal.valueOf(precio);
        this.fechaActualizacion = Instant.now();
    }
}
