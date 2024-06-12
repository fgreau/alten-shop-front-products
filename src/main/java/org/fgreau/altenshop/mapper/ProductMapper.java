package org.fgreau.altenshop.mapper;

import org.fgreau.altenshop.dto.ProductDTO;
import org.fgreau.altenshop.model.Product;
import org.mapstruct.Mapper;

/**
 * Mapper of Product.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Maps Product entity into DTO.
     * @param product entity
     * @return dto
     */
    ProductDTO map(Product product);

    /**
     * Maps Product DTO into entity.
     * @param productDTO dto
     * @return entity
     */
    Product map(ProductDTO productDTO);
}
