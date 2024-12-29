package vttp.batchb.ssf.mini_project_1.controllers;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vttp.batchb.ssf.mini_project_1.models.Recipe;
import vttp.batchb.ssf.mini_project_1.models.SearchParams;
import vttp.batchb.ssf.mini_project_1.models.User;
import vttp.batchb.ssf.mini_project_1.services.MealService;
import vttp.batchb.ssf.mini_project_1.services.SearchService;

import static vttp.batchb.ssf.mini_project_1.models.Recipe.*;

@Controller
@RequestMapping
public class SearchController {

    private static final Logger logger = Logger.getLogger(SearchController.class.getName());

    // SearchService to handle search-related operations
    @Autowired
    private SearchService searchSvc;

    @Autowired
    private MealService mealSvc;

    // render the "browse" page where users can input their search
    // also clears any previous search results from the session
    @GetMapping("/browse")
    public String getBrowse(HttpSession sess) {

        User user = (User) sess.getAttribute("user");
        String username = user.getUsername();

        String cacheKey = username + "_search";
        // clearing previous search results
        logger.info("Clearing search results for: %s".formatted(cacheKey));
        searchSvc.clearSearchResults(cacheKey);

        // allow user to input new search parameters
        return "browse";
    }

    // handles the search operation based on user's search criteria
    // returns the list of recipes and store it for 15 minutes
    @GetMapping("/search")
    public ModelAndView getSearch(@RequestParam MultiValueMap<String, String> queryParams, HttpSession sess) {

        User user = (User) sess.getAttribute("user");
        String username = user.getUsername();

        SearchParams params = new SearchParams(queryParams.get("cuisine"),
                queryParams.getFirst("diet"),
                Integer.parseInt(queryParams.getFirst("maxReadyTime")),
                Integer.parseInt(queryParams.getFirst("minServings")));

        String cacheKey = username + "_search";

        // perform search and save results using cacheKey
        List<Recipe> recipeSearch = searchSvc.search(params, username);

        ModelAndView mav = new ModelAndView("recipes");
        mav.addObject("recipeSearch", recipeSearch);
        mav.addObject("username", username);
        return mav;
    }

    // retrieves the cached search result
    // session timeout message is shown if no cache is found
    @GetMapping("/searchrecipes")
    public String getSearchRecipes(Model model, HttpSession sess, RedirectAttributes redirectAttributes){

        User user = (User) sess.getAttribute("user");
        String username = user.getUsername();

        Map<String, String> cachedSearch = searchSvc.getSearchResults(username + "_search");
        if (cachedSearch != null && !cachedSearch.isEmpty()){
            logger.info("Retrieving search results from redis");

            // convert cached search values into Recipe objects
            List<Recipe> recipeSearch = cachedSearch.values().stream()
                .map(s -> toRecipe(s))
                .toList();
            model.addAttribute("username", username);
            model.addAttribute("recipeSearch", recipeSearch);
            return "recipes";
        }

        // add a session timeout message
        redirectAttributes.addFlashAttribute("sessionTimeoutMessage", "Your search session has timed out. Please search again!");
        return "redirect:/browse";

    }

    // retrieves detailed information about a specific recipe from username_search
    @GetMapping("/recipeDetails/{id}")
    public ModelAndView getRecipeDetails(@PathVariable String id,
            HttpSession sess, Model model) {

        User user = (User) sess.getAttribute("user");
        String username = user.getUsername();
        Recipe recipe = searchSvc.getRecipeById(username, id);
        boolean isFavourite = mealSvc.favouriteExists(username, id);

        ModelAndView mav = new ModelAndView("recipe-details");
        mav.addObject("isFavourite", isFavourite);
        mav.addObject("username", username);
        mav.addObject("title", recipe.getTitle());
        mav.addObject("recipe", recipe);
        return mav;

    }

}
