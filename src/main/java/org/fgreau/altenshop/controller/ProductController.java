package org.fgreau.altenshop.controller;

import org.fgreau.altenshop.dto.ProductDTO;
import org.fgreau.altenshop.exception.NotFoundException;
import org.fgreau.altenshop.mapper.ProductMapper;
import org.fgreau.altenshop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.SortDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller of Product.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    /**
     * Product Mapper.
     */
    private final ProductMapper productMapper;

    /**
     * Product Repository.
     */
    private final ProductRepository productRepository;

    /**
     * Assembler to handle pagination.
     */
    private final PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler;

    /**
     * Constructor.
     * @param productMapper Product Mapper
     * @param productRepository Product Repository
     */
    public ProductController(final ProductMapper productMapper, final ProductRepository productRepository, final PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    /**
     * Returns a pageable list of products.
     *
     * @param pageable pageable properties
     * @return products
     */
    @GetMapping
    public PagedModel<EntityModel<ProductDTO>> getAllProductsPageable(@SortDefault("id") final Pageable pageable) {
        final Page<ProductDTO> products = productRepository.findByDeletedFalse(pageable)
            .map(productMapper::map);
        return pagedResourcesAssembler.toModel(products);
    }

    /**
     * Returns the details of a product.
     * @param id product id
     * @return DTO
     */
    @GetMapping(value = "/{productId}")
    public ProductDTO getProductDetails(@PathVariable("productId") Long id) {
        return productRepository.findByIdAndDeletedFalse(id)
            .map(productMapper::map)
            .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }
}
