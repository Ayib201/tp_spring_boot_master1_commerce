package com.groupeisi.commerceenligne.service;

import com.groupeisi.commerceenligne.dao.IProduitRepository;
import com.groupeisi.commerceenligne.dto.ProduitDto;
import com.groupeisi.commerceenligne.entities.Produits;
import com.groupeisi.commerceenligne.exception.RequestException;
import com.groupeisi.commerceenligne.mapper.ProduitMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProduitServiceTest {

    @Mock
    private IProduitRepository iProduitRepository;

    @Mock
    private ProduitMapper produitMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ProduitService produitService;

    private Produits produit;
    private ProduitDto produitDto;

    @BeforeEach
    void setUp() {
        produit = Produits.builder()
                .ref("REF001")
                .name("Laptop")
                .stock(10.0)
                .build();

        produitDto = new ProduitDto("REF001", "Laptop", 10.0);
    }

    @Test
    void getProduits_shouldReturnListOfProduitDto() {
        when(iProduitRepository.findAll()).thenReturn(List.of(produit));
        when(produitMapper.toDto(produit)).thenReturn(produitDto);

        List<ProduitDto> result = produitService.getProduits();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRef()).isEqualTo("REF001");
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        verify(iProduitRepository).findAll();
    }

    @Test
    void getProduits_shouldReturnEmptyList() {
        when(iProduitRepository.findAll()).thenReturn(List.of());

        List<ProduitDto> result = produitService.getProduits();

        assertThat(result).isEmpty();
    }


    @Test
    void getProduit_shouldReturnProduitDto_whenFound() {
        when(iProduitRepository.findById("REF001")).thenReturn(Optional.of(produit));
        when(produitMapper.toDto(produit)).thenReturn(produitDto);

        ProduitDto result = produitService.getProduit("REF001");

        assertThat(result).isNotNull();
        assertThat(result.getRef()).isEqualTo("REF001");
        assertThat(result.getName()).isEqualTo("Laptop");
    }

    @Test
    void getProduit_shouldThrowEntityNotFoundException_whenNotFound() {
        when(iProduitRepository.findById("INVALID")).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("produit.notfound"), any(), any()))
                .thenReturn("Produit INVALID non trouvé");

        assertThatThrownBy(() -> produitService.getProduit("INVALID"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Produit INVALID non trouvé");
    }

    @Test
    void createProduit_shouldCreateAndReturnProduitDto() {
        when(produitMapper.toEntity(produitDto)).thenReturn(produit);
        when(iProduitRepository.save(produit)).thenReturn(produit);
        when(produitMapper.toDto(produit)).thenReturn(produitDto);

        ProduitDto result = produitService.createProduit(produitDto);

        assertThat(result).isNotNull();
        assertThat(result.getRef()).isEqualTo("REF001");
        assertThat(result.getName()).isEqualTo("Laptop");
        verify(iProduitRepository).save(produit);
    }

    @Test
    void updateProduit_shouldUpdateAndReturnProduitDto() {
        ProduitDto updatedDto = new ProduitDto("REF001", "Laptop Pro", 20.0);
        Produits updatedEntity = Produits.builder().ref("REF001").name("Laptop Pro").stock(20.0).build();

        when(iProduitRepository.findById("REF001")).thenReturn(Optional.of(produit));
        when(produitMapper.toEntity(any(ProduitDto.class))).thenReturn(updatedEntity);
        when(iProduitRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(produitMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        ProduitDto result = produitService.updateProduit("REF001", updatedDto);

        assertThat(result.getName()).isEqualTo("Laptop Pro");
        assertThat(result.getStock()).isEqualTo(20.0);
        verify(iProduitRepository).save(updatedEntity);
    }

    @Test
    void updateProduit_shouldThrowEntityNotFoundException_whenNotFound() {
        when(iProduitRepository.findById("INVALID")).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("produit.notfound"), any(), any()))
                .thenReturn("Produit INVALID non trouvé");

        assertThatThrownBy(() -> produitService.updateProduit("INVALID", produitDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Produit INVALID non trouvé");

        verify(iProduitRepository, never()).save(any());
    }


    @Test
    void deleteProduit_shouldDeleteProduit_withoutException() {
        doNothing().when(iProduitRepository).deleteById("REF001");

        assertThatCode(() -> produitService.deleteProduit("REF001"))
                .doesNotThrowAnyException();

        verify(iProduitRepository).deleteById("REF001");
    }

    @Test
    void deleteProduit_shouldThrowRequestException_whenDeletionFails() {
        doThrow(new RuntimeException("Contrainte FK")).when(iProduitRepository).deleteById("REF001");
        when(messageSource.getMessage(eq("produit.errordeletion"), any(), any()))
                .thenReturn("Erreur suppression produit REF001");

        assertThatThrownBy(() -> produitService.deleteProduit("REF001"))
                .isInstanceOf(RequestException.class)
                .hasMessageContaining("Erreur suppression produit REF001");
    }
}