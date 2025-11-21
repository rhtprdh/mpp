package com.soms.product_service.service;

import org.springframework.stereotype.Service;

import com.soms.product_service.model.Product;
import com.soms.product_service.repo.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product create(Product p) { return repo.save(p); }

    public List<Product> findAll() { return repo.findAll(); }

    public Product findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Product update(Long id, Product newProduct) {
        Product p = findById(id);
        if (p == null) return null;

        p.setName(newProduct.getName());
        p.setPrice(newProduct.getPrice());
        p.setDescription(newProduct.getDescription());
        p.setQuantity(newProduct.getQuantity());

        return repo.save(p);
    }

    public boolean delete(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }
}