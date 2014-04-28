package core;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
/**
 * This class handles simple processing for PGM images
 *  @author James Kahng
 *  */

public class PgmImage extends Component {
	// id just to remove warning
	private static final long serialVersionUID = -1746539286090200414L;
	// image buffer for graphical display
	private BufferedImage img;
	// image buffer for grayscale pixel values
	private int[][] pixels;
	private int[][] newPixels;
	private int[][][] bitPlanes = null;
	private String[][] binary = null;
	private double[] histogram;
	private double[] probabilities;
	private int[] conversion;
	StringBuilder binaryString;
	StringBuilder[] binaryBitplanes;
	double[] values;
	
	// constructor that loads pgm image from a file
	public PgmImage(String filename) {
		pixels = null;
		readPGM(filename);
	}
	
	public PgmImage(File file) {
		pixels = null;
		readPGM(file);
	}
	
	// constructor that loads a pgm image from an array of pixels
	public PgmImage(int[][] inputPixels)	{
		pixels = inputPixels;
		if (pixels != null)
			pix2img();
	}
	
	// load gray scale pixel values from a PGM format image
	public void readPGM(String filename)	{
		try {                        		    
		    Scanner sc = new Scanner(new FileReader(filename));
		    // process the top 4 header lines
		    String filetype=sc.nextLine();
		    if (!filetype.equalsIgnoreCase("p2")) {
		    	System.out.println("[readPGM]Cannot load the image type of "+filetype);
		    	sc.close();
		    	return;
		    }
	   	   	sc.nextLine();	   	   	   
	   	   	int cols = sc.nextInt();
	   	   	int rows = sc.nextInt();
	   	   	int maxValue = sc.nextInt();
	   	   	pixels = new int[rows][cols];	   	       
	   	   	System.out.println("Reading in image from " + filename + " of size " + rows + " by " + cols);
	   	   	// process the rest lines that hold the actual pixel values
	   	   	for (int r=0; r<rows; r++) 
	   	   		for (int c=0; c<cols; c++)
	   	   			pixels[r][c] = (int)(sc.nextInt()*255.0/maxValue);
	   	   	sc.close();
	    } catch(FileNotFoundException fe) {
	    	System.out.println("Had a problem opening a file.");
	    } catch (Exception e) {
	    	System.out.println(e.toString() + " caught in readPPM.");
	    	e.printStackTrace();
	    }
	}
	
	// load gray scale pixel values from a PGM format image
		public void readPGM(File file)	{
			try {                        		    
			    Scanner sc = new Scanner(new FileReader(file));
			    // process the top 4 header lines
			    String filetype=sc.nextLine();
			    if (!filetype.equalsIgnoreCase("p2")) {
			    	System.out.println("[readPGM]Cannot load the image type of "+filetype);
			    	sc.close();
			    	return;
			    }
		   	   	sc.nextLine();	   	   	   
		   	   	int cols = sc.nextInt();
		   	   	int rows = sc.nextInt();
		   	   	int maxValue = sc.nextInt();
		   	   	pixels = new int[rows][cols];	   	       
		   	   	System.out.println("Reading in image from " + file.getName() + " of size " + rows + " by " + cols);
		   	   	// process the rest lines that hold the actual pixel values
		   	   	for (int r=0; r<rows; r++) 
		   	   		for (int c=0; c<cols; c++)
		   	   			pixels[r][c] = (int)(sc.nextInt()*255.0/maxValue);
		   	   	sc.close();
		    } catch(FileNotFoundException fe) {
		    	System.out.println("Had a problem opening a file.");
		    } catch (Exception e) {
		    	System.out.println(e.toString() + " caught in readPPM.");
		    	e.printStackTrace();
		    }
		}
	
	//returns the pixel values of the pgm
	public int[][] getPixels(){
		return pixels;
	}
	
