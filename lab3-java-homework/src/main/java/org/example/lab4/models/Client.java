package org.example.lab4.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.ejb.Local;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@Entity
@Table(name = "Clients")
@NamedQuery(name = "Client.findAll", query = "SELECT c FROM Client c")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Integer id;

    @NotNull
    private String name;

    private String email;
    private String phone;
    private Integer position_x;
    private Integer position_y;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Transient
    private String deliverTime;

    public Client(Integer id, String name, String email, String phone, Integer position_x, Integer position_y,
                  LocalTime startTime, LocalTime endTime, String deliverTime) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.position_x = position_x;
        this.position_y = position_y;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deliverTime = deliverTime;
    }

    public Client() {

    }
}