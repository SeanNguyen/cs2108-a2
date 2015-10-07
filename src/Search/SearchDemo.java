package Search;

import Feature.MagnitudeSpectrum;
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
    /**
     * Please replace the 'trainPath' with the specific path of train set in your PC.
     */
	protected final static String featureDataPath = "data\\feature";
	protected final static String trainPath = "data\\input\\train";
    protected final static String magnitudeSpectrumDataFile = "magnitudeSpectrum.txt";
    
    //Attributes
    private HashMap<String, AudioData> audioDataMap = new HashMap<>();
    
    public SearchDemo() {
    	//Process all the audio files here
    	trainFeatureList();
    	//then read them
    	readFeature();
    }
    
    /***
     * Get the feature of train set via the specific feature extraction method, and write it into offline file for efficiency;
     * Please modify this function, select or combine the methods (in the Package named 'Feature') to extract feature, such as Zero-Crossing, Energy, Magnitude-
     * Spectrum and MFCC by yourself.
     * @return the map of training features, Key is the name of file, Value is the array/vector of features.
     */
     public void trainFeatureList(){
        File trainFolder = new File(trainPath);
        File[] audioFiles = trainFolder.listFiles();
        
        //load all the audio files
        short[][] audioSignals = getSignalFromAudioFiles(audioFiles);
        //calculate data for all features
        calculateMagnitudeSpectrum(audioFiles, audioSignals);
    }

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

    //private helper methods
    /**
     * Load the offline file of features (the result of function 'trainFeatureList()');
     * @param featurePath the path of offline file including the features of training set.
     * @return the map of training features, Key is the name of file, Value is the array/vector of features.
     */
    private void readFeature(){
    	//prepare all the audioData instances
    	initializeAudioDataMap();
        
        //read all the features
    	readMagnitudeSpectrum();
    }

    private void readMagnitudeSpectrum() {
        try{
            FileReader fr = new FileReader(SearchDemo.featureDataPath + SearchDemo.magnitudeSpectrumDataFile);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while(line != null){

                String[] split = line.trim().split("\t");
                if (split.length < 2)
                    continue;
                double[] data = new double[split.length - 1];
                for (int i = 1; i < split.length; i ++){
                    data[i-1] = Double.valueOf(split[i]);
                }

                String fileName = split[0];
                AudioData audioData = this.audioDataMap.get(fileName);
                audioData.MagnitudeSpectrum = data;

                line = br.readLine();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private void calculateMagnitudeSpectrum(File[] files, short[][] audioSignals) {
    	System.out.println("Processing Feature: Magnitude Spectrum");
        
        try {
            FileWriter fw = new FileWriter(SearchDemo.featureDataPath + SearchDemo.magnitudeSpectrumDataFile);
            for (int i = 0; i < audioSignals.length; i++) {
                MagnitudeSpectrum ms = new MagnitudeSpectrum();
                double[] msFeature = ms.getFeature(audioSignals[i]);
                String line = files[i].getName() + "\t";
                for (double f: msFeature){
                    line += f + "\t";
                }
                fw.append(line+"\n");
                System.out.println(">>" + files[i].getName());
            }
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initializeAudioDataMap() {
    	File trainFolder = new File(trainPath);
        File[] audioFiles = trainFolder.listFiles();
        for (File file : audioFiles) {
        	AudioData audioData = new AudioData();
        	audioData.Path = file.getAbsolutePath();
			this.audioDataMap.put(file.getName(), new AudioData());
		}
    }

    private short[][] getSignalFromAudioFiles(File[] audioFiles) {
    	short[][] audioSignals = new short[audioFiles.length][];
        for (int i = 0; i < audioFiles.length; i++) {
            WaveIO waveIO = new WaveIO();
            short[] signal = waveIO.readWave(audioFiles[i].getAbsolutePath());
            audioSignals[i] = signal;
        }
        return audioSignals;
    }
}
