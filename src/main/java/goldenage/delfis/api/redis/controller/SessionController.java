/*
 * Classe SessionController
 * Controller da entidade Session
 * Autor: João Diniz Araujo
 * Data: 24/09/2024
 */

package goldenage.delfis.api.redis.controller;

import goldenage.delfis.api.postgresql.model.AppUser;
import goldenage.delfis.api.postgresql.service.AppUserService;
import goldenage.delfis.api.redis.model.Session;
import goldenage.delfis.api.redis.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/session")
@Schema(description = "Controlador responsável pela gestão de sessões")
public class SessionController {
    private final SessionService sessionService;
    private final AppUserService appUserService;

    public SessionController(SessionService sessionService, AppUserService appUserService) {
        this.sessionService = sessionService;
        this.appUserService = appUserService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todas as sessões", description = "Retorna uma lista de todas as sessões.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões encontradas", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Session.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhuma sessão encontrada", content = @Content)
    })
    public ResponseEntity<List<Session>> getSessions() {
        List<Session> sessions = sessionService.getSessions();
        if (sessions != null && !sessions.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(sessions);
        throw new EntityNotFoundException("Nenhuma sessão encontrada.");
    }

    @PostMapping("/insert/{fkAppUserId}")
    @Operation(summary = "Inserir uma nova sessão", description = "Insere uma nova sessão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessão inserida", content = @Content(schema = @Schema(implementation = Session.class))),
    })
    public ResponseEntity<Session> insertSession(@PathVariable long fkAppUserId) {
        verifyFk(fkAppUserId);
        Session session = new Session(null, fkAppUserId, LocalDateTime.now(), null);
        return ResponseEntity.status(HttpStatus.OK).body(sessionService.saveSession(session));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar uma sessão", description = "Deleta uma sessão com o ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessão deletada com sucesso", content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada para o ID fornecido", content = @Content)
    })
    public ResponseEntity<Boolean> deleteSession(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(sessionService.deleteSession(id));
    }

    @PostMapping("/finish/{fkAppUserId}")
    @Operation(summary = "Finalizar uma sessão", description = "Finaliza uma sessão aberta atualizando o campo finalDatetime com a data/hora de finalização.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessão finalizada com sucesso", content = @Content(schema = @Schema(implementation = Session.class))),
            @ApiResponse(responseCode = "404", description = "Nenhuma sessão aberta encontrada para o usuário fornecido", content = @Content)
    })
    public ResponseEntity<Session> finishSession(@PathVariable long fkAppUserId) {
        verifyFk(fkAppUserId);
        Session session = sessionService.getUnfinishedSessionByFkAppUserById(fkAppUserId);
        if (session == null) throw new EntityNotFoundException("Nenhuma sessão aberta desse usuário.");

        return ResponseEntity.status(HttpStatus.OK).body(sessionService.finishSession(session.getId()));
    }

    @GetMapping("/get-total-session-time-by-app-user-id/{fkAppUserId}")
    @Operation(summary = "Obter tempo total de sessão por ID de usuário",
            description = "Retorna o tempo total de todas as sessões finalizadas de um usuário, em minutos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tempo total de sessão calculado com sucesso",
                    content = @Content(schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "404", description = "Nenhuma sessão finalizada encontrada para o usuário fornecido",
                    content = @Content)
    })
    public ResponseEntity<Double> getTotalSessionTimeByFkAppUserId(@PathVariable long fkAppUserId) {
        List<Session> sessions = sessionService.getFinishedSessionsByFkAppUserId(fkAppUserId);
        if (sessions == null || sessions.isEmpty())
            throw new EntityNotFoundException("Sem sessões finalizadas para o ID informado.");

        return ResponseEntity.status(HttpStatus.OK).body(sessions
                .stream()
                .map(session -> Duration.between(session.getInitialDatetime(), session.getFinalDatetime()))
                .reduce(Duration.ZERO, Duration::plus)
                .getSeconds() / 60.0);
    }

    private void verifyFk(long fkAppUserId) {
        AppUser appUser = appUserService.getAppUserById(fkAppUserId);
        if (appUser == null) throw new EntityNotFoundException("Usuário não encontrado.");
    }
}
