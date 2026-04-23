package com.viamatica.assessment.orders_management_system.domain.port;

import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    ProductDomain save(ProductDomain product);

    Optional<ProductDomain> findById(Long id);

    List<ProductDomain> findAll();

    Optional<ProductDomain> findByName(ProductName name);

    List<ProductDomain> findAllActive();

    List<ProductDomain> findByCategoryId(Long categoryId);

    List<ProductDomain> findActiveByCategoryId(Long categoryId);

    void deleteById(Long id);

    boolean existsById(Long id);
}
