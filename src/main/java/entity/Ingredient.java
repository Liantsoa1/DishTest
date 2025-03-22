package entity;
import DataSource.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class Ingredient {
    private int idIngredient;
    private String name;
    private Price price;
    private List<StockMove> stockMoves;

    public Ingredient(int idIngredient, String name, Price price, List<StockMove> stockMoves) {
        this.idIngredient = idIngredient;
        this.name = name;
        this.price = price;
    }

    public Ingredient(int idIngredient, String name) {
        this.idIngredient = idIngredient;
        this.name = name;
    }

    public Ingredient() {
    }

    public int getIdIngredient() {
        return idIngredient;
    }

    public String getName() {
        return name;
    }

    public Price getPrice() {
        return price;
    }

    public void setIdIngredient(int idIngredient) {
        this.idIngredient = idIngredient;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public List<StockMove> getStockMoves() {
        return stockMoves;
    }

    public void setStockMoves(List<StockMove> stockMoves) {
        this.stockMoves = stockMoves;
    }

    public double getAvailableQuantity(){

        return 0.0;
    }

    public static double getAvailableQuantity(LocalDateTime date, int ingredientId) {
        DataSource dataSource = new DataSource();
        double availableQuantity = 0.0;

        String query = "SELECT move_type, ingredient_quantity " +
                "FROM stock_move " +
                "WHERE id_ingredient = ? AND move_date <= ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, ingredientId);
            ps.setTimestamp(2, Timestamp.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String moveType = rs.getString("move_type");
                    double ingredientQuantity = rs.getDouble("ingredient_quantity");

                    if ("ENTRY".equals(moveType)) {
                        availableQuantity += ingredientQuantity;
                    } else if ("EXIT".equals(moveType)) {
                        availableQuantity -= ingredientQuantity;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableQuantity;
    }

}
