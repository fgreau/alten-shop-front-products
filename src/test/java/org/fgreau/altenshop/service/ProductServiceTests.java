package org.fgreau.altenshop.service;

import org.fgreau.altenshop.dto.ProductDTO;
import org.fgreau.altenshop.exception.NotFoundException;
import org.fgreau.altenshop.mapper.ProductMapper;
import org.fgreau.altenshop.model.Product;
import org.fgreau.altenshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    private final Long ID = 1L;

    private final String CODE = "PRODUCT_CODE";

    private final String NAME = "PRODUCT_NAME";

    private final List<String> ALLOWED_SORT_PROPERTIES = List.of("property_1", "property_2");

    private final int CUSTOM_PAGE_NUMBER = 1;

    private final int CUSTOM_PAGE_SIZE = 15;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler;

    @InjectMocks
    private ProductService productService;

    // *** getAllProductsPageable() ***

    @Test
    public void testGetAllProductsPageable_codeAndNameFiltersNotNull() {
        when(productRepository.findByCodeContainsIgnoreCaseAndNameContainsIgnoreCaseAndDeletedFalse(anyString(), anyString(), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(new Product())));

        when(productMapper.map(any(Product.class))).thenReturn(new ProductDTO());

        productService.getAllProductsPageable(CODE, NAME, PageRequest.of(0, 10));

        verify(productRepository).findByCodeContainsIgnoreCaseAndNameContainsIgnoreCaseAndDeletedFalse(eq(CODE), eq(NAME), any(org.springframework.data.domain.Pageable.class));
        verify(productMapper).map(any(Product.class));
    }

    @Test
    public void testGetAllProductsPageable_codeFilterNotNull() {
        when(productRepository.findByCodeContainsIgnoreCaseAndDeletedFalse(anyString(), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(new Product())));

        when(productMapper.map(any(Product.class))).thenReturn(new ProductDTO());

        productService.getAllProductsPageable(CODE, null, PageRequest.of(0, 10));

        verify(productRepository).findByCodeContainsIgnoreCaseAndDeletedFalse(eq(CODE), any(org.springframework.data.domain.Pageable.class));
        verify(productMapper).map(any(Product.class));
    }

    @Test
    public void testGetAllProductsPageable_nameFilterNotNull() {
        when(productRepository.findByNameContainsIgnoreCaseAndDeletedFalse(anyString(), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(new Product())));

        when(productMapper.map(any(Product.class))).thenReturn(new ProductDTO());

        productService.getAllProductsPageable(null, NAME, PageRequest.of(0, 10));

        verify(productRepository).findByNameContainsIgnoreCaseAndDeletedFalse(eq(NAME), any(org.springframework.data.domain.Pageable.class));
        verify(productMapper).map(any(Product.class));
    }

    @Test
    public void testGetAllProductsPageable_nameAndCodeFiltersNull() {
        when(productRepository.findByDeletedFalse(any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(new Product())));

        when(productMapper.map(any(Product.class))).thenReturn(new ProductDTO());

        productService.getAllProductsPageable(null, null, PageRequest.of(0, 10));

        verify(productRepository).findByDeletedFalse(any(org.springframework.data.domain.Pageable.class));
        verify(productMapper).map(any(Product.class));
    }

    // *** validatePageable ***

    @Test
    public void allowedProperties_pageableNull() {
        final Pageable pageable = productService.validatePageable(null, ALLOWED_SORT_PROPERTIES);
        assertNotNull(pageable, "Pageable should not be null");
        assertEquals(0, pageable.getSort().get().count(), "Pageable sort count should be zero");
    }

    @Test
    public void allowedProperties_noSort() {
        final Pageable pageable = productService.validatePageable(PageRequest.of(0, 10), ALLOWED_SORT_PROPERTIES);
        assertNotNull(pageable, "Pageable should not be null");
        assertEquals(0, pageable.getSort().get().count(), "Pageable sort count should be zero");
    }


    private static List<Pageable> allowedSortPageableParameters() {
        return List.of(
            PageRequest.of(0, 10, Sort.by("property_1")),
            PageRequest.of(0, 10, Sort.by("property_2")),
            PageRequest.of(0, 10, Sort.by("property_1", "property_2"))
        );
    }

    @ParameterizedTest
    @MethodSource("allowedSortPageableParameters")
    public void allowedProperties_onlyAllowedSort(final Pageable inputPageable) {
        final Pageable pageable = productService.validatePageable(inputPageable, ALLOWED_SORT_PROPERTIES);
        assertNotNull(pageable, "Pageable should not be null");
        assertEquals(inputPageable.getSort().get().count(), pageable.getSort().get().count(), "Pageable sort count should be the same as input");
    }


    private static List<Pageable> forbiddenSortPageableParameters() {
        return List.of(
            PageRequest.of(0, 10, Sort.by("forbidden_1")),
            PageRequest.of(0, 10, Sort.by("forbidden_1", "property_2")),
            PageRequest.of(0, 10, Sort.by("forbidden_1", "forbidden_2"))
        );
    }

    @ParameterizedTest
    @MethodSource("forbiddenSortPageableParameters")
    public void allowedProperties_forbiddenParameters(final Pageable inputPageable) {
        final Pageable pageable = productService.validatePageable(inputPageable, ALLOWED_SORT_PROPERTIES);
        assertNotNull(pageable, "Pageable should not be null");
        assertNotEquals(inputPageable.getSort().get().count(), pageable.getSort().get().count(), "Pageable sort count should not be the same as input");
        assertTrue(pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_SORT_PROPERTIES::contains), "Only allowed sort properties should remain");
    }

    @Test
    public void allowedProperties_nullAllowedProperties() {
        final Pageable inputPageable = PageRequest.of(CUSTOM_PAGE_NUMBER, CUSTOM_PAGE_SIZE, Sort.by(ALLOWED_SORT_PROPERTIES.toArray(new String[0])));

        final Pageable pageable = productService.validatePageable(inputPageable, null);
        assertNotNull(pageable, "Pageable should not be null");
        assertEquals(CUSTOM_PAGE_NUMBER, pageable.getPageNumber(), "Invalid page number");
        assertEquals(CUSTOM_PAGE_SIZE, pageable.getPageSize(), "Invalid page size");
        assertTrue(pageable.getSort().isEmpty(), "Pageable sort list should be empty");
    }

    @Test
    public void allowedProperties_emptyAllowedProperties() {
        final Pageable inputPageable = PageRequest.of(CUSTOM_PAGE_NUMBER, CUSTOM_PAGE_SIZE, Sort.by(ALLOWED_SORT_PROPERTIES.toArray(new String[0])));

        final Pageable pageable = productService.validatePageable(inputPageable, List.of());
        assertNotNull(pageable, "Pageable should not be null");
        assertEquals(CUSTOM_PAGE_NUMBER, pageable.getPageNumber(), "Invalid page number");
        assertEquals(CUSTOM_PAGE_SIZE, pageable.getPageSize(), "Invalid page size");
        assertTrue(pageable.getSort().isEmpty(), "Pageable sort list should be empty");
    }

    // *** getProductDetails() ***

    @Test
    public void testGetProductDetails_productFound() {
        when(productRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.of(new Product()));

        when(productMapper.map(any(Product.class))).thenReturn(new ProductDTO());

        productService.getProductDetails(ID);

        verify(productRepository).findByIdAndDeletedFalse(ID);
        verify(productMapper).map(any(Product.class));
    }

    @Test
    public void testGetProductDetails_productNotFound() {
        when(productRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.getProductDetails(ID));

        verify(productRepository).findByIdAndDeletedFalse(ID);
        assertEquals("Product " + ID + " not found", exception.getMessage());
    }

}
