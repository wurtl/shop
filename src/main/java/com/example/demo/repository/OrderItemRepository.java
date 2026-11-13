package com.example.demo.repository;

import com.example.demo.DTO.TopProductResponse;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * Позиции заказов (из чего состоит каждый заказ).
 */
public interface OrderItemRepository extends BaseRepository<OrderItem> {
    // Состав заказа (что именно собирать флористу)
    List<OrderItem> findByOrderId(Long orderId);

    // Самые продаваемые товары среди заказов в переданных статусах
    @Query("""
            select new com.example.demo.DTO.TopProductResponse(
                p.id, p.name, sum(i.quantity), sum(i.quantity * i.price))
            from OrderItem i join i.product p
            where i.order.status in :statuses
            group by p.id, p.name
            order by sum(i.quantity) desc
            """)
    List<TopProductResponse> findTopProducts(Collection<OrderStatus> statuses, Pageable page);
}
