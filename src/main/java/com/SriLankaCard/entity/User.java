package com.SriLankaCard.entity;

import com.SriLankaCard.entity.enums.Funcao;
import com.SriLankaCard.entity.enums.UserStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "users")
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Banco tem a coluna "nome"
    @Column(name = "nome", nullable = false)
    @NotBlank(message = "Nome não pode estar vazio")
    private String name;

    // Use a coluna "password" (consolide dados se tinha 'senha'/'passoword')
    @Column(name = "password", nullable = false)
    @NotBlank(message = "Senha não pode estar vazia")
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    @NotBlank(message = "E-mail não pode estar vazio")
    @Email(message = "E-mail inválido")
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "users_funcoes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "funcao", nullable = false)
    private Set<Funcao> funcao = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return (funcao == null ? List.<Funcao>of() : funcao)
                .stream()
                .map(f -> (GrantedAuthority) () -> "ROLE_" + f.name())
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

}
