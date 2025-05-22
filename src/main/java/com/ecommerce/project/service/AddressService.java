package com.ecommerce.project.service;

import com.ecommerce.project.payload.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(@Valid AddressDTO addressDTO);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getAddressesByUser();

    AddressDTO updateAddressById(Long addressId, @Valid AddressDTO addressDTO);

    void deleteAddressById(Long addressId);
}
