package vttp.batchb.ssf.mini_project_1.repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import vttp.batchb.ssf.mini_project_1.models.Recipe;

@Repository
public class GroceryRepository {

    private static final Logger logger = Logger.getLogger(GroceryRepository.class.getName());

    @Autowired
    @Qualifier("redis-0")
    private RedisTemplate<String, String> template;


    // sismember username_groceries item
    public Boolean isMember(String username, String ingredient){
        SetOperations<String, String> setOps = template.opsForSet();
        String key = username + "_groceries";
        return setOps.isMember(key, ingredient);
    }

    // sadd username_groceries item
    public void addToGroceries(String username, List<String> ingredients) {
        SetOperations<String, String> setOps = template.opsForSet();
        String key = username + "_groceries";
        for (String i : ingredients){
            if (!isMember(username, i))
                setOps.add(key, i);
        }
        
    }

    // smembers username_groceries
    public Optional<Set<String>> getGroceriesByUsername(String username) {
        SetOperations<String, String> setOps = template.opsForSet();
        String key = username + "_groceries";

        Set<String> results = setOps.members(key);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results);
    }

    // srem username_groceries id
    public void removeFromGroceries(String username, String ingredient) {
        SetOperations<String, String> setOps = template.opsForSet();
        String key = username + "_groceries";

        setOps.remove(key, ingredient);
    }
}
