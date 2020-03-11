package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... strings) throws Exception {
        roleRepository.save(new Role("USER"));
        roleRepository.save(new Role("ADMIN"));
        Role adminRole = roleRepository.findByRole("ADMIN");
        Role userRole = roleRepository.findByRole("USER");

        User user = new User("jim@jim.com", "password", "Jim", "Jimmerson",
                "jim", "111");
        user.setRoles(Arrays.asList(userRole));
        userRepository.save(user);

        User user2 = new User("admin@admin.com", "password", "Admin", "User",
                "admin", "222");
        user2.setRoles(Arrays.asList(adminRole));
        userRepository.save(user2);

    }
//
//        User user3 = new User("test@test.com", "test", "Testy", "Tester", true, "test");
//        user3.setRoles(Arrays.asList(userRole));
//        userRepository.save(user3);
//
//        Message message = new Message("My favorite puppy", "so cute!");
//        message.setUser(user2);
//        messageRepository.save(message);
//
//        Set<Message> messages = new HashSet<>();
//        messages.add(message);
//        user2.setMessages(messages);
//        userRepository.save(user2);
//
//
//
//
//        message = new Message("My favorite food", "so good!");
//        message.setUser(user3);
//        messageRepository.save(message);
//
//        messages = new HashSet<>();
//        messages.add(message);
//        user3.setMessages(messages);
//        userRepository.save(user3);
//
////        message = new Message("My favorite color", "so cool!", "Boss");
////        message.setUser(user);
////        messageRepository.save(message);
//
////        Message message = new Message("asdfsdf", "dfgdfgd");
////        message.setUser(user1);
////        messageRepository.save(message);
////
////        message = new Message("rtyfghj", "vbnbffg");
////        message.setUser(user2);
////        messageRepository.save(message);
////
////        message = new Message("bcvfger", "erterggde");
////        message.setPostedBy("Tom");
////        messageRepository.save(message);
//    }
//}
    }