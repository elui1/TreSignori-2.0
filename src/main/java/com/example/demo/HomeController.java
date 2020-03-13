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
    public @ResponseBody String addPizza(HttpServletRequest request, HttpServletResponse response,
                                         Authentication auth) {
        XOrder pizza = new XOrder();
        pizza.setToppings(pizza.cleanToppings(request.getParameter("toppings")));
        pizza.setUser(userService.getUser());
        pizza.setPrice(pizza.calculatePrice(request.getParameter("toppings")));

        if (auth.getAuthorities().toString().equals("[USER]")) {
            xOrderRepository.save(pizza);

        }
        return null;

    }

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
        if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }

        model.addAttribute("orders", xOrderRepository.findAll());
        //TODO: added for top 3 toppings and total sales
        Map<String, Integer> toppingsByCounts = captureCountOfToppings(xOrderRepository.findAll());
        Map<String, Integer> sortedToppings = sortToppingsByCounts(toppingsByCounts);
        model.addAttribute("topThreeToppings", filterTopThreeToppings(sortedToppings));
        model.addAttribute("totalSales", calculateTotalSales(xOrderRepository.findAll()));
        return "admin";
    }
    // TODO: capture counts for each toppings
    public Map<String, Integer> captureCountOfToppings(ArrayList<XOrder> orders) {
        int countOfSpinach = 0;
        int countOfTomatoes = 0;
        int countOfBacon = 0;
        int countOfMushrooms = 0;
        for (XOrder o : orders) {
            for (String topping : o.getToppings().split(",")) {
                if (topping.equalsIgnoreCase("spinach")) {
                    countOfSpinach += 1;
                } else if (topping.equalsIgnoreCase("tomatoes")) {
                    countOfTomatoes += 1;
                } else if (topping.equalsIgnoreCase("bacon")) {
                    countOfBacon += 1;
                } else if (topping.equalsIgnoreCase("mushrooms")) {
                    countOfMushrooms += 1;
                } else
                    continue;
            }
        }
        Map<String, Integer> toppingsAndCounts = new HashMap<>();
        toppingsAndCounts.put("Spinach", countOfSpinach);
        toppingsAndCounts.put("Tomatoes", countOfTomatoes);
        toppingsAndCounts.put("Bacon", countOfBacon);
        toppingsAndCounts.put("Mushrooms", countOfMushrooms);
        return toppingsAndCounts;
    }
    // TODO: Filter the top three toppings
    public Map<String, Integer> sortToppingsByCounts(Map<String, Integer> unSortedMap) {

        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        unSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        return sortedMap;
    }
    //TODO: Find only the top three toppings using counts
    public String filterTopThreeToppings(Map<String, Integer> sortedMap) {
        int maxCount = 0;
        String finalTopToppings = null;
        for (Map.Entry<String, Integer> e : sortedMap.entrySet()) {
            if (maxCount != 3) {
                if (finalTopToppings == null) {
                    finalTopToppings = e.getKey() + " : " + e.getValue();
                } else {
                    finalTopToppings = finalTopToppings +" , "+ e.getKey() + ": " + e.getValue();
                }
                maxCount = maxCount + 1;
            } else
                break;
        }
        return finalTopToppings;
    }
    // TODO: Sums up all the prices for all orders and users
    public String calculateTotalSales(ArrayList<XOrder> orders) {
        double totalSales = 0.0;
        for (XOrder o : orders) {
            if (o != null) {
                totalSales = totalSales + o.getPrice();
            }
        }
        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
        System.out.println("US: " + defaultFormat.format(totalSales));
        return "Total Sales : " + defaultFormat.format(totalSales);

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

    @RequestMapping("/update/{id}")
    public String updateOrder(@PathVariable("id") long id, Model model) {
        model.addAttribute("order", xOrderRepository.findById(id).get());
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
