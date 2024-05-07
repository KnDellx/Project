import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Picture;

import java.awt.*;
import java.text.CollationElementIterator;
import java.util.HashMap;

public class SeamCarver {
    private Picture picture;
    private String mode;

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
        //如果超过边界报错
        if (x < 0||x >= width()||y < 0||y > height()){
            throw new  IndexOutOfBoundsException();
        }
        //计算能量方程
        Color color1 = picture().get(x + 1,y);
        Color color2 = picture().get(x - 1,y);
        Color color3 = picture().get(x,y - 1);
        Color color4 = picture().get(x,y + 1);
        //计算沿着x,y方向分别的能量数值
        double deltax = Math.pow(Math.abs(color1.getRed() - color2.getRed()),2) + Math.pow(Math.abs(color1.getBlue() - color2.getBlue()),2) + Math.pow(Math.abs(color1.getGreen() - color2.getGreen()),2);
        double deltay = Math.pow(Math.abs(color3.getRed() - color4.getRed()),2) + Math.pow(Math.abs(color3.getBlue() - color4.getBlue()),2) + Math.pow(Math.abs(color3.getGreen() - color4.getGreen()),2);
        return deltax + deltay;
    }

    // 水平切割
    public int[] findHorizontalSeam(){
        mode = "horizontal";
        //构造两个hashmap分别代表seam上每个点的energy和seam上每个点的row值的连接
        HashMap<String, String> pathTo = new HashMap<String, String>();
        HashMap<String, Double> energyTo = new HashMap<String, Double>();
        String cur , next, end = null;
        Double cost = Double.MAX_VALUE;
        for (int col = 0; col < width() - 1; col++)
            for (int row = 0; row < height(); row++) {
                //用第几行第几列来代表像素点所在位置
                cur = col + " " + row;
            //对于第一列的像素设置前导像素为空并填入能量
            if (col == 0){
                energyTo.put(cur,energy(col,row));
                pathTo.put(cur,null);
            }
            //遍历该点附近
                for (int i = row - 1; i <= row + 1; i++)
                    if (i >= 0 && i < height()) {
                        next = (col + 1) + " " + row;
                        double newEng = energy(col + 1, i) + energyTo.get(cur);
                        //如果我们还没有一条新的边，添加一个；或者
                        // 如果这个边代表的能量值更小就代替
                        if (energyTo.get(next) == null || newEng < energyTo.get(next)) {

                            pathTo.put(next, cur);
                            energyTo.put(next, newEng);

                            //End at the second to last column, because 'next' involves
                            // the next column.
                            if (col + 1 == width() - 1 && newEng < cost) {
                                cost = newEng;
                                end = next;
                            }
                        }
                    }
            }
        //模式为水平时，定义路径大小为宽度
        int size = 0;
        int[] path = new int[size];
        if (mode == "horizontal") {
            size = width();
            path = new int[width()];
        } else if (mode == "vertical") {
            size = height();
            path = new int[height()];
        }
        //通过回溯的方法还原最短路径
        String current = end;
        //遍历最小的
        while (size > 0){
            size = size - 1;
            path[size] = str2id(mode,current);
            current = pathTo.get(current);
        }
        return path;
    }
    private int str2id(String mode, String str) {
        if (mode.equals("v"))
            return Integer.parseInt(str.split(" ")[0]);
        else if (mode.equals("h"))
            return Integer.parseInt(str.split(" ")[1]);
        else
            throw new IllegalArgumentException();
    }


    // sequence of indices for vertical seam
    public int[] findVerticalSeam(){
        mode = "vertical";
        //构造两个hashmap分别代表seam上每个点的energy和seam上每个点的row值的连接
        HashMap<String, String> pathTo = new HashMap<String, String>();
        HashMap<String, Double> energyTo = new HashMap<String, Double>();
        String cur , next, end = null;
        Double cost = Double.MAX_VALUE;
        for (int row = 0; row < height() - 1; row++)
            for (int col = 0; col < width(); col++) {
                //用第几行第几列来代表像素点所在位置
                cur = col + " " + row;
                //对于第一列的像素设置前导像素为空并填入能量
                if (row == 0){
                    energyTo.put(cur,energy(col,row));
                    pathTo.put(cur,null);
                }
        //遍历该点附近
        for (int i = col - 1; i <= col + 1; i++)
            if (i >= 0 && i < width()) {
                next = col + " " + (row + 1);
                double newEng = energy(i, row + 1) + energyTo.get(cur);
                //如果我们还没有一条新的边，添加一个；或者
                // 如果这个边代表的能量值更小就代替
                if (energyTo.get(next) == null || newEng < energyTo.get(next)) {

                    pathTo.put(next, cur);
                    energyTo.put(next, newEng);

                    //End at the second to last column, because 'next' involves
                    // the next column.
                    if (col + 1 == width() - 1 && newEng < cost) {
                        cost = newEng;
                        end = next;
                    }
                }
            }
    }

    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam)

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam)

    //  unit testing (optional)
    public static void main(String[] args)

}