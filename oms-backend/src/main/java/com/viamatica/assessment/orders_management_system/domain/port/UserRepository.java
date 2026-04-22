package com.viamatica.assessment.orders_management_system.domain.port;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import java.util.Optional;

/**
 * Repository port for User entity.
 * Pure interface without any Spring or JPA annotations.
 * Implementations will be in the infrastructure layer.
 */
public interface UserRepository {

    /**
     * Saves a user entity.
     * @param user the user to save
     * @return the saved user
     */
    UserDomain save(UserDomain user);

    /**
     * Finds a user by ID.
     * @param id the user ID
     * @return Optional containing the user if found
     */
    Optional<UserDomain> findById(Long id);

    /**
     * Finds a user by email.
     * @param email the user email
     * @return Optional containing the user if found
     */
    Optional<UserDomain> findByEmail(Email email);

    /**
     * Checks if a user exists with the given email.
     * @param email the email to check
     * @return true if a user with the email exists
     */
    boolean existsByEmail(Email email);

    /**
     * Deletes a user by ID.
     * @param id the user ID
     */
    void deleteById(Long id);

    /**
     * Finds all active users.
     * @return list of active users
     */
    java.util.List<UserDomain> findAllActive();
}
