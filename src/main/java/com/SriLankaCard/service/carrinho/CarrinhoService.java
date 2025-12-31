package com.SriLankaCard.service.carrinho;


import com.SriLankaCard.dto.request.carrinho.AddItemCarrinhoRequest;
import com.SriLankaCard.dto.response.itemCarrinho.CarrinhoResponse;
import com.SriLankaCard.dto.response.itemCarrinho.ItemCarrinhoResponse;
import com.SriLankaCard.entity.carrinhoEntity.Carrinho;
import com.SriLankaCard.entity.carrinhoEntity.ItemCarrinho;
import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.entity.produtoEntity.GiftCodeStatus;
import com.SriLankaCard.exception.negocio.CardNotFoundException;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;
import com.SriLankaCard.repository.carrinhoRepository.CarrinhoRepository;
import com.SriLankaCard.repository.produtoRepository.CardRepository;
import com.SriLankaCard.repository.produtoRepository.GiftCodeRepository;
import com.SriLankaCard.utils.ValidationUtils;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarrinhoService {

    private CardRepository cardRepository;
    private CarrinhoRepository carrinhoRepository;
    private GiftCodeRepository giftCodeRepository;

    public CarrinhoService(CardRepository cardRepository, CarrinhoRepository carrinhoRepository, GiftCodeRepository giftCodeRepository) {
        this.cardRepository = cardRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.giftCodeRepository = giftCodeRepository;
    }

    private Carrinho getOrCreateCarrinho(Long usuarioId){
        return carrinhoRepository
                .findByUsuarioId(usuarioId)
                .orElseGet(() -> carrinhoRepository.save(new Carrinho(usuarioId)));
    }

    @Transactional
    public void adicionarItem(Long usuarioId, AddItemCarrinhoRequest request){
        ValidationUtils.validateNotNullAndPositive(request.getQuantidade(),request,"Cartão passado está vazio" );

        Carrinho carrinho = getOrCreateCarrinho(usuarioId);

        Card card = cardRepository.findById(request.getId())
                .orElseThrow(() -> new CardNotFoundException("Produto não encontrado"));

        Long disponiveisLong = giftCodeRepository
                .countByCardAndStatus(card, GiftCodeStatus.DISPONIVEL);
        int estoqueDisponivel = disponiveisLong == null ? 0 : disponiveisLong.intValue();

        int qtdJaNoCarrinho = carrinho.getItens().stream()
                .filter(item -> item.getCard().getId().equals(card.getId()))
                .mapToInt(ItemCarrinho::getQuantidade)
                .sum();

        int qtdSolicitada = request.getQuantidade();
        int qtdTotalDesejada = qtdJaNoCarrinho + qtdSolicitada;

        if (estoqueDisponivel <= 0) {
            throw new InvalidArgumentsException("Não há GiftCodes disponíveis para este produto.");
        }

        if (qtdTotalDesejada > estoqueDisponivel) {
            throw new InvalidArgumentsException(
                    "Quantidade indisponível para este Gift Card. " +
                            "Estoque disponível: " + estoqueDisponivel +
                            ", já no carrinho: " + qtdJaNoCarrinho +
                            ", tentativa de adicionar: " + qtdSolicitada
            );
        }

        carrinho.adicionarItem(card, qtdSolicitada);

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
                .orElseGet(() -> new Carrinho(usuarioId));
    }
}
