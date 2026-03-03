package com.groupeisi.commerceenligne.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenteDto {
    private Long id;
    private LocalDate dateP;
    private double quantity;
    private String produitId;
}