	public double[] getValues()	{
		values = new double[pixels.length*pixels[0].length];
		int count = 0;
		for(int r = 0; r < pixels.length; r++)	{
			for(int c = 0; c < pixels[0].length; c++)	{
				values[count] = pixels[r][c];
				count++;
			}
		}
		return values;
	}
	// translating raw grayscale pixel values to buffered image for display
	private void pix2img(){
		int g;
		img = new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_INT_ARGB);
		// copy the pixels values
		for(int row=0; row<pixels.length; ++row)
			for(int col=0; col<pixels[row].length; ++col){
				g = pixels[row][col];
				//set red, green and blue to the grayscale value for the desired gray
				img.setRGB(col, row, ((255<<24) | (g << 16) | (g <<8) | g));		
			}
		}
	
	
	// overrides the paint method of Component class
	public void paint(Graphics g) {
		// simply draw the buffered image
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
	}
	// overrides the method in Component class, to determine the window size
	public Dimension getPreferredSize() {
		if (img == null) {
			return new Dimension(100, 100);
		} else {			
			// make sure the window is not two small to be seen
			return new Dimension(Math.max(100, img.getWidth(null)), 
						Math.max(100, img.getHeight(null)));
		}
	}
	
	//shrink an image by picking choice pixels from the original image
	public void shrinkRes(int r, int c)	{
		int[][] newImage = new int[r][c];
		int rScale = pixels.length/r;
		int cScale = pixels[0].length/c;
		for(int row = 0; row < newImage.length; row++)	{
			for(int col = 0; col < newImage[0].length; col++)	{
				newImage[row][col] = pixels[row*rScale][col*cScale];
			}
		}
		pixels = newImage;
		this.pix2img();
	}
	
	//replication method
	//expand to a larger image by using original pixels multiple times
	public void replRes(int r, int c)	{
		int[][] newImage = new int[r][c];
		int rScale = r/pixels.length;
		int cScale = c/pixels[0].length;
		for(int row = 0; row < newImage.length; row++)	{
			for(int col = 0; col < newImage[0].length; col++)	{
				newImage[row][col] = pixels[row/rScale][col/cScale];
			}
		}
		pixels = newImage;
		this.pix2img();
	}
	
	//nearest neighbor method
	//expand to a larger image by copying the nearest representative of the original pixels (equivalent here because the result image resolution is evenly
	//divisible by the original image resolution)
	public void nearRes(int r, int c)	{
		int[][] newImage = new int[r][c];
		float rScale = r/pixels.length;
		float cScale = c/pixels[0].length;
		for(int row = 0; row < newImage.length; row++)	{
			for(int col = 0; col < newImage[0].length; col++)	{
				newImage[row][col] = pixels[(int)(row/rScale)][(int)(col/cScale)];
			}
		}
		pixels = newImage;
		this.pix2img();
	}
	
	//bilinear method
	//calculate the resulting grayscale value of the new pixel by
	//adding up portions of the neighboring pixels weighted by distance
	public void bilinRes(int r, int c)	{
		int[][] newImage = new int[r][c];
		float rScale = (float)(pixels.length-1)/(float)(r-1);
		float cScale = (float)(pixels[0].length-1)/(float)(c-1);
		for(int row = 0; row < newImage.length; row++)	{
			for(int col = 0; col < newImage[0].length; col++)	{
				//calculated pixel location desired
				float rFloat = row*rScale;
				float cFloat = col*cScale;
				//rounded pixel location desired
				int   rInt = (int)(rFloat);
				int   cInt = (int)(cFloat);
				//difference between calculated and rounded location
				float rDelta = rFloat - rInt;
				float cDelta = cFloat - cInt;
				// interpolate between the four nearest pixels
				if ((rInt+1 < pixels.length) && (cInt+1 < pixels[0].length)) 
					newImage[row][col] = (int) (pixels[rInt][cInt]*(1-cDelta)*(1-rDelta) 
					+ pixels[rInt][cInt+1] * (cDelta)*(1-rDelta)
					+ pixels[rInt+1][cInt] * (1-cDelta)*(rDelta)
					+ pixels[rInt+1][cInt+1] * (cDelta)*(rDelta)); 
				// handle edge cases
				else if ((rInt < pixels.length) && (cInt < pixels[0].length)) 
					newImage[row][col] = pixels[rInt][cInt]; 
				else 
					newImage[row][col] = 128;
			}
		}
		pixels = newImage;
		this.pix2img();
	}
	
	//converts image to certain bitsize
	public void toXBits(int bitSize)	{
		//Adjust original image's scale to x-bit scale
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
		for (int r=0; r<rows; r++)	{
   	   		for (int c=0; c<cols; c++)	{
   	   			int newValue = (int)(double)Math.round((pixels[r][c])/(Math.pow(2, 8-bitSize)-1));
   	   			pixels[r][c] = (int)(newValue*(Math.pow(2, 8-bitSize)-1));
   	   			//hard-code the black and whiteness of 1-bit
   	   			if(bitSize == 1 && pixels[r][c] == 127)	{
   	   				pixels[r][c] = 255;
   	   			}
   	   		}
   	   	}
		this.pix2img();
	}
	
	//logarithmic transformation: adjusts all values in image by s = c*(log(1-r)/log(max-min))
	public void log(int con)	{
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
		for (int r=0; r<rows; r++)	{
   	   		for (int c=0; c<cols; c++)	{
   	   			pixels[r][c] = (int)(con*(Math.log(1+pixels[r][c])/Math.log(255)));
   	   		}
   	   	}
		this.pix2img();
	}

	//power transformation: adjusts all values in image by s = c*r^gamma
	public void pow(int con, double g)	{
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
		for (int r=0; r<rows; r++)	{
   	   		for (int c=0; c<cols; c++)	{
   	   			pixels[r][c] = (int)(con*((Math.pow(pixels[r][c], g))/(Math.pow(255, g))));
   	   		}
   	   	}
		this.pix2img();
	}
	//histogram equalization
	public void eq()	{
		histogram = new double[256];
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
		for (int r=0; r<rows; r++)	{
   	   		for (int c=0; c<cols; c++)	{
   	   			histogram[pixels[r][c]]++;
   	   		}
   	   	}
		conversion = new int[256];
		probabilities = new double[256];
		double mn = cols*rows;
		double prob = 0;
		for (int r=0; r<histogram.length; r++)	{
			prob += histogram[r]/mn;
			probabilities[r] = histogram[r]/mn;
			conversion[r] = (int) (0.5 + 255*prob);
   	   	}
		for (int r=0; r<rows; r++)	{
   	   		for (int c=0; c<cols; c++)	{
   	   			pixels[r][c] = conversion[pixels[r][c]];
   	   		}
   	   	}
		this.pix2img();
	}
	
	public int[] getConversion()	{
		histogram = new double[256];
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
		for (int r=0; r<rows; r++)	{
   	   		for (int c=0; c<cols; c++)	{
   	   			histogram[pixels[r][c]]++;
   	   		}
   	   	}
		conversion = new int[256];
		double mn = cols*rows;
		double prob = 0;
		for (int r=0; r<histogram.length; r++)	{
			prob += histogram[r]/mn;
			conversion[r] = (int) (0.5 + 255*prob);
   	   	}
		for (int r=0; r<rows; r++)	{
   	   		for (int c=0; c<cols; c++)	{
   	   			pixels[r][c] = conversion[pixels[r][c]];
   	   		}
   	   	}
		return conversion;
	}
	
	//local histogram equalization
	public void eq(int mask)	{
		histogram = new double[256];
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	//place mask on all pixels starting from 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		histogram = new double[256];
   	    		for(int r = theRow; r < theRow + mask; r++)	{
   	    			for(int c = theCol; c < theCol + mask; c++)	{
   	    				if (r >= rows || c >= cols)	{
   	    					//assume all pixels beyond image are about the intensity of the wall (~200?)
   	    					histogram[200]++;
   	    					continue;
   	    				}
   	    				histogram[pixels[r][c]]++;
   	    			}
   	    		}
   	    		//iteratively convert each mask after taking histogram
   	    		conversion = new int[256];
	   	 		double mn = mask*mask;
	   	 		double prob = 0;
	   	 		for (int r=0; r < histogram.length; r++)	{
	   	 			prob += histogram[r]/mn;
	   	 			conversion[r] = (int) (0.5 + 255*prob);
	   	    	}
	   	 		for (int r=theRow; (r<theRow+mask && r < rows); r++)	{
   	    	   		for (int c=theCol; (c<theCol+mask && c < cols); c++)	{
   	    	   			pixels[r][c] = conversion[pixels[r][c]];
   	    	   		}
	   	    	}
	   	 		theCol += mask;
   	    	}
   	    	theRow += mask;
   	    }
		this.pix2img();
	}
	
	//match histogram distributions
	public void match(PgmImage img1)	{
		int[] originalScale = getConversion();
		int[] compareScale = img1.getConversion();
		int[] difference = new int[compareScale.length];
		int[] newScale = new int[compareScale.length];
		//look for the closest match between the compared image mapping to the original image mapping
		//then, record how to map the original histogram distribution values to the compared histogram distribution values
		for (int i = 0; i < originalScale.length; i++)	{
			difference[i] = Math.abs(originalScale[i] - compareScale[0]);
			newScale[i] = 0;
			for(int j = 0; j < compareScale.length; j++)	{
				if(Math.abs(originalScale[i] - compareScale[j]) < difference[i])	{
					difference[i] = Math.abs(originalScale[i] - compareScale[j]);
					newScale[i] = j;
				}
			}
		}
		for(int r = 0; r < pixels.length; r++)	{
			for(int c = 0; c < pixels[0].length; c++)	{
				pixels[r][c] = newScale[pixels[r][c]];
			}
		}
		
		this.pix2img();
	}

	//transfer processed pixels to displayed image
	public void updatePixels(int rows, int cols)	{
		for(int r = 0; r < rows; r++)	{
			for(int c = 0; c < cols; c++)	{
    			pixels[r][c] = newPixels[r][c];
			}
   	    }
	}
	
	//apply a general non-weighted averaging mask of user-defined size
	//1) overarching loop is through image array
	//2) another loop goes through the mask and does math
	public void smoothF(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	newPixels = new int[rows][cols];
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    int total = 0;
   	    int average = 0;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		total = 0;
   	    		for(int r = theRow - mask/2; r <= theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c <= theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					//assume all pixels beyond image are about the intensity of the mask center
   	    					total += pixels[theRow][theCol];
   	    				}
   	    				else	{
	   	    				total += pixels[r][c];
   	    				}
   	    			}
   	    		}
   	    		//take average
   	    		average = total/(mask*mask);
   	    	   	newPixels[theRow][theCol] = average;
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
   	    updatePixels(rows, cols);
		this.pix2img();
	}

	//set center of mask to the median value in mask
	public void medianF(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	newPixels = new int[rows][cols];
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    ArrayList<Integer> elementList = new ArrayList<Integer>();
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		elementList.removeAll(elementList);
   	    		for(int r = theRow - mask/2; r <= theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c <= theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					elementList.add(pixels[theRow][theCol]);
   	    				}
   	    				else	{
	   	    				elementList.add(pixels[r][c]);
   	    				}
   	    			}
   	    		}
   	    		//order from low to high
   	    		Collections.sort(elementList);
   	    		//take median
   	    		int median = elementList.get(elementList.size()/2);
   	    	   	newPixels[theRow][theCol] = median;
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
   	    updatePixels(rows, cols);
		this.pix2img();
		
	}

	//calculates and adds laplacian mask to the image for sharpening
	public void sharpF(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	int[][] laplacian = new int[rows][cols];
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    int lap = 0;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		lap = 0;
   	    		//applies [(1,1,1)(1,-8,1)(1,1,1)] mask
   	    		for(int r = theRow - mask/2; r <= theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c <= theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					//assume all pixels beyond image are about the intensity of the mask center
   	    					lap += pixels[theRow][theCol];
   	    				}
   	    				else if (r == theRow && c == theCol)	{
   	    					lap -= (mask*mask-1)*(pixels[r][c]);
   	    				}
   	    				else	{
	   	    				lap += pixels[r][c];
   	    				}
   	    			}
   	    		}
   	    		laplacian[theRow][theCol] = lap/(mask*mask-1);
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
   	    for(int r = 0; r < pixels.length; r++)	{
			for(int c = 0; c <pixels[0].length; c++)	{
				pixels[r][c] -= laplacian[r][c];
				if(pixels[r][c] < 0) pixels[r][c] = 0;
			}
		}
		this.pix2img();
	}

	//unsharp masking and high-boost filtering
	public void boostF(int A, int[][] orig, int[][] blurred) {
		//mask must be odd in order for there to be a center pixel
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	int[][] boostMask = new int[rows][cols];
   	   	for(int r = 0; r < pixels.length; r++)	{
			for(int c = 0; c < pixels[0].length; c++)	{
				//boost mask = original image - blurred image
				boostMask[r][c] = orig[r][c] - blurred[r][c];
				//eliminate negative numbers
				if(boostMask[r][c] < 0) boostMask[r][c] = 0;
				if(boostMask[r][c] > 255) boostMask[r][c] = 255;
				//final image = original + weighted boost mask
				pixels[r][c] = orig[r][c] + A*boostMask[r][c];
				if(pixels[r][c] > 255) pixels[r][c] = 255;
			}
		}
   	   	this.pix2img();
	}

	//averages all values under mask and sets center pixel to average
	public void arithMeanF(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	newPixels = new int[rows][cols];
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    int total = 0;
   	    int average = 0;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		total = 0;
   	    		for(int r = theRow - mask/2; r <= theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c <= theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					//assume all pixels beyond image are about the intensity of the mask center
   	    					total += pixels[theRow][theCol];
   	    				}
   	    				else	{
	   	    				total += pixels[r][c];
   	    				}
   	    			}
   	    		}
   	    		//take average
   	    		average = total/(mask*mask);
   	    	    newPixels[theRow][theCol] = average;
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
   	    updatePixels(rows,cols);
		this.pix2img();
	}

	//geometric mean of mask values (nth root of mask product)
	public void geoMeanF(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	newPixels = new int[rows][cols];
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    double product = 1;
   	    int geomean = 1;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		product = 1;
   	    		for(int r = theRow - mask/2; r <= theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c <= theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					//assume all pixels beyond image are about the intensity of the mask center
   	    					product *= pixels[theRow][theCol];
   	    				}
   	    				else	{
   	    					product *= pixels[r][c];
   	    				}
   	    			}
   	    		}
   	    		//calculate geometric mean
   	    		geomean = (int) Math.pow(product, (1.0/(mask*mask)));
   	    	   	newPixels[theRow][theCol] = geomean;
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
   	    updatePixels(rows, cols);
		this.pix2img();
		
	}

	//set center of mask to harmonic mean of mask
	public void harmMeanF(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	newPixels = new int[rows][cols];
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    double total = 0;
   	    int harmonicmean = 1;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		total = 0;
   	    		for(int r = theRow - mask/2; r <= theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c <= theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					//assume all pixels beyond image are about the intensity of the mask center
   	    					total += (1.0/pixels[theRow][theCol]);
   	    				}
   	    				else	{
	   	    				total += (1.0/pixels[r][c]);
   	    				}
   	    			}
   	    		}
   	    		//take average
   	    		harmonicmean = (int) ((mask*mask)/total);
   	    	   	newPixels[theRow][theCol] = harmonicmean;
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
   	    updatePixels(rows, cols);
		this.pix2img();
		
	}

	//set center of mask to contraharmonic mean of mask
	public void contraMeanF(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	newPixels = new int[rows][cols];
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    int sqtotal = 0;
   	    int total = 0;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		sqtotal = 0;
   	    		total = 0;
   	    		for(int r = theRow - mask/2; r <= theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c <= theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					//assume all pixels beyond image are about the intensity of the mask center
   	    					total += pixels[theRow][theCol];
   	    					sqtotal += Math.pow(pixels[theRow][theCol], 2);
   	    				}
   	    				else	{
	   	    				total += pixels[r][c];
	   	    				sqtotal += Math.pow(pixels[r][c], 2);
   	    				}
   	    			}
   	    		}
   	    		int contramean = 0;
   	    		//make sure there's no /byzero error
   	    		if(total == 0)	{
   	    			contramean = 0;
   	    		}
   	    		else	{
   	    			contramean = sqtotal/total;
   	    		}
   	    	   	newPixels[theRow][theCol] = contramean;
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
   	    updatePixels(rows, cols);
		this.pix2img();
		
	}

	//set center of mask to max in mask
	public void maxF(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	newPixels = new int[rows][cols];
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    int max = 0;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		max = pixels[theRow][theCol];
   	    		for(int r = theRow - mask/2; r <= theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c <= theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					//assume all pixels beyond image are about the intensity of the mask center
   	    					max = Math.max(max, pixels[theRow][theCol]);
   	    				}
   	    				else	{
   	    					max = Math.max(max, pixels[r][c]);
   	    				}
   	    			}
   	    		}
   	    	   	newPixels[theRow][theCol] = max;
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
   	    updatePixels(rows, cols);
		this.pix2img();
		
	}

	//set center of mask to min in mask
	public void minF(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	newPixels = new int[rows][cols];
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    int min = 0;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		min = pixels[theRow][theCol];
   	    		for(int r = theRow - mask/2; r <= theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c <= theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					//assume all pixels beyond image are about the intensity of the mask center
   	    					min = Math.min(min, pixels[theRow][theCol]);
   	    				}
   	    				else	{
   	    					min = Math.min(min, pixels[r][c]);
   	    				}
   	    			}
   	    		}
   	    	   	newPixels[theRow][theCol] = min;
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
   	    updatePixels(rows, cols);
		this.pix2img();
		
	}

	//midpoint of max and min of mask
	public void midpointF(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	newPixels = new int[rows][cols];
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    int min = 0;
   	    int max = 0;
   	    int midpoint = 0;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		min = pixels[theRow][theCol];
   	    		for(int r = theRow - mask/2; r <= theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c <= theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					//assume all pixels beyond image are about the intensity of the mask center
   	    					min = Math.min(min, pixels[theRow][theCol]);
   	    					max = Math.max(max, pixels[theRow][theCol]);
   	    				}
   	    				else	{
   	    					min = Math.min(min, pixels[r][c]);
   	    					max = Math.max(max, pixels[r][c]);
   	    				}
   	    			}
   	    		}
   	    		midpoint = (max+min)/2;
   	    	   	newPixels[theRow][theCol] = midpoint;
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
   	    updatePixels(rows, cols);
		this.pix2img();
		
	}

	//average of mask excluding the max and min values in mask
	public void alphaTrimF(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	newPixels = new int[rows][cols];
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    ArrayList<Integer> elementList = new ArrayList<Integer>();
   	    int total = 0;
   	    int alphatrimmed = 0;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		elementList.removeAll(elementList);
   	    		for(int r = theRow - mask/2; r <= theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c <= theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					elementList.add(pixels[theRow][theCol]);
   	    				}
   	    				else	{
	   	    				elementList.add(pixels[r][c]);
   	    				}
   	    			}
   	    		}
   	    		//order from low to high
   	    		Collections.sort(elementList);
   	    		//sum all but the first and last
   	    		total = 0;
   	    		for(int i = 1; i < elementList.size()-1; i++)	{
   	    			total += elementList.get(i);
   	    		}
   	    		alphatrimmed = total/(mask*mask-2);
   	    	   	newPixels[theRow][theCol] = alphatrimmed;
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
   	    updatePixels(rows, cols);
		this.pix2img();
	}

	//sets pixel grayscale values based on the bitplanes activated by user
	public void drawBitplanes(boolean[] onBits) {
		if (bitPlanes == null)	{
			createBinary();
			constructBitplanes();
		}
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
		newPixels = new int[rows][cols];
		binaryBitplanes = new StringBuilder[8];
		for(int i = 0; i < onBits.length; i++)	{
			binaryBitplanes[i] = new StringBuilder();
			//ignore this bitplane if it is deactivated
			if(!onBits[7-i])	{
				continue;
			}
			for(int r = 0; r < pixels.length; r++)	{
				for(int c = 0; c < pixels[0].length; c++)	{
					binaryBitplanes[i].append(bitPlanes[r][c][i]);
					newPixels[r][c] += bitPlanes[r][c][i]*Math.pow(2, 7-i);
				}
			}
		}
		updatePixels(rows, cols);
		this.pix2img();
	}

	public void createBinary() {
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	String temp = "";
   	   	binary = new String[rows][cols];
   	   	binaryString = new StringBuilder(cols*rows*8);
   	   	//process 8-bit binary strings
   	   	for(int r = 0; r < rows; r++)	{
   	   		for(int c = 0; c < cols; c++)	{
   	   			//process into 8-bit binary string
   	   			temp = Integer.toBinaryString(pixels[r][c]);
   	   			//convert to 8 bits
   	   			for(int i = temp.length(); i < 8; i++)
   	   				temp = "0" + temp;
   	   			binary[r][c] = temp;
   	   			binaryString.append(temp);
   	   		}
   	   	}
	}

	//splits image pixels into 8 bitplanes (NOTE: bitPlanes are ordered from lsb!)
	private void constructBitplanes() {
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	bitPlanes = new int[rows][cols][8];
   	   	char[] tempBits = new char[8];
   	   	int[] entry = new int[8];
   	   	for(int r = 0; r < rows; r++)	{
   	   		for(int c = 0; c < cols; c++)	{
   	   			//split binary into character array
   	   			tempBits = binary[r][c].toCharArray();
   	   			//convert to integers, entered from lsb to msb
   	   			for(int i = 0; i < tempBits.length; i++)	{
   	   				entry[i] = Character.getNumericValue(tempBits[i]);
   	   			}
   	   			//set the pixel's 8 bit values
   	   			bitPlanes[r][c] = entry.clone();
   	   		}
   	   	}
	}
	
	//run-length encoding done on the integer values of the image
	//(much better compression can be accomplished if I converted to binary)
	public void rleg()	{
   	   	ArrayList originalPixels = new ArrayList();
		ArrayList compressedPixels = new ArrayList();
		int last = (int) values[0];
		originalPixels.add(last);
		int count = 2;
		boolean run = false;
		int now = 0;
		for(int i = 1; i < values.length; i++)	{
			now = (int) values[i];
			originalPixels.add(now);
			if(run)	{
				//the streak ends, put down compressed version and reset
				if(now != last)	{
					compressedPixels.add(last);
					compressedPixels.add("x");
					compressedPixels.add(count);
					run = false;
					count = 2;
				}
				//keep counting streak
				else	{
					count++;
				}
			}
			//when no streak is detected
			else	{
				//if there still is no streak, put the one read down
				if(now != last)	{
					compressedPixels.add(now);
				}
				//streak detected, switch to count mode
				else	{
					run = true;
				}
			}
			//make room for next integer
			last = now;
		}
		//if read ends with a streak
		if(run)	{
			compressedPixels.add(last);
			compressedPixels.add("x");
			compressedPixels.add(count);
		}
		System.out.println("------RLE Greyscale Compression------");
		System.out.println("Sample:");
		for(int i = 0; i < 10; i++)	{
			System.out.print(compressedPixels.get(i) + " ");
		}
		System.out.println();
		System.out.printf("Original image size:     %10d bits\n", originalPixels.size()*8);
		System.out.printf("Result after compression:%10d bits\n", compressedPixels.size()*8);
		double compressionRatio = (double)(originalPixels.size()*8)/(double)(compressedPixels.size()*8);
		System.out.printf("Compression Ratio = %.2f", compressionRatio);
	}
	
	//run-length encoding on the binary bitplanes
	public void rleb()	{
		StringBuilder[] compressedBP = new StringBuilder[8];
		for(int i = 0; i < 8; i++)	{
			compressedBP[i] = new StringBuilder(binaryBitplanes[i].length());
			int last = Character.getNumericValue(binaryBitplanes[i].charAt(0));
			//begin count with number of 0's
			if(last == 1)	{
				compressedBP[i].append(0);
			}
			int count = 2;
			boolean run = false;
			for(int j = 1; j < binaryBitplanes[i].length(); j++)	{
				int now = Character.getNumericValue(binaryBitplanes[i].charAt(j));
				//counting the number of recurring digits
				if(run)	{
					//the streak ends, put down compressed version and reset
					if(now != last)	{
						//indicate a 2-digit length
						if(count > 9)	{
							compressedBP[i].append("!");
						}
						compressedBP[i].append(count);
						run = false;
						count = 2;
					}
					//keep counting streak
					else	{
						count++;
					}
				}
				//when no streak is detected
				else	{
					//if there still is no streak, put the one read down
					if(now != last)	{
						compressedBP[i].append(1);
					}
					//streak detected, switch to count mode
					else	{
						run = true;
					}
				}
				//make room for next integer
				last = now;
			}
			if(run)	{
				if(count > 9)	{
					compressedBP[i].append("!");
				}
				compressedBP[i].append(count);
			}
		}
		int originalTotal = 0;
		int compressedTotal = 0;
		for(int i = 0; i < compressedBP.length; i++)	{
			originalTotal += binaryBitplanes[i].length();
			compressedTotal += compressedBP[i].length();
		}
		System.out.println("\n\n------RLE Bitplanes Compression------");
		System.out.println("Sample:");
		System.out.println(compressedBP[0].substring(0, 10));
		System.out.printf("Original size across all bitplanes:%10d bits\n", originalTotal);
		System.out.printf("Result after compression:          %10d bits\n", compressedTotal);
		double compressionRatio = (double)(originalTotal)/(double)(compressedTotal);
		System.out.printf("Compression Ratio = %.2f", compressionRatio);
	}
	
	//huffman encoding compression
	public void huffman()	{
		int[] intHistogram = new int[256];
		for(int i = 0; i < 256; i++)	{
			intHistogram[i] = (int)(histogram[i]);
		}
		HuffmanTree tree = HuffmanCode.buildTree(intHistogram);
		// print out results
        System.out.println("\n\nSYMBOL\tWEIGHT\tHUFFMAN CODE");
        HuffmanCode.printCodes(tree, new StringBuffer());
        
        String[] huffmans = HuffmanCode.getValues();
        
        StringBuilder huffmanString = new StringBuilder(binaryString.length());
		for(int i = 0; i < values.length; i++)	{
			huffmanString.append(huffmans[(int) values[i]]);
		}
		
        System.out.println("\n------Huffman Coding Compression------");
        System.out.println("Sample:");
		System.out.println(huffmanString.substring(0, 10));
		System.out.printf("Original binary size:       %10d bits\n", binaryString.length());
		System.out.printf("Huffman encoded binary size:%10d bits\n", huffmanString.length());
		double compressionRatio = (double)(binaryString.length())/(double)(huffmanString.length());
		System.out.printf("Compression Ratio = %.2f", compressionRatio);
	}

	//differential pulse-code modulation: record only the differences between
	//adjacent pixels and use only the bits required to record these values.
	//here we use a simple 6 and 8-bit system.
	public void dpcm() {
		int[] diff = new int[values.length];
		int first;
		int second;
		int bits = 0;
		for(int i = 1; i < values.length-1; i++)	{
			first = (int) values[i-1];
			second= (int) values[i];
			diff[i] = first-second;
			//code next value as 8 bits to hold larger difference
			if(diff[i] < -31 || diff[i] > 30)	{
				bits += 8;
			}
			else bits += 6;
		}
		
		System.out.println("\n\n------DPCM Compression------");
		System.out.println("Sample:");
		for(int i = 0; i < 10; i++)	{
			System.out.print(diff[i] + " ");
		}
		System.out.println();
		System.out.printf("Original binary size:       %10d bits\n", binaryString.length());
		System.out.printf("Huffman encoded binary size:%10d bits\n", bits);
		double compressionRatio = (double)(binaryString.length())/(double)(bits);
		System.out.printf("Compression Ratio = %.2f", compressionRatio);
	}
	
	//dynamically record the patterns encountered in the array,
	//output as a list of unique patterns encountered
	public void lzw()	{
		int wordCount = 1;
		HashMap lzw = new HashMap();
		StringBuilder growingKey = new StringBuilder(1000);
		ArrayList output = new ArrayList();
		System.out.println("\n\n------LZW Compression------");
		System.out.printf("%11s %39s\n", "Output Code", "Dictionary Entry");
		//build dynamic dictionary that places new entry for novel strings,
		//saving the code value of that string each time
		for(int i = 0; i < values.length; i++)	{
			growingKey.append(String.valueOf((int)values[i]) + " ");
			//if string isn't in dictionary, add it to dictionary and the code to
			//output
			if(!lzw.containsKey(growingKey.toString()))	{
				lzw.put(growingKey.toString(), wordCount);
				output.add(wordCount);
				if(wordCount < 300)	{
					System.out.printf("%11d %40s\n", wordCount, growingKey.toString());
				}
				wordCount++;
				growingKey = new StringBuilder(1000);
			}
			//if string is in dictionary, look for a longer unregistered string
		}
		System.out.println("Sample:");
		for(int i = 0; i < 10; i++)	{
			System.out.print(output.get(i) + " ");
		}
		System.out.println();
		System.out.printf("Original array size:%10d bits\n", values.length*8);
		System.out.printf("LZW array size:     %10d bits\n", output.size()*8);
		double compressionRatio = (double)(values.length)/(double)(output.size());
		System.out.printf("Compression Ratio = %.2f", compressionRatio);
	}
}
