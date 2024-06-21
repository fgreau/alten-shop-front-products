package org.fgreau.altenshop.service;

import org.fgreau.altenshop.dto.ProductDTO;
import org.fgreau.altenshop.dto.ProductPatchDTO;
import org.fgreau.altenshop.exception.NotFoundException;
import org.fgreau.altenshop.mapper.ProductMapper;
import org.fgreau.altenshop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.function.Predicate.not;

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

    private static final List<String> ALLOWED_SORT_PROPERTIES = List.of("id", "code", "name", "price", "quantity", "inventoryStatus", "category", "rating");

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

        final Pageable localPageable = validatePageable(pageable, ALLOWED_SORT_PROPERTIES);

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
     * Ensures that the pageable is never null or with invalid parameters.
     *
     * @param pageable          input pageable
     * @param allowedProperties list of allowed sorting property values
     * @return compliant pageable
     */
    public Pageable validatePageable(final Pageable pageable, final List<String> allowedProperties) {

        if (pageable == null) {
            return PageRequest.of(0, 10);
        }

        final int pageNumber = pageable.getPageNumber();
        final int pageSize = pageable.getPageSize();

        if (allowedProperties == null || allowedProperties.isEmpty()) {
            return PageRequest.of(pageNumber, pageSize);
        }

        // Checks if any sort property is non-compliant, and if so, removes them
        if (!pageable.getSort().isEmpty()) {
            if (pageable.getSort().get().map(Sort.Order::getProperty).anyMatch(not(allowedProperties::contains))) {

                final Sort compliantSort = Sort.by(
                    pageable.getSort()
                        .get()
                        .filter(sort -> allowedProperties.contains(sort.getProperty()))
                        .toList()
                );

                return PageRequest.of(pageNumber, pageSize, compliantSort);
            }
        }

        return pageable;
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


    /**
     * Creates a new product.
     *
     * @param newProduct new product
     * @return created product
     */
    public ProductDTO createProduct(final ProductPatchDTO newProduct) {
        // TODO
        return null;
    }

    /**
     * Updates an existing product.
     *
     * @param id             existing product id
     * @param updatedProduct new product values
     * @return updated product
     */
    public ProductDTO updateProduct(final Long id, final ProductPatchDTO updatedProduct) {
        // TODO
        return null;
    }

    /**
     * Deletes an existing product.
     *
     * @param id product id
     */
    public void deleteProduct(final Long id) {
        productRepository.findByIdAndDeletedFalse(id)
            .map(productMapper::deleteProduct)
            .map(productRepository::save)
            .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }
}
