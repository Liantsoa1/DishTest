import DataSource.DataSource;
import DishDAO.Criteria;
import DishDAO.RestaurantCrudOperations;
import entity.Dish;
import entity.Ingredient;
import mapper.UnitMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DishTest {

    private DataSource dataSource;
    private UnitMapper unitMapper;
    private RestaurantCrudOperations restaurantCrudOperations;

    @Test
    public void testFilterAndPaginateDishWithDate() {
        dataSource = new DataSource();
        unitMapper = new UnitMapper();
        restaurantCrudOperations = new RestaurantCrudOperations(dataSource, unitMapper);

        String dishName = "Hot Dog";
        List<Criteria> criterias = List.of(new Criteria("name", "=", "Pain"));
        int page = 1;
        int pageSize = 2;

        LocalDate filterDate = LocalDate.of(2024, 2, 3);

        List<Ingredient> ingredients = restaurantCrudOperations.filterAndPaginateDish(dishName, criterias, page, pageSize, filterDate);

        assertNotNull(ingredients);
        assertEquals(1, ingredients.size());
        assertEquals("Pain", ingredients.get(0).getName());
        assertEquals(1000.0, ingredients.get(0).getPrice().getAmount());
    }

    @Test
    public void testFilterAndPaginateDishWithCurrentDate() {
        dataSource = new DataSource();
        unitMapper = new UnitMapper();
        restaurantCrudOperations = new RestaurantCrudOperations(dataSource, unitMapper);

        String dishName = "Hot Dog";
        List<Criteria> criterias = List.of(new Criteria("name", "=", "Saucisse"));
        int page = 1;
        int pageSize = 2;

        LocalDate filterDate = LocalDate.now();

        List<Ingredient> ingredients = restaurantCrudOperations.filterAndPaginateDish(dishName, criterias, page, pageSize, filterDate);

        assertNotNull(ingredients);
        assertEquals("Saucisse", ingredients.get(0).getName());
        assertEquals(30.0, ingredients.get(0).getPrice().getAmount());
    }

    @Test
    public void testGrossMarginWithClosestDate() {
        dataSource = new DataSource();
        unitMapper = new UnitMapper();
        restaurantCrudOperations = new RestaurantCrudOperations(dataSource, unitMapper);
        String dishName = "Hot Dog";

        LocalDate testDate = LocalDate.of(2024, 2, 5);

        int margin = restaurantCrudOperations.getGrossMargin(dishName, testDate);

        int expectedGrossMargin = 8500;
        Assert.assertEquals(expectedGrossMargin, margin);
    }

    @Test
    public void testAvailableQuantityAfterMovements() {
        dataSource = new DataSource();
        unitMapper = new UnitMapper();
        restaurantCrudOperations = new RestaurantCrudOperations(dataSource, unitMapper);

        LocalDateTime today = LocalDateTime.of(2025, 2, 24, 12, 0);

        int ingredientId = restaurantCrudOperations.getIngredientIdByName("Saucisse");
        double availableQuantity = Ingredient.getAvailableQuantity(today, ingredientId);
        assertEquals(9900.0, availableQuantity, 0.01);

        ingredientId = restaurantCrudOperations.getIngredientIdByName("Pain");
        availableQuantity = Ingredient.getAvailableQuantity(today, ingredientId);
        assertEquals(48.0, availableQuantity, 0.01);

        ingredientId = restaurantCrudOperations.getIngredientIdByName("Huile");
        availableQuantity = Ingredient.getAvailableQuantity(today, ingredientId);
        assertEquals(19.65, availableQuantity, 0.01);

        ingredientId = restaurantCrudOperations.getIngredientIdByName("Oeuf");
        availableQuantity = Ingredient.getAvailableQuantity(today, ingredientId);
        assertEquals(99.0, availableQuantity, 0.01);

        ingredientId = restaurantCrudOperations.getIngredientIdByName("Sel");
        availableQuantity = Ingredient.getAvailableQuantity(today, ingredientId);
        assertEquals(450.0, availableQuantity, 0.01);

        ingredientId = restaurantCrudOperations.getIngredientIdByName("Riz");
        availableQuantity = Ingredient.getAvailableQuantity(today, ingredientId);
        assertEquals(800.0, availableQuantity, 0.01);
    }

    @Test
    public void testDishAvailableQuantity() {
        dataSource = new DataSource();
        unitMapper = new UnitMapper();
        restaurantCrudOperations = new RestaurantCrudOperations(dataSource, unitMapper);

        LocalDateTime today = LocalDateTime.of(2025, 2, 24, 12, 0);

        int dishID = restaurantCrudOperations.getDishIdByName("Hot Dog");
        Dish dish = restaurantCrudOperations.getDishById(dishID);

        int availableDishQuantity = dish.getDishAvailableQuantity(today);
        assertEquals(48, availableDishQuantity);
    }

}
