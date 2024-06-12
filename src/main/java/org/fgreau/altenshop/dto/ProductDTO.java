package org.fgreau.altenshop.dto;

import lombok.Data;
import org.fgreau.altenshop.enums.InventoryStatus;
import org.fgreau.altenshop.enums.ProductCategory;

/**
 * Product DTO.
 */
@Data
public class ProductDTO {

    /**
     * Identifier.
     */
    private Long id;

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
    private float price;

    /**
     * Available quantity in inventory.
     */
    private int quantity;

    /**
     * Status of the inventory, generally depending on the quantity.
     */
    private InventoryStatus inventoryStatus;

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
    private float rating;

    /**
     * Logic deletion.
     */
    private boolean deleted;
}
