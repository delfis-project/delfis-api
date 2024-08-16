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
@Schema(description = "Streaks dos usuários")
public class Streak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único", example = "1234")
    @NotNull(message = "ID não pode ser nulo")
    private long id;

    @Column(name = "inital_date")
    @NotNull(message = "A data de início não pode ser nula")
    @Past(message = "A data de início não pode ser futura")
    private LocalDate initalDate;

    @Column(name = "final_date")
    @Past(message = "A data de fim não pode ser futura")
    private LocalDate finalDate;

    @ManyToOne
    @JoinColumn(name = "fk_app_user_id")
    @NotNull(message = "O streak deve ter um usuário")
    private AppUser appUser;
}
