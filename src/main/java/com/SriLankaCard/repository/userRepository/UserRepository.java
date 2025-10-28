package com.SriLankaCard.repository.userRepository;

import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.entity.userEntity.enums.Funcao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);


    @Query("select case when count(u)> 0 then true else false end from User u join u.funcao f where f = :role")
    boolean existsByFuncaoContains(@Param("role") Funcao role);
}
