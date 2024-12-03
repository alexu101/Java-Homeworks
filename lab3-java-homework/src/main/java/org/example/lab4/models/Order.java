package org.example.lab4.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Builder
@Getter
@Setter
@Entity
@Table(name = "Orders")
@NamedQuery(name = "Order.findAll", query = "SELECT o FROM Order o")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "product_id")
    private Integer productId;

    private int quantity;

    @Temporal(TemporalType.DATE)
    @Column(name = "shipping_date")
    private Date shippingDate;

    public Order() {
    }

    public Order(Integer id, Integer clientId, Integer productId, int quantity, Date shippingDate) {
        this.id = id;
        this.clientId = clientId;
        this.productId = productId;
        this.quantity = quantity;
        this.shippingDate = shippingDate;
    }
}
