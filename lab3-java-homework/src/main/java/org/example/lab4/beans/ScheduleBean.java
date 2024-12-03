package org.example.lab4.beans;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.example.lab4.models.Client;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Named
@RequestScoped
public class ScheduleBean implements Serializable {

    @Inject
    private RouterBean routerBean;

    @PersistenceContext
    private EntityManager entityManager; // Inject EntityManager for JPA operations

    private ScheduleModel scheduleModel;
    private ScheduleEvent<?> selectedEvent;

    @PostConstruct
    public void init() {
        this.scheduleModel = new DefaultScheduleModel();
        try {
            routerBean.calculateGreedyRoutes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        populateScheduleModel();
    }

    private void populateScheduleModel() {
        Map<java.util.Date, List<Client>> routing = routerBean.getRoutes();
        for (Map.Entry<java.util.Date, List<Client>> entry : routing.entrySet()) {

            List<Client> clients = entry.getValue();
            for (Client client : clients) {
                LocalDate date = entry.getKey().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalTime deliverTime = LocalTime.parse(client.getDeliverTime());
                LocalDateTime startDate = LocalDateTime.of(date, deliverTime);
                LocalDateTime endDate = LocalDateTime.of(date, deliverTime);

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

    // JPA-based method to fetch clients
    public List<Client> getClients() {
        TypedQuery<Client> query = entityManager.createQuery("SELECT c FROM Client c", Client.class);
        return query.getResultList();
    }
}