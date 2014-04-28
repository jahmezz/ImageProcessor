package core;

public class ImageCompression {
	public int[][] originalImage;
	public String binaryString;
	public static void main (String [] args)	{
		ImageCompression iC = new ImageCompression();
		iC.run();

		
	}
	public void run()	{
		PgmImage img = new PgmImage("image.pgm");
		originalImage = img.getPixels();
		boolean[] yes = new boolean[8];
		for(int i = 0; i < yes.length; i++) yes[i] = true;
		img.drawBitplanes(yes);
		img.getValues();
		long startTime = System.currentTimeMillis();
		img.rleg();
		System.out.println("\nRLEG Execution Time: " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		img.drawBitplanes(yes);
		img.getValues();
		img.rleb();
		System.out.println("\nRLEB Execution Time: " + (System.currentTimeMillis() - startTime) + " ms");
		img.eq();
		startTime = System.currentTimeMillis();
		img.huffman();
		System.out.println("\nHuffman Execution Time: " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		img.dpcm();
		System.out.println("\nDPCM Execution Time: " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		img.lzw();
		System.out.println("\nLZW Execution Time: " + (System.currentTimeMillis() - startTime) + " ms");
	}
}