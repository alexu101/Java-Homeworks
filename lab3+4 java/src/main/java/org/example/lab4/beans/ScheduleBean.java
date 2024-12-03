package org.example.lab4.beans;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.example.lab4.models.Client;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class ScheduleBean implements Serializable {
    @Inject
    private RouterBean routerBean;

    private ScheduleModel scheduleModel;
    private ScheduleEvent<?> selectedEvent;

    @PostConstruct
    public void init() {
        this.scheduleModel = new DefaultScheduleModel();
        try {
            routerBean.calculateGreedyRoutes();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        populateScheduleModel();

    }

    private void populateScheduleModel() {
        Map<java.sql.Date, List<Client>> routing = routerBean.getRoutes();
        for (Map.Entry<java.sql.Date, List<Client>> entry : routing.entrySet()) {

            List<Client> clients = entry.getValue();
            for (Client client : clients) {
                LocalDate date = entry.getKey().toLocalDate();
                LocalTime deliverTime = LocalTime.parse(client.getDeliverTime());
                LocalDateTime startDate = LocalDateTime.of(date, deliverTime);
                LocalDateTime endDate = LocalDateTime.of(date, deliverTime);
                //LocalDateTime endDate = LocalDateTime.from(LocalTime.parse(client.getShippingWindowEnd()));

                String title = "Delivery to " + client.getName();
                String description = "Delivery to " + client.getName() +
                        " at (" + client.getPosition_x() +
                        ", " + client.getPosition_y() + ")";

                // Create and add event to the schedule model
                ScheduleEvent<?> event = DefaultScheduleEvent.builder()
                        .title(title)
                        .startDate(startDate)
                        .endDate(endDate)
                        .description(description)
                        .build();

                scheduleModel.addEvent(event);
            }
        }
    }

    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }


    public void onEventSelect(SelectEvent<ScheduleEvent<?>> selectEvent) {
        this.selectedEvent = selectEvent.getObject();
    }


    public ScheduleEvent<?> getSelectedEvent() {
        return selectedEvent;
    }
}
