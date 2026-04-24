package com.viamatica.assessment.orders_management_system.infrastructure.controller;

import com.viamatica.assessment.orders_management_system.domain.entity.CategoryDomain;
import com.viamatica.assessment.orders_management_system.domain.port.CategoryRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.CategoryName;
import com.viamatica.assessment.orders_management_system.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = CategoryController.class)
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void CAT01_GetAllCategories_ShouldReturnList() throws Exception {
        CategoryDomain category1 = CategoryDomain.builder()
                .id(1L)
                .name(CategoryName.of("Electrónica"))
                .description("Productos electrónicos")
                .createdAt(LocalDateTime.now())
                .build();

        CategoryDomain category2 = CategoryDomain.builder()
                .id(2L)
                .name(CategoryName.of("Ropa"))
                .description("Ropa y accesorios")
                .createdAt(LocalDateTime.now())
                .build();

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @WithMockUser
    void CAT02_GetCategoryById_Exists_ShouldReturnCategory() throws Exception {
        CategoryDomain category = CategoryDomain.builder()
                .id(1L)
                .name(CategoryName.of("Electrónica"))
                .description("Productos electrónicos")
                .createdAt(LocalDateTime.now())
                .build();

        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(category));

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electrónica"))
                .andExpect(jsonPath("$.description").value("Productos electrónicos"));
    }

    @Test
    @WithMockUser
    void CAT03_GetCategoryById_NotExists_ShouldReturn404() throws Exception {
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void CAT04_CreateCategory_ValidData_ShouldReturnCreated() throws Exception {
        CategoryDomain category = CategoryDomain.builder()
                .id(1L)
                .name(CategoryName.of("Electrónica"))
                .description("Productos electrónicos")
                .createdAt(LocalDateTime.now())
                .build();

        when(categoryRepository.save(any(CategoryDomain.class))).thenReturn(category);

        String requestBody = "{\"name\":\"Electrónica\", \"description\":\"Productos electrónicos\"}";

        mockMvc.perform(post("/api/v1/categories")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electrónica"))
                .andExpect(jsonPath("$.description").value("Productos electrónicos"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void CAT05_UpdateCategory_ValidData_ShouldReturnUpdated() throws Exception {
        CategoryDomain category = CategoryDomain.builder()
                .id(1L)
                .name(CategoryName.of("Electrónica"))
                .description("Productos electrónicos")
                .createdAt(LocalDateTime.now())
                .build();

        CategoryDomain updatedCategory = CategoryDomain.builder()
                .id(1L)
                .name(CategoryName.of("Electrónica Actualizada"))
                .description("Descripción actualizada")
                .createdAt(LocalDateTime.now())
                .build();

        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(category));
        when(categoryRepository.save(any(CategoryDomain.class))).thenReturn(updatedCategory);

        String requestBody = "{\"name\":\"Electrónica Actualizada\", \"description\":\"Descripción actualizada\"}";

        mockMvc.perform(put("/api/v1/categories/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void CAT06_DeleteCategory_Exists_ShouldReturn204() throws Exception {
        CategoryDomain category = CategoryDomain.builder()
                .id(1L)
                .name(CategoryName.of("Electrónica"))
                .description("Productos electrónicos")
                .createdAt(LocalDateTime.now())
                .build();

        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(category));

        mockMvc.perform(delete("/api/v1/categories/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void CAT07_DeleteCategory_NotExists_ShouldReturn404() throws Exception {
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(delete("/api/v1/categories/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }
}
