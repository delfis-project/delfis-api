/*
 * Classe ControllerUtils
 * Funções para controllers
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package goldenage.delfis.apiusersql.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ControllerUtils {
    /*
     * Verifica um objeto e retorna se ele está com erros.
     * Valida com base nas annotations de cada entidade.
     * */
    public static <T> Map<String, String> verifyObject(T obj, List<String> fields) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Map<String, String> errors = new HashMap<>();

        for (String field : fields) {
            Set<ConstraintViolation<T>> violations = validator.validateProperty(obj, field);
            if (!violations.isEmpty()) {
                for (ConstraintViolation<T> violation : violations) {
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                }
            }
        }

        return errors;
    }

    public static String getDiveSpecificMessage(String rootMessage) {
        rootMessage = rootMessage.toUpperCase().strip();
        String specificMessage;
        if (rootMessage.contains("FOREIGN KEY")) {
            specificMessage = "Erro ao salvar o usuário: uma das referências de chave estrangeira é inválida ou não existe.";
        } else if (rootMessage.contains("UNIQUE")) {
            specificMessage = "Erro ao salvar o usuário: um campo que deve ser único já está em uso.";
        } else if (rootMessage.contains("NULL NOT ALLOWED")) {
            specificMessage = "Erro ao salvar o usuário: um campo obrigatório não foi preenchido.";
        } else {
            specificMessage = "Erro de integridade de dados ao salvar o usuário.";
        }

        return specificMessage;
    }
}
