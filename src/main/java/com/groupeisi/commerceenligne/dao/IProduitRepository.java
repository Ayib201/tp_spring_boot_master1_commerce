package com.groupeisi.commerceenligne.dao;

import com.groupeisi.commerceenligne.entities.Produits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProduitRepository extends JpaRepository<Produits,String> {
}
