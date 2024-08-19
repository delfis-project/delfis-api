/*
 * Classe UserRole
 * Model da entidade UserRole
 * Autor: João Diniz Araujo
 * Data: 13/08/2024
 * */

package com.delfis.apiuserpostgres.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity(name = "user_role")
@Schema(description = "Define o papel de um usuário no sistema.")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do papel", example = "1234")
    @NotNull(message = "O ID não pode ser nulo")
    private long id;

    @NotNull(message = "O nome do papel não pode ser nulo")
    @Size(min = 2, max = 20, message = "O nome do papel deve ter pelo menos 2 caracteres e no máximo 20")
    @Schema(description = "Nome do papel de usuário", example = "Administrador")
    private String name;
}
