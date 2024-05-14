import edu.princeton.cs.algs4.Picture;

import java.awt.*;
import java.util.HashMap;

public class SeamCarver {
    private Picture pic;
    private String mode;
    private int height;
    private int width;




    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture){
        pic = new Picture(picture);
        width = pic.width();
        height = pic.height();

    }


    // current picture
    public Picture picture(){
        return pic;
    }

    // width of current picture
    public int width(){
        return pic.width();
    }

    // height of current picture
    public int height(){
        return pic.height();
    }
    public Picture enlargeImage(int wid, int het){
        int colEnlarged = wid - pic.width();
        int rowEnlarged = het - pic.height();
        //先放大高度
        for (int col = 0; col < colEnlarged; col++) {

        }
    }

    public Picture shrinkImage(int wid, int het){
        int rowShrinked = pic.height() - het;
        int colShrinked = pic.width() - wid;
        //先缩小高度
        for (int col = 0; col < rowShrinked; col++) {
            removeHorizontalSeam(findHorizontalSeam());
        }
        //再缩小高度
        for (int row = 0; row < colShrinked; row++) {
            removeVerticalSeam(findVerticalSeam());
        }
        return pic;
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
        //中间的不算
        Color color1 = picture().get(x + 1,y);
        Color color2 = picture().get(x - 1,y);
        Color color3 = picture().get(x,y - 1);
        Color color4 = picture().get(x,y + 1);
        //计算沿着x,y方向分别的能量数值
        double deltaX = Math.pow(Math.abs(color1.getRed() - color2.getRed()),2) + Math.pow(Math.abs(color1.getBlue() - color2.getBlue()),2) + Math.pow(Math.abs(color1.getGreen() - color2.getGreen()),2);
        double deltaY = Math.pow(Math.abs(color3.getRed() - color4.getRed()),2) + Math.pow(Math.abs(color3.getBlue() - color4.getBlue()),2) + Math.pow(Math.abs(color3.getGreen() - color4.getGreen()),2);
        return deltaX + deltaY;
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
                //???
                pathTo.put(cur,null);
            }
            //遍历该点附近
                for (int i = row - 1; i <= row + 1; i++)
                    if (i >= 0 && i < height()) {
                        next = (col + 1) + " " + row;
                        double newEng = energy(col + 1, i) + energyTo.get(cur);//???
                        //如果我们还没有一条新的边，添加一个；或者
                        // 如果这个边代表的能量值更小就代替
                        if (energyTo.get(next) == null || newEng < energyTo.get(next)) {
                            //联系当前点和下一点
                            pathTo.put(next, cur);
                            energyTo.put(next, newEng);
                            //如果当前列是倒数第二列，并且新能量值小于当前最小能量值，则更新最小能量值和终点。
                            if (col + 1 == width() - 1 && newEng < cost) {
                                cost = newEng;
                                end = next;
                            }
                        }
                    }
            }
        //模式为水平时，定义路径大小为宽度
        int size = width();
        int[] path = new int[size];
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

    //optimize here
    private int str2id(String mode, String str) {
        if (mode.equals("vertical"))
            return Integer.parseInt(str.split(" ")[0]);//0->1
        else if (mode.equals("horizontal"))
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
                //对于第一行的像素设置前导像素为空并填入能量
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
                    //bug
                    if (row + 1 == height() - 1 && newEng < cost) {
                        cost = newEng;
                        end = next;
                    }
                }
            }
    }
        //模式为水平时，定义路径大小为宽度
        int size = height();
        int[] path = new int[size];
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

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam){
        Picture newOne = new Picture(width(),height() - 1);
        //使seam上的所有色素块往下移动一格
        //首先选中seam上方得区域

        for (int col = 0; col < width() ; col++) {
            for (int row = seam[col] + 1; row < height; row++) {
                newOne.set(col,row-1,pic.get(col,row ));
            }
        }
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < seam[col] ; row++) {
                newOne.set(col,row,pic.get(col,row));
            }
        }
        height = height - 1;
        pic = new Picture(newOne);

    }

    public void addHorizontalSeam(int[] seam){
        //仿照remove的方式创建比原来大一行的图片对象
        Picture newOne = new Picture(width(), height() + 1);
        //将seam复制到下一行
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height() + 1; row++) {
                if (row <= seam[col]){
                    //seam对应行及上述所有行的像素保持不变
                    newOne.set(col,row,pic.get(col,row));
                }else {
                    //seam对应行下面所有行设定为原图上一行的像素
                    newOne.set(col,row,pic.get(col,row - 1));
                }
            }
        }
        height = height + 1;
        pic = new Picture(newOne);
    }

    public void addVerticalSeam(int[] seam){
        //仿照remove的方式创建比原来大一行的图片对象
        Picture newOne = new Picture(width() + 1, height());
        //将seam复制到下一行
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width() + 1; col++) {
                if (col <= seam[row]){
                    //seam对应列及左边所有列的像素保持不变
                    newOne.set(col,row,pic.get(col,row));
                }else {
                    //seam对应行右边所有列设定为原图上一列的像素
                    newOne.set(col,row,pic.get(col - 1,row));
                }
            }
        }
        width = width + 1;
        pic = new Picture(newOne);
    }


    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam){
        Picture newOne = new Picture(width() - 1,height());
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width() - 1; col++) {
                if (col <= seam[row] - 1){
                    newOne.set(col,row,pic.get(col,row));
                }else {
                    newOne.set(col,row,pic.get(col + 1,row));
                }
            }

        }
        width = width - 1;
        pic = new Picture(newOne);
    }
}