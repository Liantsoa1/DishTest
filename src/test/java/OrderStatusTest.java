import entity.Dish;
import entity.DishOrder;
import entity.Order;
import entity.Status;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import DishDAO.DishDAO;
import DataSource.DataSource;

public class OrderStatusTest {
    private Order order = new Order();
    private DishDAO dishDAO;

    @BeforeEach
    void setUp() {
        dishDAO = new DishDAO(new DataSource());

        List<DishOrder> dishOrders = new ArrayList<>();
        Dish dish1 = dishDAO.findById(1);
        Dish dish2 = dishDAO.findById(2);

        dishOrders.add(new DishOrder(1, dish1.getIdDish(), 2));
        dishOrders.add(new DishOrder(2, dish2.getIdDish(), 1));

        Instant instantNow = LocalDateTime.of(2025, 3, 22, 0, 0)
                .atZone(ZoneId.systemDefault())
                .toInstant();

        order.setId(1);
        order.setReference("REF123");
        order.setCreationDateTime(instantNow);
        order.setDishOrders(dishOrders);
    }

    @Test
    public void testInitialStatusIsCreated() {
        assertEquals(Status.CREATED, order.getStatus());
    }
}
