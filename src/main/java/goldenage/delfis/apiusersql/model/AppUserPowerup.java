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
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "app_user_powerup")
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
    @Schema(description = "Data da transação de compra do powerup", example = "2024-05-20")
    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "fk_app_user_id")
    @Schema(description = "Usuário", example = "2")
    @NotNull(message = "A transação deve ter um usuário")
    private long fkAppUserId;

    @Column(name = "fk_powerup_id")
    @Schema(description = "Powerup que foi comprado", example = "2")
    @NotNull(message = "A transação deve ter um powerup")
    private long fkPowerupId;
}
