package Search;

import java.io.File;
import java.util.*;

import com.sun.javafx.util.Utils;

import Distance.CityBlock;
import Distance.Cosine;
import Distance.Euclidean;
import Evaluation.AveragePrecision;
import Evaluation.Evaluation;
import Evaluation.Precision;
import Evaluation.Recall;
import Search.SearchEngine.AnalyzedFeature;
import Search.SearchEngine.DistanceMethod;
import Tool.SortHashMapByValue;

/**
 * Created by workshop on 9/18/2015.
 */
public class SearchEngine {
    //Constants
	public enum AnalyzedFeature {
		Energy,
		ZeroCrossing,
		MagnitudeSpectrum,
		MFCC
	}
	public enum DistanceMethod {
		Cosine,
		CityBlock,
		Euclidean
	}
	
    private HashMap<String, AudioData> audioDataMap = new HashMap<>();
    private HashMap<String, Double> audioDistanceMap = new HashMap<>(); 
    private HashMap<String, Integer> categoryCount = new HashMap<>(); 
    private List<String> trainFiles = new ArrayList<String>();
    private List<String> queryFiles = new ArrayList<String>();
    private FeatureExtractor featureExtractor = new FeatureExtractor();
    
    
    //Constructor
    public SearchEngine() {
    	//Process all the audio files here
    	this.featureExtractor.calcuteFeatureData();
    	//then read them
    	this.featureExtractor.readFeatureData(audioDataMap, categoryCount, trainFiles, queryFiles);
    }

    //Public methods
    /***
     * Get the distances between features of the selected query audio and ones of the train set;
     * Please modify this function, select or combine the suitable and feasible methods (in the package named 'Distance') to calculate the distance,
     * such as CityBlock, Cosine and Euclidean by yourself.
     * @param query the selected query audio file;
     * @return the top 20 similar audio files;
     */
    public ArrayList<AudioData> retrieveResultList(AudioData queryAudioData, DistanceMethod distanceMethod, Vector<AnalyzedFeature> analyzedFeatures){
    	
    	for (String audioFileName : trainFiles) {
    		AudioData comparedAudioData = this.audioDataMap.get(audioFileName);
			double distance = 0.0;
			for (AnalyzedFeature analyzedFeature : analyzedFeatures) {
				switch (analyzedFeature) {
				case Energy:
					double energyDistance = calculateDistanceForArray(distanceMethod, comparedAudioData.Energy, queryAudioData.Energy); 
					distance += energyDistance;
					//System.out.println("Energy: " + energyDistance);
					break;
				case ZeroCrossing:
					double zeroCrossingDistance = calculateDistanceForNumber(distanceMethod, comparedAudioData.ZeroCrossing, queryAudioData.ZeroCrossing); 
					distance += zeroCrossingDistance;
					//System.out.println("Zero Crossing: " + zeroCrossingDistance);
					break;
				case MagnitudeSpectrum:
					double magnitudeSpectrumDistance = calculateDistanceForArray(distanceMethod, comparedAudioData.MagnitudeSpectrum, queryAudioData.MagnitudeSpectrum); 
					distance += magnitudeSpectrumDistance;
					//System.out.println("Magnitude Spectrum: " + magnitudeSpectrumDistance);
					break;
				case MFCC:
					double mfccDistance = calculateDistanceForMatrix(distanceMethod, comparedAudioData.MFCC, queryAudioData.MFCC); 
					distance += mfccDistance;
					//System.out.println("MFCC: " + mfccDistance);
					break;
				default:
					break;
				}
			}
			this.audioDistanceMap.put(audioFileName, distance);
		}

    	//sort and return results
        SortHashMapByValue sortHM = new SortHashMapByValue(20);
        ArrayList<String> result = sortHM.sort(this.audioDistanceMap);
        
        ArrayList<AudioData> resultData = new ArrayList<>();
        for (String itemName : result) {
        	AudioData audioData = this.audioDataMap.get(itemName);
			resultData.add(audioData);
		}
        
        return resultData;
    }
    
    public ArrayList<AudioData> retrieveResultList(File query, DistanceMethod distanceMethod, Vector<AnalyzedFeature> analyzedFeatures){
        AudioData queryAudioData;
        if (this.audioDataMap.containsKey(query)) {
            queryAudioData = this.audioDataMap.get(query.getName());
        } else {
            queryAudioData = featureExtractor.readFile(query);
        }
        return retrieveResultList(queryAudioData, distanceMethod, analyzedFeatures);
    }
    
    public ArrayList<AudioData> retrieveResultList(String query, DistanceMethod distanceMethod, Vector<AnalyzedFeature> analyzedFeatures){
        return retrieveResultList(this.audioDataMap.get(query), distanceMethod, analyzedFeatures);
    }
    
