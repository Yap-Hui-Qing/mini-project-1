package vttp.batchb.ssf.mini_project_1.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import vttp.batchb.ssf.mini_project_1.models.Recipe;
import vttp.batchb.ssf.mini_project_1.models.SearchParams;
import vttp.batchb.ssf.mini_project_1.repositories.MealRepository;
import vttp.batchb.ssf.mini_project_1.repositories.SearchRepository;

import static vttp.batchb.ssf.mini_project_1.models.Recipe.*;

@Service
public class SearchService {

    private static final Logger logger = Logger.getLogger(SearchService.class.getName());

    public static final String SEARCH_URL = "https://api.spoonacular.com/recipes/complexSearch";
    public static final String INGREDIENTS_URL = "https://api.spoonacular.com/recipes/{id}/ingredientWidget.json";

    @Value("${spoonacular.apikey}")
    private String apiKey;

    @Autowired
    private SearchRepository searchRepo;

    public List<Recipe> search(SearchParams params, String username) {

        // generate the cache key for the current search
        String newCacheKey = username + "_search";

        // check if results for the query are cached
        Map<String, String> cachedSearch = getSearchResults(newCacheKey);
        if (cachedSearch != null && !cachedSearch.isEmpty()){
            logger.info("Retrieving search results from redis");
            return cachedSearch.values().stream()
                .map(s -> toRecipe(s))
                .toList();
        }

        // fetch results from api
        List<String> cuisines = params.cuisine();
        String cuisine = String.join(",", cuisines);

        String url = UriComponentsBuilder
                .fromUriString(SEARCH_URL)
                .queryParam("apiKey", apiKey)
                .queryParam("addRecipeInformation", true)
                .queryParam("addRecipeInstructions", true)
                .queryParam("cuisine", cuisine)
                .queryParam("diet", params.diet())
                .queryParam("maxReadyTime", params.maxReadyTime())
                .queryParam("minServings", params.minServings())
                .queryParam("number", 100)
                .toUriString();

        RequestEntity<Void> req = RequestEntity
                .get(url)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        try {
            RestTemplate template = new RestTemplate();
            ResponseEntity<String> resp = template.exchange(req, String.class);

            String payload = resp.getBody();

            List<Recipe> recipeSearch = getRecipeInfo(payload);
            logger.info("Retrieving search results by calling api");
            saveSearchResults(newCacheKey, recipeSearch);
            logger.info("Saving search results");
            return recipeSearch;

        } catch (Exception ex) {
            ex.printStackTrace();
            return List.of();
        }
    }

    private List<Recipe> getRecipeInfo(String json) {

        List<Recipe> recipeSearch = new LinkedList<>();
        JsonArray results = Json.createReader(new StringReader(json))
                .readObject()
                .getJsonArray("results");

        for (int i = 0; i < results.size(); i++) {
            JsonObject r  = results.getJsonObject(i);

            Recipe recipe = new Recipe();
            recipe.setId(r.getInt("id"));
            recipe.setTitle(r.getString("title"));
            recipe.setReadyInMinutes(r.getInt("readyInMinutes"));
            recipe.setServings(r.getInt("servings"));
            recipe.setImageUrl(r.getString("image"));

            List<String> instructions = new LinkedList<>();
            List<String> ingredients = new LinkedList<>();

            JsonArray steps = r.getJsonArray("analyzedInstructions")
                .getJsonObject(0)
                .getJsonArray("steps");
            for (int j = 0; j < steps.size(); j ++){
                JsonObject obj = steps.getJsonObject(j);
                String instruction = obj.getString("step");
                instructions.add(instruction);
                JsonArray ingredientsArray = obj.getJsonArray("ingredients");
                for (int k = 0; k < ingredientsArray.size(); k ++){
                    String ingredient = ingredientsArray.getJsonObject(k)
                        .getString("name");
                    ingredients.add(ingredient);
                }
            }

            recipe.setInstructions(instructions);
            recipe.setIngredients(ingredients);
            recipeSearch.add(recipe);
        }
        return recipeSearch;

    }

    public Map<String, String> getSearchResults(String cacheKey){
        return searchRepo.getSearchResults(cacheKey);
    }

    public void saveSearchResults(String cacheKey, List<Recipe> search) {
        for (Recipe recipe : search) {
            searchRepo.saveSearchResults(cacheKey, recipe);
        }
    }

    public void clearSearchResults(String cacheKey){
        searchRepo.clearSearchResults(cacheKey);
    }

    public Recipe getRecipeById(String username, String id){
        return searchRepo.getRecipeById(username, id);
    }

}
