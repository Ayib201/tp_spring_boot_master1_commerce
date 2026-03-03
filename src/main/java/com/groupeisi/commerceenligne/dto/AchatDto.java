package com.groupeisi.commerceenligne.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AchatDto {
    private Long id;
    private Date dateP;
    private double quantity;
    private String produitId;
}
