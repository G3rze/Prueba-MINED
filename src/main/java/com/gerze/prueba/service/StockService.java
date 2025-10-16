package com.gerze.prueba.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gerze.prueba.controller.DTOs.StockDTO;
import com.gerze.prueba.model.Product;
import com.gerze.prueba.model.Stock;
import com.gerze.prueba.repository.ProductRepository;
import com.gerze.prueba.repository.StockRepository;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Stock getStockById(Long id) {
        return stockRepository.findById(id)
                .filter(stock -> isProductActive(stock.getProduct()))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Stock> getAllStock() {
        return stockRepository.findAll().stream()
                .filter(stock -> isProductActive(stock.getProduct()))
                .toList();
    }

    @Transactional
    public Stock createStock(StockDTO stockDTO) {
        Product product = productRepository.findById(stockDTO.getProductId())
                .filter(this::isProductActive)
                .orElse(null);
        if (product == null || stockRepository.findByProductId(product.getId()).isPresent()) {
            return null;
        }
        Stock stock = stockDTO.toEntity(product);
        return stockRepository.save(stock);
    }

    @Transactional
    public Stock updateStock(Long id, StockDTO stockDTO) {
        Stock existingStock = stockRepository.findById(id)
                .filter(stock -> isProductActive(stock.getProduct()))
                .orElse(null);
        if (existingStock == null) {
            return null;
        }

        Long newProductId = stockDTO.getProductId();
        if (newProductId != null && !existingStock.getProduct().getId().equals(newProductId)) {
            Product newProduct = productRepository.findById(newProductId)
                    .filter(this::isProductActive)
                    .orElse(null);
            if (newProduct == null) {
                return null;
            }
            boolean productAlreadyLinked = stockRepository.findByProductId(newProduct.getId())
                    .filter(stock -> !stock.getId().equals(id))
                    .isPresent();
            if (productAlreadyLinked) {
                return null;
            }
            existingStock.setProduct(newProduct);
        }

        existingStock.setQuantity(stockDTO.getQuantity());
        existingStock.setLocation(stockDTO.getLocation());
        return stockRepository.save(existingStock);
    }

    @Transactional
    public boolean deleteStock(Long id) {
        return stockRepository.findById(id)
                .map(stock -> {
                    stockRepository.delete(stock);
                    return true;
                })
                .orElse(false);
    }

    private boolean isProductActive(Product product) {
        return product != null && Boolean.TRUE.equals(product.getState());
    }
}
