package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "街道名稱至少為5個字以上")
    private String street;

    @NotBlank
    @Size(min = 5, message = "建築物名稱至少為5個字以上")
    private String buildingName;

    @NotBlank
    @Size(min = 5, message = "城市名稱名稱至少為2個字以上")
    private String city;

    @NotBlank
    @Size(min = 3, message = "郵遞區號至少為3個字以上")
    private String pincode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(String street, String buildingName, String city, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.pincode = pincode;
    }
}
