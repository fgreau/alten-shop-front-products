package org.fgreau.altenshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.fgreau.altenshop.enums.InventoryStatus;
import org.fgreau.altenshop.enums.ProductCategory;

import java.io.Serial;
import java.io.Serializable;

/**
 * Product entity.
 */
@Getter
@Setter
@Entity
@Table(name = "product")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 2762959163169472187L;

    /**
     * Identifier.
     */
    @Id
    @Column(name = "id", columnDefinition = "bigint")
    @GeneratedValue
    private Long id;

    /**
     * Reference code for the product.
     */
    @Column(name = "code")
    private String code;

    /**
     * Product's name.
     */
    @Column(name = "name")
    private String name;

    /**
     * Product's description.
     */
    @Column(name = "description")
    private String description;

    /**
     * Price, in USD.
     */
    @Column(name = "price")
    private float price;

    /**
     * Available quantity in inventory.
     */
    @Column(name = "quantity")
    private int quantity;

    /**
     * Status of the inventory, generally depending on the quantity.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "inventory_status")
    private InventoryStatus inventoryStatus;

    /**
     * Product's category.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private ProductCategory category;

    /**
     * Link to the image.
     */
    @Column(name = "image")
    private String image;

    /**
     * Rating of the product, between 0 and 5 included.
     */
    @Column(name = "rating")
    private float rating;

    /**
     * Logic deletion.
     */
    @Column(name = "deleted")
    private boolean deleted;
}
