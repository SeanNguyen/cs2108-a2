package Evaluation;

import java.util.ArrayList;

import Search.AudioData;
import Tool.Utils;

public class AveragePrecision {
    public static double analyze(String queryItem, ArrayList<AudioData> resultItems, int totalRelevantItem) {
        double averagePrecision = 0;
        String correctCategory = Utils.getCategoryFromFileName(queryItem);
        for (int i=0; i<resultItems.size(); i++) {
            double precisionAtI = Precision.analyze(queryItem, resultItems, i);
            String resultItemCategory = Utils.getCategoryFromFileName(resultItems.get(i).Name);
            double recall = 0;
            if (correctCategory.equals(resultItemCategory)) {
                recall = 1;
            }
            averagePrecision += precisionAtI * recall;
        }
        return averagePrecision / totalRelevantItem;
    }
}
