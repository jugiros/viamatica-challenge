package com.viamatica.assessment.orders_management_system.domain.entity;

import com.viamatica.assessment.orders_management_system.domain.model.UserRole;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDomain {

    private Long id;
    private String name;
    private Email email;
    private String passwordHash;
    private UserRole role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private UserDomain() {
    }

    private UserDomain(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.role = builder.role;
        this.active = builder.active;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.deletedAt = builder.deletedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Checks if this user is an administrator.
     * @return true if the user has ADMIN role
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    /**
     * Soft deletes this user by setting the deletedAt timestamp.
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.active = false;
    }

    /**
     * Restores a soft-deleted user by clearing the deletedAt timestamp.
     */
    public void restore() {
        this.deletedAt = null;
        this.active = true;
    }

    /**
     * Updates the updatedAt timestamp to current time.
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Builder pattern for UserDomain.
     */
    public static class Builder {
        private Long id;
        private String name;
        private Email email;
        private String passwordHash;
        private UserRole role = UserRole.USER;
        private boolean active = true;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();
        private LocalDateTime deletedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(Email email) {
            this.email = email;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder deletedAt(LocalDateTime deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public UserDomain build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name cannot be null or blank");
            }
            if (email == null) {
                throw new IllegalArgumentException("Email cannot be null");
            }
            if (passwordHash == null || passwordHash.isBlank()) {
                throw new IllegalArgumentException("Password hash cannot be null or blank");
            }
            if (role == null) {
                throw new IllegalArgumentException("Role cannot be null");
            }
            return new UserDomain(this);
        }
    }
}
