/*
 * Classe AppUserPowerup
 * Model da entidade AppUserPowerup
 * Uma classe intermediária entre Powerup e AppUser
 * Por causa da cardinalidade n:n
 * Autor: João Diniz Araujo
 * Data: 19/08/2024
 * */

package goldenage.delfis.apiusersql.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity(name = "app_user_powerup")
@Schema(description = "Itens dos powerups com seus respectivos usuários.")
public class AppUserPowerup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do registro", example = "1234")
    private long id;

    @NotNull(message = "O valor da transação não pode ser nulo")
    @Min(value = 0, message = "O valor da transação deve ser pelo menos 0")
    @Schema(description = "Valor da transação em delfiscoins", example = "100")
    @Column(name = "transaction_price")
    private int transactionPrice;

    @NotNull(message = "A data da transação não pode ser nula")
    @Past(message = "Data da transação não pode ser futura")
    @Schema(description = "Data da transação de compra do powerup", example = "2024-05-20")
    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @ManyToOne
    @JoinColumn(name = "fk_app_user_id")
    @Schema(description = "Usuário que comprou o powerup", example = "{ \"id\": 1234, \"username\": \"jvdinizaraujo\" }")
    @NotNull(message = "A transação deve ter um usuário")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "fk_powerup_id")
    @Schema(description = "Powerup que foi comprado", example = "{ \"id\": 5678, \"name\": \"Dobro de Pontos\" }")
    @NotNull(message = "A transação deve ter um powerup")
    private Powerup powerup;
}
