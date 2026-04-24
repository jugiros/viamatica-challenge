package com.viamatica.assessment.orders_management_system.domain.port;

import com.viamatica.assessment.orders_management_system.domain.entity.CategoryDomain;
import com.viamatica.assessment.orders_management_system.domain.valueobject.CategoryName;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    CategoryDomain save(CategoryDomain category);

    Optional<CategoryDomain> findById(Long id);

    List<CategoryDomain> findAll();

    Optional<CategoryDomain> findByName(CategoryName name);

    void deleteById(Long id);

    boolean existsById(Long id);
}
