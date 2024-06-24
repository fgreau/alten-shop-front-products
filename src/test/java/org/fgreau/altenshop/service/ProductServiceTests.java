package org.fgreau.altenshop.service;

import org.fgreau.altenshop.dto.ProductDTO;
import org.fgreau.altenshop.dto.ProductPatchDTO;
import org.fgreau.altenshop.enums.ProductCategory;
import org.fgreau.altenshop.exception.BadRequestException;
import org.fgreau.altenshop.exception.NotFoundException;
import org.fgreau.altenshop.mapper.ProductMapper;
import org.fgreau.altenshop.model.Product;
import org.fgreau.altenshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.AdditionalAnswers;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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

    // *** createProduct ***

    @Test
    public void createProduct_fullDto() {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setName(NAME);
        dto.setDescription("description");
        dto.setPrice(25F);
        dto.setQuantity(20);
        dto.setCategory(ProductCategory.ELECTRONICS);
        dto.setImage("image");
        dto.setRating(2.5F);

        when(productRepository.existsByCode(anyString())).thenReturn(false);
        when(productMapper.map(any(ProductPatchDTO.class))).thenReturn(new Product());
        when(productRepository.save(any(Product.class))).thenReturn(new Product());
        when(productMapper.map(any(Product.class))).thenReturn(new ProductDTO());

        productService.createProduct(dto);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void createProduct_minimalDto() {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setName(NAME);
        dto.setPrice(25F);
        dto.setCategory(ProductCategory.ELECTRONICS);

        when(productRepository.existsByCode(anyString())).thenReturn(false);
        when(productMapper.map(any(ProductPatchDTO.class))).thenReturn(new Product());
        when(productRepository.save(any(Product.class))).thenReturn(new Product());
        when(productMapper.map(any(Product.class))).thenReturn(new ProductDTO());

        productService.createProduct(dto);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void createProduct_codeAlreadyExists() {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setName(NAME);
        dto.setPrice(25F);
        dto.setCategory(ProductCategory.ELECTRONICS);

        when(productRepository.existsByCode(anyString())).thenReturn(true);

        final BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> productService.createProduct(dto));

        assertEquals("Product with code " + CODE + " already exists", badRequestException.getMessage());
    }

    @Test
    public void createProduct_missingCode() {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setName(NAME);
        dto.setPrice(25F);
        dto.setCategory(ProductCategory.ELECTRONICS);

        final BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> productService.createProduct(dto));

        assertEquals("New product is missing mandatory field(s) : [code]", badRequestException.getMessage());

    }

    @Test
    public void createProduct_missingName() {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setPrice(25F);
        dto.setCategory(ProductCategory.ELECTRONICS);

        final BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> productService.createProduct(dto));

        assertEquals("New product is missing mandatory field(s) : [name]", badRequestException.getMessage());
    }

    @Test
    public void createProduct_missingPrice() {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setName(NAME);
        dto.setCategory(ProductCategory.ELECTRONICS);

        final BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> productService.createProduct(dto));

        assertEquals("New product is missing mandatory field(s) : [price]", badRequestException.getMessage());
    }

    @Test
    public void createProduct_missingCategory() {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setName(NAME);
        dto.setPrice(25F);

        final BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> productService.createProduct(dto));

        assertEquals("New product is missing mandatory field(s) : [category]", badRequestException.getMessage());
    }

    @Test
    public void createProduct_missingSeveralFields() {
        final ProductPatchDTO dto = new ProductPatchDTO();

        final BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> productService.createProduct(dto));
        assertEquals("New product is missing mandatory field(s) : [code,name,price,category]", badRequestException.getMessage());
    }

    @ParameterizedTest
    @ValueSource(floats = { 0F, Float.MIN_VALUE, Float.MAX_VALUE, Float.POSITIVE_INFINITY })
    public void createProduct_validPrice(final Float price) {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setName(NAME);
        dto.setPrice(price);
        dto.setCategory(ProductCategory.ELECTRONICS);

        when(productRepository.existsByCode(anyString())).thenReturn(false);
        when(productMapper.map(any(ProductPatchDTO.class))).thenReturn(new Product());
        when(productRepository.save(any(Product.class))).thenReturn(new Product());
        when(productMapper.map(any(Product.class))).thenReturn(new ProductDTO());

        productService.createProduct(dto);

        verify(productRepository).save(any(Product.class));
    }

    @ParameterizedTest
    @ValueSource(floats = { Float.NEGATIVE_INFINITY, -Float.MAX_VALUE - Float.MIN_VALUE })
    public void createProduct_invalidPrice(final Float price) {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setName(NAME);
        dto.setPrice(price);
        dto.setCategory(ProductCategory.ELECTRONICS);

        final BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> productService.createProduct(dto));

        assertEquals("Invalid price value: the price must be greater or equal to zero", badRequestException.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, Integer.MAX_VALUE })
    public void createProduct_validQuantity(final Integer quantity) {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setName(NAME);
        dto.setPrice(25F);
        dto.setCategory(ProductCategory.ELECTRONICS);
        dto.setQuantity(quantity);

        when(productRepository.existsByCode(anyString())).thenReturn(false);
        when(productMapper.map(any(ProductPatchDTO.class))).thenReturn(new Product());
        when(productRepository.save(any(Product.class))).thenReturn(new Product());
        when(productMapper.map(any(Product.class))).thenReturn(new ProductDTO());

        productService.createProduct(dto);

        verify(productRepository).save(any(Product.class));
    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1 })
    public void createProduct_invalidQuantity(final Integer quantity) {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setName(NAME);
        dto.setPrice(25F);
        dto.setCategory(ProductCategory.ELECTRONICS);
        dto.setQuantity(quantity);

        final BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> productService.createProduct(dto));

        assertEquals("Invalid quantity value: the quantity must be greater or equal to zero", badRequestException.getMessage());
    }

    @ParameterizedTest
    @ValueSource(floats = { 0F, Float.MIN_VALUE, 5F - Float.MIN_VALUE, 5F })
    public void createProduct_validRating(final Float rating) {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setName(NAME);
        dto.setPrice(25F);
        dto.setCategory(ProductCategory.ELECTRONICS);
        dto.setRating(rating);

        when(productRepository.existsByCode(anyString())).thenReturn(false);
        when(productMapper.map(any(ProductPatchDTO.class))).thenReturn(new Product());
        when(productRepository.save(any(Product.class))).thenReturn(new Product());
        when(productMapper.map(any(Product.class))).thenReturn(new ProductDTO());

        productService.createProduct(dto);

        verify(productRepository).save(any(Product.class));
    }

    @ParameterizedTest
    @ValueSource(floats = { Float.NEGATIVE_INFINITY, -Float.MAX_VALUE, -Float.MIN_VALUE, 5.00001F, Float.MAX_VALUE, Float.POSITIVE_INFINITY })
    public void createProduct_invalidRating(final Float rating) {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setName(NAME);
        dto.setPrice(25F);
        dto.setCategory(ProductCategory.ELECTRONICS);
        dto.setRating(rating);

        final BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> productService.createProduct(dto));

        assertEquals("Invalid rating value: the rating must be between 0 and 5, included", badRequestException.getMessage());
    }

    @Test
    public void createProduct_severalConstraintViolations() {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setCode(CODE);
        dto.setName(NAME);
        dto.setCategory(ProductCategory.ELECTRONICS);

        dto.setPrice(-1F);
        dto.setQuantity(-1);
        dto.setRating(-1F);

        final BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> productService.createProduct(dto));

        assertTrue(badRequestException.getMessage().contains("Invalid price value: the price must be greater or equal to zero"));
        assertTrue(badRequestException.getMessage().contains("Invalid quantity value: the quantity must be greater or equal to zero"));
        assertTrue(badRequestException.getMessage().contains("Invalid rating value: the rating must be between 0 and 5, included"));
    }

    // *** updateProduct ***

    @Test
    public void updateProduct_ok() {
        when(productRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.of(new Product()));
        doNothing().when(productMapper).patchValues(any(Product.class), any(ProductPatchDTO.class));
        when(productRepository.save(any(Product.class))).thenReturn(new Product());
        when(productMapper.map(any(Product.class))).thenReturn(new ProductDTO());

        final ProductDTO productDTO = productService.updateProduct(ID, new ProductPatchDTO());

        verify(productRepository).save(any(Product.class));
        assertNotNull(productDTO);
    }

    @Test
    public void updateProduct_productNotFound() {
        when(productRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.empty());

        final NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> productService.updateProduct(ID, new ProductPatchDTO()));

        assertEquals("Product " + ID + " not found", notFoundException.getMessage());
    }

    @Test
    public void updateProduct_duplicateCode() {
        final ProductPatchDTO dto = new ProductPatchDTO();
        dto.setCode(CODE);

        when(productRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.of(new Product()));
        when(productRepository.existsByCode(anyString())).thenReturn(true);

        final BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> productService.updateProduct(ID, dto));

        assertEquals("Can't update code: it already belongs to another product", badRequestException.getMessage());
    }

    @Test
    public void updateProduct_constraintViolations() {
        final ProductPatchDTO dto = new ProductPatchDTO();

        dto.setPrice(-1F);
        dto.setQuantity(-1);

        dto.setRating(-1F);
        when(productRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.of(new Product()));

        final BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> productService.updateProduct(ID, dto));

        assertTrue(badRequestException.getMessage().contains("Invalid price value: the price must be greater or equal to zero"));
        assertTrue(badRequestException.getMessage().contains("Invalid quantity value: the quantity must be greater or equal to zero"));
        assertTrue(badRequestException.getMessage().contains("Invalid rating value: the rating must be between 0 and 5, included"));
    }

    // *** deleteProduct ***

    @Test
    public void deleteProduct_productFound() {
        final Product initialProduct = new Product();
        final Product deletedProduct = new Product();
        deletedProduct.setDeleted(true);

        when(productRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.of(initialProduct));
        when(productMapper.deleteProduct(any(Product.class))).thenReturn(deletedProduct);
        when(productRepository.save(any(Product.class))).then(AdditionalAnswers.returnsFirstArg());

        productService.deleteProduct(ID);

        verify(productRepository).findByIdAndDeletedFalse(ID);
        verify(productMapper).deleteProduct(initialProduct);
        verify(productRepository).save(argThat(Product::isDeleted));
    }

    @Test
    public void deleteProduct_productNotFound() {
        when(productRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.deleteProduct(ID));

        verify(productRepository).findByIdAndDeletedFalse(ID);
        assertEquals("Product " + ID + " not found", exception.getMessage());
    }

}
