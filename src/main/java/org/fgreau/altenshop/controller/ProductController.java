package org.fgreau.altenshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.fgreau.altenshop.dto.ProductDTO;
import org.fgreau.altenshop.dto.ProductPatchDTO;
import org.fgreau.altenshop.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @Operation(summary = "Get a pageable list of products")
    public PagedModel<EntityModel<ProductDTO>> getAllProductsPageable(
        @Parameter(description = "Filter products by code (contains)") @RequestParam(value = "code", required = false) String codeFilter,
        @Parameter(description = "Filter products by name (contains)") @RequestParam(value = "name", required = false) String nameFilter,
        @Parameter(description = "Pagination parameters", example = "{\"page\":0,\"size\":10,\"sort\":[\"name\",\"price,desc\"]}")
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
    @Operation(summary = "Get the details of a product")
    public ProductDTO getProductDetails(@PathVariable("productId") Long id) {
        return this.productService.getProductDetails(id);
    }

    /**
     * Creates a product.
     *
     * @param newProduct DTO
     * @return created DTO
     */
    @PostMapping
    @Operation(summary = "Create a product")
    public ProductDTO createProduct(@RequestBody ProductPatchDTO newProduct) {
        return productService.createProduct(newProduct);
    }

    /**
     * Updates a product.
     *
     * @param id             existing product id
     * @param updatedProduct new product values
     * @return updated product
     */
    @PatchMapping(value = "/{productId}")
    @Operation(summary = "Update a product")
    public ProductDTO updateProduct(@PathVariable("productId") Long id, @RequestBody ProductPatchDTO updatedProduct) {
        return productService.updateProduct(id, updatedProduct);
    }

    /**
     * Deletes a product.
     *
     * @param id product id
     * @return status
     */
    @DeleteMapping(value = "/{productId}")
    @Operation(summary = "Delete a product")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
