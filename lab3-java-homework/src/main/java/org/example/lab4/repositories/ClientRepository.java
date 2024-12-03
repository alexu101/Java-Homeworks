package org.example.lab4.repositories;

import org.example.lab4.models.Client;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

public class ClientRepository implements Serializable {

    @PersistenceContext
    private EntityManager entityManager;

    // Method to retrieve all clients using JPA
    public List<Client> findAll() {
        return entityManager.createNamedQuery("Client.findAll", Client.class).getResultList();
    }

    // Method to find a client by ID
    public Client findById(Integer clientId) {
        return entityManager.find(Client.class, clientId);
    }

    // Method to save or update a client
    public void save(Client client) {
        if (client.getId() != null && client.getId() != 0) {
            entityManager.merge(client);  // Update the client
        } else {
            entityManager.persist(client);  // Create a new client
        }
    }

    // Method to delete a client
    public void delete(Client client) {
        entityManager.remove(entityManager.contains(client) ? client : entityManager.merge(client));
    }
}
