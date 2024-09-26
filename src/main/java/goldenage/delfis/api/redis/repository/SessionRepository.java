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

@EnableRedisRepositories
public interface SessionRepository extends CrudRepository<Session, String> {
}
