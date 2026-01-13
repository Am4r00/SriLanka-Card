package com.SriLankaCard.service.emailService;


import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service
public class EmailService {
    private final JavaMailSender mailSender ;
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(String para, String assunto, String corpo){
        log.info(">>> Enviando e-mail para: [" + para + "]");

        if (para == null || !para.contains("@")) {
            throw new IllegalArgumentException("Destinatário inválido: " + para);
        }

        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setFrom("joao.amarodev@gmail.com");

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

    public void enviarAlteraçãoDeInformacoes(String para, String nome){
        String assunto = "Solicitação de Troca de informações ✅";

        String corpo =
                "Olá, " + (nome != null ? nome : "usuário") + "!\n\n" +
                        "Suas informações foram alteradas com sucesso ! ✅\n" +
                        "Qualquer dúvida, estamos por aqui.\n\n" +
                        "Equipe SriLankaCard";

        enviarEmail(para, assunto, corpo);

    }
}