package Evaluation;

import java.util.ArrayList;

import Search.AudioData;
import Tool.Utils;

/**
 * Created by workshop on 10/1/2015.
 */
public class Recall {
    //this will return of a string to print
    public static String analyze(String queryItem, ArrayList<AudioData> resultItems, int totalRelevantItem) {
    	String correctCategory = Utils.getCategoryFromFileName(queryItem);
    	int correctItemCount = 0;
    	for (AudioData resultItem : resultItems) {
    		String resultItemCategory = Utils.getCategoryFromFileName(resultItem.Name);
			if(correctCategory.equals(resultItemCategory)) {
				correctItemCount++;
			}
		}
    	return correctItemCount + "/" + totalRelevantItem;
    }
    
    public static double analyzeDouble(String queryItem, ArrayList<AudioData> resultItems, int totalRelevantItem) {
        String correctCategory = Utils.getCategoryFromFileName(queryItem);
        double correctItemCount = 0;
        for (AudioData resultItem : resultItems) {
            String resultItemCategory = Utils.getCategoryFromFileName(resultItem.Name);
            if(correctCategory.equals(resultItemCategory)) {
                correctItemCount++;
            }
        }
        return correctItemCount / totalRelevantItem;
    }
}
