package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;

import Player.AudioFilter;
import Player.SoundEffect;
import Search.AudioData;
import Search.FeatureExtractor;
import Search.SearchEngine;
import Search.SearchEngine.AnalyzedFeature;
import Search.SearchEngine.DistanceMethod;

public class AudioSearchUI extends JFrame implements ActionListener {
    //attributes
    File queryAudioFile = null;
    int resultSize = 20;
    /**
     * If need, please replace the 'querySet' with specific path of test set of audio files in your PC.
     */
    String querySet = "data/input/";
    /**
     * Please Replace the 'basePath' with specific path of train set of audio files in your PC.
     */
    ArrayList<AudioData> resultItems = new ArrayList<>();
    
    SearchEngine searchEngine = new SearchEngine();
    
	//UI Components
	JPanel contentPane;
    JButton openButton, searchButton, queryButton;
    JFileChooser fileChooser;
    
    JRadioButton distanceCityBlockRadioButton;
    JRadioButton distanceCosineRadioButton;
    JRadioButton distanceEuclideanRadioButton;
    
    JCheckBox featureEnergyCheckbox;
    JCheckBox featureZeroCrossingCheckbox;
    JCheckBox featureMfccCheckbox;
    JCheckBox featureMagnitudeSpectrumCheckbox;
    
    JButton[] resultButton = new JButton[resultSize];
    JLabel [] resultLabels = new JLabel[resultSize];
    JCheckBox[] resultCheckboxes = new JCheckBox[resultSize];

    // Constructor
    public AudioSearchUI() {
    	// Pre-load all the sound files
        queryAudioFile = null;
        SoundEffect.volume = SoundEffect.Volume.HIGH;  // un-mute
    }

