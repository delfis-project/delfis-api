/*
 * Classe PlanPayment
 * Model da entidade PlanPayment
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.apiusersql.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity(name = "plan_payment")
@Schema(description = "Registro dos pagamentos realizados para planos de assinatura.")
public class PlanPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do pagamento", example = "1234")
    private long id;

    @NotNull(message = "O preço não pode ser nulo")
    @Min(value = 0, message = "O preço deve ser pelo menos 0")
    @Schema(description = "Valor do pagamento em formato decimal", example = "19.99")
    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull(message = "A data de pagamento não pode ser nula")
    @Schema(description = "Data e hora do pagamento", example = "2024-08-16T14:30:00")
    @Column(name = "payment_timestamp")
    private LocalDateTime paymentTimestamp;

    @NotNull(message = "A data de expiração do plano não pode ser nula")
    @Schema(description = "Data e hora de expiração do plano", example = "2024-09-16T14:30:00")
    @Column(name = "expiration_timestamp")
    private LocalDateTime expirationTimestamp;

    @ManyToOne
    @NotNull(message = "O pagamento precisa ter um plano associado")
    @JoinColumn(name = "fk_plan_id")
    @Schema(description = "Plano associado ao pagamento", example = "Premium")
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "fk_app_user_id")
    @NotNull(message = "O pagamento deve ter um usuário associado")
    @Schema(description = "Usuário que realizou o pagamento", example = "jvdinizaraujo")
    private AppUser appUser;
}
