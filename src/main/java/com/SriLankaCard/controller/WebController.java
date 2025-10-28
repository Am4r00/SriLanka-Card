package com.SriLankaCard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/singup")
    public String singup() {
        return "singup";
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
    public String produtoDetalhe() {
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
}
