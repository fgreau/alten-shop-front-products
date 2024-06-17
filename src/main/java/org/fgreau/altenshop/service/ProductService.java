package org.fgreau.altenshop.service;

import org.fgreau.altenshop.dto.ProductDTO;
import org.fgreau.altenshop.exception.NotFoundException;
import org.fgreau.altenshop.mapper.ProductMapper;
import org.fgreau.altenshop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

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
     *
     * @param productMapper           Product Mapper
     * @param productRepository       Product Repository
     * @param pagedResourcesAssembler Pagination assembler
     */
    public ProductService(final ProductMapper productMapper, final ProductRepository productRepository, final PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    /**
     * Returns a pageable list of products.
     *
     * @param codeFilter filters products with codes containing this value, ignoring case
     * @param nameFilter filters products with names containing this value, ignoring case
     * @param pageable   pageable properties
     * @return products
     */
    public PagedModel<EntityModel<ProductDTO>> getAllProductsPageable(final String codeFilter, final String nameFilter, final Pageable pageable) {

        // Ensures that the pageable is never null
        final Pageable localPageable = Optional.ofNullable(pageable).orElse(PageRequest.of(0, 10));

        final Page<ProductDTO> products;

        // both filters
        if (codeFilter != null && nameFilter != null) {
            products = productRepository.findByCodeContainsIgnoreCaseAndNameContainsIgnoreCaseAndDeletedFalse(codeFilter, nameFilter, localPageable).map(productMapper::map);
        }

        // only code filter
        else if (codeFilter != null) {
            products = productRepository.findByCodeContainsIgnoreCaseAndDeletedFalse(codeFilter, localPageable).map(productMapper::map);
        }

        // only name filter
        else if (nameFilter != null) {
            products = productRepository.findByNameContainsIgnoreCaseAndDeletedFalse(nameFilter, localPageable).map(productMapper::map);
        }

        // no filter param
        else {
            products = productRepository.findByDeletedFalse(localPageable).map(productMapper::map);
        }

        return pagedResourcesAssembler.toModel(products);
    }

    /**
     * Returns the details of a product.
     *
     * @param id product id
     * @return DTO
     */
    public ProductDTO getProductDetails(final Long id) {
        return productRepository.findByIdAndDeletedFalse(id)
            .map(productMapper::map)
            .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }

}
