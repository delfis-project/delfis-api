/*
 * Classe Plan
 * Model da entidade Plan
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package com.delfis.apiuserpostgres.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Schema(description = "Plano de assinatura do usuário")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único", example = "1234")
    @NotNull(message = "ID não pode ser nulo")
    private long id;

    @NotNull(message = "O nome não pode ser nulo")
    @Size(min = 2, max = 20, message = "O nome deve ter pelo menos 2 caracteres e no máximo 20")
    @Schema(description = "Nome do plano", example = "Premium")
    private String name;

    @Column(name = "price", precision = 12, scale = 2)
    @Min(value = 0, message = "O valor mínimo é zero.")
    @NotNull(message = "O preço não pode ser nulo")
    @Schema(description = "Preço do plano", example = "25.99")
    private BigDecimal price;

    @NotNull(message = "A descrição não pode ser nula")
    @Size(min = 5, max = 500, message = "A descrição deve ter pelo menos 5 caracteres e no máximo 500")
    @Schema(description = "Descrição do plano", example = "Sem anúncios e libera X para o usuário.")
    private String description;

    public Plan(String name, BigDecimal price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
