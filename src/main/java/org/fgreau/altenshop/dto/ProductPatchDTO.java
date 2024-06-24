package org.fgreau.altenshop.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @Min(value = 0, message = "Invalid price value: the price must be greater or equal to zero")
    private Float price;

    /**
     * Available quantity in inventory.
     */
    @Min(value = 0, message = "Invalid quantity value: the quantity must be greater or equal to zero")
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
    @Min(value = 0, message = "Invalid rating value: the rating must be between 0 and 5, included")
    @Max(value = 5, message = "Invalid rating value: the rating must be between 0 and 5, included")
    private Float rating;
}
