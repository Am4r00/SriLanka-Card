package com.SriLankaCard.service.pedidoService;

import com.SriLankaCard.entity.carrinhoEntity.Carrinho;
import com.SriLankaCard.entity.carrinhoEntity.ItemCarrinho;
import com.SriLankaCard.entity.pedidoEntity.ItemPedido;
import com.SriLankaCard.entity.pedidoEntity.Pedido;
import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.entity.produtoEntity.GiftCode;
import com.SriLankaCard.entity.produtoEntity.GiftCodeStatus;
import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.exception.dominio.UserNotFoundException;
import com.SriLankaCard.exception.negocio.CarrinhoNotFoundException;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;
import com.SriLankaCard.repository.carrinhoRepository.CarrinhoRepository;
import com.SriLankaCard.repository.pedidoRepository.PedidoRepository;
import com.SriLankaCard.repository.produtoRepository.GiftCodeRepository;
import com.SriLankaCard.repository.userRepository.UserRepository;
import com.SriLankaCard.service.carrinho.CarrinhoService;
import com.SriLankaCard.service.emailService.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {
    private final CarrinhoRepository carrinhoRepository;
    private final CarrinhoService carrinhoService;
    private final PedidoRepository pedidoRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final GiftCodeRepository giftCodeRepository;

    public PedidoService(CarrinhoRepository carrinhoRepository, CarrinhoService carrinhoService, PedidoRepository pedidoRepository, UserRepository userRepository, EmailService emailService, GiftCodeRepository giftCodeRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.carrinhoService = carrinhoService;
        this.pedidoRepository = pedidoRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.giftCodeRepository = giftCodeRepository;
    }

    @Transactional
    public void finalizarPedido(Long usuarioId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new CarrinhoNotFoundException("Carrinho não encontrado!"));

        if (carrinho.getItens().isEmpty()) {
            throw new InvalidArgumentsException("Carrinho não possui itens");
        }

        User user = userRepository.findById(usuarioId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não foi encontrado !"));

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(usuarioId);
        pedido.setValorTotal(carrinho.getValorTotal());
        pedido.setQuantidadeTotal(carrinho.getQuantidade());
        pedido.setCriadoEm(LocalDateTime.now());

        // Lista de itens do pedido
        List<ItemPedido> itensPedido = new ArrayList<>();

        // Guardar quais seriais foram usados por produto (Card)
        Map<Long, List<String>> codigosPorProduto = new HashMap<>();

        // Guardar todos os GiftCodes atualizados para salvar de uma vez
        List<GiftCode> giftCodesAtualizados = new ArrayList<>();

        for (ItemCarrinho itemCarrinho : carrinho.getItens()) {
            Card card = itemCarrinho.getCard();
            int quantidade = itemCarrinho.getQuantidade();

            // Criar ItemPedido a partir do item do carrinho
            ItemPedido ip = new ItemPedido();
            ip.setPedido(pedido);
            ip.setProdutoId(card.getId());
            ip.setNomeProduto(card.getNome());
            ip.setQuantidade(quantidade);
            ip.setPrecoUnitario(itemCarrinho.getPrecoUnitario());
            ip.setTotal(itemCarrinho.total());

            itensPedido.add(ip);

            // Buscar GiftCodes disponíveis para este card
            List<GiftCode> disponiveis = giftCodeRepository
                    .findByCardAndStatus(card, GiftCodeStatus.DISPONIVEL);

            if (disponiveis.size() < quantidade) {
                throw new InvalidArgumentsException(
                        "Estoque de GiftCards insuficiente para o produto: " + card.getNome()
                );
            }

            // Selecionar os códigos que serão usados
            List<String> seriaisDoItem = new ArrayList<>();

            for (int i = 0; i < quantidade; i++) {
                GiftCode gc = disponiveis.get(i);
                gc.setStatus(GiftCodeStatus.VENDIDO);
                giftCodesAtualizados.add(gc);
                seriaisDoItem.add(gc.getSerial());
            }

            codigosPorProduto.put(card.getId(), seriaisDoItem);
        }

        pedido.setItens(itensPedido);

        Pedido salvo = pedidoRepository.save(pedido);

        // Atualiza os GiftCodes no banco (marcados como VENDIDO)
        giftCodeRepository.saveAll(giftCodesAtualizados);

        String corpoEmail = montarCorpoEmail(user, salvo, codigosPorProduto);

        emailService.enviarEmail(
                user.getEmail(),
                "Confirmação do seu Gift Card - Pedido " + salvo.getId(),
                corpoEmail
        );

        carrinhoService.limparCarrinho(usuarioId);
    }

    private String montarCorpoEmail(User user,
                                    Pedido pedido,
                                    Map<Long, List<String>> codigosPorProduto) {
        StringBuilder sb = new StringBuilder();

        sb.append("Olá, ").append(user.getName()).append("!\n\n");
        sb.append("Recebemos o seu pedido de Gift Cards.\n");
        sb.append("Número do pedido: ").append(pedido.getId()).append("\n");
        sb.append("Data: ").append(pedido.getCriadoEm()).append("\n\n");

        sb.append("Resumo dos itens:\n");
        pedido.getItens().forEach(item -> {
            sb.append("- ")
                    .append(item.getNomeProduto())
                    .append(" | Qtd: ").append(item.getQuantidade())
                    .append(" | R$ ").append(item.getPrecoUnitario())
                    .append(" | Total: R$ ").append(item.getTotal())
                    .append("\n");

            List<String> seriais = codigosPorProduto.get(item.getProdutoId());
            if (seriais != null && !seriais.isEmpty()) {
                sb.append("  Seriais deste item:\n");
                seriais.forEach(serial -> sb.append("    - ").append(serial).append("\n"));
            }
        });

        sb.append("\nTotal de itens: ").append(pedido.getQuantidadeTotal()).append("\n");
        sb.append("Valor total: R$ ").append(pedido.getValorTotal()).append("\n\n");

        sb.append("Obrigado por comprar na SriLanka Card!\n");
        sb.append("Guarde estes seriais com cuidado, eles são seus Gift Cards.");

        return sb.toString();
    }
}
