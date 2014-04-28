package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Extremely crappy test suite for each method for troubleshooting purposes.
 * @author jahmezz
 *
 */
public class test {
	static int[][]pixels = new int[3][3];
	static int[][]blurred = new int[3][3];
	public static void main(String[] args)	{
		test test = new test();
		test.makeArray();
		//test.test(3);
		//test.testMed(3);
		//test.testSharp(3);
		//test.testBoost(3, 3, pixels, blurred);
		test.drawBitplanes(new boolean[8]);
	}

	private int[][] newPixels;
	private int[][][] bitPlanes;
	
	public void makeArray()	{
		for(int r = 0; r < pixels.length; r++)	{
			for(int c = 0; c < pixels.length; c++)	{
				pixels[r][c] = (int) (Math.random() * 256);
				blurred[r][c] = 128;
				System.out.print(pixels[r][c] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	public void test(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
   		System.out.println(mask);
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    int total = 0;
   	    int average = 0;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		total = 0;
   	    		for(int r = theRow - mask/2; r < theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c < theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					//assume all pixels beyond image are about the intensity of the wall (~200?)
   	    					total += 3;
   	    				}
   	    				else	{
	   	    				total += pixels[r][c];
   	    				}
   	    			}
   	    		}
   	    		//take average
   	    		average = total/(mask*mask);
   	    		System.out.println(theCol);
   	    	   	pixels[theRow][theCol] = average;
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
	}
	public void testMed(int mask) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
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
   	    					elementList.add((int)(Math.random() * 3));
   	    				}
   	    				else	{
	   	    				elementList.add(pixels[r][c]);
   	    				}
   	    			}
   	    		}
   	    		//take median
   	    		Collections.sort(elementList);
   	    		Iterator it = elementList.iterator();
   	    		while(it.hasNext())	{
   	    			System.out.print(it.next() + " ");
   	    		}
   	    		int median = elementList.get(elementList.size()/2);
   	    	   	pixels[theRow][theCol] = median;
   	    	   	System.out.println("\n" + median);
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
	}
	
	public void testSharp(int mask) {
		
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	double[][] laplacian = new double[rows][cols];
   	   	//start at 0,0
   	    int theRow = 0;
   	    int theCol = 0;
   	    double lap = 0;
   	    while (theRow < rows)	{
   	    	theCol = 0;
   	    	while(theCol < cols)	{
   	    		lap = 0;
   	    		//applies [(1,1,1)(1,-8,1)(1,1,1)] mask
   	    		for(int r = theRow - mask/2; r <= theRow + mask/2; r++)	{
   	    			for(int c = theCol - mask/2; c <= theCol + mask/2; c++)	{
   	    				if (r >= rows || r < 0 || c >= cols || c < 0)	{
   	    					//assume all pixels beyond image are about the intensity of the wall (~200?)
   	    					lap += (pixels[theRow][theCol])/8.0;
   	    				}
   	    				else if (r == theRow && c == theCol)	{
   	    					lap -= (pixels[r][c]);
   	    				}
   	    				else	{
	   	    				lap += (pixels[r][c])/8.0;
   	    				}
   	    				System.out.print(lap + " ");
   	    			}
   	    			System.out.println();
   	    		}
   	    		//add laplacian to the original image 
   	    	    laplacian[theRow][theCol] = lap;
   	    	   	System.out.println(theRow + ", " + theCol + ": " + laplacian[theRow][theCol] + "\n");
	   	 		theCol++;
   	    	}
   	    	theRow++;
   	    }
	}
	
	public void testBoost(int mask, int A, int[][] orig, int[][] blurred) {
		//mask must be odd in order for there to be a center pixel
		if(mask%2 == 0)	mask++;
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	int[][] boostMask = new int[rows][cols];
   	   	for(int r = 0; r < pixels.length; r++)	{
			for(int c = 0; c < pixels[0].length; c++)	{
				//boost mask = original image - blurred image
				boostMask[r][c] = orig[r][c] - blurred[r][c];
				//eliminate negative numbers
				//final image = original + weighted boost mask
				pixels[r][c] = A*boostMask[r][c];
				if(pixels[r][c] > 255) pixels[r][c] = 255;
				System.out.print(pixels[r][c] + " ");
			}
			System.out.println();
		}
	}
	
	public void updatePixels(int rows, int cols)	{
		for(int r = 0; r < rows; r++)	{
			for(int c = 0; c < cols; c++)	{
    			pixels[r][c] = newPixels[r][c];
    			System.out.print(pixels[r][c] + " ");
			}
			System.out.println();
   	    }
	}
	public void drawBitplanes(boolean[] onBits) {
		if (bitPlanes == null) constructBitplanes();
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
		newPixels = new int[rows][cols];
		for(int i = 0; i < onBits.length; i++)	{
			if(onBits[i])	{
				continue;
			}
			for(int r = 0; r < pixels.length; r++)	{
				for(int c = 0; c < pixels[0].length; c++)	{
					newPixels[r][c] += bitPlanes[r][c][i]*Math.pow(2, i);
					System.out.print(newPixels[r][c] + " ");
				}
				System.out.println();
			}
			System.out.println();
		}
		updatePixels(rows, cols);
		System.out.println("done processing");
	}

	private void constructBitplanes() {
		System.out.println("i was here");
		int cols = pixels[0].length;
   	   	int rows = pixels.length;
   	   	bitPlanes = new int[rows][cols][8];
   	   	String[][] binary = new String[rows][cols];
   	   	char[] tempBits = new char[8];
   	   	int[] entry = new int[8];
   	   	//process 8-bit binary strings
   	   	for(int r = 0; r < rows; r++)	{
   	   		for(int c = 0; c < cols; c++)	{
   	   			//process into 8-bit binary string
   	   			binary[r][c] = Integer.toBinaryString(pixels[r][c]);
   	   			//convert to 8 bits
   	   			for(int i = binary[r][c].length(); i < 8; i++)
   	   				binary[r][c] = "0" + binary[r][c];
   	   			//split into character array
   	   			tempBits = binary[r][c].toCharArray();
   	   			//convert to integers, entered from lsb to msb
   	   			for(int i = 0; i < tempBits.length; i++)	{
   	   				entry[tempBits.length-1-i] = Character.getNumericValue(tempBits[i]);
   	   			}
   	   			//set the pixel's 8 bit values
   	   			bitPlanes[r][c] = entry.clone();
   	   		}
   	   	}
	}
}
