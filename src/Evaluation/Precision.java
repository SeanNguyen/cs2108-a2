package Evaluation;

import java.util.ArrayList;
import java.util.List;

import Search.AudioData;
import Tool.Utils;

/**
 * Created by workshop on 9/18/2015.
 */
public class Precision {
    //this will return of a string to print
    public static String analyze(String queryItem, ArrayList<AudioData> resultItems) {
    	String correctCategory = Utils.getCategoryFromFileName(queryItem);
    	int correctItemCount = 0;
    	for (AudioData resultItem : resultItems) {
    		String resultItemCategory = Utils.getCategoryFromFileName(resultItem.Name);
			if(correctCategory.equals(resultItemCategory)) {
				correctItemCount++;
			}
		}
    	return correctItemCount + "/" + resultItems.size();
    }
    
    public static double analyze(String queryItem, ArrayList<AudioData> resultItems, int at) {
        String correctCategory = Utils.getCategoryFromFileName(queryItem);
        double correctItemCount = 0;
        for (int i=0; i<=at; i++) {
            String resultItemCategory = Utils.getCategoryFromFileName(resultItems.get(i).Name);
            if(correctCategory.equals(resultItemCategory)) {
                correctItemCount++;
            }
        }
        return correctItemCount / (at+1);
    }
}
