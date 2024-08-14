package com.delfis.apiuserpostgres.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Papel do usuário no sistema.")
public class UserRole {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Schema(description = "ID único", example = "1234")
    private long id;

    @NotNull(message = "O nome não pode ser nulo")
    @Size(min = 2, max = 20, message = "O nome deve ter pelo menos 2 caracteres e no máximo 20")
    @Schema(description = "Nome do papel", example = "Administrador")
    private String nome;

    public UserRole(){}

    public UserRole(long id, String nome){
        this.id = id;
        this.nome = nome;
    }

    public UserRole(String nome){
        this.nome = nome;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
