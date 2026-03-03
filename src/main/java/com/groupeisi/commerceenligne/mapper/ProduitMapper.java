package com.groupeisi.commerceenligne.mapper;

import com.groupeisi.commerceenligne.dto.ProduitDto;
import com.groupeisi.commerceenligne.entities.Produits;
import org.mapstruct.Mapper;

@Mapper
public interface ProduitMapper {
    Produits toEntity(ProduitDto produitDto);
    ProduitDto toDto(Produits produit);
}
