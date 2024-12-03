package org.example.lab4.beans;

import org.example.lab4.models.Client;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SessionScoped
@Named
public class ClientsBean implements Serializable {
    private Client selectedClient;
    @Resource(name = "jdbc/routingResource")
    private DataSource ds;

    public List<Client> getClients() throws SQLException {

        List<Client> clients = new ArrayList<>();
        try (Connection con = ds.getConnection()) {
            String sql = "SELECT * FROM clients";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                clients.add(
                        Client.builder()
                                .id(resultSet.getInt("client_id")) // Updated
                                .name(resultSet.getString("name"))
                                .email(resultSet.getString("email")) // Updated
                                .phone(resultSet.getString("phone")) // Updated
                                .position_x(resultSet.getInt("position_x")) // Updated
                                .position_y(resultSet.getInt("position_y")) // Updated
                                .startTime(resultSet.getTime("start_time").toString()) // Updated
                                .endTime(resultSet.getTime("end_time").toString()) // Updated
                                .build()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clients;
    }

    public void createNewClient() {
        this.selectedClient = Client.builder().build();
    }

    public void createEditClient() {
        System.out.println("createEditClient");
    }

    public Client getSelectedClient() {
        return selectedClient;
    }

    public void setSelectedClient(Client selectedClient) {
        this.selectedClient = selectedClient;
    }

    public void updateClient() {
        try (Connection con = ds.getConnection()) {
            String sql = "UPDATE clients SET name=?, email=?, phone=?, position_x=?, position_y=?, start_time=?, end_time=? WHERE client_id=?"; // Updated
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, selectedClient.getName());
            preparedStatement.setString(2, selectedClient.getEmail()); // Adjusted if the Client model uses the same field names
            preparedStatement.setString(3, selectedClient.getPhone());
            preparedStatement.setInt(4, selectedClient.getPosition_x());
            preparedStatement.setInt(5, selectedClient.getPosition_y());
            preparedStatement.setTime(6, Time.valueOf(selectedClient.getStartTime()));
            preparedStatement.setTime(7, Time.valueOf(selectedClient.getEndTime()));
            preparedStatement.setInt(8, selectedClient.getId()); // Updated
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveClient() {
        if (selectedClient.getId() != null && selectedClient.getId() != 0) {
            updateClient();
            return;
        }
        try (Connection con = ds.getConnection()) {
            String sql = "INSERT INTO clients(name, email, phone, position_x, position_y, start_time, end_time) VALUES(?,?,?,?,?,?,?)"; // Updated
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, selectedClient.getName());
            preparedStatement.setString(2, selectedClient.getEmail()); // Adjusted if the Client model uses the same field names
            preparedStatement.setString(3, selectedClient.getPhone());
            preparedStatement.setInt(4, selectedClient.getPosition_x());
            preparedStatement.setInt(5, selectedClient.getPosition_y());
            preparedStatement.setTime(6, Time.valueOf(selectedClient.getStartTime()));
            preparedStatement.setTime(7, Time.valueOf(selectedClient.getEndTime()));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
