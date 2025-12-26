package com.SriLankaCard.config;

import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.entity.userEntity.enums.Funcao;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.repository.userRepository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Profile("!test")
public class DefaultAdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultAdminInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.email:admin@srilankacard.local}")
    private String defaultAdminEmail;

    @Value("${app.default-admin.password:ChangeMeNow!123}")
    private String defaultAdminPassword;

    public DefaultAdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (defaultAdminEmail == null || defaultAdminEmail.isBlank()
                || defaultAdminPassword == null || defaultAdminPassword.isBlank()) {
            log.warn("Configuração de admin padrão ausente. Nenhum usuário admin padrão será criado.");
            return;
        }

        userRepository.findByEmailIgnoreCase(defaultAdminEmail)
                .ifPresentOrElse(
                        user -> log.info("Admin padrão já existe com email {}", defaultAdminEmail),
                        this::createDefaultAdmin
                );
    }

    private void createDefaultAdmin() {
        User admin = new User();
        admin.setName("Administrador Padrão");
        admin.setEmail(defaultAdminEmail.toLowerCase());
        admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
        admin.setStatus(UserStatus.ATIVO);
        admin.setFuncao(new HashSet<>(Set.of(Funcao.ADMIN)));

        userRepository.save(admin);
        log.info("Admin padrão criado com email {}", defaultAdminEmail);
    }
}
