package com.viamatica.assessment.orders_management_system.domain.port;

import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    ProductDomain save(ProductDomain product);

    Optional<ProductDomain> findById(Long id);

    List<ProductDomain> findAll();

    List<ProductDomain> findAllActive();

    List<ProductDomain> findByCategoryId(Long categoryId);

    List<ProductDomain> findActiveByCategoryId(Long categoryId);

    void deleteById(Long id);

    boolean existsById(Long id);
}
