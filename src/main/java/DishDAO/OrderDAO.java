package DishDAO;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import entity.DishOrder;
import entity.DishOrderStatus;
import entity.Order;
import entity.Status;

public class OrderDAO {
    private DataSource dataSource;

    public OrderDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<DishOrder> getDishOrdersByOrderId(int orderId) {
        String query = "SELECT dish_id, quantity FROM dish_order WHERE order_id = ?";
        List<DishOrder> dishOrders = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int dishId = rs.getInt("dish_id");
                    int quantity = rs.getInt("quantity");

                    List<DishOrderStatus> statusHistory = getDishOrderStatusHistory(dishId, orderId);

                    DishOrder dishOrder = new DishOrder(dishId, quantity, statusHistory);
                    dishOrders.add(dishOrder);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dishOrders;
    }

    private List<DishOrderStatus> getDishOrderStatusHistory(int dishId, int orderId) {
        String query = "SELECT status, change_date FROM dish_order_status WHERE dish_order_id = ? ORDER BY change_date";
        List<DishOrderStatus> statusHistory = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, dishId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Status status = Status.valueOf(rs.getString("status"));
                    LocalDateTime changeDate = rs.getTimestamp("change_date").toLocalDateTime();

                    DishOrderStatus dishOrderStatus = new DishOrderStatus(0, null, status, changeDate);
                    statusHistory.add(dishOrderStatus);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return statusHistory;
    }

    public void saveAll(List<Order> orders) {
        for (Order order : orders) {
            for (DishOrder dishOrder : order.getDishOrders()) {
                saveDishOrder(dishOrder);
            }
            saveOrder(order);
        }
    }

    private void saveDishOrder(DishOrder dishOrder) {
        String query = "INSERT INTO dish_order (dish_id, quantity, order_id) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, dishOrder.getDishId());
            statement.setInt(2, dishOrder.getQuantity());
            statement.setInt(3, dishOrder.getOrded());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveOrder(Order order) {
        String query = "INSERT INTO `order` (reference, creation_date_time) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, order.getReference());
            statement.setTimestamp(2, Timestamp.from(order.getCreationDateTime()));

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long orderId = generatedKeys.getLong(1);
                    order(orderId);
                    for (DishOrder dishOrder : order.getDishOrders()) {
                        dishOrder.setOrderId(orderId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Order findById(int id) {
        String query = "SELECT * FROM \"order\" WHERE id = ?";
        Order order = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String reference = resultSet.getString("reference");
                Instant creationDateTime = resultSet.getTimestamp("creation_date_time").toInstant();

                List<DishOrder> dishOrders = findDishOrdersByOrderId(id);

                order = new Order(id, reference, creationDateTime, dishOrders);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }


}
