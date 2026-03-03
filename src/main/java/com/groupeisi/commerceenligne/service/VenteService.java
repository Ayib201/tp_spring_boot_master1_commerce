package com.groupeisi.commerceenligne.service;

import com.groupeisi.commerceenligne.dao.IVenteRepository;
import com.groupeisi.commerceenligne.dto.VenteDto;
import com.groupeisi.commerceenligne.exception.RequestException;
import com.groupeisi.commerceenligne.mapper.VenteMapper;
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
@CacheConfig(cacheNames = "ventes")
public class VenteService {
    private final VenteMapper venteMapper;
    private final IVenteRepository iVenteRepository;
    MessageSource messageSource;
    public VenteService(VenteMapper venteMapper, IVenteRepository iventeRepository, MessageSource messageSource) {
        this.venteMapper = venteMapper;
        this.iVenteRepository = iventeRepository;
        this.messageSource = messageSource;
    }
    @Transactional(readOnly = true)
    public List<VenteDto> getVentes() {
        return iVenteRepository.findAll().stream()
                .map(venteMapper::toDto)
                .toList();
    }
    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public VenteDto getVente(Long id) {
        return venteMapper.toDto(iVenteRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(messageSource.getMessage("vente.notfound", new Object[]{id},
                                Locale.getDefault()))));
    }
    @Transactional
    public VenteDto createVente(VenteDto venteDto) {
        return venteMapper.toDto(iVenteRepository.save(venteMapper.toEntity(venteDto)));
    }
    @CachePut(key = "#id")
    @Transactional
    public VenteDto updateVente(Long id, VenteDto venteDto) {
        return iVenteRepository.findById(id)
                .map(entity -> {
                    venteDto.setId(id);
                    return venteMapper.toDto(
                            iVenteRepository.save(venteMapper.toEntity(venteDto)));
                }).orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("vente.notfound", new Object[]{id},
                        Locale.getDefault())));
    }
    @CacheEvict(key = "#id")
    @Transactional
    public void deleteVente(Long id) {
        try {
            iVenteRepository.deleteById(id);
        } catch (Exception e) {
            throw new RequestException(messageSource.getMessage("vente.errordeletion", new Object[]{id},
                    Locale.getDefault()),
                    HttpStatus.CONFLICT);
        }
    }

}
