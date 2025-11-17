package com.SriLankaCard.service.carrinho;


import com.SriLankaCard.dto.request.carrinho.AddItemCarrinhoRequest;
import com.SriLankaCard.dto.response.itemCarrinho.CarrinhoResponse;
import com.SriLankaCard.dto.response.itemCarrinho.ItemCarrinhoResponse;
import com.SriLankaCard.entity.carrinhoEntity.Carrinho;
import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;
import com.SriLankaCard.repository.carrinhoRepository.CarrinhoRepository;
import com.SriLankaCard.repository.produtoRepository.CardRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarrinhoService {

    private CardRepository cardRepository;
    private CarrinhoRepository carrinhoRepository;

    public CarrinhoService(CardRepository repository, CarrinhoRepository carrinhoRepository) {
        this.cardRepository = repository;
        this.carrinhoRepository = carrinhoRepository;
    }

    private Carrinho getOrCreateCarrinho(Long usuarioId){
        return carrinhoRepository
                .findByUsuarioId(usuarioId)
                .orElseGet(() -> carrinhoRepository.save(new Carrinho(usuarioId)));
    }

    @Transactional
    public void adicionarItem(Long id, AddItemCarrinhoRequest request){
        if(request == null){
            throw new InvalidArgumentsException("Cartão passado está vazio");
        }

        if(request.getId() == null){
            throw new InvalidArgumentsException("Produto não informado");
        }
        if(request.getQuantidade() == null ||request.getQuantidade() <= 0){
            throw new InvalidArgumentsException("Quantidade deve ser maior que zero");
        }
        Carrinho carrinho = getOrCreateCarrinho(id);

        Card card = cardRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        carrinho.adicionarItem(card, request.getQuantidade());

        carrinhoRepository.save(carrinho);
    }

    @Transactional
    public CarrinhoResponse buscarCarrinhoDoUsuarioDto(Long usuarioId) {

        Carrinho carrinho = buscarCarrinhoDoUsuario(usuarioId);

        CarrinhoResponse resp = new CarrinhoResponse();
        resp.setId(carrinho.getId());
        resp.setUsuarioId(carrinho.getUsuarioId());
        resp.setValorTotal(carrinho.getValorTotal());
        resp.setQuantidade(carrinho.getQuantidade());
        resp.setPossuiItens(!carrinho.getItens().isEmpty());

        List<ItemCarrinhoResponse> itensDto = carrinho.getItens().stream()
                .map(item -> {
                    ItemCarrinhoResponse dto = new ItemCarrinhoResponse();
                    dto.setId(item.getId());

                    if (item.getCard() != null) {
                        dto.setProdutoId(item.getCard().getId());


                        dto.setNome(item.getCard().getNome());
                    }

                    dto.setQuantidade(item.getQuantidade());
                    dto.setPrecoUnitario(item.getPrecoUnitario());
                    dto.setTotal(item.total());
                    return dto;
                })
                .toList();

        resp.setItens(itensDto);

        return resp;
    }

    @Transactional
    public void limparCarrinho(Long usuarioId) {
        Carrinho carrinho = getOrCreateCarrinho(usuarioId);
        carrinho.limpar();
        carrinhoRepository.save(carrinho);
    }

    @Transactional
    public void removerItem(Long usuarioId, Long produtoId) {
        Carrinho carrinho = getOrCreateCarrinho(usuarioId);
        carrinho.removerItem(produtoId);
        carrinhoRepository.save(carrinho);
    }

    public Double totalValor(Long usuarioId) {
        Carrinho carrinho = getOrCreateCarrinho(usuarioId);
        return carrinho.getValorTotal(); // já está sendo calculado dentro da entidade
    }

    public Integer totalQuantidade(Long usuarioId) {
        Carrinho carrinho = getOrCreateCarrinho(usuarioId);
        return carrinho.getQuantidade();
    }

    private  Carrinho buscarCarrinhoDoUsuario(Long usuarioId) {
        return carrinhoRepository
                .findByUsuarioId(usuarioId)
                .orElseGet(() -> new Carrinho(usuarioId)); // aqui eu não salvo ainda, só retorno vazio
    }
}
