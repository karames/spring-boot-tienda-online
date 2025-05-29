package com.ejemplo.tienda_online.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {
    private String productoId;
    private int cantidad;
}
