package org.example.lab4.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.sql.Date;

@Builder
@Getter
@Setter
public class Order {
    private Integer id;
    private Integer clientId;
    private Integer productId;
    private int quantity;
    private Date shippingDate;

}
