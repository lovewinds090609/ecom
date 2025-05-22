package com.ecommerce.project.service;

import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.util.AuthUtil;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImplementation implements AddressService{
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private AddressRepository addressRepository;
    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        Address address = modelMapper.map(addressDTO, Address.class);
        //addressRepository.save(address);
        address.setUser(user);
        user.getAddresses().add(address);
        Address savedAddress = addressRepository.save(address);
        AddressDTO savedAddressDTO = modelMapper.map(savedAddress, AddressDTO.class);
        return savedAddressDTO;
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        List<AddressDTO> addressDTOS = addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
        return addressDTOS;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressesByUser() {
        User user = authUtil.loggedInUser();
        List<Address> addresses = addressRepository.findByUserId(user.getUserId());
        List<AddressDTO> addressDTOS = addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
        return addressDTOS;
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, @Valid AddressDTO addressDTO) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        address.setCity(addressDTO.getCity());
        address.setStreet(addressDTO.getStreet());
        address.setBuildingName(addressDTO.getBuildingName());
        address.setPincode(addressDTO.getPincode());
        Address updatedAddress = addressRepository.save(address);
        AddressDTO savedAddressDTO = modelMapper.map(updatedAddress, AddressDTO.class);
        return savedAddressDTO;
    }

    @Override
    public void deleteAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        address.getUser().getAddresses().remove(address);
        address.setUser(null);
        addressRepository.delete(address);
    }
}
