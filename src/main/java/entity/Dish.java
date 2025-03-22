package entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dish {
    private int idDish;
    private String name;
    private int unitPrice;
    private List<DishIngredient> ingredients = new ArrayList<DishIngredient>();

    public Dish(String name, int unitPrice, List<DishIngredient> ingredients) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.ingredients = ingredients;
    }

    public Dish(int idDish, String name, int unitPrice) {
        this.idDish = idDish;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public Dish(int idDish, String name, int unitPrice, List<DishIngredient> ingredients) {
        this.idDish = idDish;
        this.name = name;
        this.unitPrice = unitPrice;
        this.ingredients = ingredients;
    }

    public Dish(){

    };

    public int getIdDish() {
        return idDish;
    }

    public void setIdDish(int idDish) {
        this.idDish = idDish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public List<DishIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<DishIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return idDish == dish.idDish && unitPrice == dish.unitPrice && Objects.equals(name, dish.name) && Objects.equals(ingredients, dish.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDish, name, unitPrice, ingredients);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "idDish=" + idDish +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", ingredients=" + ingredients +
                '}';
    }

    public int getIngredientCost() {
        int cost = 0;
        for (DishIngredient dishIngredient : ingredients) {
            Ingredient ingredient = dishIngredient.getIngredient();
            int price = ingredient.getPrice().getAmount();
            cost += price * dishIngredient.getRequiredQuantity();
        }
        return cost;
    }

    public int getTotalIngredientPrice() {
        int totalPrice = 0;
        for (DishIngredient dishIngredient : ingredients) {
            totalPrice += dishIngredient.getIngredient().getPrice().getAmount();
        }
        return totalPrice;
    }

    public int getGrossMargin() {
        int marginValue = unitPrice - getIngredientCost() ;
        return marginValue;
    }

    public int getDishAvailableQuantity(LocalDateTime date) {
        int minQuantity = Integer.MAX_VALUE;

        for (DishIngredient dishIngredient : this.getIngredients()) {
            double availableQuantity = Ingredient.getAvailableQuantity(date, dishIngredient.getIngredient().getIdIngredient());
            double requiredQuantity = dishIngredient.getRequiredQuantity();

            if (requiredQuantity > 0) {
                int possibleDishes = (int) Math.floor(availableQuantity / requiredQuantity);
                minQuantity = Math.min(minQuantity, possibleDishes);
            }
        }

        return minQuantity;
    }


}
