package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    XOrderRepository xOrderRepository;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result,
                                          Model model) {
        model.addAttribute("user", user);
        if (result.hasErrors())
        {
            return "registration";
        }
        else
        {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "redirect:/";
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("orders", xOrderRepository.findAll());
        return "index";
    }

    @GetMapping("/addPizza")
    public String addPizza(Model model) {
        model.addAttribute("order", new XOrder());
        return "orderform";
    }

    @PostMapping("/addPizza")
    public @ResponseBody String addPizza(HttpServletRequest request, HttpServletResponse response,
                                         Authentication auth) {
        XOrder pizza = new XOrder();
//        pizza.setPrice(0);
        pizza.setToppings(pizza.cleanToppings(request.getParameter("toppings")));
        pizza.setUser(userService.getUser());
        pizza.setPrice(pizza.calculatePrice(request.getParameter("toppings")));

        if (auth.getAuthorities().toString().equals("[USER]")) {
            xOrderRepository.save(pizza);

        }
        return null;
//        else {
//            System.out.println("i am not logged in");
//            return "redirect:/register";
//        }

    }

//    @PostMapping("/process")
//    public String processForm(@ModelAttribute XOrder order, Model model) {
//
//        order.setUser(userService.getUser());
//        xOrderRepository.save(order);
//        return "redirect:/";
//    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

//    @RequestMapping("/logout")
//    public String logout() {
//        return "redirect:/";
//    }

    @RequestMapping("/secure")
    public String secure(Principal principal, Model model) {
        String username = principal.getName();
        model.addAttribute("user", userRepository.findByUsername(username));
        return "secure";
    }

    @RequestMapping("/myorders")
    public String myOrders(Model model) {
        User user = userService.getUser();

        ArrayList<XOrder> orders = (ArrayList<XOrder>) xOrderRepository.findByUser(user);
        model.addAttribute("orders", orders);


        return "myorders";
    }

    @RequestMapping("/admin")
    public String allOrders(Model model) {
        //        User user = userService.getUser();
        roleRepository.save(new Role("USER"));
        roleRepository.save(new Role("ADMIN"));
        Role adminRole = roleRepository.findByRole("ADMIN");
        Role userRole = roleRepository.findByRole("USER");

        User user1 = new User("jim@jim.com", "j", "Jim", "Jimmerson",
                "j", "111");
        user1.setRoles(Arrays.asList(userRole));
        userRepository.save(user1);

        User user2 = new User("jimm@jim.com", "k", "Jimm", "Jimmmerson",
                "k", "222");
        user1.setRoles(Arrays.asList(userRole));
        userRepository.save(user2);

        XOrder order = new XOrder("cauliflower, curry, american, spinach, tomatoes, bacon, mushrooms", user1);
        XOrder order1 = new XOrder("traditional, cream, provolone, tomatoes, bacon, mushrooms", user1);
        XOrder order2 = new XOrder("traditional, curry, provolone, tomatoes, bacon, mushrooms", user2);

        xOrderRepository.save(order);
        xOrderRepository.save(order1);
        xOrderRepository.save(order2);

        User user3 = new User("admin@admin.com", "password", "Admin", "User",
                "admin", "222");
        user3.setRoles(Arrays.asList(adminRole));
        userRepository.save(user3);

//        ArrayList<XOrder> orders = (ArrayList<XOrder>) xOrderRepository.findByUser(user);
        ArrayList<XOrder> orders = (ArrayList<XOrder>) xOrderRepository.findAll();
        model.addAttribute("orders", orders);
//        if (userService.getUser() != null) {
//            model.addAttribute("user_id", userService.getUser().getId());
//        }
//
//        model.addAttribute("orders", xOrderRepository.findAll());
        return "admin";
    }

    @RequestMapping("/search")
    public String search(@RequestParam("search") String search, Model model){
       /* if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }*/


        /*model.addAttribute("orders", xOrderRepository.findAll());*/
        model.addAttribute("orders", userRepository.findByUsername(search).getOrders());

        return "admin";
    }

    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model) {
        model.addAttribute("order", xOrderRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model) {
        model.addAttribute("order", xOrderRepository.findById(id).get());
        return "orderform";
    }

    @RequestMapping("/delete/{id}")
    public String delMessage(@PathVariable("id") long id, Authentication auth) {
        xOrderRepository.deleteById(id);

        if (auth.getAuthorities().toString().equals("[ADMIN]")) {
            return "redirect:/admin";
        }

        else {
            return "redirect:/myorders";
        }
    }
}
