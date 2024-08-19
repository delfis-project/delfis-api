/*
 * Classe Streak
 * Model da entidade Streak
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package com.delfis.apiuserpostgres.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
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
@Entity
@Schema(description = "Representa o streak (sequência de dias) dos usuários.")
public class Streak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do streak", example = "1234")
    @NotNull(message = "ID não pode ser nulo")
    private long id;

    @Column(name = "initial_date")
    @NotNull(message = "A data de início não pode ser nula")
    @Past(message = "A data de início não pode ser futura")
    @Schema(description = "Data de início do streak", example = "2024-08-01")
    private LocalDate initialDate;

    @Column(name = "final_date")
    @Past(message = "A data de fim não pode ser futura")
    @Schema(description = "Data de fim do streak", example = "2024-08-15")
    private LocalDate finalDate;

    @ManyToOne
    @JoinColumn(name = "fk_app_user_id")
    @NotNull(message = "O streak deve ter um usuário associado")
    @Schema(description = "Usuário associado ao streak", example = "jvdinizaraujo")
    private AppUser appUser;
}
