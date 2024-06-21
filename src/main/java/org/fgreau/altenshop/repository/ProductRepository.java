package org.fgreau.altenshop.repository;

import org.fgreau.altenshop.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository of Product.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find any product by Id that has not been deleted.
     *
     * @param id id
     * @return optional of product
     */
    Optional<Product> findByIdAndDeletedFalse(Long id);

    /**
     * Returns a pageable list of all elements that have not been deleted.
     *
     * @param pageable pageable parameters
     * @return pageable list
     */
    Page<Product> findByDeletedFalse(Pageable pageable);

    /**
     * Returns a pageable list of all elements that have not been deleted, with code and name filters.
     *
     * @param codeFilter code contains
     * @param nameFilter name contains
     * @param pageable   pageable parameters
     * @return pageable list
     */
    Page<Product> findByCodeContainsIgnoreCaseAndNameContainsIgnoreCaseAndDeletedFalse(String codeFilter, String nameFilter, Pageable pageable);

    /**
     * Returns a pageable list of all elements that have not been deleted, with code filters.
     *
     * @param codeFilter code contains
     * @param pageable   pageable parameters
     * @return pageable list
     */
    Page<Product> findByCodeContainsIgnoreCaseAndDeletedFalse(String codeFilter, Pageable pageable);

    /**
     * Returns a pageable list of all elements that have not been deleted, with name filters.
     *
     * @param nameFilter name contains
     * @param pageable   pageable parameters
     * @return pageable list
     */
    Page<Product> findByNameContainsIgnoreCaseAndDeletedFalse(String nameFilter, Pageable pageable);

    /**
     * Checks if a product with a specific code exists.
     *
     * @param code product code
     * @return boolean
     */
    boolean existsByCode(String code);
}
