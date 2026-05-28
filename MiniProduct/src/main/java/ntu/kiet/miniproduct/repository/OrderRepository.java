package ntu.kiet.miniproduct.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ntu.kiet.miniproduct.entity.Order;
import java.util.List; // Bổ sung thư viện List

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByOrderByOrderDateDesc();
}