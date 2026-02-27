package com.groupeisi.commerceenligne.dao;

import com.groupeisi.commerceenligne.entities.Ventes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVenteRepository extends JpaRepository<Ventes,Integer> {
}
