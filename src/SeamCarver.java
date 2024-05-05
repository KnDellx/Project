import edu.princeton.cs.algs4.Picture;

import java.awt.*;
import java.text.CollationElementIterator;

public class SeamCarver {
    private Picture picture;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture){
        picture = new Picture(picture);
    }

    // current picture
    public Picture picture(){
        return picture;
    }

    // width of current picture
    public int width(){
        return picture.width();
    }

    // height of current picture
    public int height(){
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y){
        //设定边界情况都为1000
        if(x == 0||y == 0||x == width() - 1||y == height() - 1){
            return 1000;
        }
        if (x < 0||x >= width()||y < 0||y > height()){
            throw new  IndexOutOfBoundsException();
        }
        //calculate the energy functon of each pixel
        Color color1 = picture().get(x + 1,y);
        Color color2 = picture().get(x - 1,y);
        Color color3 = picture().get(x,y - 1);
        Color color4 = picture().get(x,y + 1);
        //calculate the energy function along the x,y axis seperately
        double deltax = Math.pow(Math.abs(color1.getRed() - color2.getRed()),2) + Math.pow(Math.abs(color1.getBlue() - color2.getBlue()),2) + Math.pow(Math.abs(color1.getGreen() - color2.getGreen()),2);
        double deltay = Math.pow(Math.abs(color3.getRed() - color4.getRed()),2) + Math.pow(Math.abs(color3.getBlue() - color4.getBlue()),2) + Math.pow(Math.abs(color3.getGreen() - color4.getGreen()),2);
        return deltax + deltay;
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