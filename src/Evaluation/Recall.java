package Evaluation;

import java.util.ArrayList;

import Tool.Utils;

/**
 * Created by workshop on 10/1/2015.
 */
public class Recall {
    //this will return of a string to print
    public static String analyze(String queryItem, ArrayList<String> resultItems, int totalRelevantItem) {
    	String correctCategory = Utils.getCategoryFromFileName(queryItem);
    	int correctItemCount = 0;
    	for (String resultItem : resultItems) {
    		String resultItemCategory = Utils.getCategoryFromFileName(resultItem);
			if(correctCategory.equals(resultItemCategory)) {
				correctItemCount++;
			}
		}
    	return correctItemCount + "/" + totalRelevantItem;
    }
}
