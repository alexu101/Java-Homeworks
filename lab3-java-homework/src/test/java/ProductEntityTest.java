import org.example.lab4.models.Product;
import org.example.lab4.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductEntityTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for Create (save) operation
    @Test
    void testSaveNewProduct() {
        Product newProduct = new Product();
        newProduct.setId(0); // Null ID to indicate new product

        productRepository.save(newProduct);

        // Verify persist is called for a new product
        verify(entityManager, times(1)).persist(newProduct);
        verify(entityManager, never()).merge(newProduct);
    }

    @Test
    void testUpdateExistingProduct() {
        Product existingProduct = new Product();
        existingProduct.setId(1); // Non-null ID for existing product

        productRepository.save(existingProduct);

        // Verify merge is called for an existing product
        verify(entityManager, times(1)).merge(existingProduct);
        verify(entityManager, never()).persist(existingProduct);
    }

    // Test for Read (findById) operation
    @Test
    void testFindById() {
        int productId = 1;
        Product mockProduct = new Product();
        mockProduct.setId(productId);

        when(entityManager.find(Product.class, productId)).thenReturn(mockProduct);

        Product result = productRepository.findById(productId);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(entityManager, times(1)).find(Product.class, productId);
    }

    // Test for Read (findAll) operation
    @Test
    void testFindAll() {
        TypedQuery<Product> query = mock(TypedQuery.class);
        when(entityManager.createNamedQuery("Product.findAll", Product.class)).thenReturn(query);

        productRepository.findAll();

        // Verify named query and results are retrieved
        verify(entityManager, times(1)).createNamedQuery("Product.findAll", Product.class);
        verify(query, times(1)).getResultList();
    }

    // Test for Delete operation
    @Test
    void testDeleteProduct() {
        Product productToDelete = new Product();
        productToDelete.setId(1);

        when(entityManager.contains(productToDelete)).thenReturn(false);
        when(entityManager.merge(productToDelete)).thenReturn(productToDelete);

        productRepository.delete(productToDelete);

        // Verify product is merged and removed
        verify(entityManager, times(1)).merge(productToDelete);
        verify(entityManager, times(1)).remove(productToDelete);
    }
}
