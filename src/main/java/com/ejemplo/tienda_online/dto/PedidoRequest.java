package com.ejemplo.tienda_online.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO para solicitudes de creación de pedidos.
 *
 * <p>Esta clase contiene los campos necesarios para crear
 * un nuevo pedido en el sistema.</p>
 *
 * @author Sistema Tienda Online
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

    /**
     * Lista de items a incluir en el pedido.
     */
    private List<ItemPedidoRequest> productos;

    /**
     * Dirección de envío del pedido.
     */
    private String direccionEnvio;

    /**
     * Notas adicionales del pedido.
     */
    private String notas;

    /**
     * Clase interna para representar un item del pedido en la solicitud.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoRequest {

        /**
         * ID del producto a incluir en el pedido.
         */
        private String productoId;

        /**
         * Cantidad solicitada del producto.
         */
        private Integer cantidad;
    }
}
