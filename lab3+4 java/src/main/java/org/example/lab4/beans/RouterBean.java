package org.example.lab4.beans;

import lombok.Getter;
import lombok.Setter;
import org.example.lab4.models.Client;
import org.example.lab4.models.Order;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.sql.DataSource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.sql.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SessionScoped
@Named
@Getter
@Setter
public class RouterBean implements Serializable {

    @Resource(name = "jdbc/routingResource")
    private DataSource ds;

    private Map<Date,List<Client>> routes;

    private Date selectedDate=Date.valueOf(LocalDate.now());

    private byte[] img;

    public RouterBean() throws IOException {
       setDefaultImg();
    }

    private void setDefaultImg() throws IOException {
        DefaultStreamedContent content=DefaultStreamedContent.builder()
                .contentType("image/png")
                .stream(()->{
                    try {
                        BufferedImage bufferedImg = new BufferedImage(300, 100, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2 = bufferedImg.createGraphics();
                        g2.drawString("No route found for this date or invalid data for calculate", 0, 10);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImg, "png", os);
                        return new ByteArrayInputStream(os.toByteArray());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }).build();
        InputStream is=content.getStream().get();
        img=new byte[is.available()];
        is.read(img);
    }

    public void getRouteImg() throws SQLException, IOException {
        if(routes==null){

                calculateGreedyRoutes();

        }
        if(!routes.containsKey(selectedDate)){
           setDefaultImg();

        }
        DefaultStreamedContent content= DefaultStreamedContent.builder()
                .contentType("image/png")
                .stream(() -> {
                    try {

                        int imageWidth = 1200;
                        int imageHeight = 1200;
                        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = bufferedImage.createGraphics();

                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(0, 0, imageWidth, imageHeight);
                        g2d.setColor(Color.BLACK);


                       List<Client> route=routes.get(selectedDate);
                        int scaleFactor = 5;

                        Point warehouse = new Point(0, 0);
                        int x = (int) (warehouse.getX() * scaleFactor + (double) imageWidth / 2);
                        int y = (int) (warehouse.getY() * scaleFactor + (double) imageHeight / 2);
                        g2d.setColor(Color.RED);
                        g2d.fillOval(x - 5, y - 5, 10, 10);
                        g2d.drawString("Warehouse", x + 5, y - 5);

                        g2d.setColor(Color.BLUE);

                        for (Client client:route) {

                            Point endPoint = new Point(client.getPosition_x(), client.getPosition_y());
                            int startX = (int) (warehouse.getX() * scaleFactor + (double) imageWidth / 2);
                            int startY = (int) (warehouse.getY() * scaleFactor + (double) imageHeight / 2);
                            int endX = (int) (endPoint.getX() * scaleFactor + (double) imageWidth / 2);
                            int endY = (int) (endPoint.getY() * scaleFactor + (double) imageHeight / 2);
                            g2d.drawLine(startX, startY, endX, endY);

                            g2d.setColor(Color.GREEN);
                            g2d.fillOval(endX - 5, endY - 5, 10, 10);
                            g2d.setColor(Color.BLUE);
                            g2d.drawString(client.getId()+"|"+client.getDeliverTime(), endX + 5, endY - 5);
                            warehouse=new Point(client.getPosition_x(),client.getPosition_y());
                        }

                        g2d.dispose();

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "png", outputStream);

                        return new ByteArrayInputStream(outputStream.toByteArray());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .build();

        InputStream is=content.getStream().get();
        img=new byte[is.available()];
        is.read(img);
    }

    public void calculateGreedyRoutes() throws SQLException, IOException {
        List<Client> remainingClients = getClients();
        Map<Integer,Client> clients = remainingClients.stream().map(client -> Map.entry(client.getId(),client)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<Order> orders = getOrders();
        System.out.println("cool");
        System.out.println(remainingClients.size());

        Map<Date,Set<Client>> clientsPerDay = orders.stream().collect(Collectors.groupingBy(Order::getShippingDate, Collectors.mapping(order -> clients.get(order.getClientId()), Collectors.toSet())));
        System.out.println("wew");
        System.out.println(clientsPerDay);
        this.routes=new HashMap<>();
        for(Map.Entry<Date,Set<Client>> entry : clientsPerDay.entrySet()){

            List<Client> clientsForDay = new ArrayList<>(entry.getValue().stream().toList());

            List<Client> orderedClients = new ArrayList<>();
            Point warehouse = new Point(0, 0);
            Point current = warehouse;
            LocalTime currentTime = LocalTime.of(7, 0);
            while (!clientsForDay.isEmpty()) {
                System.out.println("wow");
                System.out.println(clientsForDay);
                Client nearestClient = findNearestClient(current, clientsForDay, currentTime);
                if(nearestClient==null){
                    break;
                }
                orderedClients.add(nearestClient);
                currentTime = currentTime.plusMinutes((long) calculateDistance(current, new Point(nearestClient.getPosition_x(), nearestClient.getPosition_y())));
                current = new Point(nearestClient.getPosition_x(), nearestClient.getPosition_y());
                clientsForDay.remove(nearestClient);
            }
            this.routes.put(entry.getKey(),orderedClients);

        }
        System.out.println("Routes calculated");
        getRouteImg();
    }

    private Client findNearestClient(Point currentLocation, List<Client> remainingClients, LocalTime currentTime) {
        System.out.println(remainingClients);
        List<Client> feasibleClients = remainingClients.stream()
                .filter(client -> {
                    double distance = calculateDistance(currentLocation, new Point(client.getPosition_x(), client.getPosition_y()));
                    LocalTime arrival = currentTime.plusMinutes((long) (distance ));
                    return arrival.isAfter(LocalTime.parse(client.getStartTime())) && arrival.isBefore(LocalTime.parse(client.getEndTime()));
                })
                .toList();

        if (feasibleClients.isEmpty()) return null;

        Client maxClient = null;
        double maxScore = Double.MAX_VALUE;
        for (Client client : feasibleClients) {
            double score = evaluateClient(currentLocation, client, remainingClients, currentTime);
            if (score < maxScore) {
                maxScore = score;
                maxClient = client;
            }
        }
        if(maxClient != null) {
            maxClient.setDeliverTime(currentTime.plusMinutes((long) calculateDistance(currentLocation, new Point(maxClient.getPosition_x(), maxClient.getPosition_y()))).toString());
        }
        return maxClient;
    }

    private double evaluateClient(Point currentLocation, Client candidateClient, List<Client> remainingClients, LocalTime currentTime) {
        double distanceToCandidate = calculateDistance(currentLocation, new Point(candidateClient.getPosition_x(), candidateClient.getPosition_y()));
        LocalTime candidateArrivalTime = currentTime.plusMinutes((long) (distanceToCandidate ));

        //not good to arrive after the end of the shipping window
        if (candidateArrivalTime.isAfter(LocalTime.parse(candidateClient.getEndTime()))) {
            return Double.MAX_VALUE;
        }

        Point candidateLocation = new Point(candidateClient.getPosition_x(), candidateClient.getPosition_y());

        // Check other clients reachable from candidate location within their time windows
        long feasibleFollowingClients = remainingClients.stream()
                .filter(client -> !client.equals(candidateClient))
                .filter(client -> {
                    double distance = calculateDistance(candidateLocation, new Point(client.getPosition_x(), client.getPosition_y()));
                    LocalTime arrivalTime = candidateArrivalTime.plusMinutes((long) (distance ));
                    return arrivalTime.isAfter(LocalTime.parse(client.getStartTime())) && arrivalTime.isBefore(LocalTime.parse(client.getEndTime()));
                })
                .count();
        int nrRemaining=remainingClients.size()-1;//without the candidate
        return distanceToCandidate+ (nrRemaining-feasibleFollowingClients)*1000;
    }

    private double calculateDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }


    private List<Client> getClients() throws SQLException {

        List<Client> clients = new ArrayList<Client>();
        try (Connection con = ds.getConnection()) {
            String sql = "select * from clients";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                clients.add(
                        Client.builder()
                                .id(resultSet.getInt("client_id"))
                                .name(resultSet.getString("name"))
                                .email(resultSet.getString("email"))
                                .phone(resultSet.getString("phone"))
                                .position_x(resultSet.getInt("position_x"))
                                .position_y(resultSet.getInt("position_y"))
                                .startTime(resultSet.getTime("start_time").toString())
                                .endTime(resultSet.getTime("end_time").toString())
                                .build()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clients;
    }
    public List<Order> getOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        try (Connection con = ds.getConnection()) {
            String sql = "select * from orders";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orders.add(
                        Order.builder()
                                .id(resultSet.getInt("id"))
                                .clientId(resultSet.getInt("client_id"))
                                .productId(resultSet.getInt("product_id"))
                                .quantity(resultSet.getInt("quantity"))
                                .shippingDate(resultSet.getDate("shipping_date"))
                                .build()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }
}
