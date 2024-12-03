package org.example.lab4.repositories;

import org.example.lab4.models.Order;
import org.example.lab4.models.Product;

import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

public class OrderRepository implements Serializable {

    @PersistenceContext
    private EntityManager entityManager;

    // Retrieve all orders
    public List<Order> findAll() {
        return entityManager.createNamedQuery("Order.findAll", Order.class).getResultList();
    }

    // Find an order by ID
    public Order findById(Integer orderId) {
        return entityManager.find(Order.class, orderId);
    }

    // Save or update an order
    @Transactional
    public void save(Order order) {
        if (order.getId() != null && order.getId() != 0) {
            entityManager.merge(order);
        } else {
            entityManager.persist(order);
        }
    }

    // Delete an order
    @Transactional
    public void delete(Order order) {
        entityManager.remove(entityManager.contains(order) ? order : entityManager.merge(order));
    }

    // Check the quantity of a product
    public int checkProductQuantity(Integer productId) {
        Product product = entityManager.find(Product.class, productId);
        return product != null ? product.getQuantity() : 0;
    }

    // Update the product quantity after an order
    @Transactional
    public void updateProductQuantity(Integer productId, int quantityOrdered) {
        Product product = entityManager.find(Product.class, productId);
        if (product != null) {
            product.setQuantity(product.getQuantity() - quantityOrdered);
            entityManager.merge(product);
        }
    }
}
