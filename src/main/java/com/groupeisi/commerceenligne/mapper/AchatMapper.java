package com.groupeisi.commerceenligne.mapper;

import com.groupeisi.commerceenligne.dto.AchatDto;
import com.groupeisi.commerceenligne.entities.Achats;
import org.mapstruct.Mapper;

@Mapper
public interface AchatMapper {
    Achats toEntity(AchatDto achatDto);
    AchatDto toDto(Achats achats);
}
