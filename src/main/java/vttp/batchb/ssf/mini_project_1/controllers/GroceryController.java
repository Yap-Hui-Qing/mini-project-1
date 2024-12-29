package vttp.batchb.ssf.mini_project_1.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp.batchb.ssf.mini_project_1.services.GroceryService;
import vttp.batchb.ssf.mini_project_1.services.MealService;

@Controller
@RequestMapping
public class GroceryController {

    private static final Logger logger = Logger.getLogger(GroceryController.class.getName());

    @Autowired
    private GroceryService grocerySvc;

    // method to show the user's grocery list
    @GetMapping("/{username}/groceries")
    public String getGroceriesList(@PathVariable String username, Model model) {

        Optional<Set<String>> opt1 = grocerySvc.getGroceriesByUsername(username);
        if (opt1.isEmpty()) {
            model.addAttribute("groceries", new LinkedList<>());
            return "groceries";
        }

        Set<String> items = opt1.get();
        List<String> groceries = getUniqueGroceries(items);
        model.addAttribute("username", username);
        model.addAttribute("groceries", groceries);
        return "groceries";
    }

    // method to remove recipe from favourites
    @GetMapping(path = "/removeFromGroceries/{username}/{ingredient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> removeFromFavourite(@PathVariable String username, @PathVariable String ingredient, Model model) {

        grocerySvc.removeFromGroceries(username, ingredient);
        logger.info("%s removed from groceries list".formatted(ingredient));

        String message = "Ingredient %s has been removed from your groceries list!".formatted(ingredient);
        JsonObject resp = Json.createObjectBuilder()
                .add("message", message)
                .build();

        return ResponseEntity.ok(resp.toString());
    }

    public List<String> getUniqueGroceries(Set<String> items) {
        Set<String> uniqueGroceries = new HashSet<>(items); // Remove duplicates
        return new ArrayList<>(uniqueGroceries); // Convert back to a list
    }

}
