package com.ejemplo.tienda_online.service;

import com.ejemplo.tienda_online.model.Pedido;
import com.ejemplo.tienda_online.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;

    public List<Pedido> getAll() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> getPedidosUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // Aquí deberías buscar el usuario y obtener su id, pero para simplificar:
        return pedidoRepository.findByUsuarioId(username);
    }

    public Pedido crearPedido(Pedido pedido) {
        pedido.setId(null); // Deja que Mongo genere el id
        return pedidoRepository.save(pedido);
    }

    public Pedido cambiarEstado(String id, String estado) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            pedido.setEstado(Pedido.EstadoPedido.valueOf(estado));
            return pedidoRepository.save(pedido);
        }
        throw new RuntimeException("Pedido no encontrado");
    }
}
