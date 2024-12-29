package vttp.batchb.ssf.mini_project_1.models;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Recipe {
    private int id;
    private String title;
    private int readyInMinutes;
    private int servings;
    private String imageUrl;
    private List<String> instructions;
    private List<String> ingredients;

    @Override
    public String toString() {
        return "Recipe [id=" + id + ", title=" + title + ", readyInMinutes=" + readyInMinutes + ", servings=" + servings
                + ", imageUrl=" + imageUrl + ", instructions=" + instructions + ", ingredients=" + ingredients + "]";
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getReadyInMinutes() {
        return readyInMinutes;
    }
    public void setReadyInMinutes(int readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }
    public int getServings() {
        return servings;
    }
    public void setServings(int servings) {
        this.servings = servings;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public List<String> getInstructions() {
        return instructions;
    }
    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }
    public List<String> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    // Recipe -> json string
    public JsonObject toJson(){
        JsonArrayBuilder ingredientsBuilder = Json.createArrayBuilder();
        for (String i : this.ingredients){
            JsonObject j = Json.createObjectBuilder()
                .add("name", i)
                .build();
            ingredientsBuilder.add(j);
        }

        JsonArrayBuilder instructionsBuilder = Json.createArrayBuilder();
        for (int i = 0; i < this.instructions.size(); i ++){
            JsonObject j = Json.createObjectBuilder()
                .add("step %d".formatted(i+1), this.instructions.get(i))
                .build();
            instructionsBuilder.add(j);
        }

        return Json.createObjectBuilder()
            .add("id", this.id)
            .add("title", this.title)
            .add("readyInMinutes", this.readyInMinutes)
            .add("servings", this.servings)
            .add("image", this.imageUrl)
            .add("instructions", instructionsBuilder.build())
            .add("ingredients", ingredientsBuilder.build())
            .build();
    }

    // json string -> Recipe
    public static Recipe toRecipe(String json){
        Recipe recipe = new Recipe();

        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject obj = reader.readObject();

        recipe.setId(obj.getInt("id"));
        recipe.setTitle(obj.getString("title"));
        recipe.setReadyInMinutes(obj.getInt("readyInMinutes"));
        recipe.setServings(obj.getInt("servings"));
        recipe.setImageUrl(obj.getString("image"));

        List<String> instructions = new LinkedList<>();
        List<String> ingredients = new LinkedList<>();

        JsonArray instructionsArray = obj.getJsonArray("instructions");
        JsonArray ingredientsArray = obj.getJsonArray("ingredients");

        for (int i = 0; i < instructionsArray.size(); i++){
            String instruction = instructionsArray.getJsonObject(i).getString("step %d".formatted(i + 1));
            instructions.add(instruction);
        }

        for (int i = 0; i < ingredientsArray.size(); i++){
            String ingredient = ingredientsArray.getJsonObject(i).getString("name");
            ingredients.add(ingredient);
        }

        recipe.setIngredients(ingredients);
        recipe.setInstructions(instructions);
        return recipe;
          
    }
}
