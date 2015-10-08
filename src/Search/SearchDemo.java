package Search;

import Feature.Energy;
import Feature.MFCC;
import Feature.MagnitudeSpectrum;
import Feature.ZeroCrossing;
import SignalProcess.WaveIO;
import Distance.Cosine;
import Tool.SortHashMapByValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by workshop on 9/18/2015.
 */
public class SearchDemo {
    private HashMap<String, AudioData> audioDataMap = new HashMap<>();
    private FeatureExtractor featureExtractor = new FeatureExtractor();
    
    //Constructor
    public SearchDemo() {
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
    public ArrayList<String> resultList(String query){
        WaveIO waveIO = new WaveIO();

        short[] inputSignal = waveIO.readWave(query);
        MagnitudeSpectrum ms = new MagnitudeSpectrum();
        double[] msFeature1 = ms.getFeature(inputSignal);
        HashMap<String, Double> simList = new HashMap<String, Double>();

        /**
         * Example of calculating the distance via Cosine Similarity, modify it by yourself please.
         */
        Cosine cosine = new Cosine();

//        System.out.println(trainFeatureList.size() + "=====");
//        for (Map.Entry f: trainFeatureList.entrySet()){
//            simList.put((String)f.getKey(), cosine.getDistance(msFeature1, (double[]) f.getValue()));
//        }
//
//        SortHashMapByValue sortHM = new SortHashMapByValue(20);
//        ArrayList<String> result = sortHM.sort(simList);
        
        return new ArrayList<>();
    }
}