    public ArrayList<AudioData> getFinalResultFromFeedbackResult( ArrayList<AudioData> resultItems,
            ArrayList<AudioData> relevantFeedbackItems, ArrayList< ArrayList<AudioData> > feedbackResults) {
        ArrayList<AudioData> combinedResults = new ArrayList<>();
        //add all feed back to the result
        for (AudioData feedbackItem : relevantFeedbackItems) {
            combinedResults.add(feedbackItem);
        }
        
        feedbackResults.add(resultItems);
        int[] currentPickingPositions = new int[feedbackResults.size()]; 
        int index = 0;
        while(combinedResults.size() < resultItems.size()) {
            ArrayList<AudioData> chosenResultList = feedbackResults.get(index);
            AudioData audioItem = chosenResultList.get(currentPickingPositions[index]);
            if(!isItemInResultList(combinedResults, audioItem)) {
                combinedResults.add(audioItem);
            }
            currentPickingPositions[index]++;
            index = (index + 1) % feedbackResults.size();
        }
        return combinedResults;
    }
    
    public ArrayList< ArrayList<AudioData> > getResultsFromFeedbackItems(ArrayList<AudioData> feedbackItems, 
            DistanceMethod distanceMethod, Vector<AnalyzedFeature> analyzedFeatures) {
        ArrayList< ArrayList<AudioData> > results = new ArrayList<>();
        for (AudioData relevantFeedbackItem : feedbackItems) {
            ArrayList<AudioData> additionalResults = retrieveResultList(relevantFeedbackItem.Name, distanceMethod, analyzedFeatures);
            results.add(additionalResults);
        }
        return results;
    }
    
    public Evaluation evaluateFeatures(DistanceMethod distanceMethod, Vector<AnalyzedFeature> analyzedFeatures, boolean feedback) {
        int i = 1;
        Evaluation evaluation = new Evaluation();
        for (String query: queryFiles) {
            System.out.printf("%s/%s\n", i, queryFiles.size());
            ArrayList<AudioData> result = retrieveResultList(query, distanceMethod, analyzedFeatures);
            String category = Tool.Utils.getCategoryFromFileName(query);
            if (feedback) {
                ArrayList<AudioData> relevantFeedbackItems = new ArrayList<AudioData>();
                for (AudioData audioData: result) {
                    String resultCategory = Tool.Utils.getCategoryFromFileName(audioData.Name);
                    if (category.equals(resultCategory)) {
                        relevantFeedbackItems.add(audioData);
                    }
                }
                ArrayList< ArrayList<AudioData> > additionalResults = getResultsFromFeedbackItems(relevantFeedbackItems, distanceMethod, analyzedFeatures);
                if(!additionalResults.isEmpty()) {
                    result = getFinalResultFromFeedbackResult(result, relevantFeedbackItems, additionalResults);
                }
            }
            double averagePrecision = AveragePrecision.analyze(query, result, this.categoryCount.get(category));
            evaluation.addAveragePrecision(category, averagePrecision);
            i++;
        }
        return evaluation;       
    }
    
    public void printEvaluation(String query, ArrayList<AudioData> result) {
        //before return result, do some evaluations
        String category = Tool.Utils.getCategoryFromFileName(query);
        if (this.categoryCount.containsKey(category)) {
            System.out.println("=======EVALUATION============================");
            System.out.println("Precision: " + Precision.analyze(query, result));
            System.out.println("Recall: " + Recall.analyze(query, result, this.categoryCount.get(category)));
            System.out.println("Average Precision: " + AveragePrecision.analyze(query, result, this.categoryCount.get(category)));
        }
    }
    
   //private helper methods
    private double calculateDistanceForArray(DistanceMethod distanceMethod, double[] data1, double[] data2) {
    	double distance  = 0;
    	switch (distanceMethod) {
		case Cosine:
			distance = Cosine.getDistance(data1, data2);
			break;
		case CityBlock:
			distance = CityBlock.getDistance(data1, data2);
			break;
		case Euclidean: 
			distance = Euclidean.getDistance(data1, data2);
			break;
		}
		return distance;
	}

    private double calculateDistanceForNumber(DistanceMethod distanceMethod, double value1, double value2) {
    	double distance  = 0;
    	double[] arrayValue1 = {value1};
    	double[] arrayValue2 = {value2};
    	switch (distanceMethod) {
		case Cosine:
			distance = Cosine.getDistance(arrayValue1, arrayValue2);
			break;
		case CityBlock:
			distance = CityBlock.getDistance(arrayValue1, arrayValue2);
			break;
		case Euclidean:
			distance = Euclidean.getDistance(arrayValue1, arrayValue2);
			break;
		}
		return distance;
    }
    
    private double calculateDistanceForMatrix(DistanceMethod distanceMethod, double[][] matrix1, double[][] matrix2) {
    	double distance  = 0;
    	if(matrix1.length != matrix2.length)
    		return -1000; //return a very big number
    	for (int i = 0; i < matrix1.length; i++) {
    		switch (distanceMethod) {
    		case Cosine:
    			distance += Cosine.getDistance(matrix1[i], matrix2[i]);
    			break;
    		case CityBlock:
    			distance += CityBlock.getDistance(matrix1[i], matrix2[i]);
    			break;
    		case Euclidean: 
    			distance += Euclidean.getDistance(matrix1[i], matrix2[i]);
    			break;
    		}
		}
		return distance / matrix1.length;
    }
    
    private boolean isItemInResultList(ArrayList<AudioData> list, AudioData item) {
        for (AudioData audioData : list) {
            if(audioData.Name.equals(item.Name))
                return true;
        }
        return false;
    }
}
