package entity;

import java.time.LocalDate;

public class Price {
    private int idPrice;
    private int amount;
    private Unit unit;
    private LocalDate date;

    public Price(int idPrice, int amount, Unit unit, LocalDate date) {
        this.idPrice = idPrice;
        this.amount = amount;
        this.unit = unit;
        this.date = date;
    }

    public int getIdPrice() {
        return idPrice;
    }

    public int getAmount() {
        return amount;
    }

    public Unit getUnit() {
        return unit;
    }

    public LocalDate getDate() {
        return date;
    }
}
