package org.fgreau.altenshop.mapper;

import org.fgreau.altenshop.enums.InventoryStatus;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProductMapperTests {

    @InjectMocks
    private ProductMapperImpl productMapper;

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    public void mapInventoryStatus_outOfStock(int quantity) {
        final InventoryStatus inventoryStatus = productMapper.mapInventoryStatus(quantity);
        assertEquals(InventoryStatus.OUTOFSTOCK, inventoryStatus);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9})
    public void mapInventoryStatus_lowStock(int quantity) {
        final InventoryStatus inventoryStatus = productMapper.mapInventoryStatus(quantity);
        assertEquals(InventoryStatus.LOWSTOCK, inventoryStatus);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, Integer.MAX_VALUE})
    public void mapInventoryStatus_inStock(int quantity) {
        final InventoryStatus inventoryStatus = productMapper.mapInventoryStatus(quantity);
        assertEquals(InventoryStatus.INSTOCK, inventoryStatus);
    }

}
