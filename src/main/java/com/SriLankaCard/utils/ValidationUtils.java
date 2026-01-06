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

    public static void validateNotNullOrNotBlank(Object o, String message){
        validateNotNull(o,message);
        if(o.toString().isBlank())
            throw new InvalidArgumentsException(message);

    }

    public static void validateNotNullOrNotBlank(Object o, Object o2, String message){
        validateNotNull(o,o2,message);
        if(o.toString().isBlank() || o2.toString().isBlank())
            throw new InvalidArgumentsException(message);

    }

    public static void validateNotNullOrNotBlank(Object o, Object o2, Object o3, String message){
        validateNotNull(o,o2,o3,message);
        if(o.toString().isBlank() || o2.toString().isBlank() || o3.toString().isBlank())
            throw new InvalidArgumentsException(message);

    }

    public static void validateNumbers(Long number){
        if(number == null || number <=0)
            throw new InvalidArgumentsException("O número passado precisa ser maior que 0");
    }

    public static void validateNotNullAndPositive(Number o, Object o2, String message){
        validateNotNull(o,o2,message);
        if(o.doubleValue() <= 0)
            throw new InvalidArgumentsException("O " + o.toString() + " precisa ser maior que 0");
    }

    public static void validateListNotEmpty(List<?> list,String message){
        if(list == null || list.isEmpty())
            throw new ListIsEmptyException(message);
    }
}
