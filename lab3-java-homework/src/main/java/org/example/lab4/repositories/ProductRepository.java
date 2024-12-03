package org.example.lab4.repositories;

import org.example.lab4.models.Product;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

public class ProductRepository implements Serializable {

    @PersistenceContext
    private EntityManager entityManager;

    // Method to retrieve all products
    public List<Product> findAll() {
        return entityManager.createNamedQuery("Product.findAll", Product.class).getResultList();
    }

    // Method to find a product by ID
    public Product findById(Integer productId) {
        return entityManager.find(Product.class, productId);
    }

    // Method to save or update a product
    public void save(Product product) {
        if  (product.getId() != 0) {
            entityManager.merge(product);  // Update existing product
        } else {
            entityManager.persist(product);  // Save new product
        }
    }

    // Method to delete a product
    public void delete(Product product) {
        entityManager.remove(entityManager.contains(product) ? product : entityManager.merge(product));
    }
}
