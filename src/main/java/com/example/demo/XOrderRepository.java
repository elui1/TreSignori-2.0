package com.example.demo;

import org.springframework.data.repository.CrudRepository;

public interface XOrderRepository extends CrudRepository<XOrder, Long> {
    Iterable<XOrder> findByUser(User user);
}
