package com.SriLankaCard;

import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.entity.userEntity.enums.Funcao;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.repository.userRepository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder; // se estiver usando Spring Security

import java.util.Set;

@SpringBootApplication
public class SriLankaCardApplication {

    public static void main(String[] args) {
        SpringApplication.run(SriLankaCardApplication.class, args);
    }

    @Bean
    CommandLineRunner initAdminUser(UserRepository repository,
                                    PasswordEncoder passwordEncoder) {
        return args -> {
            String emailAdmin = "admin@gmail.com";


            if (repository.findByEmail(emailAdmin).isEmpty()) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail(emailAdmin);
                admin.setPassword(passwordEncoder.encode("12345678"));
                admin.setFuncao(Set.of(Funcao.ADMIN));
                admin.setFuncao(Set.of(Funcao.USUARIO));
                admin.setStatus(UserStatus.ATIVO);

                repository.save(admin);
                System.out.println("Usuário ADMIN criado: " + emailAdmin);
            } else {
                System.out.println("Usuário ADMIN já existe: " + emailAdmin);
            }
        };
    }
}
