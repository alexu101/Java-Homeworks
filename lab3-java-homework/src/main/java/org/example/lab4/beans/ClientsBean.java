package org.example.lab4.beans;

import org.example.lab4.models.Client;
import org.example.lab4.repositories.ClientRepository;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.List;

@SessionScoped
@Named
@Transactional
public class ClientsBean implements Serializable {

    private Client selectedClient;

    @Inject
    private ClientRepository clientRepository;  // Injecting the repository

    // Method to retrieve all clients using the repository
    public List<Client> getClients() {
        return clientRepository.findAll();  // Use the repository to fetch all clients
    }

    // Method to create a new client instance
    public void createNewClient() {
        this.selectedClient = new Client(); // Initialize selectedClient to a new Client instance
    }

    // Method to edit an existing client by its ID
    public void editClient(Integer clientId) {
        this.selectedClient = clientRepository.findById(clientId);  // Fetch client to edit
    }

    // Getter and setter for selectedClient
    public Client getSelectedClient() {
        return selectedClient;
    }

    public void setSelectedClient(Client selectedClient) {
        this.selectedClient = selectedClient;
    }

    // Method to save or update the client using the repository
    public void saveClient() {
        // Logic to handle time parsing if needed
        if (selectedClient.getEndTime() != null) {
            try {
                // Convert the string (e.g., "16:00:00") to a valid SQL TIME format
                String endTimeString = String.valueOf(selectedClient.getEndTime());

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                java.util.Date parsedEndTime = sdf.parse(endTimeString);

                java.sql.Time sqlEndTime = new java.sql.Time(parsedEndTime.getTime());
                selectedClient.setEndTime(LocalTime.parse(sqlEndTime.toString())); // Store it as string if needed
            } catch (ParseException e) {
                System.out.println("Invalid time format: " + selectedClient.getEndTime());
            }
        }

        clientRepository.save(selectedClient);  // Use the repository to save or update the client
    }

    // Method to delete the client
    public void deleteClient() {
        clientRepository.delete(selectedClient);  // Use the repository to delete the client
    }

    public void createEditClient() {
        System.out.println("createEditClient");
    }

    public void prepareDeleteClient() {
        System.out.println("deleteClient");
    }
}
