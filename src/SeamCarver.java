import edu.princeton.cs.algs4.Picture;

import java.awt.*;

public class SeamCarver {

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture)

    // current picture
    public Picture picture(){
        Picture picture   = new Picture("https://introcs.cs.princeton.edu/java/stdlib/mandrill.jpg");
        return picture;
    }

    // width of current picture
    public int width(){
        return picture().width();
    }

    // height of current picture
    public int height(){
        return picture().height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y){
        //calculate the energy functon of each pixel
        Color color1 = picture().get(x + 1,y);
        Color color2 = picture().get(x - 1,y);
        int deltax = Math.pow(Math.abs(color1.getRed() - color2.getRed()),2) + Math.pow(Math.abs(color1.getRed() - color2.getRed()),2)
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam()

    // sequence of indices for vertical seam
    public int[] findVerticalSeam()

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam)

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam)

    //  unit testing (optional)
    public static void main(String[] args)

}