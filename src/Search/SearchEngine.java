package Search;

import java.util.*;

import Distance.CityBlock;
import Distance.Cosine;
import Distance.Euclidean;
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
    private FeatureExtractor featureExtractor = new FeatureExtractor();
    
    
    //Constructor
    public SearchEngine() {
    	//Process all the audio files here
    	this.featureExtractor.calcuteFeatureData();
    	//then read them
    	this.featureExtractor.readFeatureData(audioDataMap);
    }

    //Public methods
    /***
     * Get the distances between features of the selected query audio and ones of the train set;
     * Please modify this function, select or combine the suitable and feasible methods (in the package named 'Distance') to calculate the distance,
     * such as CityBlock, Cosine and Euclidean by yourself.
     * @param query the selected query audio file;
     * @return the top 20 similar audio files;
     */
    public ArrayList<String> retrieveResultList(String query, DistanceMethod distanceMethod, Vector<AnalyzedFeature> analyzedFeatures){
    	//Assume the query only come from the train/test folder
    	AudioData queryAudioData = this.audioDataMap.get(query);
    	
    	for (String audioFileName : this.audioDataMap.keySet()) {
    		this.audioDistanceMap.put(audioFileName, 0.0);
    		AudioData comparedAudioData = this.audioDataMap.get(audioFileName);
			Double distance = this.audioDistanceMap.get(audioFileName);
			for (AnalyzedFeature analyzedFeature : analyzedFeatures) {
				switch (analyzedFeature) {
				case Energy:
					distance += calculateDistanceForArray(distanceMethod, comparedAudioData.Energy, queryAudioData.Energy);
					break;
				case ZeroCrossing:
					distance += calculateDistanceForNumber(distanceMethod, comparedAudioData.ZeroCrossing, queryAudioData.ZeroCrossing);
					break;
				case MagnitudeSpectrum:
					distance += calculateDistanceForArray(distanceMethod, comparedAudioData.MagnitudeSpectrum, queryAudioData.MagnitudeSpectrum);
					break;
				case MFCC:
					distance += calculateDistanceForMatrix(distanceMethod, comparedAudioData.MFCC, queryAudioData.MFCC);
					break;
				default:
					break;
				}
			}
		}

    	//sort and return results
        SortHashMapByValue sortHM = new SortHashMapByValue(20);
        ArrayList<String> result = sortHM.sort(this.audioDistanceMap);
        return result;
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
