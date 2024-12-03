package org.example.lab4.beans;

import lombok.Getter;
import lombok.Setter;
import org.example.lab4.models.Order;
import org.example.lab4.repositories.OrderRepository;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@SessionScoped
@Named
public class OrdersBean implements Serializable {

    @Getter
    @Setter
    private Order selectedOrder;

    @Inject
    private OrderRepository orderRepository;

    // Retrieve all orders
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    // Create a new order instance
    public void createNewOrder() {
        this.selectedOrder = new Order();
    }

    // Create/edit order logic
    public void createEditOrder() {
        System.out.println("createEditOrder");
    }

    // Save or update an order
    @Transactional
    public void saveOrderFunc() {
        if (selectedOrder.getId() != null && selectedOrder.getId() != 0) {
            // Existing order - update
            orderRepository.save(selectedOrder);
        } else {
            // New order - check stock
            int productQuantity = orderRepository.checkProductQuantity(selectedOrder.getProductId());
            if (productQuantity < selectedOrder.getQuantity()) {
                System.out.println("Not enough quantity");
                return;
            }
            // Save new order and update product quantity
            orderRepository.save(selectedOrder);
            orderRepository.updateProductQuantity(selectedOrder.getProductId(), selectedOrder.getQuantity());
        }
    }

    // Delete the selected order
    @Transactional
    public void deleteOrder() {
        if (selectedOrder != null) {
            orderRepository.delete(selectedOrder);
        }
    }

    public void prepareDeleteOrder() {
        System.out.println("deleteClient");
    }
}
