package org.fgreau.altenshop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Product's name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Product's description.
     */
    @Column(name = "description")
    private String description;

    /**
     * Price, in USD.
     */
    @Column(name = "price", nullable = false)
    private float price;

    /**
     * Available quantity in inventory.
     */
    @Column(name = "quantity", nullable = false)
    private int quantity;

    /**
     * Status of the inventory, generally depending on the quantity.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "inventory_status", nullable = false)
    private InventoryStatus inventoryStatus;

    /**
     * Product's category.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
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
    private Float rating;

    /**
     * Logic deletion.
     */
    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}
