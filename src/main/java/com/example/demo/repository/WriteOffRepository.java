package com.example.demo.repository;

import com.example.demo.entity.WriteOff;
import org.springframework.data.jpa.repository.Query;

/**
 * История списаний со склада.
 */
public interface WriteOffRepository extends BaseRepository<WriteOff> {

    // Сколько единиц списано за всё время (0, если списаний не было)
    @Query("select coalesce(sum(w.quantity), 0) from WriteOff w")
    long totalQuantity();
}
