/*
 * Classe AddressController
 * Controller da entidade Address
 * Autor: João Diniz Araujo
 * Data: 04/09/2024
 */

package goldenage.delfis.apiusersql.controller;

import goldenage.delfis.apiusersql.model.Address;
import goldenage.delfis.apiusersql.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os endereços de usuário", description = "Retorna uma lista de todos os endereços de usuário registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de endereços encontrados", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Address.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum endereço encontrado", content = @Content)
    })
    public ResponseEntity<List<Address>> getAddresses() {
        List<Address> addresses = addressService.getAddresses();
        if (addresses != null) {
            return ResponseEntity.status(HttpStatus.OK).body(addresses);
        }
        throw new EntityNotFoundException("Nenhum endereço encontrado.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo endereço de usuário", description = "Cria um novo endereço de usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso", content = @Content(schema = @Schema(implementation = Address.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Endereço já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<Address> insertAddress(
            @Parameter(description = "Dados do novo endereço de usuário", required = true)
            @Valid @RequestBody Address address) {
        try {
            formatAddressFields(address);
            Address savedAddress = addressService.saveAddress(address);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um endereço de usuário", description = "Remove um endereço de usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem usuários cadastrados com esse endereço", content = @Content)
    })
    public ResponseEntity<String> deleteAddress(
            @Parameter(description = "ID do endereço de usuário a ser deletado", required = true)
            @PathVariable Long id) {
        try {
            if (addressService.deleteAddressById(id) == null) {
                throw new EntityNotFoundException("Endereço não encontrado.");
            }
            return ResponseEntity.status(HttpStatus.OK).body("Endereço deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Existem usuários cadastrados com esse endereço. Mude-os para excluir esse endereço.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar um endereço de usuário", description = "Atualiza todos os dados de um endereço de usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso", content = @Content(schema = @Schema(implementation = Address.class))),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<Address> updateAddress(
            @Parameter(description = "ID do endereço de usuário a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Dados atualizados do endereço de usuário", required = true)
            @Valid @RequestBody Address address) {
        if (addressService.getAddressById(id) == null) {
            throw new EntityNotFoundException("Endereço não encontrado.");
        }

        formatAddressFields(address);
        address.setId(id);
        Address updatedAddress = addressService.saveAddress(address);
        return ResponseEntity.status(HttpStatus.OK).body(updatedAddress);
    }

    private void formatAddressFields(Address address) {
        address.setCountry(address.getCountry().strip().toUpperCase());
        address.setCity(address.getCity().strip().toUpperCase());
        address.setState(address.getState().strip().toUpperCase());
    }
}
