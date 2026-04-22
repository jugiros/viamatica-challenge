package com.viamatica.assessment.orders_management_system.domain.port;

import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import java.util.List;
import java.util.Optional;

/**
 * Repository port for Product entity.
 * Pure interface without any Spring or JPA annotations.
 * Implementations will be in the infrastructure layer.
 */
public interface ProductRepository {

    /**
     * Saves a product entity.
     * @param product the product to save
     * @return the saved product
     */
    ProductDomain save(ProductDomain product);

    /**
     * Finds a product by ID.
     * @param id the product ID
     * @return Optional containing the product if found
     */
    Optional<ProductDomain> findById(Long id);

    /**
     * Finds all products.
     * @return list of all products
     */
    List<ProductDomain> findAll();

    /**
     * Finds all active products.
     * @return list of active products
     */
    List<ProductDomain> findAllActive();

    /**
     * Finds products by category ID.
     * @param categoryId the category ID
     * @return list of products in the category
     */
    List<ProductDomain> findByCategoryId(Long categoryId);

    /**
     * Finds active products by category ID.
     * @param categoryId the category ID
     * @return list of active products in the category
     */
    List<ProductDomain> findActiveByCategoryId(Long categoryId);

    /**
     * Deletes a product by ID.
     * @param id the product ID
     */
    void deleteById(Long id);

    /**
     * Checks if a product exists with the given ID.
     * @param id the product ID
     * @return true if the product exists
     */
    boolean existsById(Long id);
}
