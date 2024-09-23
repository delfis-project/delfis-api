/*
 * Classe Address
 * Model da entidade Address
 * Autor: João Diniz Araujo
 * Data: 29/08/2024
 * */

package goldenage.delfis.api.postgresql.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity(name = "address")
@Schema(description = "Endereço do usuário do app.")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do endereço", example = "1234")
    private long id;

    @NotNull(message = "O estado não pode ser nulo")
    @Size(min = 2, max = 30, message = "O estado deve ter pelo menos 3 caracteres e no máximo 30")
    @Schema(description = "Estado do endereço (UF)", example = "São Paulo")
    private String state;

    @NotNull(message = "A cidade não pode ser nula")
    @Size(min = 2, max = 30, message = "A cidade deve ter pelo menos 3 caracteres e no máximo 30")
    @Schema(description = "Cidade do endereço", example = "Osasco")
    private String city;

    @NotNull(message = "O país não pode ser nulo")
    @Size(min = 2, max = 30, message = "O país deve ter pelo menos 3 caracteres e no máximo 30")
    @Schema(description = "País do endereço", example = "Brasil")
    private String country;
}
