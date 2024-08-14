/*
 * Classe UserRole
 * Model da entidade Streak
 * Autor: João Diniz Araujo
 * Data: 13/08/2024
 * */

package com.delfis.apiuserpostgres.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Schema(description = "Representa streaks do usuário")
public class Streak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único", example = "1234")
    private long id;

    @NotNull(message = "A data de início não pode ser nula")
    @Schema(description = "Data de início do streak", example = "20/06/2023")
    @Column(name = "initial_date")
    private LocalDate initialDate;

    @Schema(description = "Data de fim do streak", example = "25/06/2023")
    @Column(name = "final_date")
    private LocalDate finalDate;

    @NotNull(message = "O usuário não pode ser nulo")
    @Schema(description = "Usuário do app")
    @ManyToOne
    @JoinColumn(name = "fk_app_user_id", nullable = false)
    private AppUser appUser;

    public UserRole() {
    }

    public UserRole(long id, LocalDate initialDate, AppUser appUser) {
        this.id = id;
        this.initialDate = initialDate;
        this.finalDate = null;
        this.appUser = appUser;
    }

    public UserRole(long id, LocalDate initialDate, LocalDate finalDate, AppUser appUser) {
        this.id = id;
        this.initialDate = initialDate;
        this.finalDate = finalDate;
        this.appUser = appUser;
    }

    public UserRole(LocalDate initialDate, AppUser appUser) {
        this.initialDate = initialDate;
        this.finalDate = null;
        this.appUser = appUser;
    }

    public UserRole(LocalDate initialDate, LocalDate finalDate, AppUser appUser) {
        this.initialDate = initialDate;
        this.finalDate = finalDate;
        this.appUser = appUser;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    public LocalDate getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(LocalDate finalDate) {
        this.finalDate = finalDate;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    @Override
    public String toString() {
        return "Streak{" +
                "id=" + id +
                ", initialDate=" + initialDate +
                ", finalDate=" + finalDate +
                ", appUser=" + appUser +
                '}';
    }
}
