/*
 * Classe AddressRepository
 * Repository da entidade Address
 * Autor: João Diniz Araujo
 * Data: 29/08/2024
 * */

package goldenage.delfis.apiusersql.repository;

import goldenage.delfis.apiusersql.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
