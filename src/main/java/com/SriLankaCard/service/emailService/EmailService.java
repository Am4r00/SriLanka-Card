package com.SriLankaCard.service.emailService;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender ;


    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(String para, String assunto, String corpo){
        // LOG SIMPLES PRA CONFERIR
        System.out.println(">>> Enviando e-mail para: [" + para + "]");

        if (para == null || !para.contains("@")) {
            throw new IllegalArgumentException("Destinatário inválido: " + para);
        }

        SimpleMailMessage msg = new SimpleMailMessage();

        // remetente explícito = o mesmo do spring.mail.username
        msg.setFrom("joao.amarodev@gmail.com");

        // destinatário limpo
        msg.setTo(para.trim());

        msg.setSubject(assunto);
        msg.setText(corpo);

        mailSender.send(msg);
    }

    public void enviarBoasVindas(String para, String nome){
        String assunto = "🎉 Bem-vindo(a) à SriLankaCard!";
        String corpo =
                "Olá, " + (nome != null ? nome : "usuário") + "!\n\n" +
                        "Seja muito bem-vindo(a) à SriLankaCard. 🚀\n" +
                        "Agora você já pode comprar Gift Cards e jogos com segurança e praticidade.\n\n" +
                        "Qualquer dúvida, estamos por aqui.\n\n" +
                        "Equipe SriLankaCard";

        enviarEmail(para, assunto, corpo);
    }
}
