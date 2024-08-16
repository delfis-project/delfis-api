/*
 * Classe AppUser
 * Model da entidade AppUser
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package com.delfis.apiuserpostgres.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Schema(description = "Usuário base do app.")
public class AppUser {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único", example = "1234")
    private long id;

    @NotNull(message = "O nome não pode ser nulo")
    @Size(min = 3, max = 150, message = "O nome deve ter pelo menos 3 caracteres e no máximo 150")
    @Schema(description = "Nome real do usuário", example = "João Victor Diniz Araujo")
    private String name;

    @NotNull(message = "O apelido não pode ser nulo")
    @Size(min = 3, max = 20, message = "O apelido deve ter pelo menos 3 caracteres e no máximo 20")
    @Schema(description = "Apelido único do usuário", example = "jvdinizaraujo")
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
    private LocalDate birthDate;

    @NotNull(message = "A foto não pode ser nula")
    @Size(min = 10, message = "A foto deve ter pelo menos 10 caracteres")
    @Schema(description = "Url da foto de perfil do usuário", example = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRnGUKEObKP7a2L1helo1SZY1HLowd4ACTvqw&s")
    @Column(name = "picture_url")
    private String pictureUrl;

    @ManyToOne
    @NotNull(message = "O usuário deve ter um plano")
    @JoinColumn(name = "fk_plan_id")
    @Schema(description = "Plano do usuário", example = "Premium")
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "fk_user_role_id")
    @Schema(description = "Papel do usuário", example = "Administrador")
    @NotNull(message = "O usuário deve ter um papel")
    private UserRole userRole;

    @NotNull(message = "A data de criação da conta não pode ser nula")
    @Past(message = "A data de criação não pode ser futura")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Past(message = "A data de atualização não pode ser futura")
    @Column(name = "created_at")
    private LocalDateTime updatedAt;

    public AppUser(String name,
                   String username,
                   String password,
                   String email,
                   LocalDate birthDate,
                   Plan plan,
                   UserRole userRole) {
        this.name = name;
        this.username = username;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
        this.email = email;
        this.birthDate = birthDate;
        this.createdAt = LocalDateTime.now();
        this.plan = plan;
        this.userRole = userRole;
    }
}
