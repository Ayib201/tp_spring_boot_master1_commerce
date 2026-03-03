package com.groupeisi.commerceenligne.service;

import com.groupeisi.commerceenligne.dao.IProduitRepository;
import com.groupeisi.commerceenligne.dto.ProduitDto;
import com.groupeisi.commerceenligne.exception.RequestException;
import com.groupeisi.commerceenligne.mapper.ProduitMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@CacheConfig(cacheNames = "produits")
public class ProduitService {
    private final ProduitMapper produitMapper;
    private final IProduitRepository iProduitRepository;
    MessageSource messageSource;
    public ProduitService(ProduitMapper produitMapper, IProduitRepository iProduitRepository, MessageSource messageSource) {
        this.produitMapper = produitMapper;
        this.iProduitRepository = iProduitRepository;
        this.messageSource = messageSource;
    }
    @Transactional(readOnly = true)
    public List<ProduitDto> getProduits() {
        return iProduitRepository.findAll().stream()
                .map(produitMapper::toDto)
                .toList();
    }
    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public ProduitDto getProduit(String id) {
        return produitMapper.toDto(iProduitRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(messageSource.getMessage("produit.notfound", new Object[]{id},
                                Locale.getDefault()))));
    }
    @Transactional
    public ProduitDto createProduit(ProduitDto produitDto) {
        return produitMapper.toDto(iProduitRepository.save(produitMapper.toEntity(produitDto)));
    }
    @CachePut(key = "#id")
    @Transactional
    public ProduitDto updateProduit(String id, ProduitDto produitDto) {
        return iProduitRepository.findById(id)
                .map(entity -> {
                    produitDto.setRef(id);
                    return produitMapper.toDto(
                            iProduitRepository.save(produitMapper.toEntity(produitDto)));
                }).orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("produit.notfound", new Object[]{id},
                        Locale.getDefault())));
    }
    @CacheEvict(key = "#id")
    @Transactional
    public void deleteProduit(String id) {
        try {
            iProduitRepository.deleteById(id);
        } catch (Exception e) {
            throw new RequestException(messageSource.getMessage("produit.errordeletion", new Object[]{id},
                    Locale.getDefault()),
                    HttpStatus.CONFLICT);
        }
    }
}
