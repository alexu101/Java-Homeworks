package org.example.lab4.beans;

import org.example.lab4.models.Client;
import org.example.lab4.models.Order;
import org.example.lab4.models.Product;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SessionScoped
@Named
public class OrdersBean implements Serializable {


    private Order selectedOrder;
    @Resource(name = "jdbc/routingResource")
    private DataSource ds;

    public List<Order> getOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        try (Connection con = ds.getConnection()) {
            String sql = "select * from orders";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orders.add(
                        Order.builder()
                                .id(resultSet.getInt("id"))
                                .clientId(resultSet.getInt("client_id"))
                                .productId(resultSet.getInt("product_id"))
                                .quantity(resultSet.getInt("quantity"))
                                .shippingDate(resultSet.getDate("shipping_date"))
                                .build()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    public void createNewOrder() {
        this.selectedOrder = Order.builder().build();
    }

    public void createEditOrder() {
        System.out.println("createEditOrder");
    }

    public Order getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(Order selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    public void updateOrder() {
        try (Connection con = ds.getConnection()) {
            String sql = "update orders set client_id=?, product_id=?, quantity=?, shipping_date=? where id=?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, selectedOrder.getClientId());
            preparedStatement.setInt(2, selectedOrder.getProductId());
            preparedStatement.setInt(3, selectedOrder.getQuantity());
            preparedStatement.setDate(4, new java.sql.Date(selectedOrder.getShippingDate().getTime()));
            preparedStatement.setInt(5, selectedOrder.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveOrderFunc() {
        if (selectedOrder.getId() != null && selectedOrder.getId() != 0) {
            updateOrder();
            return;
        }
        try (Connection con = ds.getConnection()) {
            String sql = "select quantity from products where id=?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, selectedOrder.getProductId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int quantity = resultSet.getInt("quantity");
                if (quantity < selectedOrder.getQuantity()) {
                    System.out.println("Not enough quantity");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try (Connection con = ds.getConnection()) {
            String sql = "insert into orders(client_id, product_id, quantity, shipping_date) values(?,?,?,?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, selectedOrder.getClientId());
            preparedStatement.setInt(2, selectedOrder.getProductId());
            preparedStatement.setInt(3, selectedOrder.getQuantity());
            preparedStatement.setDate(4, new java.sql.Date(selectedOrder.getShippingDate().getTime()));

            preparedStatement.executeUpdate();

            sql = "update products set quantity=quantity-? where id=?";
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, selectedOrder.getQuantity());
            preparedStatement.setInt(2, selectedOrder.getProductId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
