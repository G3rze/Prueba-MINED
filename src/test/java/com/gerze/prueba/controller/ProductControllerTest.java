package com.gerze.prueba.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerze.prueba.controller.DTOs.ProductDTO;
import com.gerze.prueba.model.Product;
import com.gerze.prueba.service.ProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("GET /api/products should return list of active products")
    void shouldReturnListOfProducts() throws Exception {
        Product product = buildProduct(1L);
        when(productService.getAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Producto 1"));
    }

    @Test
    @DisplayName("GET /api/products/{id} should return product when found")
    void shouldReturnProductById() throws Exception {
        Product product = buildProduct(2L);
        when(productService.getProductById(2L)).thenReturn(product);

        mockMvc.perform(get("/api/products/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Producto 2"));
    }

    @Test
    @DisplayName("GET /api/products/{id} should return 404 when not found or inactive")
    void shouldReturnNotFoundWhenProductMissing() throws Exception {
        when(productService.getProductById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/products should create product when payload is valid")
    void shouldCreateProduct() throws Exception {
        ProductDTO productDTO = buildProductDTO();
        Product created = buildProduct(3L);
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Producto 3"));
    }

    @Test
    @DisplayName("POST /api/products should return 400 when validation fails")
    void shouldReturnBadRequestWhenProductPayloadInvalid() throws Exception {
        ProductDTO invalidProduct = new ProductDTO();
        invalidProduct.setPrice(BigDecimal.TEN);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/products/{id} should update product when found")
    void shouldUpdateProduct() throws Exception {
        ProductDTO updates = buildProductDTO();
        Product updated = buildProduct(5L);
        when(productService.updateProduct(eq(5L), any(ProductDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/products/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("Producto 5"));
    }

    @Test
    @DisplayName("PUT /api/products/{id} should return 404 when product not found")
    void shouldReturnNotFoundWhenUpdatingMissingProduct() throws Exception {
        ProductDTO updates = buildProductDTO();
        when(productService.updateProduct(eq(55L), any(ProductDTO.class))).thenReturn(null);

        mockMvc.perform(put("/api/products/55")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} should soft delete product when found")
    void shouldDeleteProduct() throws Exception {
        Product product = buildProduct(7L);
        when(productService.getProductById(7L)).thenReturn(product);
        doNothing().when(productService).deleteProduct(7L);

        mockMvc.perform(delete("/api/products/7"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(7L);
    }

    @Test
    @DisplayName("DELETE /api/products/{id} should return 404 when product not found")
    void shouldReturnNotFoundWhenDeletingMissingProduct() throws Exception {
        when(productService.getProductById(77L)).thenReturn(null);

        mockMvc.perform(delete("/api/products/77"))
                .andExpect(status().isNotFound());
    }

    private Product buildProduct(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("Producto " + id);
        product.setDescription("Descripción " + id);
        product.setPrice(BigDecimal.valueOf(100 + id));
        product.setState(Boolean.TRUE);
        return product;
    }

    private ProductDTO buildProductDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Nuevo producto");
        dto.setDescription("Descripción nueva");
        dto.setPrice(BigDecimal.valueOf(200));
        return dto;
    }
}
