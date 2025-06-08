package com.ejemplo.tienda_online.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "pedidos")
public class Pedido {
    @Id
    private String id;
    private String usuarioId;
    private List<ItemPedido> productos;
    private double total;
    private Instant fecha;
    private EstadoPedido estado;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemPedido {
        private String productoId;
        private int cantidad;
        private double precioUnitario; // Precio al momento de la compra
    }

    public enum EstadoPedido {
        PENDIENTE,
        ENVIADO,
        ENTREGADO,
        CANCELADO
    }
}
