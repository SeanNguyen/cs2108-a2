package Search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import Feature.Energy;
import Feature.MFCC;
import Feature.MagnitudeSpectrum;
import Feature.ZeroCrossing;
import SignalProcess.WaveIO;
import Tool.Utils;

public class FeatureExtractor {
	protected final static String featureDataPath = "data\\feature\\";
	protected final static String trainPath = "data\\input\\train\\";
	protected final static String magnitudeSpectrumDataFile = "magnitudeSpectrum.txt";
	protected final static String zeroCrossingDataFile = "zeroCrossing.txt";
	protected final static String energyDataFile = "energy.txt";
	protected final static String mfccDataFile = "mfcc.txt";

	// public methods
	/***
	 * Get the feature of train set via the specific feature extraction method,
	 * and write it into offline file for efficiency; Please modify this
	 * function, select or combine the methods (in the Package named 'Feature')
	 * to extract feature, such as Zero-Crossing, Energy, Magnitude- Spectrum
	 * and MFCC by yourself.
	 * 
	 * @return the map of training features, Key is the name of file, Value is
	 *         the array/vector of features.
	 */
	public void calcuteFeatureData() {
		File trainFolder = new File(trainPath);
		File[] audioFiles = trainFolder.listFiles();

		// load all the audio files
		short[][] audioSignals = getSignalFromAudioFiles(audioFiles);
		// calculate data for all features
		if(!Utils.isFileExist(featureDataPath + magnitudeSpectrumDataFile)) {
			calculateMagnitudeSpectrum(audioFiles, audioSignals);
		}
		if(!Utils.isFileExist(featureDataPath + zeroCrossingDataFile)) {
			calculateZeroCrossing(audioFiles, audioSignals);
		}
		if(!Utils.isFileExist(featureDataPath + energyDataFile)) {
			calculateEnergy(audioFiles, audioSignals);
		}
		if(!Utils.isFileExist(featureDataPath + mfccDataFile)) {
			calculateMFCC(audioFiles, audioSignals);
		}
	}

	/**
	 * Load the offline file of features (the result of function
	 * 'trainFeatureList()');
	 * 
	 * @param featurePath
	 *            the path of offline file including the features of training
	 *            set.
	 * @return the map of training features, Key is the name of file, Value is
	 *         the array/vector of features.
	 */
	public void readFeatureData(HashMap<String, AudioData> audioDataMap, HashMap<String, Integer> categoryCount) {
		// prepare all the audioData instances
		initializeAudioDataMap(audioDataMap,categoryCount);

		// read all the features
		readMagnitudeSpectrum(audioDataMap);
		readZeroCrossing(audioDataMap);
		readEnergy(audioDataMap);
		readMFCC(audioDataMap);
	}

