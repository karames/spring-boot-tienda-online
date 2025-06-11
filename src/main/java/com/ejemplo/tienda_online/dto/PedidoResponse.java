package com.ejemplo.tienda_online.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;
import java.util.List;

/**
 * DTO para respuestas que contienen información de pedidos.
 *
 * <p>Esta clase representa la información completa de un pedido que se envía
 * como respuesta en las operaciones de consulta.</p>
 *
 * @author Sistema Tienda Online
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {

    /**
     * ID único del pedido.
     */
    private String id;

    /**
     * ID del usuario que realizó el pedido.
     */
    private String usuarioId;

    /**
     * Nombre del usuario que realizó el pedido.
     */
    private String nombreUsuario;

    /**
     * Email del usuario que realizó el pedido.
     */
    private String emailUsuario;

    /**
     * Lista de items incluidos en el pedido.
     */
    private List<ItemPedidoResponse> productos;

    /**
     * Fecha de creación del pedido.
     */
    private Instant fecha;

    /**
     * Estado actual del pedido.
     */
    private String estado;

    /**
     * Descripción del estado del pedido.
     */
    private String estadoDescripcion;

    /**
     * Fecha de última actualización.
     */
    private Instant fechaActualizacion;

    /**
     * Dirección de envío del pedido.
     */
    private String direccionEnvio;

    /**
     * Notas adicionales del pedido.
     */
    private String notas;

    /**
     * Número de seguimiento del envío.
     */
    private String numeroSeguimiento;

    /**
     * Fecha estimada de entrega.
     */
    private Instant fechaEstimadaEntrega;

    /**
     * Fecha real de entrega.
     */
    private Instant fechaEntrega;

    /**
     * Motivo de cancelación (si aplica).
     */
    private String motivoCancelacion;

    /**
     * Indica si el pedido puede ser cancelado.
     */
    private Boolean puedeSerCancelado;

    /**
     * Indica si el pedido está finalizado.
     */
    private Boolean estaFinalizado;

    /**
     * Cantidad total de items en el pedido.
     */
    private Integer cantidadTotalItems;

    /**
     * Devuelve el total del pedido formateado en formato español (punto para miles, coma para decimales)
     */
    public String getTotalFormateado() {
        double total = 0.0;
        if (productos != null) {
            for (ItemPedidoResponse item : productos) {
                if (item.getSubtotal() != null) total += item.getSubtotal();
            }
        }
        java.text.DecimalFormat df = new java.text.DecimalFormat("###,##0.00");
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(symbols);
        return df.format(total);
    }

    /**
     * Clase interna para representar un item del pedido en la respuesta.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoResponse {

        /**
         * ID del producto.
         */
        private String productoId;

        /**
         * Nombre del producto al momento de la compra.
         */
        private String nombreProducto;

        /**
         * Cantidad solicitada del producto.
         */
        private Integer cantidad;

        /**
         * Precio unitario al momento de la compra.
         */
        private Double precioUnitario;

        /**
         * Subtotal del item (cantidad × precio unitario).
         */
        private Double subtotal;

        /**
         * Categoría del producto.
         */
        private String categoria;

        /**
         * URL de la imagen del producto.
         */
        private String imagenUrl;

        /**
         * Devuelve el precio unitario formateado en formato español
         */
        public String getPrecioUnitarioFormateado() {
            if (precioUnitario == null) return "0,00";
            java.text.DecimalFormat df = new java.text.DecimalFormat("###,##0.00");
            java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
            symbols.setDecimalSeparator(',');
            symbols.setGroupingSeparator('.');
            df.setDecimalFormatSymbols(symbols);
            return df.format(precioUnitario);
        }

        /**
         * Devuelve el subtotal formateado en formato español
         */
        public String getSubtotalFormateado() {
            if (subtotal == null) return "0,00";
            java.text.DecimalFormat df = new java.text.DecimalFormat("###,##0.00");
            java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
            symbols.setDecimalSeparator(',');
            symbols.setGroupingSeparator('.');
            df.setDecimalFormatSymbols(symbols);
            return df.format(subtotal);
        }
    }
}
