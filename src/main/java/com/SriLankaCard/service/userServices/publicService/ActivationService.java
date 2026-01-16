package com.SriLankaCard.service.userServices.publicService;

import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.exception.dominio.UserNotFoundException;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;
import com.SriLankaCard.repository.userRepository.UserRepository;
import com.SriLankaCard.service.emailService.EmailService;
import com.SriLankaCard.utils.ValidationUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActivationService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int CODE_EXPIRY_MINUTES = 15;

    private final UserRepository userRepository;
    private final EmailService emailService;

    // email -> (code + expiry)
    private final Map<String, ActivationEntry> codes = new ConcurrentHashMap<>();

    public ActivationService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void enviarCodigo(String email) {
        ValidationUtils.validateNotNullOrNotBlank(email, "Email é obrigatório");

        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        // Se já estiver ativo, não precisa enviar código
        if (user.getStatus() == UserStatus.ATIVO) {
            return;
        }

        String code = gerarCodigo6Digitos();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES);

        codes.put(email.toLowerCase(), new ActivationEntry(code, expiry));

        String assunto = "Código de ativação - SriLankaCard";
        String corpo = "Olá, " + user.getName() + "!\n\n" +
                "Seu código de ativação é: " + code + "\n" +
                "Ele expira em " + CODE_EXPIRY_MINUTES + " minutos.\n" +
                "Use-o para reativar sua conta.\n\n" +
                "Equipe SriLankaCard";

        emailService.enviarEmail(user.getEmail(), assunto, corpo);
    }

    public void ativar(String email, String code) {
        ValidationUtils.validateNotNullOrNotBlank(email, code, "Email e código são obrigatórios");

        String key = email.toLowerCase();
        ActivationEntry entry = codes.get(key);

        ValidationUtils.validateNotNull(entry, "Nenhum código ativo. Solicite outro.");

        if (entry.expiresAt().isBefore(LocalDateTime.now())) {
            codes.remove(key);
            throw new InvalidArgumentsException("Código expirado. Solicite outro.");
        }

        if (!entry.code().equals(code.trim())) {
            throw new InvalidArgumentsException("Código inválido.");
        }

        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        user.setStatus(UserStatus.ATIVO);
        userRepository.save(user);

        codes.remove(key);
    }

    private String gerarCodigo6Digitos() {
        int c = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(c);
    }

    private record ActivationEntry(String code, LocalDateTime expiresAt) {}
}

