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
    /**
     * Please replace the 'trainPath' with the specific path of train set in your PC.
     */
	protected final static String featureDataPath = "data\\feature\\";
	protected final static String trainPath = "data\\input\\train\\";
    protected final static String magnitudeSpectrumDataFile = "magnitudeSpectrum.txt";
    protected final static String zeroCrossingDataFile = "zeroCrossing.txt";
    protected final static String energyDataFile = "energy.txt";
    protected final static String mfccDataFile = "mfcc.txt";
    
    //Attributes
    private HashMap<String, AudioData> audioDataMap = new HashMap<>();
    
    //Constructor
    public SearchDemo() {
    	//Process all the audio files here
    	trainFeatureList();
    	//then read them
    	readFeature();
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

    //private helper methods
    /***
     * Get the feature of train set via the specific feature extraction method, and write it into offline file for efficiency;
     * Please modify this function, select or combine the methods (in the Package named 'Feature') to extract feature, such as Zero-Crossing, Energy, Magnitude-
     * Spectrum and MFCC by yourself.
     * @return the map of training features, Key is the name of file, Value is the array/vector of features.
     */
     private void trainFeatureList(){
        File trainFolder = new File(trainPath);
        File[] audioFiles = trainFolder.listFiles();
        
        //load all the audio files
        short[][] audioSignals = getSignalFromAudioFiles(audioFiles);
        //calculate data for all features
        calculateMagnitudeSpectrum(audioFiles, audioSignals);
        calculateZeroCrossing(audioFiles, audioSignals);
        calculateEnergy(audioFiles, audioSignals);
        calculateMFCC(audioFiles, audioSignals);
    }
    
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
    	readZeroCrossing();
    	readEnergy();
    	readMFCC();
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
    
    private void readEnergy() {
        try{
            FileReader fr = new FileReader(SearchDemo.featureDataPath + SearchDemo.energyDataFile);
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
                audioData.Energy = data;

                line = br.readLine();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private void readMFCC() {
        try{
            FileReader fr = new FileReader(SearchDemo.featureDataPath + SearchDemo.mfccDataFile);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while(line != null){

                String[] split = line.trim().split("\t");
                if (split.length < 2)
                    continue;
                
                int rowNumber = Integer.parseInt(split[1]);
                double[][] data = new double[rowNumber][];
                for (int i = 0; i < data.length; i ++){
                	String rowLine = br.readLine();
                	String[] rowLineArray = rowLine.split("\t");
                	double[] row = new double[rowLineArray.length];
                	for (int j = 0; j < rowLineArray.length; j++) {
                		row[j] = Double.parseDouble(rowLineArray[j]);
					}
                	data[i] = row;
                }

                String fileName = split[0];
                AudioData audioData = this.audioDataMap.get(fileName);
                audioData.MFCC = data;

                line = br.readLine();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private void readZeroCrossing() {
        try{
            FileReader fr = new FileReader(SearchDemo.featureDataPath + SearchDemo.zeroCrossingDataFile);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while(line != null){
                String[] split = line.trim().split("\t");
                if (split.length < 2)
                    continue;
                
                String fileName = split[0];
                double ZeroCrossingValue = Double.parseDouble(split[1]); 
                AudioData audioData = this.audioDataMap.get(fileName);
                audioData.ZeroCrossing = ZeroCrossingValue;

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
    
    private void calculateZeroCrossing(File[] files, short[][] audioSignals) {
    	System.out.println("Processing Feature: Zero Crossing");
        
        try {
            FileWriter fw = new FileWriter(SearchDemo.featureDataPath + SearchDemo.zeroCrossingDataFile);
            for (int i = 0; i < audioSignals.length; i++) {
                double zeroCrossingResult = ZeroCrossing.getFeature(audioSignals[i]);
                String line = files[i].getName() + "\t" + zeroCrossingResult + "\t" + "\n";
                fw.append(line);
            }
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private void calculateEnergy(File[] files, short[][] audioSignals) {
    	System.out.println("Processing Feature: Energy");
        
        try {
            FileWriter fw = new FileWriter(SearchDemo.featureDataPath + SearchDemo.energyDataFile);
            for (int i = 0; i < audioSignals.length; i++) {
                double[] msFeature = Energy.getFeature(audioSignals[i]);
                String line = files[i].getName() + "\t";
                for (double f: msFeature){
                    line += f + "\t";
                }
                fw.append(line+"\n");
            }
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private void calculateMFCC(File[] files, short[][] audioSignals) {
    	System.out.println("Processing Feature: MFCC");
        
        try {
            FileWriter fw = new FileWriter(SearchDemo.featureDataPath + SearchDemo.mfccDataFile);
            for (int i = 0; i < audioSignals.length; i++) {
            	MFCC mfcc = new MFCC();
                double[][] mfccData = mfcc.process(audioSignals[i]);
                String line = files[i].getName() + "\t" + mfccData.length + "\n";
                for (double[] mfccDataRow: mfccData){
                	for (double mfccDataEntry : mfccDataRow) {
                		line += mfccDataEntry + "\t";
					}
                	line += "\n";
                }
                fw.append(line);
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
