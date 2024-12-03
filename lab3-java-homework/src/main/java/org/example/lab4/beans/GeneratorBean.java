package org.example.lab4.beans;

import com.github.javafaker.Faker;
import lombok.Getter;
import lombok.Setter;
import org.example.lab4.models.Client;
import org.example.lab4.models.Order;
import org.example.lab4.models.Product;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@SessionScoped
@Named
@Getter
@Setter
@Transactional
public class GeneratorBean implements Serializable {

    @PersistenceContext
    private EntityManager em;

    private Integer nrClients = 0;
    private Integer nrOrders = 0;
    private Integer nrProducts = 0;
    private Integer maxX = 100;
    private Integer maxY = 100;

    private final Faker faker = new Faker();

    public void generateData() {
        // Products Generator
        if (nrProducts >0){
            Random random = new Random();

            for (int i = 0; i < nrProducts; i++) {
                String productName = faker.commerce().productName();
                int quantity = random.nextInt(nrProducts) + 1;
                double price = random.nextDouble() * 100 +1;
                price =  Math.round(price *10.0)/10.0;

                Product product = Product.builder()
                        .name(productName)
                        .quantity(quantity)
                        .price(price)
                        .build();

                saveProduct(product);
            }
        }



        if (nrClients > 0 && nrOrders > 0) {
            Random rand = new Random();
            clearData();

            Set<String> usedEmails = new HashSet<>();
            Set<String> usedNames = new HashSet<>();

            for (int i = 0; i < nrClients; i++) {
                LocalTime start = LocalTime.of(rand.nextInt(6) + 8, 0);
                LocalTime end = start.plusHours(rand.nextInt(10) + 1);
                Integer x = rand.nextInt(maxX);
                if (rand.nextBoolean()) {
                    x = -x;
                }

                Integer y = rand.nextInt(maxY);
                if (rand.nextBoolean()) {
                    y = -y;
                }

                String name;
                String email;
                do{
                    name = faker.name().fullName();
                }while(usedNames.contains(name));
                usedNames.add(name);

                do{
                    email = faker.internet().emailAddress();
                } while(usedEmails.contains(email));
                usedEmails.add(email);

                String phone = "+40" + faker.number().digits(9);

                Client client = Client.builder()
                        .name(name)
                        .email(email)
                        .phone(phone)
                        .position_x(x)
                        .position_y(y)
                        .startTime(LocalTime.parse(start.toString()))
                        .endTime(LocalTime.parse(end.toString()))
                        .build();

                saveClient(client);

                int orderCount = rand.nextInt(nrOrders) + 1;
                for (int j = 0; j < orderCount; j++) {
                    generateOrderForClient(client);
                }
            }
        }

    }

    @Transactional
    private void clearData() {
        em.createQuery("DELETE FROM Order o").executeUpdate();
        em.createQuery("DELETE FROM Client c").executeUpdate();
    }

    @Transactional
    private void saveClient(Client client) {
        em.persist(client);
    }

    private List<Product> getAllProducts() {
        return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    private void generateOrderForClient(Client client) {
        List<Product> products = getAllProducts();
        Random rand = new Random();

        Product product = products.get(rand.nextInt(products.size()));
        int quantity = rand.nextInt(20) + 1;
        LocalDate shippingDate = LocalDate.now().plusDays(rand.nextInt(7));

        Order order = Order.builder()
                .clientId(client.getId())
                .productId(product.getId())
                .quantity(quantity)
                .shippingDate(java.sql.Date.valueOf(shippingDate))
                .build();

        saveOrder(order);
    }

    @Transactional
    private void saveOrder(Order order) {
        em.persist(order);
    }

    @Transactional
    private void saveProduct(Product product) {
        em.persist(product);
    }
}
