package Evaluation;

import java.util.ArrayList;
import java.util.List;

import Tool.Utils;

/**
 * Created by workshop on 9/18/2015.
 */
public class Precision {
    //this will return of a string to print
    public static String analyze(String queryItem, ArrayList<String> resultItems) {
    	String correctCategory = Utils.getCategoryFromFileName(queryItem);
    	int correctItemCount = 0;
    	for (String resultItem : resultItems) {
    		String resultItemCategory = Utils.getCategoryFromFileName(resultItem);
			if(correctCategory.equals(resultItemCategory)) {
				correctItemCount++;
			}
		}
    	return correctItemCount + "/" + resultItems.size();
    }
}
