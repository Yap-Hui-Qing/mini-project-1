package vttp.batchb.ssf.mini_project_1.controllers;

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
import org.springframework.web.servlet.ModelAndView;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;
import vttp.batchb.ssf.mini_project_1.models.Recipe;
import vttp.batchb.ssf.mini_project_1.models.User;
import vttp.batchb.ssf.mini_project_1.services.GroceryService;
import vttp.batchb.ssf.mini_project_1.services.MealService;
import vttp.batchb.ssf.mini_project_1.services.SearchService;

@Controller
@RequestMapping
public class MealController {

    private static final Logger logger = Logger.getLogger(MealController.class.getName());

    // autowired services for meal and search operations
    @Autowired
    private SearchService searchSvc;

    @Autowired
    private MealService mealSvc;

    @Autowired
    private GroceryService grocerySvc;

    // method to handle the home page for a user -- displays their meal plan
    @GetMapping("/{username}/home")
    public String getHome(@PathVariable String username, Model model) {

        model.addAttribute("username", username);

        // retrieve user's meal plan
        Optional<Set<String>> opt = mealSvc.getMealPlanByUsername(username);
        if (opt.isEmpty()) {
            model.addAttribute("recipes", new LinkedList<>());
            return "home";
        }

        // if meal plan is not empty, fetch the recipes using their ids
        Set<String> meals = opt.get();
        List<Recipe> recipes = new LinkedList<>();
        for (String id : meals) {
            Recipe recipe = mealSvc.getRecipeById(username, id);

            recipes.add(recipe);
        }
        model.addAttribute("recipes", recipes);
        return "home";
    }

    // method to add a recipe to a user's meal plan
    @GetMapping(path = "/addToPlan/{username}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> addRecipe(@PathVariable String username, @PathVariable String id) {

        // retrieve recipe by its id
        Recipe recipe = searchSvc.getRecipeById(username, id);

        List<String> ingredients = new LinkedList<>();
        List<String> items = recipe.getIngredients();
        for (String item : items) {
            ingredients.add(item);
        }

        grocerySvc.addToGroceries(username, ingredients);

        // check if recipe is already in meal plan
        if (mealSvc.mealExists(username, id)) {
            String message = "Recipe (ID: %d) is already inside your meal plan !".formatted(recipe.getId());

            JsonObject resp = Json.createObjectBuilder()
                    .add("message", message)
                    .build();

            return ResponseEntity.ok(resp.toString());
        }

        // add if recipe is not in meal plan
        mealSvc.addToMealPlan(username, recipe);
        logger.info("%d added to meal plan".formatted(recipe.getId()));

        String message = "Recipe (ID: %d) has been successfully added to your meal plan !".formatted(recipe.getId());

        // return a success message with recipe details
        JsonObject resp = Json.createObjectBuilder()
                .add("message", message) // The success message
                .add("recipeId", recipe.getId()) // Include the recipe ID
                .add("recipeTitle", recipe.getTitle()) // Include the recipe title
                .build();

        return ResponseEntity.ok(resp.toString());
    }

    // method to mark a recipe as "cooked" and remove it from meal plan
    @GetMapping(path = "/cooked/{username}/{id}")
    public String cookedRecipe(@PathVariable String username, @PathVariable String id) {

        Recipe recipe = mealSvc.getRecipeById(username, id);
        
        List<String> items = recipe.getIngredients();
        for (String item : items) {
            grocerySvc.removeFromGroceries(username, item);

        }

        mealSvc.removeFromMealPlan(username, id);
        logger.info("%d removed from meal plan".formatted(Integer.parseInt(id)));

        return "redirect:/{username}/home";
    }

    // method to remove recipe from meal plan when they are editing meal plan
    @GetMapping(path = "/remove/{username}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> removeRecipe(@PathVariable String username, @PathVariable String id) {

        Recipe recipe = mealSvc.getRecipeById(username, id);

        List<String> items = recipe.getIngredients();
        for (String item : items) {
            grocerySvc.removeFromGroceries(username, item);

        }

        mealSvc.removeFromMealPlan(username, id);
        logger.info("%d removed from meal plan".formatted(Integer.parseInt(id)));

        String message = "Recipe (ID: %d) has been removed from your meal plan !".formatted(Integer.parseInt(id));
        JsonObject resp = Json.createObjectBuilder()
                .add("message", message)
                .build();

        return ResponseEntity.ok(resp.toString());
    }

