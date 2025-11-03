package com.SriLankaCard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup";
    }

    @GetMapping("/contato")
    public String showContactPage() {
        return "contato";
    }

    @GetMapping("/faq")
    public String showFaqPage() {
        return "faq";
    }

    @GetMapping("/sobre")
    public String showSobrePage() {
        return "sobre";
    }

    @GetMapping("/giftcard")
    public String showgiftcardPage() {
        return "giftcard";
    }

    @GetMapping("/jogos")
    public String showJogosdPage() {
        return "giftcard";
    }

    @GetMapping("/produto")
    public String showProdutoPage() {
        return "produto";
    }

    @GetMapping("/funcionarios")
    public String showFuncionariosPage() {
        return "funcionarios";
    }

    @GetMapping("/cart")
    public String showCartPage() {
        return "cart";
    }
}
