/*
 * Classe AppUser
 * Model da entidade AppUser
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package goldenage.delfis.apiusersql.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity(name = "app_user")
@Schema(description = "Usuário base do app.")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do usuário", example = "1234")
    private long id;

    @NotNull(message = "O nome não pode ser nulo")
    @Size(min = 3, max = 150, message = "O nome deve ter pelo menos 3 caracteres e no máximo 150")
    @Schema(description = "Nome real do usuário", example = "João Victor Diniz Araujo")
    private String name;

    @NotNull(message = "O apelido não pode ser nulo")
    @Size(min = 3, max = 20, message = "O apelido deve ter pelo menos 3 caracteres e no máximo 20")
    @Schema(description = "Apelido único do usuário", example = "jvdinizaraujo")
    @Column(unique = true)
    private String username;

    @NotNull(message = "A senha não pode ser nula")
    @Schema(description = "Senha criptografada do usuário")
    private String password;

    @NotNull(message = "O email não pode ser nulo")
    @Size(min = 5, max = 100, message = "O email deve ter pelo menos 5 caracteres e no máximo 100")
    @Email(message = "Email não é válido",
            regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    @Schema(description = "Email único do usuário", example = "jvdinizaraujo@gmail.com")
    @Column(unique = true)
    private String email;

    @NotNull(message = "O level não pode ser nulo")
    @Min(value = 0, message = "O level deve ser pelo menos 0")
    @Schema(description = "Level do usuário", example = "3")
    private int level;

    @NotNull(message = "Os pontos não podem ser nulo")
    @Min(value = 0, message = "Os pontos deve ser pelo menos 0")
    @Schema(description = "Pontos do usuário", example = "40")
    private int points;

    @NotNull(message = "As delfiscoins não podem ser nulas")
    @Min(value = 0, message = "As delfiscoins deve ser pelo menos 0")
    @Schema(description = "Delfiscoins do usuário", example = "100")
    private int coins;

    @NotNull(message = "Data de nascimento não pode ser nula")
    @Past(message = "Data de nascimento não pode ser futura")
    @Column(name = "birth_date")
    @Schema(description = "Data de nascimento do usuário", example = "1990-05-15")
    private LocalDate birthDate;

    @Size(min = 10, message = "A foto deve ter pelo menos 10 caracteres")
    @Schema(description = "Url da foto de perfil do usuário", example = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRnGUKEObKP7a2L1helo1SZY1HLowd4ACTvqw&s")
    @Column(name = "picture_url")
    private String pictureUrl;

    @ManyToOne
    @NotNull(message = "O usuário deve ter um plano")
    @JoinColumn(name = "fk_plan_id")
    @Schema(description = "Plano do usuário", example = "{ \"id\": 1, \"name\": \"Premium\", \"price\": 29.99 }")
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "fk_user_role_id")
    @Schema(description = "Papel do usuário", example = "{ \"id\": 1, \"name\": \"Administrador\" }")
    @NotNull(message = "O usuário deve ter um papel")
    private UserRole userRole;

    @Past(message = "A data de criação não pode ser futura")
    @Column(name = "created_at")
    @Schema(description = "Data e hora de criação da conta", example = "2024-08-15T14:30:00")
    private LocalDateTime createdAt;

    @Past(message = "A data de atualização não pode ser futura")
    @Column(name = "updated_at")
    @Schema(description = "Data e hora da última atualização da conta", example = "2024-08-18T10:00:00")
    private LocalDateTime updatedAt;

    @OneToMany
    @Schema(description = "Lista de streaks do usuário", implementation = Streak.class)
    private List<Streak> streaks;

    @OneToMany
    @Schema(description = "Lista de pagamentos de plano do usuário", implementation = PlanPayment.class)
    private List<PlanPayment> payments;

    @OneToMany(mappedBy = "fkAppUserId", cascade = CascadeType.ALL)
    @Schema(description = "Lista de associações entre o usuário e seus powerups", implementation = AppUserPowerup.class)
    private List<AppUserPowerup> appUserPowerups;

    @OneToMany(mappedBy = "fkAppUserId", cascade = CascadeType.ALL)
    @Schema(description = "Lista de temas do usuário", implementation = AppUserTheme.class)
    private List<AppUserTheme> appUserThemes;
}
