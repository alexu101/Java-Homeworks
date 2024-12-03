package org.example.lab4.beans;

import lombok.Getter;
import lombok.Setter;
import org.example.lab4.models.Product;
import org.example.lab4.repositories.ProductRepository;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@SessionScoped
@Named
@Transactional
public class ProductsBean implements Serializable {

    @Setter
    @Getter
    private Product selectedProduct;

    @Inject
    private ProductRepository productRepository;  // Injecting the repository

    // Method to retrieve all products using the repository
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    // Method to create a new product instance
    public void createNewProduct() {
        this.selectedProduct = new Product();  // Initialize selectedProduct to a new Product instance
    }

    // Method to edit an existing product by its ID
    public void editProduct(Integer productId) {
        this.selectedProduct = productRepository.findById(productId);  // Fetch product to edit
    }

    // Method to save or update the product using the repository
    public void saveProduct() {
        productRepository.save(selectedProduct);
    }

    // Method to delete the selected product
    public void deleteProduct() {
        productRepository.delete(selectedProduct);
    }

    public void createEditProduct() {
        System.out.println("createEditProduct");
    }

    public void prepareDeleteProduct() {
        System.out.println("deleteClient");
    }
}
