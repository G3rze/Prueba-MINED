package com.gerze.prueba.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.gerze.prueba.controller.DTOs.StockDTO;
import com.gerze.prueba.model.Product;
import com.gerze.prueba.model.Stock;
import com.gerze.prueba.service.StockService;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StockService stockService;

    @Test
    @DisplayName("GET /api/stocks should return active stock entries")
    void shouldReturnAllStock() throws Exception {
        Stock stock = buildStock(1L, 10, "A1");
        when(stockService.getAllStock()).thenReturn(List.of(stock));

        mockMvc.perform(get("/api/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].quantity").value(10));
    }

    @Test
    @DisplayName("GET /api/stocks/{id} should return stock when found")
    void shouldReturnStockById() throws Exception {
        Stock stock = buildStock(2L, 20, "B2");
        when(stockService.getStockById(2L)).thenReturn(stock);

        mockMvc.perform(get("/api/stocks/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.quantity").value(20))
                .andExpect(jsonPath("$.product.id").value(102L));
    }

    @Test
    @DisplayName("GET /api/stocks/{id} should return 404 when not found")
    void shouldReturnNotFoundForMissingStock() throws Exception {
        when(stockService.getStockById(222L)).thenReturn(null);

        mockMvc.perform(get("/api/stocks/222"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/stocks should create new stock entry when valid")
    void shouldCreateStock() throws Exception {
        StockDTO stockDTO = buildStockDTO();
        Stock created = buildStock(3L, stockDTO.getQuantity(), stockDTO.getLocation());
        when(stockService.createStock(any(StockDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.quantity").value(stockDTO.getQuantity()));
    }

    @Test
    @DisplayName("POST /api/stocks should return 400 when service rejects creation")
    void shouldReturnBadRequestWhenCreateFails() throws Exception {
        StockDTO stockDTO = buildStockDTO();
        when(stockService.createStock(any(StockDTO.class))).thenReturn(null);

        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/stocks/{id} should update stock when found and valid")
    void shouldUpdateStock() throws Exception {
        StockDTO stockDTO = buildStockDTO();
        Stock existing = buildStock(4L, 40, "C3");
        Stock updated = buildStock(4L, stockDTO.getQuantity(), stockDTO.getLocation());
        when(stockService.getStockById(4L)).thenReturn(existing);
        when(stockService.updateStock(eq(4L), any(StockDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/stocks/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.quantity").value(stockDTO.getQuantity()));
    }

    @Test
    @DisplayName("PUT /api/stocks/{id} should return 404 when stock not found")
    void shouldReturnNotFoundWhenUpdatingMissingStock() throws Exception {
        StockDTO stockDTO = buildStockDTO();
        when(stockService.getStockById(44L)).thenReturn(null);

        mockMvc.perform(put("/api/stocks/44")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/stocks/{id} should return 400 when service rejects update")
    void shouldReturnBadRequestWhenUpdateFails() throws Exception {
        StockDTO stockDTO = buildStockDTO();
        Stock existing = buildStock(5L, 50, "D4");
        when(stockService.getStockById(5L)).thenReturn(existing);
        when(stockService.updateStock(eq(5L), any(StockDTO.class))).thenReturn(null);

        mockMvc.perform(put("/api/stocks/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/stocks/{id} should delete stock when found")
    void shouldDeleteStock() throws Exception {
        when(stockService.deleteStock(6L)).thenReturn(true);

        mockMvc.perform(delete("/api/stocks/6"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/stocks/{id} should return 404 when not found")
    void shouldReturnNotFoundWhenDeletingMissingStock() throws Exception {
        when(stockService.deleteStock(66L)).thenReturn(false);

        mockMvc.perform(delete("/api/stocks/66"))
                .andExpect(status().isNotFound());
    }

    private Stock buildStock(Long stockId, int quantity, String location) {
        Product product = new Product();
        product.setId(100L + stockId);
        product.setName("Producto " + stockId);
        product.setDescription("Descripción producto " + stockId);
        product.setPrice(BigDecimal.valueOf(50 + stockId));
        product.setState(Boolean.TRUE);

        Stock stock = new Stock();
        stock.setId(stockId);
        stock.setProduct(product);
        stock.setQuantity(quantity);
        stock.setLocation(location);
        product.setStock(stock);
        return stock;
    }

    private StockDTO buildStockDTO() {
        StockDTO dto = new StockDTO();
        dto.setProductId(101L);
        dto.setQuantity(12);
        dto.setLocation("Almacén 1");
        return dto;
    }
}
