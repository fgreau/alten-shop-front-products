package org.fgreau.altenshop.repository;

import org.fgreau.altenshop.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/test-scripts/insert-test-products.sql")
@SuppressWarnings("FieldCanBeLocal")
public class ProductRepositoryTests {

    private final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10);

    private final long ID_EXISTENT = 1L;

    private final long ID_NON_EXISTENT = -1L;

    private final List<Long> IDS_SPEC = List.of(3L, 4L);

    private final String PARTIAL_CODE = "ct1";

    private final String CODE_EXISTENT = "product1";

    private final String CODE_NON_EXISTENT = "product-1";

    private final String PARTIAL_NAME = "ct 1";

    private final String PARTIAL_VALUE_MULTIPLE = "spec";

    private final String NAME_EXISTENT = "Product 1";

    private final String NAME_EXISTENT_LOWERCASE = "product 1";

    private final String NAME_NON_EXISTENT = "Product -1";


    @Autowired
    private ProductRepository productRepository;

    @Test
    public void findByDeletedFalse() {
        final List<Product> allProducts = productRepository.findAll();
        assertTrue(allProducts.stream().anyMatch(Product::isDeleted), "Missing deleted product");

        final List<Product> notDeletedProducts = productRepository.findByDeletedFalse(DEFAULT_PAGEABLE).getContent();
        assertTrue(notDeletedProducts.stream().noneMatch(Product::isDeleted), "Unexpected deleted product in filtered products");
    }

    @Test
    public void findByIdAndDeletedFalse() {
        final Optional<Product> product = productRepository.findByIdAndDeletedFalse(ID_EXISTENT);
        assertTrue(product.isPresent(), "Product not found");
        assertEquals(ID_EXISTENT, product.get().getId(), "Unexpected product id");
    }

    @Test
    public void findByIdAndDeletedFalse_noneMatch() {
        final Optional<Product> product = productRepository.findByIdAndDeletedFalse(ID_NON_EXISTENT);
        assertTrue(product.isEmpty(), "Unexpected product found");
    }

    @Test
    public void findByCodeContainsIgnoreCaseAndNameContainsIgnoreCaseAndDeletedFalse() {
        final Page<Product> products = productRepository.findByCodeContainsIgnoreCaseAndNameContainsIgnoreCaseAndDeletedFalse(PARTIAL_CODE, PARTIAL_NAME, DEFAULT_PAGEABLE);
        assertEquals(1, products.getContent().size(), "Unexpected amount of products found");
        assertEquals(CODE_EXISTENT, products.getContent().getFirst().getCode(), "Unexpected product code");
        assertEquals(NAME_EXISTENT, products.getContent().getFirst().getName(), "Unexpected product name");
    }

    @Test
    public void findByCodeContainsIgnoreCaseAndNameContainsIgnoreCaseAndDeletedFalse_noneMatch() {
        Page<Product> products = productRepository.findByCodeContainsIgnoreCaseAndNameContainsIgnoreCaseAndDeletedFalse(CODE_NON_EXISTENT, PARTIAL_NAME, DEFAULT_PAGEABLE);
        assertTrue(products.isEmpty(), "Unexpected product found");

        products = productRepository.findByCodeContainsIgnoreCaseAndNameContainsIgnoreCaseAndDeletedFalse(PARTIAL_CODE, NAME_NON_EXISTENT, DEFAULT_PAGEABLE);
        assertTrue(products.isEmpty(), "Unexpected product found");
    }

    @Test
    public void findByCodeContainsIgnoreCaseAndDeletedFalse() {
        final Page<Product> products = productRepository.findByCodeContainsIgnoreCaseAndDeletedFalse(PARTIAL_CODE, DEFAULT_PAGEABLE);
        assertEquals(1, products.getContent().size(), "Unexpected amount of products found");
        assertEquals(CODE_EXISTENT, products.getContent().getFirst().getCode(), "Unexpected product code");
    }

    @Test
    public void findByCodeContainsIgnoreCaseAndDeletedFalse_multipleMatch() {
        final Page<Product> products = productRepository.findByCodeContainsIgnoreCaseAndDeletedFalse(PARTIAL_VALUE_MULTIPLE, DEFAULT_PAGEABLE);
        final List<Long> productIds = products.get().map(Product::getId).toList();
        assertEquals(IDS_SPEC.size(), productIds.size(), "Unexpected amount of products found");
        assertTrue(productIds.containsAll(IDS_SPEC), "Unexpected IDs found");
    }

    @Test
    public void findByCodeContainsIgnoreCaseAndDeletedFalse_noneMatch() {
        Page<Product> products = productRepository.findByCodeContainsIgnoreCaseAndDeletedFalse(CODE_NON_EXISTENT, DEFAULT_PAGEABLE);
        assertTrue(products.isEmpty(), "Unexpected product found");
    }

    @Test
    public void findByNameContainsIgnoreCaseAndDeletedFalse() {
        final Page<Product> products = productRepository.findByNameContainsIgnoreCaseAndDeletedFalse(NAME_EXISTENT_LOWERCASE, DEFAULT_PAGEABLE);
        assertEquals(1, products.getContent().size(), "Unexpected amount of products found");
        assertEquals(NAME_EXISTENT, products.getContent().getFirst().getName(), "Unexpected product name");
    }

    @Test
    public void findByNameContainsIgnoreCaseAndDeletedFalse_multipleMatch() {
        final Page<Product> products = productRepository.findByNameContainsIgnoreCaseAndDeletedFalse(PARTIAL_VALUE_MULTIPLE, DEFAULT_PAGEABLE);
        final List<Long> productIds = products.get().map(Product::getId).toList();
        assertEquals(IDS_SPEC.size(), productIds.size(), "Unexpected amount of products found");
        assertTrue(productIds.containsAll(IDS_SPEC), "Unexpected IDs found");
    }

    @Test
    public void findByNameContainsIgnoreCaseAndDeletedFalse_noneMatch() {
        Page<Product> products = productRepository.findByNameContainsIgnoreCaseAndDeletedFalse(NAME_NON_EXISTENT, DEFAULT_PAGEABLE);
        assertTrue(products.isEmpty(), "Unexpected product found");
    }

    @Test
    public void existsByCode_true() {
        assertTrue(productRepository.existsByCode(CODE_EXISTENT), "Product not found");
    }

    @Test
    public void existsByCode_false() {
        assertFalse(productRepository.existsByCode(CODE_NON_EXISTENT), "Unexpected product found");
    }

}
