package com.ejemplo.tienda_online.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para solicitudes de cancelación de pedidos.
 *
 * <p>Esta clase contiene los campos necesarios para cancelar
 * un pedido existente.</p>
 *
 * @author Sistema Tienda Online
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelacionPedidoRequest {

    /**
     * Motivo de la cancelación del pedido.
     */
    private String motivo;
}
