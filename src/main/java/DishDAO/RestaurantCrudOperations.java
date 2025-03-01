package DishDAO;

import DataSource.DataSource;
import entity.*;
import mapper.UnitMapper;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RestaurantCrudOperations {
    private DataSource dataSource;
    private UnitMapper unitMapper;

    public RestaurantCrudOperations(DataSource dataSource, UnitMapper unitMapper) {
        this.dataSource = dataSource;
        this.unitMapper = unitMapper;
    }

    public RestaurantCrudOperations() {
        this.dataSource = new DataSource();
        this.unitMapper = new UnitMapper();
    }

    public int getDishIdByName(String dishName) {
        String query = "SELECT id_dish FROM dish WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, dishName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_dish");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving dish ID", e);
        }
    }

    public List<Ingredient> filterAndPaginateDish(String dishName, List<Criteria> criterias, int page, int pageSize, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        List<Ingredient> ingredientList = new ArrayList<>();

        int dishId = getDishIdByName(dishName);
        if (dishId == -1) {
            throw new IllegalArgumentException("Dish with name " + dishName + " not found");
        }

        StringBuilder sql = new StringBuilder("SELECT i.id_ingredient, i.name, " +
                "p.id AS price_id, p.amount AS price_amount, p.unit AS price_unit, p.date AS price_date " +
                "FROM dish_ingredient di " +
                "JOIN ingredient i ON di.id_ingredient = i.id_ingredient " +
                "JOIN price p ON i.id_ingredient = p.id_ingredient " +
                "WHERE di.id_dish = ? AND p.date <= ? ");

        for (Criteria crit : criterias) {
            sql.append("AND i.")
                    .append(crit.getField())
                    .append(" ")
                    .append(crit.getOperator())
                    .append(" ? ");
        }

        sql.append("ORDER BY p.date DESC LIMIT ? OFFSET ?");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            ps.setInt(index++, dishId);
            ps.setObject(index++, date);

            for (Criteria crit : criterias) {
                ps.setObject(index++, crit.getValue());
            }

            ps.setInt(index++, pageSize);
            ps.setInt(index, (page - 1) * pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setIdIngredient(rs.getInt("id_ingredient"));
                    ingredient.setName(rs.getString("name"));

                    Price price = new Price(
                            rs.getInt("price_id"),
                            rs.getInt("price_amount"),
                            unitMapper.mapFromResultSet(rs.getString("price_unit")),
                            rs.getDate("price_date").toLocalDate()
                    );

                    ingredient.setPrice(price);
                    ingredientList.add(ingredient);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving ingredients", e);
        }

        return ingredientList;
    }

   public int getUnitPrice(String dishName) {
           String query = "SELECT unit_price FROM dish WHERE name = ?";
           try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
               ps.setString(1, dishName);

               try (ResultSet rs = ps.executeQuery()) {
                   if (rs.next()) {
                       return rs.getInt("unit_price");
                   } else {
                       return -1;
                   }
               }
           } catch (SQLException e) {
               throw new RuntimeException("Error retrieving unit price", e);
           }
       };

    public int getTotalIngredientPriceForDish(int dishId, LocalDate dateFilter) {
        String query = "SELECT i.name, SUM(COALESCE(p.amount, 0) * di.required_quantity) AS total_price " +
        "FROM dish_ingredient di " +
        "JOIN ingredient i ON di.id_ingredient = i.id_ingredient " +
        "LEFT JOIN price p ON i.id_ingredient = p.id_ingredient " +
        "AND p.date = ( SELECT MAX(p2.date)  " +
        "FROM price p2 " +
        "WHERE p2.id_ingredient = i.id_ingredient " +
        "AND p2.date <= ? " +
        ")  " +
        "WHERE di.id_dish = ? " +
        "GROUP BY i.name; ";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setObject(1, dateFilter);
            ps.setInt(2, dishId);

            try (ResultSet rs = ps.executeQuery()) {
                int totalPrice = 0;
                while (rs.next()) {
                    String ingredientName = rs.getString("name");
                    int ingredientPrice = rs.getInt("total_price");
                    System.out.println("Ingrédient: " + ingredientName + ", Coût: " + ingredientPrice + " à la date " + dateFilter);
                    totalPrice += ingredientPrice;
                }
                return totalPrice;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du coût total des ingrédients", e);
        }
    }

    public int getGrossMargin(String dishName, LocalDate dateFilter) {
        int dishId = getDishIdByName(dishName);
        int dishPrices = getTotalIngredientPriceForDish(dishId, dateFilter);
        int dishUnitPrice = getUnitPrice(dishName);
        int dishMargin = dishUnitPrice - dishPrices;

        System.out.println("Prix de vente du plat : " + dishUnitPrice);
        System.out.println("Coût total des ingrédients à la date " + dateFilter + " : " + dishPrices);
        System.out.println("Marge brute calculée : " + dishMargin);

        return dishMargin;
    }

    public List<StockMove> getStockMovesByIngredient(int ingredientId) {
        List<StockMove> stockMoves = new ArrayList<>();
        String sql = "SELECT id_move, move_type, ingredient_quantity, unit, move_date, id_ingredient " +
                "FROM stock_move WHERE id_ingredient = ? ORDER BY move_date ASC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ingredientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StockMove stockMove = new StockMove(
                        rs.getInt("id_move"),
                        MoveType.valueOf(rs.getString("move_type")),
                        rs.getDouble("ingredient_quantity"),
                        Unit.valueOf(rs.getString("unit")),
                        rs.getTimestamp("move_date").toLocalDateTime()
                );
                stockMoves.add(stockMove);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stockMoves;
    };

    public int getIngredientIdByName(String ingredientName) {
        String query = "SELECT id_ingredient FROM ingredient WHERE name = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, ingredientName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_ingredient");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public Dish getDishById(int dishID) {
        String query = "SELECT d.id_dish, d.name, d.unit_price " +
                "FROM dish d WHERE d.id_dish = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, dishID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Dish dish = new Dish(rs.getInt("id_dish"), rs.getString("name"), rs.getInt("unit_price"));

                    // Load ingredients
                    dish.setIngredients(getIngredientsForDish(dishID));

                    return dish;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching dish", e);
        }
        return null;
    }


    public List<DishIngredient> getIngredientsForDish(int dishID) {
        List<DishIngredient> ingredients = new ArrayList<>();
        String query = "SELECT di.id_ingredient, di.required_quantity, di.unit, i.name " +
                "FROM dish_ingredient di " +
                "JOIN ingredient i ON di.id_ingredient = i.id_ingredient " +
                "WHERE di.id_dish = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, dishID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient(
                            rs.getInt("id_ingredient"),
                            rs.getString("name")
                    );
                    DishIngredient dishIngredient = new DishIngredient(
                            ingredient,
                            rs.getDouble("required_quantity"),
                            unitMapper.mapFromResultSet(rs.getString("unit"))
                    );
                    ingredients.add(dishIngredient);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }


}
