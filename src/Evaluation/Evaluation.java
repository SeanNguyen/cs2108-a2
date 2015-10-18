package Evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluation {
    
    Map<String, List<Double>> averagePrecisionMap = new HashMap<String, List<Double>>();
    
    public void addAveragePrecision(String category, Double averagePrecision) {
        if (!averagePrecisionMap.containsKey(category)) {
            averagePrecisionMap.put(category, new ArrayList<Double>());
        }
        averagePrecisionMap.get(category).add(averagePrecision);
    }
    
    public Map<String, Double> getAPForCategories() {
        Map<String, Double> APFC = new HashMap<String, Double>();
        for (String category : averagePrecisionMap.keySet()) {
            APFC.put(category, getAverage(averagePrecisionMap.get(category)));
        }
        return APFC;
    }
    
    public double getOverallAP () {         
        return getAverage(new ArrayList<Double>(getAPForCategories().values()));
    }
    
    private double getAverage(List<Double> AP) {
        double i = 0;
        for (double j : AP ) {
            i += j;
        }
        return i / AP.size();
    }
}
