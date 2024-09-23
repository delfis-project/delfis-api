/*
 * Classe AddressService
 * Service da entidade Address
 * Autor: João Diniz Araujo
 * Data: 29/08/2024
 * */

package goldenage.delfis.api.postgresql.service;

import goldenage.delfis.api.postgresql.model.Address;
import goldenage.delfis.api.postgresql.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * @return todos os endereços do banco.
     */
    public List<Address> getAddresses() {
        List<Address> addresss = addressRepository.findAll();
        return addresss.isEmpty() ? null : addresss;
    }

    /**
     * @return endereço pelo id.
     */
    public Address getAddressById(Long id) {
        Optional<Address> address = addressRepository.findById(id);
        return address.orElse(null);  // vai retornar address se ele existir, se não retorna null
    }


    /**
     * @return address deletado.
     */
    public Address deleteAddressById(Long id) {
        Address address = getAddressById(id);
        if (address != null) addressRepository.deleteById(id);
        return address;
    }

    /**
     * @return address inserido.
     */
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }
}
