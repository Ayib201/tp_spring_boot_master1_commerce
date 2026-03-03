package com.groupeisi.commerceenligne.mapper;

import com.groupeisi.commerceenligne.dto.AchatDto;
import com.groupeisi.commerceenligne.dto.VenteDto;
import com.groupeisi.commerceenligne.entities.Achats;
import com.groupeisi.commerceenligne.entities.Ventes;
import org.mapstruct.Mapper;

@Mapper
public interface VenteMapper {
    Ventes toEntity(VenteDto venteDto);
    VenteDto toDto(Ventes ventes);
}
