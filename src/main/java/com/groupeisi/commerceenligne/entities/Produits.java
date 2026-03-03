package com.groupeisi.commerceenligne.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Setter
@Getter

public class Produits implements Serializable {
    @Id
    private String ref;
    @Column(nullable = false,unique = true)
    private String name;
    private double stock;
}