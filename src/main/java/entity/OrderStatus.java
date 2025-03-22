package entity;

import java.time.LocalDateTime;

public class OrderStatus {
    private int id;
    private Order order;
    private Status status;
    private LocalDateTime changeDate;

    public OrderStatus(int id, Order order, Status status, LocalDateTime changeDate) {
        this.id = id;
        this.order = order;
        this.status = status;
        this.changeDate = changeDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }
}
