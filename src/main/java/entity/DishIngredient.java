package entity;

public class DishIngredient {
    private Ingredient ingredient;
    private double requiredQuantity;
    private Unit unit;

    public DishIngredient(Ingredient ingredient, double requiredQuantity, Unit unit) {
        this.ingredient = ingredient;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public Unit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return ingredient.getName() + " - " + requiredQuantity + " " + unit;
    }
}

