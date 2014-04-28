package core;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
 
public class ImageGUI extends JPanel implements ActionListener, ItemListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImageGUI() {
	}
	final static String PANELA = "HW1: Resolutions";
	final static String PANELB = "HW2: Filtering & Bitplanes";
    final static String PANEL1 = "Spatial Resolutions";
    final static String PANEL2 = "Grayscale Resolutions";
    final static String PANEL3 = "Power and Log Transforms";
    final static String PANEL4 = "Histogram Equalization";
    final static String PANEL5 = "Histogram Matching";
    final static String PANEL6 = "Spatial Filters";
    final static String PANEL7 = "Bitplanes";
    String filename = "image.pgm";
    int[][] originalImage;
    int[][] compareImage;
    int[][][] noisy = new int[8][464][448];
    JPanel picPanel, sliderPanel, transPanel, eqPanel, hmPanel, filterPanel, listPanel, bpPanel;
    final static int extraWindowWidth = 100;
    JCheckBox bitOne, bitTwo, bitThree, bitFour, bitFive, bitSix, bitSeven, bitEight;
    public int c = 255;
    public int A, filterMask, filterIndex;
    public boolean[] bitPlanes = new boolean[8];
    JLabel maskLabel, boostLabel, compareLabel;
    JSlider maskSlider, boostSlider;
    public double g = 1;
    public int mask = 640;
    public boolean logSelected, general;
    private static final ButtonGroup buttonGroup = new ButtonGroup();
    JFileChooser fc = new JFileChooser();
 
    //Methods involved with initializing GUI
    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
         
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    //scan in pgm array so we only need to scan it once
    private void scanImage() {
    	PgmImage scanImg = new PgmImage(filename);
        originalImage = scanImg.getPixels();
	}
    
    private void scanNoisy()	{
    	File folder = new File("Noisy_Images");
    	File[] listOfFiles = folder.listFiles();
    	for (int i = 0; i < listOfFiles.length; i++) {
    		PgmImage scanImg = new PgmImage("Noisy_Images/" + listOfFiles[i].getName());
    		noisy[i] = scanImg.getPixels();
    	}
    }
    /**
    * Create the GUI and show it.  For thread safety,
    * this method should be invoked from the
    * event dispatch thread.
    */
    private static void createAndShowGUI() {
    	//Create and set up the window.
        JFrame frame = new JFrame("ImageGUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Create and set up the content pane.
        ImageGUI run = new ImageGUI();
        run.scanImage();
        run.scanNoisy();
        run.setupGUI(frame.getContentPane());
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        
    }
    
    //apply algorithms and show results on GUI panels
    public void setupGUI(Container pane) {
    	//Set up tabbed GUI
    	JTabbedPane mainPane = new JTabbedPane();
        JTabbedPane resPane = new JTabbedPane();
        setupSpatial(resPane);
        setupGray(resPane);
        setupLP(resPane);
        setupHE(resPane);
        setupHM(resPane);
        JTabbedPane filPane = new JTabbedPane();
        setupFiltering(filPane);
        setupBitplanes(filPane);
        mainPane.addTab(PANELA, resPane);
        mainPane.addTab(PANELB, filPane);
        pane.add(mainPane, BorderLayout.CENTER);
    }
    
    //Spatial resolutions
    public void setupSpatial(JTabbedPane tabbedPane)	{
        //menu with my choices
        String[] listElements = {"Replication", "Nearest Neighbor", "Bilinear Interpolation"};
        JPanel listPanel = new JPanel(new BorderLayout());
        JList list = new JList(listElements);
        //listener that applies the corresponding algorithm to the image
        list.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
        		picPanel.removeAll();
        		JList list = (JList)e.getSource();
                switch (list.getSelectedIndex())	{
                	case 0: replMethod(); break;
                	case 1: nearMethod(); break;
                	case 2: bilinMethod(); break;
                }
                picPanel.revalidate();
                picPanel.repaint();
        	}
        });
        
        //panel that holds my pictures
        picPanel = new JPanel();
        //start panel with replication method
        list.setSelectedIndex(0);
        listPanel.add(list);
        //add listPanel and picPanel for spatial resolution
        JSplitPane card1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, picPanel);
        card1.setOneTouchExpandable(true);
        card1.setDividerLocation(120);

        //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 50);
        listPanel.setMinimumSize(minimumSize);
        picPanel.setMinimumSize(minimumSize);
        tabbedPane.addTab(PANEL1, card1);
    }
    
    //Grayscale resolutions
    public void setupGray(JTabbedPane tabbedPane)	{
        JPanel card2 = new JPanel();
        stepDownByBit(card2);
        tabbedPane.addTab(PANEL2, card2);
    }
    
    //Log and Power transformations
    public void setupLP(JTabbedPane tabbedPane)	{
        JPanel card3 = new JPanel(new BorderLayout());
        
        //picture in center
        transPanel = new JPanel();
        PgmImage img = new PgmImage(originalImage);
   	 	img.shrinkRes(320, 240);
   	 	transPanel.add(img);
   	    logTrans(c);
   	 	card3.add(transPanel, BorderLayout.CENTER);
   	 	
        //add calculate button
        JButton processButton = new JButton("Process!");
        processButton.setPreferredSize(new Dimension(1000, 100));
        processButton.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent e) {
				transPanel.removeAll();
				PgmImage img = new PgmImage(originalImage);
		   	 	img.shrinkRes(320, 240);
		   	 	transPanel.add(img);
				if(logSelected)	{
					logTrans(c);
				}
				else	{
					powerTrans(c, g);
				}
				transPanel.revalidate();
				transPanel.repaint();
			}
		});
        card3.add(processButton, BorderLayout.SOUTH);
        
        //add c and gamma sliders
        sliderPanel = new JPanel();
        
        //labels
        String cText ="<html>c</html>";
        JLabel cLabel = new JLabel(cText, JLabel.LEFT);
        Font font = new Font("Serif", Font.PLAIN, 30);
        cLabel.setFont(font);
        String gText ="<html>&#736;</html>";
        JLabel gLabel = new JLabel(gText, JLabel.LEFT);
        gLabel.setFont(font);
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.add(cLabel);
        
        //sliders
        JSlider cSlider = new JSlider(0, 255, 255);
        cSlider.setMajorTickSpacing(100);
        cSlider.setMinorTickSpacing(10);
        cSlider.setPaintTicks(true);
        cSlider.setPaintLabels(true);
        sliderPanel.add(cSlider);
        cSlider.addChangeListener(new ChangeListener()	{
			public void stateChanged(ChangeEvent e) {
				c = ((JSlider) e.getSource()).getValue();
			}
        	
        });
        sliderPanel.add(gLabel);
        
        //radio buttons for gamma
        JRadioButton gamma1 = new JRadioButton("0.1");
        gamma1.addActionListener(this);
        buttonGroup.add(gamma1);
        sliderPanel.add(gamma1);
        
        JRadioButton gamma2 = new JRadioButton("0.5");
        gamma2.addActionListener(this);
        buttonGroup.add(gamma2);
        sliderPanel.add(gamma2);
        
        JRadioButton gamma3 = new JRadioButton("1");
        gamma3.addActionListener(this);
        buttonGroup.add(gamma3);
        gamma3.setSelected(true);
        sliderPanel.add(gamma3);
        
        JRadioButton gamma4 = new JRadioButton("5");
        gamma4.addActionListener(this);
        buttonGroup.add(gamma4);
        sliderPanel.add(gamma4);
        
        JRadioButton gamma5 = new JRadioButton("10");
        gamma5.addActionListener(this);
        buttonGroup.add(gamma5);
        sliderPanel.add(gamma5);
        
        JPanel choicePanel = new JPanel(new BorderLayout());
        String[] choices = {"Log: s = c*log(1-r)/log(max-min)", 
        		"Power: c*(i^gamma)/((max-min)^gamma)"};
        JList selectList = new JList(choices);
        choicePanel.add(selectList, BorderLayout.CENTER);
        
        //adjusts slider increments if user changes transformation
        selectList.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
        		JList list = (JList)e.getSource();
                switch (list.getSelectedIndex())	{
                	case 0:
                		logSelected = true;
	                    break;
                	case 1:
                		logSelected = false;
	                    break;
                }
        	}
        });
        selectList.setSelectedIndex(0);
        card3.add(choicePanel, BorderLayout.WEST);
        card3.add(sliderPanel, BorderLayout.EAST);
        
        tabbedPane.addTab(PANEL3, card3);
    }
    
    public void setupHE(JTabbedPane tabbedPane)	{
        JPanel card4 = new JPanel();
        card4.setLayout(new BorderLayout());
        
        PgmImage img = new PgmImage(originalImage);
   	 	img.shrinkRes(320, 240);
        eqPanel = new JPanel();
        eqPanel.add(img);
        histEq();
        card4.add(eqPanel, BorderLayout.CENTER);
        
        JButton processButton = new JButton("Process!");
        processButton.setPreferredSize(new Dimension(1000, 100));
        processButton.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent e) {
				eqPanel.removeAll();
				PgmImage img = new PgmImage(originalImage);
		   	 	img.shrinkRes(320, 240);
		        eqPanel.add(img);
				if(general)	{
					histEq();
				}
				else	{
					histEq(mask);
				}
				eqPanel.revalidate();
				eqPanel.repaint();
			}
		});
        card4.add(processButton, BorderLayout.SOUTH);
        
        String[] type = {"General", "Local"};
        JList heList = new JList(type);
      //adjusts slider increments if user changes transformation
        heList.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
        		JList list = (JList)e.getSource();
                switch (list.getSelectedIndex())	{
                	case 0:
                		general = true;
	                    break;
                	case 1:
                		general = false;
	                    break;
                }
        	}
        });
        heList.setSelectedIndex(0);
        card4.add(heList, BorderLayout.WEST);
        
        JPanel sliderPanel = new JPanel();
        card4.add(sliderPanel, BorderLayout.EAST);
        Label label = new Label("X by X Square Mask");
        sliderPanel.add(label);
        JSlider maskSlider = new JSlider(1, 640, 640);
        maskSlider.setOrientation(SwingConstants.VERTICAL);
        maskSlider.setMajorTickSpacing(100);
        maskSlider.setMinorTickSpacing(10);
        maskSlider.setPaintTicks(true);
        maskSlider.setPaintLabels(true);
        sliderPanel.add(maskSlider);
        maskSlider.addChangeListener(new ChangeListener()	{
			public void stateChanged(ChangeEvent e) {
				mask = ((JSlider) e.getSource()).getValue();
			}
        });
        
        tabbedPane.addTab(PANEL4, card4);
    }
    public void setupHM(JTabbedPane tabbedPane)	{
    	JButton uploadButton = new JButton("Upload a PGM image");
        uploadButton.setPreferredSize(new Dimension(200, 100));
        uploadButton.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent e) {
				hmPanel.removeAll();
				int returnVal = fc.showOpenDialog(ImageGUI.this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                compareImage = new PgmImage(file).getPixels();
	            }
		        PgmImage img1 = new PgmImage(compareImage);
		        hmPanel.add(img1);
				PgmImage img2 = new PgmImage(originalImage);
		    	img2.shrinkRes(640, 480);
		        hmPanel.add(img2);
		        histMatch(img1, img2);
				hmPanel.revalidate();
				hmPanel.repaint();
			}
		});
        JPanel uploadPanel = new JPanel();
        uploadPanel.add(uploadButton);
        
    	PgmImage img = new PgmImage(originalImage);
    	img.shrinkRes(640, 480);
        hmPanel = new JPanel();
        hmPanel.add(img);
        
        //add listPanel and picPanel for spatial resolution
        JSplitPane card5 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, hmPanel, uploadPanel);
        card5.setOneTouchExpandable(true);
        card5.setDividerLocation(1150);
         
         tabbedPane.addTab(PANEL5, card5);
    }
     
  //spatial filtering setup methods!=====================================================================================
    //spatial filtering setup methods!=====================================================================================
    
  	private void setupFiltering(JTabbedPane tabbedPane) {
  		filterPanel = new JPanel();
  		
        //menu with my choices
        listPanel = new JPanel();
        
        String[] listElements = {"Smoothing", "Median", "Sharpening Laplacian",
        		"High-boost", "Arithmetic Mean", "Geometric Mean",
        		"Harmonic Mean", "Contraharmonic Mean", "Max",
        		"Min", "Midpoint", "Alpha-trimmed Mean", "Comparisons"};
        JList list = new JList(listElements);
        
        //listener that applies the corresponding algorithm to the image based
        // on what is clicked
        list.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
        		JList list = (JList)e.getSource();
        		filterMask = maskSlider.getValue();
        		A = boostSlider.getValue();
        		filterIndex = list.getSelectedIndex();
        		runFilter(filterIndex);
        	}
        });
        listPanel.add(list);
        
        //sliders
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        
        //labels
        Font font = new Font("Serif", Font.PLAIN, 12);
        String maskText ="<html>X by X Mask</html>";
        maskLabel = new JLabel(maskText, JLabel.LEFT);
        maskLabel.setFont(font);
        String boostText ="<html>Value of A</html>";
        boostLabel = new JLabel(boostText, JLabel.LEFT);
        boostLabel.setFont(font);
        String compareText ="<html>Noisy Image</html>";
        compareLabel = new JLabel(compareText, JLabel.LEFT);
        compareLabel.setFont(font);
        
        //sliders
        maskSlider = new JSlider(3, 23, 3);
        maskSlider.setMajorTickSpacing(5);
        maskSlider.setMinorTickSpacing(1);
        maskSlider.setPaintTicks(true);
        maskSlider.setPaintLabels(true);
        maskSlider.addChangeListener(new ChangeListener()	{
			public void stateChanged(ChangeEvent e) {
				filterMask = ((JSlider) e.getSource()).getValue();
				runFilter(filterIndex);
			}
        });
        boostSlider = new JSlider(1, 7, 1);
        boostSlider.setMajorTickSpacing(2);
        boostSlider.setMinorTickSpacing(1);
        boostSlider.setPaintTicks(true);
        boostSlider.setPaintLabels(true);
        boostSlider.addChangeListener(new ChangeListener()	{
			public void stateChanged(ChangeEvent e) {
				A = ((JSlider) e.getSource()).getValue();
				runFilter(filterIndex);
			}
        });
        
        //add sliders
        sliderPanel.add(maskLabel);
        sliderPanel.add(maskSlider);
        sliderPanel.add(boostLabel);
        sliderPanel.add(compareLabel);
        sliderPanel.add(boostSlider);
        
        //panel that holds my pictures and slider
        JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, filterPanel);
        mainPanel.setDividerLocation(120);
        //start panel with replication method
        list.setSelectedIndex(0);
        //add listPanel and picPanel for spatial resolution
        JSplitPane card6 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainPanel, sliderPanel);
        card6.setOneTouchExpandable(true);
        card6.setDividerLocation(1200);

        //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 50);
        listPanel.setMinimumSize(minimumSize);
        filterPanel.setMinimumSize(minimumSize);
        tabbedPane.addTab(PANEL6, card6);
  	}
  	
  	private void runFilter(int selectedIndex) {
		filterPanel.removeAll();
		PgmImage img;
		if(selectedIndex < 4)	{
			img = new PgmImage(originalImage);
			img.shrinkRes(640, 480);
	  		filterPanel.add(img);
		}
		else if(selectedIndex != 12)	{
			img = new PgmImage(noisy[0]);
			img.shrinkRes(220, 228);
	  		filterPanel.add(img);
		}
		else	{
			img = new PgmImage(noisy[0]);
			img.shrinkRes(200, 202);
	  		filterPanel.add(img);
		}
  		maskLabel.setVisible(true);
		maskSlider.setVisible(true);
		boostLabel.setVisible(false);
		compareLabel.setVisible(false);
		boostSlider.setVisible(false);
        switch (selectedIndex)	{
        	case 0:
        		smoothMethod();
        		break;
        	case 1:
        		medianMethod();
        		break;
        	case 2:
        		sharpMethod();
        		break;
        	case 3:
        		boostLabel.setVisible(true);
        		boostSlider.setVisible(true);
        		boostMethod(); 
        		break;
        	case 4: arithMeanMethod(); break;
        	case 5: geoMeanMethod(); break;
        	case 6: harmMeanMethod(); break;
        	case 7: contraMeanMethod(); break;
        	case 8: maxMethod(); break;
        	case 9: minMethod(); break;
        	case 10: midpointMethod(); break;
        	case 11: alphaTrimMethod(); break;
        	case 12: 
        		compareLabel.setVisible(true);
        		boostSlider.setVisible(true);
        		compareFilters();
        		break;
        }
        filterPanel.revalidate();
        filterPanel.repaint();
		
	}

	private void setupBitplanes(JTabbedPane tabbedPane) {
  		for(int i = 0; i < bitPlanes.length; i++)
  			bitPlanes[i] = true;
  		//set of checkboxes to select the bitplanes to activate
  		JPanel tickPanel = new JPanel();
  		JLabel label = new JLabel("Bitplanes shown (1=LSB):    ");
  		tickPanel.add(label);
  		bitOne = new JCheckBox("1");
  	    bitOne.setSelected(true);
  	    bitTwo = new JCheckBox("2");
  	    bitTwo.setSelected(true);
  	    bitThree = new JCheckBox("3");
  	    bitThree.setSelected(true);
  	    bitFour = new JCheckBox("4");
  	    bitFour.setSelected(true);
  	    bitFive = new JCheckBox("5");
	    bitFive.setSelected(true);
	    bitSix = new JCheckBox("6");
	    bitSix.setSelected(true);
	    bitSeven = new JCheckBox("7");
	    bitSeven.setSelected(true);
	    bitEight = new JCheckBox("8");
	    
  	    //Register a listener for the check boxes.
  	    bitOne.addItemListener(this);
  	    bitTwo.addItemListener(this);
  	    bitThree.addItemListener(this);
  	    bitFour.addItemListener(this);
  	    bitFive.addItemListener(this);
  	    bitSix.addItemListener(this);
  	    bitSeven.addItemListener(this);
  	    bitEight.addItemListener(this);
  	    tickPanel.add(bitOne);
  	    tickPanel.add(bitTwo);
  	    tickPanel.add(bitThree);
  	    tickPanel.add(bitFour);
  	    tickPanel.add(bitFive);
  	    tickPanel.add(bitSix);
  	    tickPanel.add(bitSeven);
  	    tickPanel.add(bitEight);
  		//start with original image
  		PgmImage img = new PgmImage(originalImage);
  		img.shrinkRes(640, 480);
		bpPanel = new JPanel();
		bpPanel.add(img);
		bitEight.setSelected(true);
		
		//add listPanel and picPanel for spatial resolution
		JSplitPane card7 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bpPanel, tickPanel);
		card7.setOneTouchExpandable(true);
		card7.setDividerLocation(1200);
		
		tabbedPane.addTab(PANEL7, card7);
	
  	}

     
    //spatial resolution algorithms==================================================================
    //all follow workflow as follows:
    //1) shrink to 640x480
    //2) shrink to 80x60
    //3) expand using corresponding algorithm to 640x480
	public void replMethod()	{
    	 PgmImage img1 = new PgmImage(originalImage);
         img1.shrinkRes(640, 480);
         picPanel.add(img1);
         PgmImage img2 = new PgmImage(originalImage);
         img2.shrinkRes(80, 60);
         picPanel.add(img2);
         PgmImage img3 = new PgmImage(originalImage);
         img3.shrinkRes(80, 60);
         img3.replRes(640, 480);
         picPanel.add(img3);
     }
     
     public void nearMethod()	{
    	 PgmImage img1 = new PgmImage(originalImage);
         img1.shrinkRes(640, 480);
         picPanel.add(img1);
         PgmImage img2 = new PgmImage(originalImage);
         img2.shrinkRes(80, 60);
         picPanel.add(img2);
         PgmImage img3 = new PgmImage(originalImage);
         img3.shrinkRes(80, 60);
         img3.nearRes(640, 480);
         picPanel.add(img3);
     }
     
     public void bilinMethod()	{
    	 PgmImage img1 = new PgmImage(originalImage);
         img1.shrinkRes(640, 480);
         picPanel.add(img1);
         PgmImage img2 = new PgmImage(originalImage);
         img2.shrinkRes(80, 60);
         picPanel.add(img2);
         PgmImage img3 = new PgmImage(originalImage);
         img3.shrinkRes(80, 60);
         img3.bilinRes(640, 480);
         picPanel.add(img3);
     }
     
     //convert grayscale image to black and white
     private void stepDownByBit(JPanel card2) {
    	 PgmImage img1 = new PgmImage(originalImage);
    	 img1.shrinkRes(320, 240);
    	 card2.add(img1);
    	 for(int i = 7; i > 0; i--)	{
	    	 PgmImage img2 = new PgmImage(originalImage);
	    	 img2.shrinkRes(320, 240);
	         img2.toXBits(i);
	         card2.add(img2);
    	 }
 	}
    
    //apply log transform to image
    public void logTrans(int c)	{
    	PgmImage img1 = new PgmImage(originalImage);
   	 	img1.shrinkRes(320, 240);
   	 	img1.log(c);
   	 	transPanel.add(img1);
    }
    
    //apply power transform to image
    public void powerTrans(int c, double g)	{
    	PgmImage img1 = new PgmImage(originalImage);
   	 	img1.shrinkRes(320, 240);
   	 	img1.pow(c, g);
   	 	transPanel.add(img1);
    }
    
    //equalize general histogram
    public void histEq()	{
    	PgmImage img1 = new PgmImage(originalImage);
   	 	img1.shrinkRes(320, 240);
   	 	img1.eq();
   	 	eqPanel.add(img1);
    }
    //equalize local histogram
    public void histEq(int mask)	{
    	PgmImage img1 = new PgmImage(originalImage);
   	 	img1.shrinkRes(320, 240);
   	 	img1.eq(mask);
   	 	eqPanel.add(img1);
    }
    
    //match histogram to uploaded image
    public void histMatch(PgmImage img1, PgmImage img2)	{
    	img2.match(img1);
    	hmPanel.add(img1);
    	hmPanel.add(img2);
    	
    }
    
    
    //spatial filtering algorithms!=================================================================
    private void smoothMethod() {
    	PgmImage img1 = new PgmImage(originalImage);
   	 	img1.shrinkRes(640, 480);
   	 	img1.smoothF(filterMask);
   	 	filterPanel.add(img1);
	}

	private void medianMethod() {
		PgmImage img1 = new PgmImage(originalImage);
		img1.shrinkRes(640, 480);
   	 	img1.medianF(filterMask);
   	 	filterPanel.add(img1);
		
	}

	private void sharpMethod() {
		PgmImage img1 = new PgmImage(originalImage);
   	 	img1.shrinkRes(640, 480);
   	 	img1.sharpF(filterMask);
   	 	filterPanel.add(img1);
		
	}

	private void boostMethod() {
		PgmImage img1 = new PgmImage(originalImage);
   	 	img1.shrinkRes(640, 480);
   	 	
   	 	PgmImage img2 = new PgmImage(originalImage);
	 	img2.shrinkRes(640, 480);
	 	img2.smoothF(filterMask);
   	 	img1.boostF(A, img1.getPixels(), img2.getPixels());
   	 	filterPanel.add(img1);
		
	}

	private void arithMeanMethod() {
		PgmImage img1 = new PgmImage(noisy[1]);
		PgmImage img2 = new PgmImage(noisy[2]);
		PgmImage img3 = new PgmImage(noisy[3]);
		PgmImage img4 = new PgmImage(noisy[4]);
		PgmImage img5 = new PgmImage(noisy[5]);
		PgmImage img6 = new PgmImage(noisy[6]);
		PgmImage img7 = new PgmImage(noisy[7]);
		img1.shrinkRes(220, 228);
		img2.shrinkRes(220, 228);
		img3.shrinkRes(220, 228);
		img4.shrinkRes(220, 228);
		img5.shrinkRes(220, 228);
		img6.shrinkRes(220, 228);
		img7.shrinkRes(220, 228);
   	 	img1.arithMeanF(filterMask);
   	 	img2.arithMeanF(filterMask);
	   	img3.arithMeanF(filterMask);
	   	img4.arithMeanF(filterMask);
	   	img5.arithMeanF(filterMask);
	   	img6.arithMeanF(filterMask);
	   	img7.arithMeanF(filterMask);
   	 	filterPanel.add(img1);
   	 	filterPanel.add(img2);
	   	filterPanel.add(img3);
	   	filterPanel.add(img4);
	   	filterPanel.add(img5);
	   	filterPanel.add(img6);
	   	filterPanel.add(img7);
	}

	private void geoMeanMethod() {
		PgmImage img1 = new PgmImage(noisy[1]);
		PgmImage img2 = new PgmImage(noisy[2]);
		PgmImage img3 = new PgmImage(noisy[3]);
		PgmImage img4 = new PgmImage(noisy[4]);
		PgmImage img5 = new PgmImage(noisy[5]);
		PgmImage img6 = new PgmImage(noisy[6]);
		PgmImage img7 = new PgmImage(noisy[7]);
		img1.shrinkRes(220, 228);
		img2.shrinkRes(220, 228);
		img3.shrinkRes(220, 228);
		img4.shrinkRes(220, 228);
		img5.shrinkRes(220, 228);
		img6.shrinkRes(220, 228);
		img7.shrinkRes(220, 228);
   	 	img1.geoMeanF(filterMask);
   	 	img2.geoMeanF(filterMask);
	   	img3.geoMeanF(filterMask);
	   	img4.geoMeanF(filterMask);
	   	img5.geoMeanF(filterMask);
	   	img6.geoMeanF(filterMask);
	   	img7.geoMeanF(filterMask);
   	 	filterPanel.add(img1);
   	 	filterPanel.add(img2);
	   	filterPanel.add(img3);
	   	filterPanel.add(img4);
	   	filterPanel.add(img5);
	   	filterPanel.add(img6);
	   	filterPanel.add(img7);
		
	}

	private void harmMeanMethod() {
		PgmImage img1 = new PgmImage(noisy[1]);
		PgmImage img2 = new PgmImage(noisy[2]);
		PgmImage img3 = new PgmImage(noisy[3]);
		PgmImage img4 = new PgmImage(noisy[4]);
		PgmImage img5 = new PgmImage(noisy[5]);
		PgmImage img6 = new PgmImage(noisy[6]);
		PgmImage img7 = new PgmImage(noisy[7]);
		img1.shrinkRes(220, 228);
		img2.shrinkRes(220, 228);
		img3.shrinkRes(220, 228);
		img4.shrinkRes(220, 228);
		img5.shrinkRes(220, 228);
		img6.shrinkRes(220, 228);
		img7.shrinkRes(220, 228);
   	 	img1.harmMeanF(filterMask);
   	 	img2.harmMeanF(filterMask);
	   	img3.harmMeanF(filterMask);
	   	img4.harmMeanF(filterMask);
	   	img5.harmMeanF(filterMask);
	   	img6.harmMeanF(filterMask);
	   	img7.harmMeanF(filterMask);
   	 	filterPanel.add(img1);
   	 	filterPanel.add(img2);
	   	filterPanel.add(img3);
	   	filterPanel.add(img4);
	   	filterPanel.add(img5);
	   	filterPanel.add(img6);
	   	filterPanel.add(img7);
		
	}

	private void contraMeanMethod() {
		PgmImage img1 = new PgmImage(noisy[1]);
		PgmImage img2 = new PgmImage(noisy[2]);
		PgmImage img3 = new PgmImage(noisy[3]);
		PgmImage img4 = new PgmImage(noisy[4]);
		PgmImage img5 = new PgmImage(noisy[5]);
		PgmImage img6 = new PgmImage(noisy[6]);
		PgmImage img7 = new PgmImage(noisy[7]);
		img1.shrinkRes(220, 228);
		img2.shrinkRes(220, 228);
		img3.shrinkRes(220, 228);
		img4.shrinkRes(220, 228);
		img5.shrinkRes(220, 228);
		img6.shrinkRes(220, 228);
		img7.shrinkRes(220, 228);
   	 	img1.contraMeanF(filterMask);
   	 	img2.contraMeanF(filterMask);
	   	img3.contraMeanF(filterMask);
	   	img4.contraMeanF(filterMask);
	   	img5.contraMeanF(filterMask);
	   	img6.contraMeanF(filterMask);
	   	img7.contraMeanF(filterMask);
   	 	filterPanel.add(img1);
   	 	filterPanel.add(img2);
	   	filterPanel.add(img3);
	   	filterPanel.add(img4);
	   	filterPanel.add(img5);
	   	filterPanel.add(img6);
	   	filterPanel.add(img7);
		
	}

	private void maxMethod() {
		PgmImage img1 = new PgmImage(noisy[1]);
		PgmImage img2 = new PgmImage(noisy[2]);
		PgmImage img3 = new PgmImage(noisy[3]);
		PgmImage img4 = new PgmImage(noisy[4]);
		PgmImage img5 = new PgmImage(noisy[5]);
		PgmImage img6 = new PgmImage(noisy[6]);
		PgmImage img7 = new PgmImage(noisy[7]);
		img1.shrinkRes(220, 228);
		img2.shrinkRes(220, 228);
		img3.shrinkRes(220, 228);
		img4.shrinkRes(220, 228);
		img5.shrinkRes(220, 228);
		img6.shrinkRes(220, 228);
		img7.shrinkRes(220, 228);
   	 	img1.maxF(filterMask);
   	 	img2.maxF(filterMask);
	   	img3.maxF(filterMask);
	   	img4.maxF(filterMask);
	   	img5.maxF(filterMask);
	   	img6.maxF(filterMask);
	   	img7.maxF(filterMask);
   	 	filterPanel.add(img1);
   	 	filterPanel.add(img2);
	   	filterPanel.add(img3);
	   	filterPanel.add(img4);
	   	filterPanel.add(img5);
	   	filterPanel.add(img6);
	   	filterPanel.add(img7);
		
	}

	private void minMethod() {
		PgmImage img1 = new PgmImage(noisy[1]);
		PgmImage img2 = new PgmImage(noisy[2]);
		PgmImage img3 = new PgmImage(noisy[3]);
		PgmImage img4 = new PgmImage(noisy[4]);
		PgmImage img5 = new PgmImage(noisy[5]);
		PgmImage img6 = new PgmImage(noisy[6]);
		PgmImage img7 = new PgmImage(noisy[7]);
		img1.shrinkRes(220, 228);
		img2.shrinkRes(220, 228);
		img3.shrinkRes(220, 228);
		img4.shrinkRes(220, 228);
		img5.shrinkRes(220, 228);
		img6.shrinkRes(220, 228);
		img7.shrinkRes(220, 228);
   	 	img1.minF(filterMask);
   	 	img2.minF(filterMask);
	   	img3.minF(filterMask);
	   	img4.minF(filterMask);
	   	img5.minF(filterMask);
	   	img6.minF(filterMask);
	   	img7.minF(filterMask);
   	 	filterPanel.add(img1);
   	 	filterPanel.add(img2);
	   	filterPanel.add(img3);
	   	filterPanel.add(img4);
	   	filterPanel.add(img5);
	   	filterPanel.add(img6);
	   	filterPanel.add(img7);
		
	}

	private void midpointMethod() {
		PgmImage img1 = new PgmImage(noisy[1]);
		PgmImage img2 = new PgmImage(noisy[2]);
		PgmImage img3 = new PgmImage(noisy[3]);
		PgmImage img4 = new PgmImage(noisy[4]);
		PgmImage img5 = new PgmImage(noisy[5]);
		PgmImage img6 = new PgmImage(noisy[6]);
		PgmImage img7 = new PgmImage(noisy[7]);
		img1.shrinkRes(220, 228);
		img2.shrinkRes(220, 228);
		img3.shrinkRes(220, 228);
		img4.shrinkRes(220, 228);
		img5.shrinkRes(220, 228);
		img6.shrinkRes(220, 228);
		img7.shrinkRes(220, 228);
   	 	img1.midpointF(filterMask);
   	 	img2.midpointF(filterMask);
	   	img3.midpointF(filterMask);
	   	img4.midpointF(filterMask);
	   	img5.midpointF(filterMask);
	   	img6.midpointF(filterMask);
	   	img7.midpointF(filterMask);
   	 	filterPanel.add(img1);
   	 	filterPanel.add(img2);
	   	filterPanel.add(img3);
	   	filterPanel.add(img4);
	   	filterPanel.add(img5);
	   	filterPanel.add(img6);
	   	filterPanel.add(img7);
	}

	private void alphaTrimMethod() {
		PgmImage img1 = new PgmImage(noisy[1]);
		PgmImage img2 = new PgmImage(noisy[2]);
		PgmImage img3 = new PgmImage(noisy[3]);
		PgmImage img4 = new PgmImage(noisy[4]);
		PgmImage img5 = new PgmImage(noisy[5]);
		PgmImage img6 = new PgmImage(noisy[6]);
		PgmImage img7 = new PgmImage(noisy[7]);
		img1.shrinkRes(220, 228);
		img2.shrinkRes(220, 228);
		img3.shrinkRes(220, 228);
		img4.shrinkRes(220, 228);
		img5.shrinkRes(220, 228);
		img6.shrinkRes(220, 228);
		img7.shrinkRes(220, 228);
   	 	img1.alphaTrimF(filterMask);
   	 	img2.alphaTrimF(filterMask);
	   	img3.alphaTrimF(filterMask);
	   	img4.alphaTrimF(filterMask);
	   	img5.alphaTrimF(filterMask);
	   	img6.alphaTrimF(filterMask);
	   	img7.alphaTrimF(filterMask);
   	 	filterPanel.add(img1);
   	 	filterPanel.add(img2);
	   	filterPanel.add(img3);
	   	filterPanel.add(img4);
	   	filterPanel.add(img5);
	   	filterPanel.add(img6);
	   	filterPanel.add(img7);
	}
	//compare original noisy image to their filtered outputs
  	private void compareFilters() {
  		PgmImage img0 = new PgmImage(noisy[A]);
		PgmImage img1 = new PgmImage(noisy[A]);
		PgmImage img2 = new PgmImage(noisy[A]);
		PgmImage img3 = new PgmImage(noisy[A]);
		PgmImage img4 = new PgmImage(noisy[A]);
		PgmImage img5 = new PgmImage(noisy[A]);
		PgmImage img6 = new PgmImage(noisy[A]);
		PgmImage img7 = new PgmImage(noisy[A]);
		PgmImage img8 = new PgmImage(noisy[A]);
		img0.shrinkRes(200, 202);
		img1.shrinkRes(200, 202);
		img2.shrinkRes(200, 202);
		img3.shrinkRes(200, 202);
		img4.shrinkRes(200, 202);
		img5.shrinkRes(200, 202);
		img6.shrinkRes(200, 202);
		img7.shrinkRes(200, 202);
		img8.shrinkRes(200, 202);
   	 	img1.arithMeanF(filterMask);
   	 	img2.geoMeanF(filterMask);
	   	img3.harmMeanF(filterMask);
	   	img4.contraMeanF(filterMask);
	   	img5.maxF(filterMask);
	   	img6.minF(filterMask);
	   	img7.midpointF(filterMask);
	   	img8.alphaTrimF(filterMask);
	   	filterPanel.add(img0);
   	 	filterPanel.add(img1);
   	 	filterPanel.add(img2);
	   	filterPanel.add(img3);
	   	filterPanel.add(img4);
	   	filterPanel.add(img5);
	   	filterPanel.add(img6);
	   	filterPanel.add(img7);
	   	filterPanel.add(img8);
	}
    //parse int from listener
  	public void actionPerformed(ActionEvent e) {
  		g = Double.parseDouble(e.getActionCommand());
  	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		bpPanel.removeAll();
		PgmImage img1 = new PgmImage(originalImage);
   	 	img1.shrinkRes(320, 240);
   	 	bpPanel.add(img1);
		JCheckBox source = (JCheckBox) e.getItemSelectable();
		if (source == bitOne)	{
			bitPlanes[0] = source.isSelected();
		}
		if (source == bitTwo)	{
			bitPlanes[1] = source.isSelected();
		}
		if (source == bitThree)	{
			bitPlanes[2] = source.isSelected();
		}
		if (source == bitFour)	{
			bitPlanes[3] = source.isSelected();
		}
		if (source == bitFive)	{
			bitPlanes[4] = source.isSelected();
		}
		if (source == bitSix)	{
			bitPlanes[5] = source.isSelected();
		}
		if (source == bitSeven)	{
			bitPlanes[6] = source.isSelected();
		}
		if (source == bitEight)	{
			bitPlanes[7] = source.isSelected();
		}
		PgmImage img2 = new PgmImage(originalImage);
   	 	img2.shrinkRes(320, 240);
   	 	img2.drawBitplanes(bitPlanes);
   	 	bpPanel.add(img2);
	   	HistogramDataset dataset = new HistogramDataset();
	    dataset.setType(HistogramType.FREQUENCY);
	    dataset.addSeries("Histogram",img2.getValues(), 256);
	    String plotTitle = "Histogram"; 
	    String xaxis = "Grayscale";
	    String yaxis = "Count"; 
	    PlotOrientation orientation = PlotOrientation.VERTICAL; 
	    boolean show = false; 
	    boolean toolTips = false;
	    boolean urls = false; 
	    JFreeChart chart = ChartFactory.createHistogram( plotTitle, xaxis, yaxis, 
	             dataset, orientation, show, toolTips, urls);
	    XYPlot xy = chart.getXYPlot();
	    NumberAxis domainAxis = (NumberAxis) xy.getDomainAxis();
	    domainAxis.setRange(0.0, 255.0);
	    ChartPanel cp = new ChartPanel(chart);
	 	bpPanel.add(cp);
   	 	bpPanel.revalidate();
   	 	bpPanel.repaint();
	}
}