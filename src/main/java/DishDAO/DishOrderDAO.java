package DishDAO;

import DataSource.DataSource;
import entity.DishOrder;
import entity.Dish;
import mapper.UnitMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishOrderDAO {
    private DataSource dataSource;
    private UnitMapper unitMapper;

    public DishOrderDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<DishOrder> getAll(int page, int size) {
        List<DishOrder> dishOrders = new ArrayList<>();
        String query = "SELECT id_dish_order, id_dish, quantity FROM dish_order LIMIT ? OFFSET ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, size);
            ps.setInt(2, page * size);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int dishId = rs.getInt("id_dish");
                    Dish dish = new DishDAO(dataSource).findById(dishId); // Fetch the dish for this dish order

                    // DishOrderStatus is not being set here as we don't have that table in this query. We'll deal with it later.
                    DishOrder dishOrder = new DishOrder(
                            dishId,
                            rs.getInt("quantity"),
                            null // No status history in this query yet
                    );
                    dishOrder.setId(rs.getInt("id_dish_order"));
                    dishOrders.add(dishOrder);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishOrders;
    }

    public DishOrder findById(Long id) {
        String query = "SELECT id_dish_order, id_dish, quantity FROM dish_order WHERE id_dish_order = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int dishId = rs.getInt("id_dish");
                    Dish dish = new DishDAO(dataSource).findById(dishId); // Fetch the dish for this dish order

                    // DishOrderStatus is not being set here as we don't have that table in this query yet.
                    DishOrder dishOrder = new DishOrder(
                            dishId,
                            rs.getInt("quantity"),
                            null // No status history yet
                    );
                    dishOrder.setId(rs.getInt("id_dish_order"));
                    return dishOrder;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DishOrder> saveAll(List<DishOrder> entities) {
        List<DishOrder> savedDishOrders = new ArrayList<>();
        String query = "INSERT INTO dish_order (id_dish, quantity) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (DishOrder dishOrder : entities) {
                ps.setInt(1, dishOrder.getDishId()); // Assuming we get the dish ID
                ps.setInt(2, dishOrder.getQuantity());

                ps.addBatch();
            }

            ps.executeBatch();

            // Retrieve generated IDs and set them
            try (ResultSet rs = ps.getGeneratedKeys()) {
                int i = 0;
                while (rs.next()) {
                    entities.get(i).setId(rs.getInt(1));
                    savedDishOrders.add(entities.get(i));
                    i++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return savedDishOrders;
    }
}