	// private helper methods
	private void calculateMagnitudeSpectrum(File[] files, short[][] audioSignals) {
		System.out.println("Processing Feature: Magnitude Spectrum");

		try {
			FileWriter fw = new FileWriter(
					FeatureExtractor.featureDataPath + FeatureExtractor.magnitudeSpectrumDataFile);
			for (int i = 0; i < audioSignals.length; i++) {
				MagnitudeSpectrum ms = new MagnitudeSpectrum();
				double[] msFeature = ms.getFeature(audioSignals[i]);
				String line = files[i].getName() + "\t";
				for (double f : msFeature) {
					line += f + "\t";
				}
				fw.append(line + "\n");
				System.out.println(">>" + files[i].getName());
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculateZeroCrossing(File[] files, short[][] audioSignals) {
		System.out.println("Processing Feature: Zero Crossing");

		try {
			FileWriter fw = new FileWriter(FeatureExtractor.featureDataPath + FeatureExtractor.zeroCrossingDataFile);
			for (int i = 0; i < audioSignals.length; i++) {
				double zeroCrossingResult = ZeroCrossing.getFeature(audioSignals[i]);
				String line = files[i].getName() + "\t" + zeroCrossingResult + "\t" + "\n";
				fw.append(line);
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculateEnergy(File[] files, short[][] audioSignals) {
		System.out.println("Processing Feature: Energy");

		try {
			FileWriter fw = new FileWriter(FeatureExtractor.featureDataPath + FeatureExtractor.energyDataFile);
			for (int i = 0; i < audioSignals.length; i++) {
				double[] msFeature = Energy.getFeature(audioSignals[i]);
				String line = files[i].getName() + "\t";
				for (double f : msFeature) {
					line += f + "\t";
				}
				fw.append(line + "\n");
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculateMFCC(File[] files, short[][] audioSignals) {
		System.out.println("Processing Feature: MFCC");

		try {
			FileWriter fw = new FileWriter(FeatureExtractor.featureDataPath + FeatureExtractor.mfccDataFile);
			for (int i = 0; i < audioSignals.length; i++) {
				MFCC mfcc = new MFCC();
				double[][] mfccData = mfcc.process(audioSignals[i]);
				String line = files[i].getName() + "\t" + mfccData.length + "\n";
				for (double[] mfccDataRow : mfccData) {
					for (double mfccDataEntry : mfccDataRow) {
						line += mfccDataEntry + "\t";
					}
					line += "\n";
				}
				fw.append(line);
				System.out.println(">>" + files[i].getName());
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
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

	private void readMagnitudeSpectrum(HashMap<String, AudioData> audioDataMap) {
		try {
			System.out.println("Reading Magnitude Spectrum Data");
			FileReader fr = new FileReader(
					FeatureExtractor.featureDataPath + FeatureExtractor.magnitudeSpectrumDataFile);
			BufferedReader br = new BufferedReader(fr);

			String line = br.readLine();
			while (line != null) {

				String[] split = line.trim().split("\t");
				if (split.length < 2)
					continue;
				double[] data = new double[split.length - 1];
				for (int i = 1; i < split.length; i++) {
					data[i - 1] = Double.valueOf(split[i]);
				}

				String fileName = split[0];
				AudioData audioData = audioDataMap.get(fileName);
				audioData.MagnitudeSpectrum = data;

				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readEnergy(HashMap<String, AudioData> audioDataMap) {
		try {
			System.out.println("Reading Energy Data");
			FileReader fr = new FileReader(FeatureExtractor.featureDataPath + FeatureExtractor.energyDataFile);
			BufferedReader br = new BufferedReader(fr);

			String line = br.readLine();
			while (line != null) {

				String[] split = line.trim().split("\t");
				if (split.length < 2)
					continue;
				double[] data = new double[split.length - 1];
				for (int i = 1; i < split.length; i++) {
					data[i - 1] = Double.valueOf(split[i]);
				}

				String fileName = split[0];
				AudioData audioData = audioDataMap.get(fileName);
				audioData.Energy = data;

				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readMFCC(HashMap<String, AudioData> audioDataMap) {
		try {
			System.out.println("Reading MFCC Data");
			FileReader fr = new FileReader(FeatureExtractor.featureDataPath + FeatureExtractor.mfccDataFile);
			BufferedReader br = new BufferedReader(fr);

			String line = br.readLine();
			while (line != null) {

				String[] split = line.trim().split("\t");
				if (split.length < 2)
					continue;

				int rowNumber = Integer.parseInt(split[1]);
				double[][] data = new double[rowNumber][];
				for (int i = 0; i < data.length; i++) {
					String rowLine = br.readLine();
					String[] rowLineArray = rowLine.split("\t");
					double[] row = new double[rowLineArray.length];
					for (int j = 0; j < rowLineArray.length; j++) {
						row[j] = Double.parseDouble(rowLineArray[j]);
					}
					data[i] = row;
				}

				String fileName = split[0];
				AudioData audioData = audioDataMap.get(fileName);
				audioData.MFCC = data;

				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readZeroCrossing(HashMap<String, AudioData> audioDataMap) {
		try {
			System.out.println("Reading Zero Crossing Data");
			FileReader fr = new FileReader(FeatureExtractor.featureDataPath + FeatureExtractor.zeroCrossingDataFile);
			BufferedReader br = new BufferedReader(fr);

			String line = br.readLine();
			while (line != null) {
				String[] split = line.trim().split("\t");
				if (split.length < 2)
					continue;

				String fileName = split[0];
				double ZeroCrossingValue = Double.parseDouble(split[1]);
				AudioData audioData = audioDataMap.get(fileName);
				audioData.ZeroCrossing = ZeroCrossingValue;

				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeAudioDataMap(HashMap<String, AudioData> audioDataMap, HashMap<String, Integer> categoryCount) {
		File trainFolder = new File(trainPath);
		File[] audioFiles = trainFolder.listFiles();
		for (File file : audioFiles) {
			AudioData audioData = new AudioData();
			audioData.Path = file.getAbsolutePath();
			audioData.Name = file.getName();
			audioDataMap.put(file.getName(), audioData);
			
			//plus 1 to its category
			String category = Utils.getCategoryFromFileName(file.getName());
			if(categoryCount.get(category) != null) {
				int count = categoryCount.get(category);
				categoryCount.put(category, ++count);
			} else {
				categoryCount.put(category, 1);
			}
		}
	}
}
