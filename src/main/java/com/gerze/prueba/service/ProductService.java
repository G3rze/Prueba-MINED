package com.gerze.prueba.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gerze.prueba.controller.DTOs.ProductDTO;
import com.gerze.prueba.model.Product;
import com.gerze.prueba.model.Stock;
import com.gerze.prueba.repository.ProductRepository;
import com.gerze.prueba.repository.StockRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .filter(product -> Boolean.TRUE.equals(product.getState()))
                .orElse(null);
    }

    public Product createProduct(ProductDTO productDTO) {
        Product product = productDTO.toEntity();
        Product savedProduct = productRepository.save(product);

        Stock stock = new Stock();
        stock.setProduct(savedProduct);
        stock.setQuantity(1);
        stockRepository.save(stock);

        return savedProduct;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAllByStateTrue();
    }

    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .filter(product -> Boolean.TRUE.equals(product.getState()))
                .orElse(null);
        if (existingProduct != null) {
            existingProduct.setName(productDTO.getName());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setPrice(productDTO.getPrice());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    public void deleteProduct(Long id) {
        productRepository.findById(id)
                .filter(product -> Boolean.TRUE.equals(product.getState()))
                .ifPresent(product -> {
                    product.setState(false);
                    productRepository.save(product);

                    stockRepository.findByProductId(id)
                            .ifPresent(stock -> {
                                stock.setQuantity(0);
                                stockRepository.save(stock);
                            });
                });
    }
}
