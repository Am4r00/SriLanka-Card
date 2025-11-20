package com.SriLankaCard.utils;

import com.SriLankaCard.dto.request.user.admin.AdminCreateRequest;
import com.SriLankaCard.dto.request.user.userPublic.RegisterUserRequest;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;


public class RegisterValidation {

    public static RegisterUserRequest checkRegister(RegisterUserRequest req){
        if(req == null)
            throw new InvalidArgumentsException("Argumento passad está inválido");

        if(req.getName() == null || req.getName().isEmpty())
            throw new InvalidArgumentsException("Nome é obrigatório.");

        if (req.getEmail() == null || req.getEmail().isEmpty())
            throw new InvalidArgumentsException("E-mail é obrigatório.");


        if (req.getPassword() == null || req.getPassword().isEmpty())
            throw new InvalidArgumentsException("Senha é obrigatória.");

        req.setEmail(req.getEmail().toLowerCase());

        return req;
    }

    public static AdminCreateRequest checkRegisterByAdmin(AdminCreateRequest req){

        RegisterUserRequest basic = new RegisterUserRequest();
        basic.setName(req.getName());
        basic.setEmail(req.getEmail());
        // Password pode ser null se será definido pelo controller
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            basic.setPassword(req.getPassword());
            checkRegister(basic);
        } else {
            // Validar apenas name e email se password não foi fornecido
            if(req.getName() == null || req.getName().isEmpty())
                throw new InvalidArgumentsException("Nome é obrigatório.");
            if (req.getEmail() == null || req.getEmail().isEmpty())
                throw new InvalidArgumentsException("E-mail é obrigatório.");
            basic.setEmail(req.getEmail().toLowerCase());
        }

        req.setEmail(basic.getEmail());

        // Status e funcoes podem ser definidos pelo controller, então só validar se não foram fornecidos
        // (mas o controller já deve ter definido antes de chamar esta validação)
        if(req.getStatus() == null)
            throw new InvalidArgumentsException("É nescessário passar algum Status");

        if(req.getFuncoes() == null||req.getFuncoes().isEmpty())
            throw new InvalidArgumentsException("Usuário precisa ter alguma função");

        return req;
    }
}
