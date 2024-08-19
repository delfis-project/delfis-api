/*
 * Classe AppUserTheme
 * Model da entidade AppUserTheme
 * Uma classe intermediária entre Theme e AppUser
 * Por causa da cardinalidade n:n
 * Autor: João Diniz Araujo
 * Data: 19/08/2024
 * */

package com.delfis.apiuserpostgres.model;

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
@Entity(name = "app_user_theme")
@Schema(description = "Itens dos temas com seus respectivos usuários.")
public class AppUserTheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único", example = "1234")
    private long id;

    @NotNull(message = "Está em uso não pode ser nulo")
    @Schema(description = "Tema está sendo usado neste momento?", example = "true")
    private boolean isInUse;

    @NotNull(message = "O valor da transação não pode ser nulo")
    @Min(value = 0, message = "As delfiscoins deve ser pelo menos 0")
    @Schema(description = "Delfiscoins da transação de compra do tema", example = "100")
    @Column(name = "transaction_price")
    private int transactionPrice;

    @NotNull(message = "A data da transação não pode ser nula")
    @Past(message = "Data da transação não pode ser futura")
    @Schema(description = "Data da transação de compra do tema", example = "20/05/2024")
    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @ManyToOne
    @JoinColumn(name = "fk_app_user_id")
    @Schema(description = "Usuário que comprou o tema", example = "jvdinizaraujo")
    @NotNull(message = "A transação deve ter um usuário")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "fk_theme_id")
    @Schema(description = "Tema que foi comprado", example = "Vermelho Escuro")
    @NotNull(message = "A transação deve ter um tema")
    private Theme theme;

    public boolean isInUse() {
        return isInUse;
    }
}
