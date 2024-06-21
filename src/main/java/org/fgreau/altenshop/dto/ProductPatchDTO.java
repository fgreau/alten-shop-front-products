package org.fgreau.altenshop.dto;

import lombok.Data;
import org.fgreau.altenshop.enums.ProductCategory;

/**
 * DTO used to create or update a ProductDTO.
 */
@Data
public class ProductPatchDTO {

    /**
     * Reference code for the product.
     */
    private String code;

    /**
     * Product's name.
     */
    private String name;

    /**
     * Product's description.
     */
    private String description;

    /**
     * Price, in USD.
     */
    private Float price;

    /**
     * Available quantity in inventory.
     */
    private Integer quantity;

    /**
     * Product's category.
     */
    private ProductCategory category;

    /**
     * Link to the image.
     */
    private String image;

    /**
     * Rating of the product, between 0 and 5 included.
     */
    private Float rating;
}