    // method to show detailed information about a specific recipe from username
    @GetMapping("/mealRecipeDetails/{id}")
    public ModelAndView getMealRecipeDetails(@PathVariable String id,
            HttpSession sess, Model model) {

        User user = (User) sess.getAttribute("user");
        String username = user.getUsername();
        Recipe recipe = mealSvc.getRecipeById(username, id);

        ModelAndView mav = new ModelAndView("meal-recipe-details");
        mav.addObject("username", username);
        mav.addObject("title", recipe.getTitle());
        mav.addObject("recipe", recipe);
        return mav;

    }

    // method to create a new meal plan by deleting the current one
    @GetMapping("/{username}/newPlan")
    public String getNewPlan(@PathVariable String username) {
        mealSvc.deletePlan(username);
        return "redirect:/browse";
    }

    // method to handle the user's favourites
    @GetMapping("/{username}/favourites")
    public String getFavourites(@PathVariable String username, Model model) {

        model.addAttribute("username", username);

        // retrieve user's favourites
        Optional<Set<String>> opt = mealSvc.getFavouritesByUsername(username);
        if (opt.isEmpty()) {
            model.addAttribute("favourites", new LinkedList<>());
            return "favourites";
        }

        // if meal plan is not empty, fetch the recipes using their ids
        Set<String> meals = opt.get();
        List<Recipe> favourites = new LinkedList<>();
        for (String id : meals) {
            Recipe recipe = mealSvc.getRecipeFavouriteById(username, id);

            favourites.add(recipe);
        }
        model.addAttribute("username", username);
        model.addAttribute("favourites", favourites);
        return "favourites";
    }

    // method to add save recipe as favourite
    @GetMapping(path = "/addToFavourite/{username}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> addFavourite(@PathVariable String username, @PathVariable String id) {

        Recipe recipe = searchSvc.getRecipeById(username, id);
        boolean isFavourite = mealSvc.favouriteExists(username, id);
        if (isFavourite) {

            String message = "Recipe (ID: %d) is already inside your favourites !".formatted(recipe.getId());

            JsonObject resp = Json.createObjectBuilder()
                    .add("message", message)
                    .add("isFavourite", true)
                    .build();

            return ResponseEntity.ok(resp.toString());
        }

        mealSvc.addToFavourites(username, recipe);
        logger.info("%d added to favourites".formatted(recipe.getId()));

        String message = "Recipe (ID: %d) has been successfully added to your favourites !".formatted(recipe.getId());

        // return a success message with recipe details
        JsonObject resp = Json.createObjectBuilder()
                .add("message", message) // The success message
                .add("recipeId", recipe.getId()) // Include the recipe ID
                .add("recipeTitle", recipe.getTitle()) // Include the recipe title
                .add("isFavourite", true)
                .build();

        return ResponseEntity.ok(resp.toString());
    }
    
    @GetMapping("/favouriteRecipeDetails/{id}")
    public ModelAndView getFavouriteRecipeDetails(@PathVariable String id,
            HttpSession sess, Model model) {

        User user = (User) sess.getAttribute("user");
        String username = user.getUsername();
        Recipe recipe = mealSvc.getRecipeFavouriteById(username, id);

        ModelAndView mav = new ModelAndView("recipe-details");
        mav.addObject("username", username);
        mav.addObject("title", recipe.getTitle());
        mav.addObject("recipe", recipe);
        return mav;

    }

    // method to remove recipe from favourites
    @GetMapping(path = "/removeFromFavourite/{username}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> removeFromFavourite(@PathVariable String username, @PathVariable String id) {
        mealSvc.removeFromFavourites(username, id);
        logger.info("%d removed from favourites".formatted(Integer.parseInt(id)));

        String message = "Recipe (ID: %d) has been removed from your favourites !".formatted(Integer.parseInt(id));
        JsonObject resp = Json.createObjectBuilder()
                .add("message", message)
                .add("isFavourite", false)
                .build();

        return ResponseEntity.ok(resp.toString());
    }

}
