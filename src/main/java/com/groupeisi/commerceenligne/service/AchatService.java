package com.groupeisi.commerceenligne.service;

import com.groupeisi.commerceenligne.dao.IAchatRepository;
import com.groupeisi.commerceenligne.dto.AchatDto;
import com.groupeisi.commerceenligne.exception.RequestException;
import com.groupeisi.commerceenligne.mapper.AchatMapper;
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
@CacheConfig(cacheNames = "achats")
public class AchatService {
    private final AchatMapper achatMapper;
    private final IAchatRepository iAchatRepository;
    MessageSource messageSource;
    public AchatService(AchatMapper achatMapper, IAchatRepository iAchatRepository, MessageSource messageSource) {
        this.achatMapper = achatMapper;
        this.iAchatRepository = iAchatRepository;
        this.messageSource = messageSource;
    }
    @Transactional(readOnly = true)
    public List<AchatDto> getAchats() {
        return iAchatRepository.findAll().stream()
                .map(achatMapper::toDto)
                .toList();
    }
    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public AchatDto getAchat(Long id) {
        return achatMapper.toDto(iAchatRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(messageSource.getMessage("Achat.notfound", new Object[]{id},
                                Locale.getDefault()))));
    }
    @Transactional
    public AchatDto createAchat(AchatDto achatDto) {
        return achatMapper.toDto(iAchatRepository.save(achatMapper.toEntity(achatDto)));
    }
    @CachePut(key = "#id")
    @Transactional
    public AchatDto updateAchat(Long id, AchatDto achatDto) {
        return iAchatRepository.findById(id)
                .map(entity -> {
                    achatDto.setId(id);
                    return achatMapper.toDto(
                            iAchatRepository.save(achatMapper.toEntity(achatDto)));
                }).orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("Achat.notfound", new Object[]{id},
                        Locale.getDefault())));
    }
    @CacheEvict(key = "#id")
    @Transactional
    public void deleteAchat(Long id) {
        try {
            iAchatRepository.deleteById(id);
        } catch (Exception e) {
            throw new RequestException(messageSource.getMessage("Achat.errordeletion", new Object[]{id},
                    Locale.getDefault()),
                    HttpStatus.CONFLICT);
        }
    }
}
