package org.example.lab4.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Builder
@Getter
@Setter
@Entity
@NamedQuery(name = "Client.findAll", query = "SELECT c FROM Client c")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String email;
    private String phone;
    private Integer position_x;
    private Integer position_y;
    private String startTime;
    private String endTime;
    private String deliverTime;

}
