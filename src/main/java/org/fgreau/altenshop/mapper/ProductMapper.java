package org.fgreau.altenshop.mapper;

import org.fgreau.altenshop.dto.ProductDTO;
import org.fgreau.altenshop.dto.ProductPatchDTO;
import org.fgreau.altenshop.enums.InventoryStatus;
import org.fgreau.altenshop.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper of Product.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Maps Product entity into DTO.
     *
     * @param product entity
     * @return dto
     */
    ProductDTO map(Product product);

    /**
     * Maps Product DTO into entity.
     *
     * @param productDTO dto
     * @return entity
     */
    Product map(ProductDTO productDTO);

    /**
     * Maps Product Patch DTO into entity.
     *
     * @param productPatchDTO patch dto
     * @return entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inventoryStatus", source = "quantity", qualifiedByName = "mapInventoryStatus")
    @Mapping(target = "deleted", constant = "false")
    Product map(ProductPatchDTO productPatchDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "inventoryStatus", source = "quantity", qualifiedByName = "mapInventoryStatus")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void patchValues(@MappingTarget final Product product, final ProductPatchDTO productPatchDTO);

    @Named("mapInventoryStatus")
    default InventoryStatus mapInventoryStatus(final int quantity) {
        if (quantity <= 0) {
            return InventoryStatus.OUTOFSTOCK;
        }

        if (quantity < 10) {
            return InventoryStatus.LOWSTOCK;
        }

        return InventoryStatus.INSTOCK;
    }

    /**
     * Logic deletion of a product.
     *
     * @param product product
     * @return deleted product
     */
    @Mapping(target = "deleted", constant = "true")
    Product deleteProduct(final Product product);
}
