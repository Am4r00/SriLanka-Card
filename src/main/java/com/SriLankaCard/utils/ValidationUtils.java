package com.SriLankaCard.utils;

import com.SriLankaCard.exception.dominio.ListIsEmptyException;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;

import java.util.List;

public class ValidationUtils {

    public static void validateNotNull(Object o, String message) {
        if (o == null)
            throw new InvalidArgumentsException(message);
    }

    public static void validateNotNull(Object o, Object o2, String message) {
        if (o == null || o2 == null)
            throw new InvalidArgumentsException(message);
    }

    public static void validateNotNull(Object o, Object o2,Object o3,String message) {
        if (o == null || o2 == null || o3 == null)
            throw new InvalidArgumentsException(message);
    }

    public static void validateNumber(Number o,String message){
        if(o.doubleValue() <= 0){
            throw new InvalidArgumentsException("Os argumentos precisam ser válidos ");
        }
    }

    public static void validateListNotEmpty(List<?> list){
        if(list == null || list.isEmpty())
            throw new ListIsEmptyException("A lista está vazia ");
    }
}
