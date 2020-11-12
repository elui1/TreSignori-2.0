package com.example.demo;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class XOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private LocalDateTime dateTime;
    private String toppings;
    private double price;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private final double basePrice = 5.00;

    public XOrder(String toppings, User user, double price) {
        dateTime = LocalDateTime.now();
        this.toppings = toppings;
        this.user = user;
        this.price = price;
    }

    public XOrder() {
       dateTime = LocalDateTime.now();
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getToppings() {
        return toppings;
    }

    public void setToppings(String toppings) {
        this.toppings = toppings;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String cleanToppings(String toppings) {
        if (toppings.contains("null") == true) {
            return toppings.substring(toppings.indexOf(",") + 1);
        }

        return toppings;
    }

    public double calculatePrice(String toppings) {
        return basePrice + (cleanToppings(toppings).split(",").length - 5) * 0.50;
    }
}