    //public methods
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == openButton){
        	chooseQueryFile();
        }else if (e.getSource() == searchButton){
        	search();
        }else if (e.getSource() == queryButton){
        	playQueryAudio();
        }else {
            playResultAudio(e);
        }
    }

    public void start() {
        // Set up UI components;
    	//Select query file button
        openButton = new JButton("Select an audio clip...");
        openButton.addActionListener(this);

        //play current audio button
        queryButton = new JButton("Current Audio:");
        queryButton.addActionListener(this);

        //perform search button
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);

        //add all of that to the query panel
        JPanel queryPanel = new JPanel();
        queryPanel.add(openButton);
        queryPanel.add(queryButton);
        queryPanel.add(searchButton);
        
        //option panel
        JLabel distanceOptionLabel = new JLabel("Distance Calculation Method: ");
        ButtonGroup distanceOptionButtonGroup = new ButtonGroup();
        this.distanceCityBlockRadioButton = new JRadioButton("City Block");
        this.distanceCosineRadioButton = new JRadioButton("Cosine");
        this.distanceEuclideanRadioButton = new JRadioButton("Euclidean");
        
        distanceOptionButtonGroup.add(this.distanceCityBlockRadioButton);
        distanceOptionButtonGroup.add(this.distanceCosineRadioButton);
        distanceOptionButtonGroup.add(this.distanceEuclideanRadioButton);
        this.distanceCosineRadioButton.setSelected(true);
        
        JPanel distanceOptionPanel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.LEFT));
        distanceOptionPanel.add(distanceOptionLabel);
        distanceOptionPanel.add(this.distanceCityBlockRadioButton);
        distanceOptionPanel.add(this.distanceCosineRadioButton);
        distanceOptionPanel.add(this.distanceEuclideanRadioButton);
        
        JLabel featureOptionLabel = new JLabel("Feature Combination Method: ");
        this.featureEnergyCheckbox = new JCheckBox("Energy");
        this.featureZeroCrossingCheckbox = new JCheckBox("Zero Crossing");
        this.featureMagnitudeSpectrumCheckbox = new JCheckBox("Magnitude Spectrum");
        this.featureMfccCheckbox = new JCheckBox("MFCC");
        
        JPanel featureOptionPanel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.LEFT));
        featureOptionPanel.add(featureOptionLabel);
        featureOptionPanel.add(this.featureEnergyCheckbox);
        featureOptionPanel.add(this.featureZeroCrossingCheckbox);
        featureOptionPanel.add(this.featureMagnitudeSpectrumCheckbox);
        featureOptionPanel.add(this.featureMfccCheckbox);
        
        JPanel topPanel = new JPanel();
        topPanel.setSize(800, 100);
        topPanel.setLayout((LayoutManager) new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(queryPanel);
        topPanel.add(distanceOptionPanel);
        topPanel.add(featureOptionPanel);
        
        //result panel
        JPanel resultPanel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.LEFT));
        resultPanel.setLayout(new GridLayout(0, 3, 5, 5));

        for (int i = 0; i < resultLabels.length; i ++){
            resultLabels[i] = new JLabel();

            resultCheckboxes[i] = new JCheckBox("(relevant)");
            resultCheckboxes[i].setVisible(false);
            
            resultButton[i] = new JButton(resultLabels[i].getText());
            resultButton[i].addActionListener(this);
            resultButton[i].setVisible(false);
            
            resultPanel.add(resultLabels[i]);
            resultPanel.add(resultButton[i]);
            resultPanel.add(resultCheckboxes[i]);
        }


        resultPanel.setBorder(BorderFactory.createEmptyBorder(30,16,10,16));

        contentPane = (JPanel)this.getContentPane();
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane.add(topPanel, BorderLayout.PAGE_START);
        contentPane.add(resultPanel, BorderLayout.PAGE_END);

        contentPane.setVisible(true);
        setVisible(true);
    }

    //private helper methods
    private void chooseQueryFile() {
    	if (fileChooser == null) {
            fileChooser = new JFileChooser(querySet);

            fileChooser.addChoosableFileFilter(new AudioFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);
        }
        int returnVal = fileChooser.showOpenDialog(AudioSearchUI.this);

        if (returnVal == JFileChooser.APPROVE_OPTION){
            queryAudioFile = fileChooser.getSelectedFile();
        }

        fileChooser.setSelectedFile(null);

        queryButton.setText(queryAudioFile.getName());

        fileChooser.setSelectedFile(null);
    }
    
    private void search() {
    	//check if a file is chosen
    	if(queryAudioFile == null)
    		return;
    	//prepare query
    	ArrayList<AudioData> relevantFeedbackItems = getRelevantFeedbackItems();
    	resetRelevantFeedbackCheckboxes();
    	SearchEngine.DistanceMethod distanceMethod = getQueryDistanceMethod();
    	Vector<SearchEngine.AnalyzedFeature> analyzedFeatures = getQyeryAnalyzeFeatures();
        
    	//query
    	resultItems = searchEngine.retrieveResultList(queryAudioFile.getName(), distanceMethod, analyzedFeatures);
    	
    	//process relevant feedback if have
    	ArrayList< ArrayList<AudioData> > additionalResults = getResultsFromFeedbackItems(relevantFeedbackItems, distanceMethod, analyzedFeatures);
    	if(!additionalResults.isEmpty()) {
			this.resultItems = getFinalResultFromFeedbackResult(relevantFeedbackItems, additionalResults);
    	}

        for (int i = 0; i < resultItems.size(); i ++){
            resultLabels[i].setText(i + 1 + ". " + resultItems.get(i).Name);
            resultCheckboxes[i].setVisible(true);
            resultButton[i].setText("Play");
            resultButton[i].setVisible(true);
        }
    }
    
    private ArrayList<AudioData> getFinalResultFromFeedbackResult(ArrayList<AudioData> relevantFeedbackItems, 
    		ArrayList< ArrayList<AudioData> > feedbackResults) {
    	ArrayList<AudioData> combinedResults = new ArrayList<>();
		//add all feed back to the result
		for (AudioData feedbackItem : relevantFeedbackItems) {
			combinedResults.add(feedbackItem);
		}
		
		feedbackResults.add(this.resultItems);
		int[] currentPickingPositions = new int[feedbackResults.size()]; 
		int index = 0;
		while(combinedResults.size() < this.resultSize) {
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
    
    private ArrayList< ArrayList<AudioData> > getResultsFromFeedbackItems(ArrayList<AudioData> feedbackItems, 
    		DistanceMethod distanceMethod, Vector<AnalyzedFeature> analyzedFeatures) {
    	ArrayList< ArrayList<AudioData> > results = new ArrayList<>();
    	for (AudioData relevantFeedbackItem : feedbackItems) {
			ArrayList<AudioData> additionalResults = searchEngine.retrieveResultList(relevantFeedbackItem.Name, distanceMethod, analyzedFeatures);
			results.add(additionalResults);
		}
    	return results;
    }
    
    private Vector<SearchEngine.AnalyzedFeature> getQyeryAnalyzeFeatures() {
    	Vector<SearchEngine.AnalyzedFeature> analyzedFeatures = new Vector<>();
    	if(this.featureEnergyCheckbox.isSelected()) {
    		analyzedFeatures.addElement(SearchEngine.AnalyzedFeature.Energy);
    	}
    	if(this.featureMagnitudeSpectrumCheckbox.isSelected()) {
    		analyzedFeatures.addElement(SearchEngine.AnalyzedFeature.MagnitudeSpectrum);
    	}
    	if(this.featureMfccCheckbox.isSelected()) {
    		analyzedFeatures.addElement(SearchEngine.AnalyzedFeature.MFCC);
    	}
    	if(this.featureZeroCrossingCheckbox.isSelected()) {
    		analyzedFeatures.addElement(SearchEngine.AnalyzedFeature.ZeroCrossing);
    	}
    	return analyzedFeatures;
    }
    
    private SearchEngine.DistanceMethod getQueryDistanceMethod() {
    	SearchEngine.DistanceMethod distanceMethod;
    	if(this.distanceCityBlockRadioButton.isSelected()) {
    		distanceMethod = SearchEngine.DistanceMethod.CityBlock;
    	} else if(this.distanceCosineRadioButton.isSelected()) {
        	distanceMethod = SearchEngine.DistanceMethod.Cosine;
    	} else {
    		distanceMethod = SearchEngine.DistanceMethod.Euclidean;
    	}
    	return distanceMethod;
    }
    
    private void resetRelevantFeedbackCheckboxes() {
    	for (JCheckBox checkbox : this.resultCheckboxes) {
			checkbox.setSelected(false);
		}
    }
    
    private ArrayList<AudioData> getRelevantFeedbackItems() {
    	ArrayList<AudioData> results = new ArrayList<>();
    	for (int i = 0; i < this.resultCheckboxes.length; i++) {
			if(!this.resultCheckboxes[i].isSelected())
				continue;
			results.add(this.resultItems.get(i));
		}
    	return results;
    }
    
    private void playQueryAudio() {
    	SoundEffect sound = new SoundEffect(queryAudioFile.getAbsolutePath());
    	sound.play();
    }
    
    private void playResultAudio(ActionEvent e) {
    	for (int i = 0; i < resultSize; i ++){
            if (e.getSource() == resultButton[i]){
                String filePath = resultItems.get(i).Path;
                new SoundEffect(filePath).play();
                break;
            }
        }
    }
    
    private boolean isItemInResultList(ArrayList<AudioData> list, AudioData item) {
    	for (AudioData audioData : list) {
			if(audioData.Name.equals(item.Name))
				return true;
		}
    	return false;
    }
    
    //initializer

    public static void main(String[] args) {
    	AudioSearchUI ui = new AudioSearchUI();
    	ui.start();
    }
}