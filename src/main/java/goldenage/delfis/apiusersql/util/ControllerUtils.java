/*
 * Classe ControllerUtils
 * Funções para controllers
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package goldenage.delfis.apiusersql.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import javax.xml.crypto.Data;
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
}
