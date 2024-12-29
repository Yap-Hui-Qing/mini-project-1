package vttp.batchb.ssf.mini_project_1.repositories;

import java.io.StringReader;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import vttp.batchb.ssf.mini_project_1.models.User;
import static vttp.batchb.ssf.mini_project_1.models.User.*;

@Repository
public class UserRepository {
    
    @Autowired @Qualifier("redis-0")
    private RedisTemplate<String, String> template;

    // hexists users username
    public boolean hasUser(String username){
        HashOperations<String, String, String> hashOps = template.opsForHash();
        return hashOps.hasKey("users", username);
    }

    // hset users username password
    public void addUser(User user){
        HashOperations<String, String, String> hashOps = template.opsForHash();
        hashOps.put("users", user.getUsername(), user.toJson().toString());
    }

    // hget users username
    public Optional<User> getUserByUsername(String username){
        HashOperations<String, String, String> hashOps = template.opsForHash();
        String userString = hashOps.get("users", username);
        if (userString == null){
            return Optional.empty();
        }

        User user = toUser(userString);
        return Optional.of(user);
    }

}
