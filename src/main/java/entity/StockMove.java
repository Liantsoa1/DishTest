package entity;

import java.time.LocalDateTime;

public class StockMove {
    private int id;
    private MoveType moveType;
    private Double ingredientQuantity;
    private Unit unit;
    private LocalDateTime moveDate;

    public StockMove(MoveType moveType, Unit unit, LocalDateTime moveDate) {
        this.id = id;
        this.moveType = moveType;
        this.unit = unit;
        this.moveDate = moveDate;
    }

    public StockMove(int id, MoveType moveType, Double ingredientQuantity, Unit unit, LocalDateTime moveDate) {
        this.id = id;
        this.moveType = moveType;
        this.ingredientQuantity = ingredientQuantity;
        this.unit = unit;
        this.moveDate = moveDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public LocalDateTime getMoveDate() {
        return moveDate;
    }

    public void setMoveDate(LocalDateTime moveDate) {
        this.moveDate = moveDate;
    }

    public Double getIngredientQuantity() {
        return ingredientQuantity;
    }

    public void setIngredientQuantity(Double ingredientQuantity) {
        this.ingredientQuantity = ingredientQuantity;
    }
}
