/*
 * Classe ThemeController
 * Controller da entidade Theme
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package com.delfis.apiuserpostgres.controller;

import com.delfis.apiuserpostgres.model.Theme;
import com.delfis.apiuserpostgres.service.ThemeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/theme")
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getThemes() {
        List<Theme> themes = themeService.getThemes();
        if (!themes.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(themes);

        throw new EntityNotFoundException("Nenhum tema encontrado.");
    }

    @GetMapping("/get-by-name/{name}")
    public ResponseEntity<?> getThemeByName(@PathVariable String name) {
        Theme theme = themeService.getThemeByName(name.strip());
        if (theme != null) return ResponseEntity.status(HttpStatus.OK).body(theme);

        throw new EntityNotFoundException("Nenhum tema encontrado.");
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertTheme(@Valid @RequestBody Theme theme) {
        try {
            Theme savedTheme = themeService.saveTheme(theme);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTheme);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tema com esse nome ou foto já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTheme(@PathVariable Long id) {
        try {
            if (themeService.deleteThemeById(id) == null) throw new EntityNotFoundException("Tema não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Tema deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem usuários cadastrados com esse tema. Mude-os para excluir esse tema.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTheme(@PathVariable Long id, @Valid @RequestBody Theme theme) {
        if (themeService.getThemeById(id) == null) throw new EntityNotFoundException("Tema não encontrado.");

        return insertTheme(new Theme(id, theme.getName(), theme.getPrice(), theme.getStorePictureUrl()));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateThemePartially(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Theme existingTheme = themeService.getThemeById(id);  // validando se existe
        if (existingTheme == null) throw new EntityNotFoundException("Tema não encontrado.");

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "name":
                        existingTheme.setName((String) value);
                        break;
                    case "price":
                        existingTheme.setPrice((Integer) value);
                        break;
                    case "storePictureUrl":
                        existingTheme.setStorePictureUrl((String) value);
                        break;
                    default:
                        throw new IllegalArgumentException("Campo " + key + " não é atualizável.");
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Valor inválido para o campo " + key + ": " + e.getMessage(), e);
            }
        });

        // validando se ele mandou algum campo errado
        Map<String, String> errors = ControllerUtils.verifyObject(existingTheme, new ArrayList<>(updates.keySet()));
        if (!errors.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        themeService.saveTheme(existingTheme);
        return ResponseEntity.status(HttpStatus.OK).body("Tema atualizado com sucesso.");
    }
}
