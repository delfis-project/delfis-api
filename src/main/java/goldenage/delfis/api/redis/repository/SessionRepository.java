/*
 * Classe SessionRepository
 * Repository da entidade Session
 * Autor: João Diniz Araujo
 * Data: 23/09/2024
 * */

package goldenage.delfis.api.redis.repository;

import goldenage.delfis.api.redis.model.Session;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableRedisRepositories
public interface SessionRepository extends CrudRepository<Session, String> {
    List<Session> findSessionByFkAppUserIdAndFinalDatetimeIsNull(long fkAppUserId);
    List<Session> findSessionByFkAppUserIdAndFinalDatetimeIsNotNull(long fkAppUserId);
}
