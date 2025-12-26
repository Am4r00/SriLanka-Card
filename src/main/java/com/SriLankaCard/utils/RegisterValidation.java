package com.SriLankaCard.utils;

import com.SriLankaCard.dto.request.user.admin.AdminCreateRequest;
import com.SriLankaCard.dto.request.user.userPublic.RegisterUserRequest;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;


public class RegisterValidation {

    public static RegisterUserRequest checkRegister(RegisterUserRequest req){
        if(req == null)
            throw new InvalidArgumentsException("Argumento passado está inválido");

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
        basic.setPassword(req.getPassword());
        checkRegister(basic);

        req.setEmail(basic.getEmail());

        if(req.getStatus() == null)
            throw new InvalidArgumentsException("É nescessário passar algum Status");

        if(req.getFuncoes() == null||req.getFuncoes().isEmpty())
            throw new InvalidArgumentsException("Usuário precisa ter alguma função");

        return req;
    }
}
