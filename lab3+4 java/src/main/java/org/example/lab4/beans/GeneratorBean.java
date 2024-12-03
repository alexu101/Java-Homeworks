package org.example.lab4.beans;

import lombok.Getter;
import lombok.Setter;
import org.example.lab4.models.Client;
import org.example.lab4.models.Order;
import org.example.lab4.models.Product;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SessionScoped
@Named
@Getter
@Setter
public class GeneratorBean implements Serializable {
    private Integer nrClients=0;
    private Integer nrOrders=0;
    private Integer maxX=100;
    private Integer maxY=100;
    @Resource(name = "jdbc/routingResource")
    private DataSource ds;

    public void generateData() {
        if(nrClients==0 || nrOrders==0){
            return;
        }
        Random rand = new Random();
        clearData();
        for (int i = 0; i < nrClients; i++) {
            LocalTime start=LocalTime.of(rand.nextInt(6) + 8, 0);
            Time startWindow = Time.valueOf(start);
            Time endWindow = Time.valueOf(start.plusHours(rand.nextInt(10) + 1));
            Integer x=rand.nextInt(maxX);
            if(rand.nextBoolean()){
                x=-x;
            }

            Integer y=rand.nextInt(maxY);
            if(rand.nextBoolean()){
                y=-y;
            }
            Client client = Client.builder()
                    .name("Name_Client_" + i)
                    .email("name.client" + i + "@gmail.com")
                    .phone("0700000000")
                    .position_x(x)
                    .position_y(y)
                    .startTime(startWindow.toString())
                    .endTime(endWindow.toString())
                    .build();
            saveClient(client);

             int orderCount = rand.nextInt(nrOrders) + 1;
            for (int j = 0; j < orderCount; j++) {
                generateOrderForClient(client);
            }
        }
    }
    private void clearData() {
        String sql = "delete from orders";
        try (Connection con = ds.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql2 = "delete from clients";
        try (Connection con = ds.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(sql2);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
        private void saveClient(Client client){
        String sql = "insert into clients(name, email, phone, position_x, position_y, start_time, end_time) values(?,?,?,?,?,?,?)";
        try (Connection con = ds.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getEmail());
            preparedStatement.setString(3, client.getPhone());
            preparedStatement.setInt(4, client.getPosition_x());
            preparedStatement.setInt(5, client.getPosition_y());
            preparedStatement.setTime(6, Time.valueOf(client.getStartTime()));
            preparedStatement.setTime(7, Time.valueOf(client.getEndTime()));
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                client.setId(generatedKeys.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private List<Product> getAllProducts(){
        List<Product> products=new ArrayList<>();
        try (Connection con = ds.getConnection()) {
            String sql = "select * from products";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                products.add(
                        Product.builder()
                                .id(resultSet.getInt("id"))
                                .name(resultSet.getString("name"))
                                .price(resultSet.getDouble("price"))
                                .quantity(resultSet.getInt("quantity"))
                                .build()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    private void generateOrderForClient(Client client) {
        List<Product> products = getAllProducts();
        Random rand = new Random();

        Product product = products.get(rand.nextInt(products.size()));
        int quantity = rand.nextInt(20) + 1;
        LocalDate shippingDate = LocalDate.now().plusDays(rand.nextInt(7));

        Order order =Order.builder()
                .clientId(client.getId())
                .productId(product.getId())

                .build();
        order.setClientId(client.getId());
        order.setProductId(product.getId());
        order.setQuantity(quantity);
        order.setShippingDate(Date.valueOf(shippingDate));
        saveOrder(order);
    }
    private void saveOrder(Order order){
        String sql="insert into orders(client_id, product_id, quantity, shipping_date) values(?,?,?,?)";
        try(Connection con=ds.getConnection()){
            PreparedStatement preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1, order.getClientId());
            preparedStatement.setInt(2, order.getProductId());
            preparedStatement.setInt(3, order.getQuantity());
            preparedStatement.setDate(4, order.getShippingDate());
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);

        }
    }

}
