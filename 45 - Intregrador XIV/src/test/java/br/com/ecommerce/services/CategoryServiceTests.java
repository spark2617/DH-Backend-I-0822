package br.com.ecommerce.services;

import br.com.ecommerce.dtos.CategoryDto;
import br.com.ecommerce.entities.Category;
import br.com.ecommerce.repositories.CategoryRepository;
import br.com.ecommerce.services.exceptions.DatabaseWineException;
import br.com.ecommerce.services.exceptions.EntityWineNotFoundException;
import br.com.ecommerce.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    private Integer existingId;
    private Integer nonExistingId;
    private Integer dependentId;
    private Category category;
    private CategoryDto categoryDto;
    private List<Category> list;

    @BeforeEach
    void Setup() throws Exception {
        existingId = 1;
        nonExistingId = 999;
        dependentId = 2;
        category = Factory.criarCategoria();
        categoryDto = Factory.criarCategoriaDto();
        list = new ArrayList<>();

        // Ainda no BeforeEach precisamos criar os mocks do repository

        // Mock do findAll
        Mockito.when(repository.findAll((Sort) ArgumentMatchers.any())).thenReturn(list);

        // Mock do findById
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Mock do save
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(category);

        // Mock do getReferenceById
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(repository.getReferenceById(nonExistingId))
                .thenThrow(EntityWineNotFoundException.class);

        // Mock do delete
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class)
                .when(repository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class)
                .when(repository).deleteById(dependentId);
    }

    // Teste do m??todo findAll
    @Test
    public void findAllDeveriaRetornarUmaLista() {
        List<CategoryDto> result = service.searchAll();
        Assertions.assertNotNull(result);
    }

    // Teste do m??todo findById
    @Test
    public void findByIdDeveriaRetornarUmRegistroDoBD() {
        CategoryDto result = service.searchById(existingId);
        Assertions.assertNotNull(result);
    }

    // Teste do m??todo findById retornando uma Exce????o
    @Test
    public void findByIdDeveriaRetornarUmaExcecao() {
        Assertions.assertThrows(EntityWineNotFoundException.class, () -> {
            service.searchById(nonExistingId);
        });
    }

    // Teste do m??todo save
    @Test
    public void saveDeveriaPersistirNoBD() {
        CategoryDto result = service.insert(categoryDto);
        Assertions.assertNotNull(result);
    }

    // Teste do m??todo update
    @Test
    public void updateDeveriaAtualizarUmRegistro() {
        CategoryDto result = service.update(existingId, categoryDto);
        Assertions.assertNotNull(result);
    }

    // Teste do m??todo update com exce????o
    @Test
    public void updateDeveriaLancarUmaExcecao() {
        Assertions.assertThrows(EntityWineNotFoundException.class, () -> {
            service.update(nonExistingId, categoryDto);
        });
    }

    // Teste do m??todo delete
    @Test
    public void deleteDeveriaExcluirUmRegistro() {
        Assertions.assertDoesNotThrow(() -> service.delete(existingId));
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    // Teste do m??todo delete com exce????o de recurso n??o encontrado
    @Test
    public void deleteDeveriaLancarExcecao() {
        Assertions.assertThrows(EntityWineNotFoundException.class, () ->
                service.delete(nonExistingId));
        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
    }

    // Teste do m??todo delete com exce????o de integridade do BD
    @Test
    public void deleteDeveriaLancarExcecaoDeIntegridadeDeBD() {
        Assertions.assertThrows(DatabaseWineException.class, () ->
                service.delete(dependentId));
        Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
    }

}
