package org.fgreau.altenshop.enums;

/**
 * Inventory Status.
 */
public enum InventoryStatus {
    OUTOFSTOCK,
    LOWSTOCK,
    INSTOCK;

    /**
     * Threshold distinguishing normal stock from low stock.
     */
    private static final int LOW_STOCK_THRESHOLD = 10;

    /**
     * Converts the stock quantity into an InventoryStatus value.
     *
     * @param quantity quantity in stock
     * @return InventoryStatus
     */
    public static InventoryStatus getInventoryStatus(final int quantity) {
        if (quantity <= 0) {
            return OUTOFSTOCK;
        }

        if (quantity < LOW_STOCK_THRESHOLD) {
            return LOWSTOCK;
        }

        return INSTOCK;
    }
}