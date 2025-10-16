package com.gerze.prueba.controller.DTOs;

import com.gerze.prueba.model.Product;
import com.gerze.prueba.model.Stock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockDTO {

    @NotNull(message = "El identificador del producto es obligatorio")
    private Long productId;

    @Min(value = 0, message = "La cantidad debe ser mayor o igual a 0")
    private int quantity;

    private String location;

    public Stock toEntity(Product product) {
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(quantity);
        stock.setLocation(location);
        return stock;
    }
}
