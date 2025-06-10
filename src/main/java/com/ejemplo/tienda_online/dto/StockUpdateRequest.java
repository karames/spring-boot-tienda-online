package com.ejemplo.tienda_online.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para solicitudes de actualización de stock de productos.
 *
 * <p>Esta clase contiene los campos necesarios para realizar
 * operaciones de gestión de stock.</p>
 *
 * @author Sistema Tienda Online
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequest {

    /**
     * Cantidad a agregar o reducir del stock.
     * Valores positivos agregan stock, valores negativos lo reducen.
     */
    private Integer cantidad;

    /**
     * Motivo de la actualización del stock.
     */
    private String motivo;
}
