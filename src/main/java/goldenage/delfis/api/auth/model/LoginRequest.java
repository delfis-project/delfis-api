package goldenage.delfis.api.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "Entidade de login na API.")
public class LoginRequest {
    @NotNull(message = "O apelido não pode ser nulo")
    @Size(min = 3, max = 20, message = "O apelido deve ter pelo menos 3 caracteres e no máximo 20")
    @Schema(description = "Apelido único do usuário", example = "jvdinizaraujo")
    private String username;

    @NotNull(message = "A senha não pode ser nula")
    @Schema(description = "Senha do usuário")
    private String password;
}
