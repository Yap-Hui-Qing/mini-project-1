package vttp.batchb.ssf.mini_project_1.models;

import java.util.List;

public record SearchParams(List<String> cuisine, String diet, int maxReadyTime, int minServings) {
    
}
