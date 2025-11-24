package com.SriLankaCard.service.userServices.publicService;

import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;
import com.SriLankaCard.repository.userRepository.UserRepository;
import com.SriLankaCard.service.emailService.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int RESET_CODE_MINUTES = 15;

    // email -> (code + expiry)
    private final Map<String, ResetEntry> resetTokens = new ConcurrentHashMap<>();

    public PasswordResetService(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void enviarCodigoReset(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidArgumentsException("Email é obrigatório");
        }

        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new InvalidArgumentsException("Usuário não encontrado para este email"));

        String code = gerarCodigo6Digitos();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(RESET_CODE_MINUTES);

        resetTokens.put(email.toLowerCase(), new ResetEntry(code, expiry));

        String assunto = "Código de recuperação de senha - SriLankaCard";
        String corpo =
                "Olá, " + user.getName() + "!\n\n" +
                        "Seu código de recuperação é: " + code + "\n\n" +
                        "Ele expira em " + RESET_CODE_MINUTES + " minutos.\n" +
                        "Se você não solicitou essa alteração, ignore este e-mail.\n\n" +
                        "Equipe SriLankaCard";

        emailService.enviarEmail(user.getEmail(), assunto, corpo);
    }

    public void resetarSenha(String email, String code, String novaSenha) {
        if (email == null || email.isBlank()
                || code == null || code.isBlank()
                || novaSenha == null || novaSenha.isBlank()) {
            throw new InvalidArgumentsException("Email, código e nova senha são obrigatórios");
        }

        String key = email.toLowerCase();
        ResetEntry entry = resetTokens.get(key);

        if (entry == null) {
            throw new InvalidArgumentsException("Nenhum código ativo. Solicite outro.");
        }

        if (entry.expiresAt().isBefore(LocalDateTime.now())) {
            resetTokens.remove(key);
            throw new InvalidArgumentsException("Código expirado. Solicite outro.");
        }

        if (!entry.code().equals(code.trim())) {
            throw new InvalidArgumentsException("Código inválido.");
        }

        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new InvalidArgumentsException("Usuário não encontrado"));

        user.setPassword(passwordEncoder.encode(novaSenha));
        userRepository.save(user);

        // limpa token depois de usar
        resetTokens.remove(key);
    }

    private String gerarCodigo6Digitos() {
        int c = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(c);
    }

    private record ResetEntry(String code, LocalDateTime expiresAt) {}
}
