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
import java.text.NumberFormat;
import java.util.*;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    XOrderRepository xOrderRepository;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result,
                                          Model model) {
        model.addAttribute("user", user);
        if (result.hasErrors())
        {
            return "register";
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
    public @ResponseBody String addPizza(@ModelAttribute XOrder order, HttpServletRequest request, HttpServletResponse response,
                                         Authentication auth) {
//        XOrder pizza = new XOrder();

        order.setToppings(order.cleanToppings(request.getParameter("toppings")));
        order.setUser(userService.getUser());
        order.setPrice(order.calculatePrice(request.getParameter("toppings")));

        if (auth.isAuthenticated()) {
            xOrderRepository.save(order);

        }

        // seems can return anything here
//        return "orderform";
        return null;

    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

//    @RequestMapping("/secure")
//    public String secure(Principal principal, Model model) {
//        String username = principal.getName();
//        model.addAttribute("user", userRepository.findByUsername(username));
//        return "secure";
//    }

    @RequestMapping("/myorders")
    public String myOrders(Model model) {
        User user = userService.getUser();

        ArrayList<XOrder> orders = (ArrayList<XOrder>) xOrderRepository.findByUser(user);
        model.addAttribute("orders", orders);

        return "myorders";
    }

    @RequestMapping("/admin")
    public String allOrders(Model model) {
        if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }

        model.addAttribute("orders", xOrderRepository.findAll());

        return "admin";
    }


    @RequestMapping("/search")
    public String search(@RequestParam("search") String search, Model model) {
        model.addAttribute("orders", userRepository.findByUsername(search).getOrders());
        return "admin";
    }

//    @RequestMapping("/detail/{id}")
//    public String showOrder(@PathVariable("id") long id, Model model) {
//        model.addAttribute("order", xOrderRepository.findById(id).get());
//        return "show";
//    }

    @GetMapping("/update/{id}")
    public String updateOrder(@PathVariable("id") long id, Model model) {
        XOrder order = xOrderRepository.findById(id).get();
        model.addAttribute("order", order);
        return "orderform";
    }

    @RequestMapping("/update/{id}")
    public String updateOrder(@PathVariable("id") long id, HttpServletRequest request, Model model) {
//        xOrderRepository.deleteById(id);
//
        XOrder order = xOrderRepository.findById(id).get();
        model.addAttribute("order", order);
//        System.out.println(xOrderRepository.findById(id).get().getPrice());
//        xOrderRepository.save(pizza);

        order.setToppings(order.cleanToppings(request.getParameter("toppings")));
        order.setUser(userService.getUser());
        order.setPrice(order.calculatePrice(request.getParameter("toppings")));

        xOrderRepository.save(order);

        return "orderform";

    }

    @RequestMapping("/delete/{id}")
    public String delOrder(@PathVariable("id") long id, Authentication auth) {
        xOrderRepository.deleteById(id);

        if (auth.getAuthorities().toString().equals("[ADMIN]")) {
            return "redirect:/admin";
        }

        else {
            return "redirect:/myorders";
        }
    }
}
