package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    XOrderRepository xOrderRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... strings) throws Exception {
        roleRepository.save(new Role("USER"));
        roleRepository.save(new Role("ADMIN"));
        Role adminRole = roleRepository.findByRole("ADMIN");
        Role userRole = roleRepository.findByRole("USER");

        User user = new User("jim@jim.com", "j", "Jim", "Jimmerson",
                "j", "111");
        user.setRoles(Arrays.asList(userRole));

        XOrder order = new XOrder("cauliflower, curry, american, spinach, tomatoes, bacon, mushrooms", user, 5.50);
//        order.setUser(user);
        xOrderRepository.save(order);

        Set<XOrder> orders = new HashSet<>();
        orders.add(order);
        user.setOrders(orders);
        userRepository.save(user);


        User user1 = new User("jimm@jimm.com", "k", "Jimm", "Jimmmerson",
                "k", "222");
        user1.setRoles(Arrays.asList(userRole));

        order = new XOrder("traditional, cream, provolone, tomatoes, bacon, mushrooms", user1, 6.00);
        xOrderRepository.save(order);

        orders = new HashSet<>();
        orders.add(order);
        user1.setOrders(orders);
        userRepository.save(user1);


        User user2 = new User("admin@admin.com", "password", "Admin", "User",
                "admin", "222");
        user2.setRoles(Arrays.asList(adminRole));
        userRepository.save(user2);


        //        XOrder order1 = new XOrder("traditional, cream, provolone, tomatoes, bacon, mushrooms", user1);
//        XOrder order2 = new XOrder("traditional, curry, provolone, tomatoes, bacon, mushrooms", user2);
//
//        xOrderRepository.save(order);
//        xOrderRepository.save(order1);
//        xOrderRepository.save(order2);

    }

}