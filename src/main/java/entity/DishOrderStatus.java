package entity;

import java.time.LocalDateTime;

public class DishOrderStatus {
    private int id;
    private DishOrder dishOrder;
    private Status status;
    private LocalDateTime changeDate;

    public DishOrderStatus(int id, DishOrder dishOrder, Status status, LocalDateTime changeDate) {
        this.id = id;
        this.dishOrder = dishOrder;
        this.status = status;
        this.changeDate = changeDate;
    }

    public DishOrderStatus(int id, Status status, LocalDateTime changeDate) {
        this.id = id;
        this.status = status;
        this.changeDate = changeDate;
    }

    public DishOrderStatus(Status status, LocalDateTime changeDate) {
        this.status = status;
        this.changeDate = changeDate;
    }

    public DishOrderStatus() {
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DishOrder getDishOrder() {
        return dishOrder;
    }

    public void setDishOrder(DishOrder dishOrder) {
        this.dishOrder = dishOrder;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.changeDate = LocalDateTime.now();  // Met à jour la date du changement de statut
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }
}
