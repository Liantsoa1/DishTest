import DishDAO.RestaurantCrudOperations;
import StaticTest.StaticDataSource;
import entity.Dish;
import entity.StockMove;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DishStaticTest {
    @Test
    public void testGetIngredientCost() {
        Dish hotDog = StaticDataSource.getPlatsTest();
        int expectedCost = 5500;
        Assert.assertEquals(expectedCost, hotDog.getIngredientCost());
    }

    @Test
    public void testDishGrossMargin(){
        Dish hotDog = StaticDataSource.getPlatsTest();
        int expectedGrossMargin = 9500;
        Assert.assertEquals(expectedGrossMargin, hotDog.getGrossMargin());
    }

    public static void main(String[] args) {
        RestaurantCrudOperations stockMoveDAO = new RestaurantCrudOperations();
        List<StockMove> moves = stockMoveDAO.getStockMovesByIngredient(1);

        for (StockMove move : moves) {
            System.out.println("Mouvement: " + move.getMoveType() +
                    ", Quantité: " + move.getIngredientQuantity() +
                    " " + move.getUnit() +
                    ", Date: " + move.getMoveDate());
        }
    }
}
