package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Player.AudioFilter;
import Player.SoundEffect;
import Search.SearchDemo;

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
    String basePath = "./";
    ArrayList<String> resultFiles = new ArrayList<String>();
    
    SearchDemo searchDemo = new SearchDemo();
    
	//UI Components
	JPanel contentPane;
    JButton openButton, searchButton, queryButton;
    JFileChooser fileChooser;
    JButton[] resultButton = new JButton[resultSize];
    JLabel [] resultLabels = new JLabel[resultSize];

    // Constructor
    public AudioSearchUI() {
    	// Pre-load all the sound files
        queryAudioFile = null;
        SoundEffect.volume = SoundEffect.Volume.LOW;  // un-mute
    }

    //public methods
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == openButton){
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

        }else if (e.getSource() == searchButton){
            resultFiles = searchDemo.resultList(queryAudioFile.getAbsolutePath());

            for (int i = 0; i < resultFiles.size(); i ++){
                resultLabels[i].setText(resultFiles.get(i));
                resultButton[i].setText(resultFiles.get(i));
                resultButton[i].setVisible(true);
            }

        }else if (e.getSource() == queryButton){
            new SoundEffect(queryAudioFile.getAbsolutePath()).play();
        }else {
            for (int i = 0; i < resultSize; i ++){
                if (e.getSource() == resultButton[i]){
                    String filePath = basePath+resultFiles.get(i);
                    new SoundEffect(filePath).play();
                    break;
                }
            }
        }
    }

    public void start() {
        // Set up UI components;
        openButton = new JButton("Select an audio clip...");
        openButton.addActionListener(this);

        String tempName = "";

        queryButton = new JButton("Current Audio:"+tempName);
        queryButton.addActionListener(this);

        searchButton = new JButton("Search");
        searchButton.addActionListener(this);

        JPanel queryPanel = new JPanel();
        queryPanel.add(openButton);
        queryPanel.add(queryButton);
        queryPanel.add(searchButton);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new GridLayout(0, 4, 60, 60));

        for (int i = 0; i < resultLabels.length; i ++){
            resultLabels[i] = new JLabel();

            resultButton[i] = new JButton(resultLabels[i].getText());

            resultButton[i].addActionListener(this);

            resultButton[i].setVisible(false);
            resultPanel.add(resultLabels[i]);
            resultPanel.add(resultButton[i]);
        }


        resultPanel.setBorder(BorderFactory.createEmptyBorder(30,16,10,16));

        contentPane = (JPanel)this.getContentPane();
        setSize(800,900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane.add(queryPanel, BorderLayout.PAGE_START);
        contentPane.add(resultPanel, BorderLayout.CENTER);

        contentPane.setVisible(true);
        setVisible(true);
    }
    
    //initializer
    public static void main(String[] args) {
    	AudioSearchUI ui = new AudioSearchUI();
    	ui.start();
    }
}