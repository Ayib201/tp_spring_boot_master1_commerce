package com.groupeisi.commerceenligne.service;

import com.groupeisi.commerceenligne.dao.IAchatRepository;
import com.groupeisi.commerceenligne.dto.AchatDto;
import com.groupeisi.commerceenligne.entities.Achats;
import com.groupeisi.commerceenligne.exception.RequestException;
import com.groupeisi.commerceenligne.mapper.AchatMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchatServiceTest {

    @Mock
    private IAchatRepository iAchatRepository;

    @Mock
    private AchatMapper achatMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private AchatService achatService;

    private Achats achat;
    private AchatDto achatDto;

    @BeforeEach
    void setUp() {
        achat = new Achats();
        achat.setId(1L);
        achat.setDateP(new Date());
        achat.setQuantity(5.0);

        achatDto = new AchatDto(1L, new Date(), 5.0, "REF001");
    }

    @Test
    @DisplayName("getAchats - retourne la liste de tous les achats")
    void getAchats_shouldReturnListOfAchatDto() {
        when(iAchatRepository.findAll()).thenReturn(List.of(achat));
        when(achatMapper.toDto(achat)).thenReturn(achatDto);

        List<AchatDto> result = achatService.getAchats();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getProduitId()).isEqualTo("REF001");
        verify(iAchatRepository).findAll();
    }

    @Test
    @DisplayName("getAchats - retourne une liste vide si aucun achat")
    void getAchats_shouldReturnEmptyList() {
        when(iAchatRepository.findAll()).thenReturn(List.of());

        List<AchatDto> result = achatService.getAchats();

        assertThat(result).isEmpty();
        verify(iAchatRepository).findAll();
    }

    @Test
    @DisplayName("getAchat - retourne l'achat correspondant à l'id")
    void getAchat_shouldReturnAchatDto_whenFound() {
        when(iAchatRepository.findById(1L)).thenReturn(Optional.of(achat));
        when(achatMapper.toDto(achat)).thenReturn(achatDto);

        AchatDto result = achatService.getAchat(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getQuantity()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("getAchat - lève EntityNotFoundException si achat non trouvé")
    void getAchat_shouldThrowEntityNotFoundException_whenNotFound() {
        when(iAchatRepository.findById(99L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("Achat.notfound"), any(), any()))
                .thenReturn("Achat 99 non trouvé");

        assertThatThrownBy(() -> achatService.getAchat(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Achat 99 non trouvé");
    }

    @Test
    @DisplayName("createAchat - crée et retourne l'achat")
    void createAchat_shouldCreateAndReturnAchatDto() {
        when(achatMapper.toEntity(achatDto)).thenReturn(achat);
        when(iAchatRepository.save(achat)).thenReturn(achat);
        when(achatMapper.toDto(achat)).thenReturn(achatDto);

        AchatDto result = achatService.createAchat(achatDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getQuantity()).isEqualTo(5.0);
        verify(iAchatRepository).save(achat);
    }

    @Test
    @DisplayName("updateAchat - met à jour et retourne l'achat modifié")
    void updateAchat_shouldUpdateAndReturnAchatDto() {
        AchatDto updatedDto = new AchatDto(1L, new Date(), 15.0, "REF002");
        Achats updatedEntity = new Achats();
        updatedEntity.setId(1L);
        updatedEntity.setQuantity(15.0);

        when(iAchatRepository.findById(1L)).thenReturn(Optional.of(achat));
        when(achatMapper.toEntity(any(AchatDto.class))).thenReturn(updatedEntity);
        when(iAchatRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(achatMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        AchatDto result = achatService.updateAchat(1L, updatedDto);

        assertThat(result.getQuantity()).isEqualTo(15.0);
        assertThat(result.getProduitId()).isEqualTo("REF002");
        verify(iAchatRepository).save(updatedEntity);
    }

    @Test
    @DisplayName("updateAchat - lève EntityNotFoundException si achat non trouvé")
    void updateAchat_shouldThrowEntityNotFoundException_whenNotFound() {
        when(iAchatRepository.findById(99L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("Achat.notfound"), any(), any()))
                .thenReturn("Achat 99 non trouvé");

        assertThatThrownBy(() -> achatService.updateAchat(99L, achatDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Achat 99 non trouvé");

        verify(iAchatRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteAchat - supprime l'achat sans exception")
    void deleteAchat_shouldDeleteSuccessfully() {
        doNothing().when(iAchatRepository).deleteById(1L);

        assertThatCode(() -> achatService.deleteAchat(1L))
                .doesNotThrowAnyException();

        verify(iAchatRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteAchat - lève RequestException en cas d'erreur de suppression")
    void deleteAchat_shouldThrowRequestException_whenDeletionFails() {
        doThrow(new RuntimeException("Contrainte FK")).when(iAchatRepository).deleteById(1L);
        when(messageSource.getMessage(eq("Achat.errordeletion"), any(), any()))
                .thenReturn("Erreur suppression achat 1");

        assertThatThrownBy(() -> achatService.deleteAchat(1L))
                .isInstanceOf(RequestException.class)
                .hasMessageContaining("Erreur suppression achat 1");
    }
}