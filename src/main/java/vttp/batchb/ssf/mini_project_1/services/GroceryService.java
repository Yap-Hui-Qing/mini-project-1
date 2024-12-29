package vttp.batchb.ssf.mini_project_1.services;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import vttp.batchb.ssf.mini_project_1.models.Recipe;
import vttp.batchb.ssf.mini_project_1.repositories.GroceryRepository;
import vttp.batchb.ssf.mini_project_1.repositories.MealRepository;

@Service
public class GroceryService {
    
    @Autowired
    private GroceryRepository groceryRepo;

    public void addToGroceries(String username, List<String> ingredients) {
        groceryRepo.addToGroceries(username, ingredients);
    }

    public Optional<Set<String>> getGroceriesByUsername(String username) {
        return groceryRepo.getGroceriesByUsername(username);
    }

    // srem username_groceries id
    public void removeFromGroceries(String username, String ingredient) {
        groceryRepo.removeFromGroceries(username, ingredient);
    }
    
}
