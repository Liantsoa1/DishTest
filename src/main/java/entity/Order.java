package entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DataSource.DataSource;
import DishDAO.DishDAO;

public class Order {
    private int id;
    private String reference;
    private Instant creationDateTime;
    private List<DishOrder> dishOrders;
    private Status status;

    public Order(int id, List<DishOrder> dishOrders) {
        this.id = id;
        this.dishOrders = dishOrders;
        this.status = Status.CREATED;
    }

    public Order(int id, String reference, Instant creationDateTime, List<DishOrder> dishOrders, Status status) {
        this.id = id;
        this.reference = reference;
        this.creationDateTime = creationDateTime;
        this.dishOrders = dishOrders;
        this.status = status;
    }

    public Order(int id, String reference, Instant creationDateTime, List<DishOrder> dishOrders) {
        this.id = id;
        this.reference = reference;
        this.creationDateTime = creationDateTime;
        this.dishOrders = dishOrders;
        this.status = Status.CREATED;
    }

    public Order() {
        this.status = Status.CREATED;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Instant creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }

    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addDishOrder(DishOrder dishOrder) {
        this.dishOrders.add(dishOrder);
    }

    public void removeDishOrder(DishOrder dishOrder) {
        this.dishOrders.remove(dishOrder);
    }

    public void confirmOrder() throws Exception {
        DataSource dataSource = new DataSource();
        DishDAO dishDAO = new DishDAO(dataSource);
        Map<String, Integer> availableIngredients = dishDAO.getAvailableIngredients();

        for (DishOrder dishOrder : dishOrders) {
            Map<String, Integer> requiredIngredients = dishDAO.getRequiredIngredients(dishOrder.getDishId());

            if (!dishOrder.hasSufficientIngredients(availableIngredients, requiredIngredients)) {
                String dishName = dishDAO.getDishName(dishOrder.getDishId());
                Map<String, Integer> missingIngredients = dishOrder.getMissingIngredients(availableIngredients, requiredIngredients);

                StringBuilder errorMessage = new StringBuilder("Insufficient ingredients for the dish: " + dishName + ". ");
                for (Map.Entry<String, Integer> entry : missingIngredients.entrySet()) {
                    String ingredientName = entry.getKey();
                    int missingQuantity = entry.getValue();
                    errorMessage.append(missingQuantity).append(" ").append(ingredientName)
                            .append(missingQuantity > 1 ? "s" : "").append(" are required.");
                }
                throw new Exception(errorMessage.toString());
            }
        }

        this.setStatus(Status.CONFIRMED);

        for (DishOrder dishOrder : dishOrders) {
            dishOrder.addStatus(Status.CONFIRMED);
        }
    }

    public void startPreparation() {
        this.setStatus(Status.IN_PREPARATION);
        for (DishOrder dishOrder : dishOrders) {
            dishOrder.addStatus(Status.IN_PREPARATION);
        }
    }

    public void markAsFinished() {
        for (DishOrder dishOrder : dishOrders) {
            if (!dishOrder.getStatusHistory().contains(Status.FINISHED)) {
                return;
            }
        }
        this.setStatus(Status.FINISHED);
    }

    public void markAsServed() {
        for (DishOrder dishOrder : dishOrders) {
            if (!dishOrder.getStatusHistory().contains("SERVED")) {
                return;
            }
        }
        this.setStatus(Status.SERVED);
    }

    public DishOrderStatus getActualStatus() {
        if (dishOrders == null || dishOrders.isEmpty()) {
            return null;
        }

        DishOrderStatus currentStatus = null;
        for (DishOrder dishOrder : dishOrders) {
            DishOrderStatus actualStatus = dishOrder.getActualStatus();
            if (actualStatus != null) {
                if (currentStatus == null || actualStatus.getChangeDate().isAfter(currentStatus.getChangeDate())) {
                    currentStatus = actualStatus;
                }
            }
        }
        return currentStatus;
    }

    public List<DishOrderStatus> getDishOrdersStatus() {
        List<DishOrderStatus> dishOrderStatuses = new ArrayList<>();
        for (DishOrder dishOrder : dishOrders) {
            DishOrderStatus actualStatus = dishOrder.getActualStatus();
            if (actualStatus != null) {
                dishOrderStatuses.add(actualStatus);
            }
        }
        return dishOrderStatuses;
    }

    public double getTotalAmount(DishDAO dishDAO) {
        double totalAmount = 0.0;

        if (dishOrders != null && !dishOrders.isEmpty()) {
            for (DishOrder dishOrder : dishOrders) {
                Dish dish = dishDAO.findById(dishOrder.getDishId());
                if (dish != null) {
                    double dishPrice = dish.getUnitPrice();
                    totalAmount += dishPrice * dishOrder.getQuantity();
                }
            }
        }

        return totalAmount;
    }

}
