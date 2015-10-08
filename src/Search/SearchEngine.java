package Search;

import java.util.*;

import com.sun.javafx.util.Utils;

import Distance.CityBlock;
import Distance.Cosine;
import Distance.Euclidean;
import Evaluation.Precision;
import Evaluation.Recall;
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
    private FeatureExtractor featureExtractor = new FeatureExtractor();
    
    
    //Constructor
    public SearchEngine() {
    	//Process all the audio files here
    	this.featureExtractor.calcuteFeatureData();
    	//then read them
    	this.featureExtractor.readFeatureData(audioDataMap, categoryCount);
    }

    //Public methods
    /***
     * Get the distances between features of the selected query audio and ones of the train set;
     * Please modify this function, select or combine the suitable and feasible methods (in the package named 'Distance') to calculate the distance,
     * such as CityBlock, Cosine and Euclidean by yourself.
     * @param query the selected query audio file;
     * @return the top 20 similar audio files;
     */
    public ArrayList<AudioData> retrieveResultList(String query, DistanceMethod distanceMethod, Vector<AnalyzedFeature> analyzedFeatures){
    	//Assume the query only come from the train/test folder
    	AudioData queryAudioData = this.audioDataMap.get(query);
    	
    	for (String audioFileName : this.audioDataMap.keySet()) {
    		AudioData comparedAudioData = this.audioDataMap.get(audioFileName);
			double distance = 0.0;
			for (AnalyzedFeature analyzedFeature : analyzedFeatures) {
				switch (analyzedFeature) {
				case Energy:
					double energyDistance = calculateDistanceForArray(distanceMethod, comparedAudioData.Energy, queryAudioData.Energy); 
					distance += energyDistance;
					System.out.println("Energy: " + energyDistance);
					break;
				case ZeroCrossing:
					double zeroCrossingDistance = calculateDistanceForNumber(distanceMethod, comparedAudioData.ZeroCrossing, queryAudioData.ZeroCrossing); 
					distance += zeroCrossingDistance;
					System.out.println("Zero Crossing: " + zeroCrossingDistance);
					break;
				case MagnitudeSpectrum:
					double magnitudeSpectrumDistance = calculateDistanceForArray(distanceMethod, comparedAudioData.MagnitudeSpectrum, queryAudioData.MagnitudeSpectrum); 
					distance += magnitudeSpectrumDistance;
					System.out.println("Magnitude Spectrum: " + magnitudeSpectrumDistance);
					break;
				case MFCC:
					double mfccDistance = calculateDistanceForMatrix(distanceMethod, comparedAudioData.MFCC, queryAudioData.MFCC); 
					distance += mfccDistance;
					System.out.println("MFCC: " + mfccDistance);
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
        
        //before return result, do some evaluations
        System.out.println("=======EVALUATION============================");
        System.out.println("Precision: " + Precision.analyze(query, result));
        String category = Tool.Utils.getCategoryFromFileName(query);
        System.out.println("Recall: " + Recall.analyze(query, result, this.categoryCount.get(category)));
        
        ArrayList<AudioData> resultData = new ArrayList<>();
        for (String itemName : result) {
        	AudioData audioData = this.audioDataMap.get(itemName);
			resultData.add(audioData);
		}
        
        return resultData;
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
    			distance = Cosine.getDistance(matrix1[i], matrix2[i]);
    			break;
    		case CityBlock:
    			distance = CityBlock.getDistance(matrix1[i], matrix2[i]);
    			break;
    		case Euclidean: 
    			distance = CityBlock.getDistance(matrix1[i], matrix2[i]);
    			break;
    		}
		}
		return distance / matrix1.length;
    }
}
