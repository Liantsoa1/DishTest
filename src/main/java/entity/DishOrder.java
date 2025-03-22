package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;

public class DishOrder {
    private int id;
    private int dishId;
    private int quantity;
    private List<DishOrderStatus> statusHistory;

    public DishOrder(int dishId, int quantity, List<DishOrderStatus> statusHistory) {
        this.dishId = dishId;
        this.quantity = quantity;
        this.statusHistory = statusHistory;
    }

    public DishOrder(int id, int dishId, int quantity, List<DishOrderStatus> statusHistory) {
        this.id = id;
        this.dishId = dishId;
        this.quantity = quantity;
        this.statusHistory = statusHistory;
    }

    public DishOrder(int id, int dishId, int quantity) {
        this.id = id;
        this.dishId = dishId;
        this.quantity = quantity;;
    }

    public DishOrderStatus getActualStatus() {
        if (statusHistory != null && !statusHistory.isEmpty()) {
            return statusHistory.get(statusHistory.size() - 1);
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<DishOrderStatus> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<DishOrderStatus> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public void addStatus(Status status) {
        this.statusHistory.add(new DishOrderStatus(status, LocalDateTime.now()));
    }

    public boolean hasSufficientIngredients(Map<String, Integer> availableIngredients, Map<String, Integer> requiredIngredients) {
        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
            String ingredient = entry.getKey();
            int requiredQuantity = entry.getValue() * quantity;

            if (availableIngredients.getOrDefault(ingredient, 0) < requiredQuantity) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Integer> getMissingIngredients(Map<String, Integer> availableIngredients, Map<String, Integer> requiredIngredients) {
        Map<String, Integer> missingIngredients = new HashMap<>();

        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
            String ingredient = entry.getKey();
            int requiredQuantity = entry.getValue() * quantity;
            int availableQuantity = availableIngredients.getOrDefault(ingredient, 0);

            if (availableQuantity < requiredQuantity) {
                missingIngredients.put(ingredient, requiredQuantity - availableQuantity);
            }
        }
        return missingIngredients;
    }
}
