/*
 * Classe AddressRepository
 * Repository da entidade Address
 * Autor: João Diniz Araujo
 * Data: 29/08/2024
 * */

package goldenage.delfis.api.postgresql.repository;

import goldenage.delfis.api.postgresql.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
