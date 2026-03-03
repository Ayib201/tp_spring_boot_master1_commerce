package com.groupeisi.commerceenligne.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProduitDto {
    private String ref;
    private String name;
    private double stock;
}
