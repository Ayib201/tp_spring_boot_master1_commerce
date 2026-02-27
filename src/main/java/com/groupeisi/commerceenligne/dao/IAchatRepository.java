package com.groupeisi.commerceenligne.dao;

import com.groupeisi.commerceenligne.entities.Achats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAchatRepository extends JpaRepository<Achats,Integer> {
}
