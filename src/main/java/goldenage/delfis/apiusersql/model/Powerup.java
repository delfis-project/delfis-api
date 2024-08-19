/*
 * Classe Powerup
 * Model da entidade Powerup
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package goldenage.delfis.apiusersql.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Schema(description = "Representa um powerup disponível para os jogos.")
public class Powerup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do powerup", example = "1234")
    private long id;

    @NotNull(message = "O nome não pode ser nulo")
    @Size(min = 2, max = 30, message = "O nome deve ter pelo menos 2 caracteres e no máximo 30")
    @Schema(description = "Nome do powerup", example = "Dobro de Pontos")
    private String name;

    @NotNull(message = "O preço não pode ser nulo")
    @Min(value = 0, message = "O preço deve ser pelo menos 0")
    @Schema(description = "Preço do powerup em delfiscoins", example = "450")
    private int price;

    @NotNull(message = "A foto não pode ser nula")
    @Size(min = 10, message = "A URL da foto deve ter pelo menos 10 caracteres")
    @Schema(description = "URL da foto do powerup", example = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTWP0CCdUG2O8eMxg1xgAt-yUSUEz4lt6eIkA&s")
    @Column(name = "store_picture_url")
    private String storePictureUrl;
}
