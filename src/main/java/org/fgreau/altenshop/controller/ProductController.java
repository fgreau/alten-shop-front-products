package org.fgreau.altenshop.controller;

import org.fgreau.altenshop.dto.ProductDTO;
import org.fgreau.altenshop.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller of Product.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    /**
     * Product Service.
     */
    private final ProductService productService;

    /**
     * Constructor.
     *
     * @param productService Product Service
     */
    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    /**
     * Returns a pageable list of products.
     *
     * @param codeFilter filters products with codes containing this value, ignoring case
     * @param nameFilter filters products with names containing this value, ignoring case
     * @param pageable   pageable properties
     * @return products
     */
    @GetMapping
    public PagedModel<EntityModel<ProductDTO>> getAllProductsPageable(
        @RequestParam(value = "code", required = false) String codeFilter,
        @RequestParam(value = "name", required = false) String nameFilter,
        @SortDefault("id") final Pageable pageable
    ) {
        return this.productService.getAllProductsPageable(codeFilter, nameFilter, pageable);
    }

    /**
     * Returns the details of a product.
     *
     * @param id product id
     * @return DTO
     */
    @GetMapping(value = "/{productId}")
    public ProductDTO getProductDetails(@PathVariable("productId") Long id) {
        return this.productService.getProductDetails(id);
    }
}
