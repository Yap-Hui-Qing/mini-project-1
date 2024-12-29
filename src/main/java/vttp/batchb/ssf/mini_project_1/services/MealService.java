package vttp.batchb.ssf.mini_project_1.services;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import vttp.batchb.ssf.mini_project_1.models.Recipe;
import vttp.batchb.ssf.mini_project_1.repositories.MealRepository;

@Service
public class MealService {

    @Autowired
    private MealRepository mealRepo;

    // check if recipe is in plan
    public boolean mealExists(String username, String id) {
        return mealRepo.mealExists(username, id);
    }

    // add to meal plan
    public void addToMealPlan(String username, Recipe recipe) {
        mealRepo.addToMealPlan(username, recipe);
    }

    // get recipe details
    public Recipe getRecipeById(String username, String id) {
        return mealRepo.getRecipeById(username, id);
    }

    // get meal plan
    public Optional<Set<String>> getMealPlanByUsername(String username) {
        return mealRepo.getMealPlanByUsername(username);
    }

    // remove meal plan
    public void removeFromMealPlan(String username, String id) {
        mealRepo.removeFromMealPlan(username, id);
    }

    // delete plan
    public void deletePlan(String username) {
        mealRepo.deletePlan(username);
    }

    // favourites

    // check if recipe is in favourites
    public boolean favouriteExists(String username, String id) {
        return mealRepo.favouriteExists(username, id);
    }

    public Recipe getRecipeFavouriteById(String username, String id) {

        return mealRepo.getRecipeFavouriteById(username, id);
    }

    public void addToFavourites(String username, Recipe recipe) {
        mealRepo.addToFavourites(username, recipe);
    }

    public Optional<Set<String>> getFavouritesByUsername(String username) {
        return mealRepo.getFavouritesByUsername(username);
    }

    // remove a recipe from favourites
    public void removeFromFavourites(String username, String id) {
        mealRepo.removeFromFavourites(username, id);
    }

}
