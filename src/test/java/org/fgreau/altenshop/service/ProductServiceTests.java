package org.fgreau.altenshop.service;

import org.fgreau.altenshop.dto.ProductDTO;
import org.fgreau.altenshop.exception.NotFoundException;
import org.fgreau.altenshop.mapper.ProductMapper;
import org.fgreau.altenshop.model.Product;
import org.fgreau.altenshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    public void testGetAllProductsPageable_pageableNull() {
        when(productRepository.findByDeletedFalse(any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(new Product())));

        when(productMapper.map(any(Product.class))).thenReturn(new ProductDTO());

        productService.getAllProductsPageable(null, null, null);

        verify(productRepository).findByDeletedFalse(PageRequest.of(0, 10));
        verify(productMapper).map(any(Product.class));
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
