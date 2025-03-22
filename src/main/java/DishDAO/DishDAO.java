package DishDAO;

import entity.*;
import mapper.UnitMapper;
import DataSource.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DishDAO {
    private DataSource dataSource;
    private UnitMapper unitMapper;

    public DishDAO(DataSource dataSource, UnitMapper unitMapper) {
        this.dataSource = dataSource;
        this.unitMapper = unitMapper;
    }

    public DishDAO(DataSource dataSource) {
        this.dataSource = dataSource;
        this.unitMapper = new UnitMapper();
    }

    public DishDAO(){
        this.dataSource = new DataSource();
        this.unitMapper = new UnitMapper();
    }

    public Dish findById(int id) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM dish WHERE id_dish = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    Dish dish = new Dish();
                    dish.setIdDish(rs.getInt("id_dish"));
                    dish.setName(rs.getString("name"));
                    return dish;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<DishIngredient> getDishIngredients(int dishId) {
        List<DishIngredient> ingredients = new ArrayList<>();
        String query = "SELECT * FROM dish_ingredient WHERE id_dish = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, dishId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int ingredientId = rs.getInt("id_ingredient");
                double requiredQuantity = rs.getDouble("required_quantity");
                String unitString = rs.getString("unit");
                Unit unit = unitMapper.mapFromResultSet(unitString);

                Ingredient ingredient = getIngredientById(ingredientId);
                ingredients.add(new DishIngredient(ingredient, requiredQuantity, unit));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving dish ingredients", e);
        }

        return ingredients;
    }

    private Ingredient getIngredientById(int ingredientId) {
        String query = "SELECT * FROM ingredient WHERE id_ingredient = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, ingredientId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                int priceAmount = rs.getInt("price");
                Price price = new Price(priceAmount);
                List<StockMove> stockMoves = getStockMovesForIngredient(ingredientId);

                return new Ingredient(ingredientId, name, price, stockMoves);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving ingredient by ID", e);
        }
    }

    private List<StockMove> getStockMovesForIngredient(int ingredientId) {
        List<StockMove> stockMoves = new ArrayList<>();
        String query = "SELECT * FROM stock_move WHERE id_ingredient = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, ingredientId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int stockMoveId = rs.getInt("id_stock_move");
                MoveType moveType = MoveType.valueOf(rs.getString("move_type"));
                double ingredientQuantity = rs.getDouble("ingredient_quantity");
                String unitString = rs.getString("unit");
                Unit unit = unitMapper.mapFromResultSet(unitString);
                LocalDateTime moveDate = rs.getTimestamp("move_date").toLocalDateTime();

                stockMoves.add(new StockMove(stockMoveId, moveType, ingredientQuantity, unit, moveDate));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving stock moves for ingredient", e);
        }

        return stockMoves;
    }

    public String getDishName(int dishId) throws SQLException {
        String sql = "SELECT name FROM dish WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            } else {
                throw new SQLException("Plat avec l'ID " + dishId + " non trouvé.");
            }
        }
    }

    public Map<String, Integer> getAvailableIngredients() throws SQLException {
        Map<String, Integer> availableIngredients = new HashMap<>();
        String sql = "SELECT ingredient_name, quantity FROM stock";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String ingredientName = rs.getString("ingredient_name");
                int quantity = rs.getInt("quantity");
                availableIngredients.put(ingredientName, quantity);
            }
        }
        return availableIngredients;
    }

    public Map<String, Integer> getRequiredIngredients(int dishId) throws SQLException {
        Map<String, Integer> requiredIngredients = new HashMap<>();
        String sql = "SELECT ingredient_name, quantity FROM dish_ingredients WHERE dish_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, dishId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String ingredientName = rs.getString("ingredient_name");
                    int quantity = rs.getInt("quantity");
                    requiredIngredients.put(ingredientName, quantity);
                }
            }
        }
        return requiredIngredients;
    }
}
