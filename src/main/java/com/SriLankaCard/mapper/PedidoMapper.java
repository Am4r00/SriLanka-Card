package com.SriLankaCard.mapper;

import com.SriLankaCard.dto.response.pedido.ItemPedidoResponse;
import com.SriLankaCard.dto.response.pedido.PedidoHistoricoResponse;
import com.SriLankaCard.entity.pedidoEntity.ItemPedido;
import com.SriLankaCard.entity.pedidoEntity.Pedido;

import java.util.stream.Collectors;

public class PedidoMapper {

    public static PedidoHistoricoResponse toPeidoByPedidoResponse(Pedido pedido){
        PedidoHistoricoResponse pedidoHistoricoResponse = new PedidoHistoricoResponse();

        pedidoHistoricoResponse.setId(pedido.getId());
        pedidoHistoricoResponse.setValorTotal(pedido.getValorTotal());
        pedidoHistoricoResponse.setQuantidadeTotal(pedido.getQuantidadeTotal());
        pedidoHistoricoResponse.setCriadoEm(pedido.getCriadoEm());
        pedidoHistoricoResponse.setItensPedido(
                pedido.getItens().stream()
                .map(PedidoMapper::toItemPedidoResponseByItemPedido)
                .collect(Collectors.toList()));


        return  pedidoHistoricoResponse;
    }

    public static ItemPedidoResponse toItemPedidoResponseByItemPedido(ItemPedido pedido){
        ItemPedidoResponse itemPedidoResponse = new ItemPedidoResponse();

        itemPedidoResponse.setTotal(pedido.getTotal());
        itemPedidoResponse.setQuantidade(pedido.getQuantidade());
        itemPedidoResponse.setNomeProduto(pedido.getNomeProduto());
        itemPedidoResponse.setPrecoUnitario(pedido.getPrecoUnitario());

        return itemPedidoResponse;
    }
}
