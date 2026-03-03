package com.groupeisi.commerceenligne.service;

import com.groupeisi.commerceenligne.dao.IVenteRepository;
import com.groupeisi.commerceenligne.dto.VenteDto;
import com.groupeisi.commerceenligne.entities.Ventes;
import com.groupeisi.commerceenligne.exception.RequestException;
import com.groupeisi.commerceenligne.mapper.VenteMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class VenteServiceTest {

    @Mock
    private IVenteRepository iVenteRepository;

    @Mock
    private VenteMapper venteMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private VenteService venteService;

    private Ventes vente;
    private VenteDto venteDto;

    @BeforeEach
    void setUp() {
        vente = new Ventes();
        vente.setId(1L);
        vente.setDateP(LocalDate.now());
        vente.setQuantity(3.0);

        venteDto = new VenteDto(1L, LocalDate.now(), 3.0, "REF001");
    }

    @Test
    @DisplayName("getVentes - retourne la liste de toutes les ventes")
    void getVentes_shouldReturnListOfVenteDto() {
        when(iVenteRepository.findAll()).thenReturn(List.of(vente));
        when(venteMapper.toDto(vente)).thenReturn(venteDto);

        List<VenteDto> result = venteService.getVentes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getProduitId()).isEqualTo("REF001");
        verify(iVenteRepository).findAll();
    }

    @Test
    @DisplayName("getVentes - retourne une liste vide si aucune vente")
    void getVentes_shouldReturnEmptyList() {
        when(iVenteRepository.findAll()).thenReturn(List.of());

        List<VenteDto> result = venteService.getVentes();

        assertThat(result).isEmpty();
        verify(iVenteRepository).findAll();
    }

    @Test
    @DisplayName("getVente - retourne la vente correspondante à l'id")
    void getVente_shouldReturnVenteDto_whenFound() {
        when(iVenteRepository.findById(1L)).thenReturn(Optional.of(vente));
        when(venteMapper.toDto(vente)).thenReturn(venteDto);

        VenteDto result = venteService.getVente(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getQuantity()).isEqualTo(3.0);
    }

    @Test
    @DisplayName("getVente - lève EntityNotFoundException si vente non trouvée")
    void getVente_shouldThrowEntityNotFoundException_whenNotFound() {
        when(iVenteRepository.findById(99L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("vente.notfound"), any(), any()))
                .thenReturn("Vente 99 non trouvée");

        assertThatThrownBy(() -> venteService.getVente(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Vente 99 non trouvée");
    }

    @Test
    @DisplayName("createVente - crée et retourne la vente")
    void createVente_shouldCreateAndReturnVenteDto() {
        when(venteMapper.toEntity(venteDto)).thenReturn(vente);
        when(iVenteRepository.save(vente)).thenReturn(vente);
        when(venteMapper.toDto(vente)).thenReturn(venteDto);

        VenteDto result = venteService.createVente(venteDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getQuantity()).isEqualTo(3.0);
        verify(iVenteRepository).save(vente);
    }

    @Test
    @DisplayName("updateVente - met à jour et retourne la vente modifiée")
    void updateVente_shouldUpdateAndReturnVenteDto() {
        VenteDto updatedDto = new VenteDto(1L, LocalDate.now(), 12.0, "REF003");
        Ventes updatedEntity = new Ventes();
        updatedEntity.setId(1L);
        updatedEntity.setQuantity(12.0);

        when(iVenteRepository.findById(1L)).thenReturn(Optional.of(vente));
        when(venteMapper.toEntity(any(VenteDto.class))).thenReturn(updatedEntity);
        when(iVenteRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(venteMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        VenteDto result = venteService.updateVente(1L, updatedDto);

        assertThat(result.getQuantity()).isEqualTo(12.0);
        assertThat(result.getProduitId()).isEqualTo("REF003");
        verify(iVenteRepository).save(updatedEntity);
    }

    @Test
    @DisplayName("updateVente - lève EntityNotFoundException si vente non trouvée")
    void updateVente_shouldThrowEntityNotFoundException_whenNotFound() {
        when(iVenteRepository.findById(99L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("vente.notfound"), any(), any()))
                .thenReturn("Vente 99 non trouvée");

        assertThatThrownBy(() -> venteService.updateVente(99L, venteDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Vente 99 non trouvée");

        verify(iVenteRepository, never()).save(any());
    }


    @Test
    @DisplayName("deleteVente - supprime la vente sans exception")
    void deleteVente_shouldDeleteSuccessfully() {
        doNothing().when(iVenteRepository).deleteById(1L);

        assertThatCode(() -> venteService.deleteVente(1L))
                .doesNotThrowAnyException();

        verify(iVenteRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteVente - lève RequestException en cas d'erreur de suppression")
    void deleteVente_shouldThrowRequestException_whenDeletionFails() {
        doThrow(new RuntimeException("Contrainte FK")).when(iVenteRepository).deleteById(1L);
        when(messageSource.getMessage(eq("vente.errordeletion"), any(), any()))
                .thenReturn("Erreur suppression vente 1");

        assertThatThrownBy(() -> venteService.deleteVente(1L))
                .isInstanceOf(RequestException.class)
                .hasMessageContaining("Erreur suppression vente 1");
    }
}