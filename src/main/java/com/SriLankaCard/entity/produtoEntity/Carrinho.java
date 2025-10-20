package com.SriLankaCard.entity.produtoEntity;

import com.SriLankaCard.entity.userEntity.User;

import java.util.ArrayList;
import java.util.List;

public class Carrinho {

    private User usuarioCarrinho;
    private List<ItemCarrinho> produtosNoCarrinho;

    public List<ItemCarrinho> listar(){
        return produtosNoCarrinho;
    }




}
