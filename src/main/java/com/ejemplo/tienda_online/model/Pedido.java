package com.ejemplo.tienda_online.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

/**
 * Entidad que representa un pedido en la tienda online.
 *
 * <p>Esta clase encapsula toda la información relacionada con un pedido,
 * incluyendo los productos solicitados, el total, estado y metadatos.</p>
 *
 * @author Sistema Tienda Online
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "pedidos")
public class Pedido {

    /**
     * Identificador único del pedido en la base de datos.
     */
    @Id
    private String id;

    /**
     * ID del usuario que realizó el pedido.
     */
    private String usuarioId;

    /**
     * Lista de productos incluidos en el pedido.
     */
    @Builder.Default
    private List<ItemPedido> productos = new ArrayList<>();

    /**
     * Fecha de creación del pedido.
     */
    @Builder.Default
    private Instant fecha = Instant.now();

    /**
     * Estado actual del pedido.
     */
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    /**
     * Fecha de última actualización del pedido.
     */
    @Builder.Default
    private Instant fechaActualizacion = Instant.now();

    /**
     * Dirección de envío del pedido.
     */
    private String direccionEnvio;

    /**
     * Notas adicionales del pedido.
     */
    private String notas;

    /**
     * Número de seguimiento del envío (si aplica).
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
     * Clase interna que representa un item dentro del pedido.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemPedido {

        /**
         * ID del producto.
         */
        private String productoId;

        /**
         * Nombre del producto al momento de la compra (para historial).
         */
        private String nombreProducto;

        /**
         * Cantidad solicitada del producto.
         */
        private Integer cantidad;

        /**
         * Precio unitario al momento de la compra usando Double.
         */
        private Double precioUnitario;

        /**
         * Calcula el subtotal de este item del pedido.
         *
         * @return subtotal del item (cantidad * precio unitario)
         */
        public Double calcularSubtotal() {
            if (cantidad == null || precioUnitario == null) {
                return 0.0;
            }
            return precioUnitario * cantidad;
        }
    }

    /**
     * Enumeración que define los posibles estados de un pedido.
     */
    public enum EstadoPedido {
        /**
         * Pedido creado pero aún no procesado.
         */
        PENDIENTE("Pendiente de procesamiento"),

        /**
         * Pedido confirmado y en preparación.
         */
        CONFIRMADO("Confirmado y en preparación"),

        /**
         * Pedido enviado al cliente.
         */
        ENVIADO("Enviado"),

        /**
         * Pedido entregado al cliente.
         */
        ENTREGADO("Entregado"),

        /**
         * Pedido cancelado.
         */
        CANCELADO("Cancelado");

        private final String descripcion;

        EstadoPedido(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }

        /**
         * Verifica si el estado es cancelable.
         *
         * @return true si el pedido puede ser cancelado desde este estado
         */
        public boolean esCancelable() {
            return this == PENDIENTE || this == CONFIRMADO;
        }

        /**
         * Verifica si el estado es final (no permite más cambios).
         *
         * @return true si el estado es final
         */
        public boolean esFinal() {
            return this == ENTREGADO || this == CANCELADO;
        }
    }

    /**
     * Verifica si el pedido puede ser cancelado.
     *
     * @return true si el pedido puede ser cancelado
     */
    public boolean puedeSerCancelado() {
        return estado != null && estado.esCancelable();
    }

    /**
     * Verifica si el pedido está en un estado final.
     *
     * @return true si el pedido está en estado final
     */
    public boolean estaFinalizado() {
        return estado != null && estado.esFinal();
    }

    /**
     * Cambia el estado del pedido validando las transiciones permitidas.
     *
     * @param nuevoEstado nuevo estado del pedido
     * @throws IllegalStateException si la transición no es válida
     */
    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }

        if (estaFinalizado()) {
            throw new IllegalStateException("No se puede cambiar el estado de un pedido finalizado");
        }

        // Validar transiciones específicas
        if (estado == EstadoPedido.ENVIADO && nuevoEstado == EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("No se puede regresar un pedido enviado a pendiente");
        }

        this.estado = nuevoEstado;
        this.fechaActualizacion = Instant.now();

        // Establecer fecha de entrega si el estado es ENTREGADO
        if (nuevoEstado == EstadoPedido.ENTREGADO && fechaEntrega == null) {
            this.fechaEntrega = Instant.now();
        }
    }

    /**
     * Cancela el pedido con un motivo específico.
     *
     * @param motivo motivo de la cancelación
     * @throws IllegalStateException si el pedido no puede ser cancelado
     */
    public void cancelar(String motivo) {
        if (!puedeSerCancelado()) {
            throw new IllegalStateException("El pedido no puede ser cancelado en el estado actual: " + estado);
        }

        this.estado = EstadoPedido.CANCELADO;
        this.motivoCancelacion = motivo;
        this.fechaActualizacion = Instant.now();
    }

    /**
     * Agrega un item al pedido.
     *
     * @param item item a agregar
     * @throws IllegalStateException si el pedido no puede ser modificado
     */
    public void agregarItem(ItemPedido item) {
        if (estaFinalizado()) {
            throw new IllegalStateException("No se pueden agregar items a un pedido finalizado");
        }

        if (productos == null) {
            productos = new ArrayList<>();
        }

        productos.add(item);
    }

    /**
     * Remueve un item del pedido por ID de producto.
     *
     * @param productoId ID del producto a remover
     * @return true si se removió el item
     * @throws IllegalStateException si el pedido no puede ser modificado
     */
    public boolean removerItem(String productoId) {
        if (estaFinalizado()) {
            throw new IllegalStateException("No se pueden remover items de un pedido finalizado");
        }

        if (productos == null) {
            return false;
        }

        return productos.removeIf(item ->
            item.getProductoId() != null && item.getProductoId().equals(productoId));
    }
}
