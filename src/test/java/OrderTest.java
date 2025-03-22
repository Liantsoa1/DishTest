import static org.junit.jupiter.api.Assertions.*;

import DataSource.DataSource;
import entity.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import DishDAO.DishDAO;
import DishDAO.OrderDAO;

public class OrderTest {
    @Test
    void testSaveAll() {
        OrderDAO orderDAO = new OrderDAO(DataSource dataSource);
        List<DishOrder> dishOrders = new ArrayList<>();
        dishOrders.add(new DishOrder(5, 1, 2));
        dishOrders.add(new DishOrder(6, 2, 1))

        Order order = new Order(4, "REF123", Instant.now(), dishOrders);

        List<Order> orders = new ArrayList<>();
        orders.add(order);
        orderDAO.saveAll(orders);

        Order savedOrder = orderDAO.findById(order.getId());

        assertEquals("REF123", savedOrder.getReference());
        assertEquals(2, savedOrder.getDishOrders().size());
    }

    @Test
    public void testGetActualStatus() {
        DishOrderStatus status1 = new DishOrderStatus(1, Status.CREATED, LocalDateTime.of(2025, 3, 1, 14, 0, 0));
        DishOrderStatus status2 = new DishOrderStatus(2, Status.SERVED, LocalDateTime.of(2025, 3, 1, 16, 0, 0));

        List<DishOrderStatus> statuses = new ArrayList<>();

        statuses.add(status1);
        statuses.add(status2);

        DishOrder dishOrder = new DishOrder(1, 2, statuses);

        List<DishOrder> dishOrders = new ArrayList<>();
        dishOrders.add(dishOrder);

        Order order = new Order();
        order.setDishOrders(dishOrders);

        DishOrderStatus actualStatus = order.getActualStatus();

        assertEquals(Status.SERVED, actualStatus.getStatus());
        assertEquals(LocalDateTime.of(2025, 3, 1, 16, 0, 0), actualStatus.getChangeDate());
    }

    @Test
    public void testGetTotalAmount() {
        Dish pizza = new Dish(1, "Pizza Margherita", 12);
        Dish pasta = new Dish(2, "Spaghetti Carbonara", 10);

        DishDAO dishDAO = new DishDAO() {
            public Dish findById(int id) {
                if (id == 1) {
                    return pizza;
                }
                if (id == 2) {
                    return pasta;
                }
                return null;
            }
        };

        DishOrder dishOrder1 = new DishOrder(1, 2, new ArrayList<>());
        DishOrder dishOrder2 = new DishOrder(2, 1, new ArrayList<>());

        List<DishOrder> dishOrders = new ArrayList<>();

        dishOrders.add(dishOrder1);
        dishOrders.add(dishOrder2);

        Order order = new Order();
        order.setDishOrders(dishOrders);

        double totalAmount = order.getTotalAmount(dishDAO);

        assertEquals(34.0, totalAmount, 0.01);
    }

}