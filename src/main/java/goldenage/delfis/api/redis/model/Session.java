package goldenage.delfis.api.redis.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@RedisHash("Session")
@Schema(description = "Entidade que representa uma sessão de usuário no sistema, com timestamps de início e fim.")
public class Session {
    @Id
    @Schema(description = "Identificador único da sessão", example = "abc123", required = true)
    private String id;

    @NotNull
    @Positive
    @Schema(description = "Chave estrangeira que faz referência ao usuário associado à sessão", example = "42", required = true)
    private long fkAppUserId;

    @NotNull
    @Schema(description = "Data e hora de início da sessão", example = "2024-09-23T08:30:00", required = true)
    private LocalDateTime initialDatetime;

    @Schema(description = "Data e hora de término da sessão", example = "2024-09-23T09:30:00")
    private LocalDateTime finalDatetime;

    public boolean isActive() {
        return finalDatetime == null;
    }
}
