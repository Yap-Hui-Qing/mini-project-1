package vttp.batchb.ssf.mini_project_1.repositories;

import static vttp.batchb.ssf.mini_project_1.models.Recipe.toRecipe;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import vttp.batchb.ssf.mini_project_1.models.Recipe;

@Repository
public class MealRepository {

    @Autowired
    @Qualifier("redis-0")
    private RedisTemplate<String, String> template;

    // check if id exists
    // hexists username id
    public boolean mealExists(String username, String id) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        return hashOps.hasKey(username, id);
    }

    // hset username id recipe
    public void addToMealPlan(String username, Recipe recipe) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        hashOps.put(username, String.valueOf(recipe.getId()), recipe.toJson().toString());
    }

    // hget username id
    public Recipe getRecipeById(String username, String id) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        String json = hashOps.get(username, id);

        Recipe recipe = toRecipe(json);

        return recipe;
    }

    // del username
    public void deletePlan(String username) {
        template.delete(username);
    }

    // hkeys username
    public Optional<Set<String>> getMealPlanByUsername(String username) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        Set<String> results = hashOps.keys(username);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results);
    }

    // remove a recipe from meal plan
    // hdel username id
    public void removeFromMealPlan(String username, String id) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        hashOps.delete(username, id);
    }

    // check if recipe is in favourites
    // hexists username id
    public boolean favouriteExists(String username, String id) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        return hashOps.hasKey(username + "_favourites", id);
    }

    // hget username_favourites id
    public Recipe getRecipeFavouriteById(String username, String id) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        String json = hashOps.get(username + "_favourites", id);

        Recipe recipe = toRecipe(json);

        return recipe;
    }

    // hset username_favourites id recipe
    public void addToFavourites(String username, Recipe recipe) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        String key = username + "_favourites";
        hashOps.put(key, String.valueOf(recipe.getId()), recipe.toJson().toString());
    }

    // hkeys username_favourites
    public Optional<Set<String>> getFavouritesByUsername(String username) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        String key = username + "_favourites";
        Set<String> results = hashOps.keys(key);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results);
    }

    // remove a recipe from favourites
    // hdel username_favourites id
    public void removeFromFavourites(String username, String id) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        hashOps.delete(username + "_favourites", id);
    }
}
