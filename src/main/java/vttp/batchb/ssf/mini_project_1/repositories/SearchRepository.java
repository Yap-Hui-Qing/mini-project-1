package vttp.batchb.ssf.mini_project_1.repositories;

import static vttp.batchb.ssf.mini_project_1.models.Recipe.toRecipe;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import vttp.batchb.ssf.mini_project_1.models.Recipe;

@Repository
public class SearchRepository {

    private static final Logger logger = Logger.getLogger(SearchRepository.class.getName());
    
    @Autowired
    @Qualifier("redis-0")
    private RedisTemplate<String, String> template;

    // del username_search
    public void clearSearchResults(String cacheKey){
        template.delete(cacheKey);

        if (template.hasKey(cacheKey)) {
            logger.warning("Failed to delete cache key: " + cacheKey);
        } else {
            logger.info("Successfully deleted cache key: " + cacheKey);
        }
    }
    
    // hset username_search id recipe
    public void saveSearchResults(String cacheKey, Recipe recipe){
        HashOperations<String, String, String> hashOps = template.opsForHash();

        hashOps.put(cacheKey, String.valueOf(recipe.getId()), recipe.toJson().toString());

        // store the search data for 15 minutes
        // expire username_search
        template.expire(cacheKey, 15, TimeUnit.MINUTES);  // 15 minutes TTL

    }

    // hgetall username_search
    public Map<String, String> getSearchResults(String cacheKey){
        HashOperations<String, String, String> hashOps = template.opsForHash();
        return hashOps.entries(cacheKey);
    }

    // hget fred_search id
    public Recipe getRecipeById(String username, String id){
        String cacheKey = username + "_search";
        HashOperations<String, String, String> hashOps = template.opsForHash();

        String json = hashOps.get(cacheKey, id);
        Recipe recipe = toRecipe(json);
        return recipe;
    }

}
