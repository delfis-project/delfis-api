/*
 * Classe SessionService
 * Service da entidade Session
 * Autor: João Diniz Araujo
 * Data: 16/09/2024
 * */

package goldenage.delfis.api.redis.service;

import goldenage.delfis.api.redis.model.Session;
import goldenage.delfis.api.redis.repository.SessionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session saveSession(Session session) {
        return sessionRepository.save(session);
    }

    public List<Session> getSessions() {
        List<Session> sessions = new ArrayList<>();
        sessionRepository.findAll().forEach(sessions::add);
        return !sessions.isEmpty() ? sessions : null;
    }

    public Session getUnfinishedSessionByFkAppUserById(long fkAppUserId) {
        List<Session> sessions = sessionRepository.findSessionByFkAppUserIdAndFinalDatetimeIsNull(fkAppUserId);

        return sessions.stream()
                .filter(session -> session.getFkAppUserId() == fkAppUserId && session.getFinalDatetime() == null)
                .sorted((s1, s2) -> s2.getInitialDatetime().compareTo(s1.getInitialDatetime()))
                .findFirst()
                .orElse(null);
    }

    public List<Session> getFinishedSessionsByFkAppUserId(long fkAppUserId) {
        List<Session> sessions = sessionRepository.findSessionByFkAppUserIdAndFinalDatetimeIsNotNull(fkAppUserId);
        return !sessions.isEmpty() ? sessions : null;
    }

    public boolean deleteSession(String id) {
        if (sessionRepository.findById(id).isEmpty())
            throw new EntityNotFoundException("Sem sessões para o ID enviado.");
        sessionRepository.deleteById(id);
        return sessionRepository.findById(id).isEmpty();
    }

    public Session finishSession(String sessionId) {
        return sessionRepository.findById(sessionId).map(existingSession -> {
            existingSession.setFinalDatetime(LocalDateTime.now());
            return sessionRepository.save(existingSession);
        }).orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada com ID: " + sessionId));
    }
}