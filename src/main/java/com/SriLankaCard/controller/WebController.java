package com.SriLankaCard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/giftcard")
    public String giftcard() {
        return "giftcard";
    }

    @GetMapping("/jogos")
    public String jogos() {
        return "jogos";
    }

    @GetMapping("/employe")
    public String employe() {
        return "employe";
    }

    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }
    
    @GetMapping("/funcionarios")
    public String funcionarios() {
        return "funcionarios";
    }

    @GetMapping("/contato")
    public String contato() {
        return "contato";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/forgot")
    public String forgot() {
        return "forgot";
    }

    @GetMapping("/payment")
    public String payment() {
        return "payment";
    }

    @GetMapping("/produto")
    public String produto() {
        return "produto";
    }

    @GetMapping("/produtoDetalhe")
    public String produtoDetalhe(@RequestParam(required = false) Long id, Model model) {
        // O produto será carregado via JavaScript se o ID for fornecido
        if (id != null) {
            model.addAttribute("productId", id);
        }
        return "produtoDetalhe";
    }

    @GetMapping("/sobre")
    public String sobre() {
        return "sobre";
    }

    @GetMapping("/verify")
    public String verify() {
        return "verify";
    }

    @GetMapping("/addEmploye")
    public String addEmploye() {
        return "addEmploye";
    }

    @GetMapping("/home_admin")
    public String homeAdmin() {
        return "home_admin";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/static-test")
    public String staticTest() {
        return "static-test";
    }
    
    @GetMapping("/test-admin")
    public String testAdmin() {
        return "test-admin";
    }
    
    @GetMapping("/update-to-admin")
    public String updateToAdmin() {
        return "update-to-admin";
    }
    
    @GetMapping("/usuariodetalhe")
    public String usuarioDetalhe() {
        return "usuariodetalhe";

    @GetMapping("/confirmacaoPagamento")
    public String showConfirmacaoPage() {
        return "confirmacaoPagamento";
    }
}
