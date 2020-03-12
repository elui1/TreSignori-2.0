package com.example.demo;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface XOrderRepository extends CrudRepository<XOrder, Long> {
    Iterable<XOrder> findByUser(User user);
    ArrayList<XOrder> findAll();

    /*ArrayList<XOrder> findByUser(User user);*/
}
