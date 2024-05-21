import edu.princeton.cs.algs4.Picture;

import java.awt.*;
import java.util.HashMap;

public class SeamCarver  {
    //当前图片
    private Picture pic;
    //定义模式是水平还是垂直
    private String mode;
    private int height;
    private int width;
    // 一个二维数组，用于标记保护区域
    private boolean[][] protectedArea;

    // 一个二维数组，用于标记易于移除的区域
    private boolean[][] removalArea;

    // 用户调用此方法来保护图像中的某些区域
    public void protectArea(int[][] area) {
        for (int[] pixel : area) {
            int x = pixel[0];
            int y = pixel[1];
            protectedArea[x][y] = true;
        }
    }
    // 用户调用此方法来标记易于移除的区域
    public void markRemovalArea(int[][] area) {
        for (int[] pixel : area) {
            int x = pixel[0];
            int y = pixel[1];
            removalArea[x][y] = true;
        }
    }


    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
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
        //保存旧的图片
        SeamCarver oldseamcarver = new SeamCarver(pic);
        //先放大高度
        for (int col = 0; col < colEnlarged; col++) {
            //每次找到一个seam后remove该seam进而选取第二小的seam,以此类推
            addVerticalSeam(oldseamcarver.findVerticalSeam());
            oldseamcarver.removeVerticalSeam(oldseamcarver.findVerticalSeam());
        }
        for (int row = 0; row < rowEnlarged; row++) {
            //每次找到一个水平seam后remove掉去寻找第二小的seam以此类推
            addHorizontalSeam(oldseamcarver.findHorizontalSeam());
            oldseamcarver.removeHorizontalSeam(oldseamcarver.findHorizontalSeam());
        }
        return pic;
    }

    public Picture shrinkImage(int wid, int het){
        int rowShrinked = pic.height() - het;
        int colShrinked = pic.width() - wid;
        //先缩小高度
        for (int col = 0; col < rowShrinked; col++) {
            removeHorizontalSeam(findHorizontalSeam());}
        //再缩小高度
        for (int row = 0; row < colShrinked; row++) {
            removeVerticalSeam(findVerticalSeam());
        }
        return pic;
    }
    public void initMarkedArea() {
        protectedArea = new boolean[width()][height()];
        removalArea = new boolean[width()][height()];
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y){
        //考虑保护和易于移除的情况
        //首先当protectedArea没有被初始化时，则忽略
        if(protectedArea != null) {
            if(protectedArea[x][y]) {
                return 1000;
            }
        }
        //和保护区域逻辑一样
        if(removalArea != null) {
            if(removalArea[x][y]) {
                return 0;
            }
        }
        //设定 边界情况都为1000
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


    public void addHorizontalSeam(int[] seam){
        //首先判断seam是否有效
        if (!isValidHorizontalSeam(seam)){
            throw new IllegalArgumentException("Seam is invalid.");
        }

        //采用平均的方法添加seam
        //仿照remove的方式创建比原来大一行的图片对象
        Picture newOne = new Picture(width(), height() + 1);
        //将seam复制到下一行
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height() + 1; row++) {
                if (row < seam[col]){
                    //seam对应行及上述所有行的像素保持不变
                    newOne.set(col,row,pic.get(col,row));
                } else if (row == seam[col]) {

                    if (row == 0){
                        //row在第一行只取下方像素
                        newOne.set(col,row,pic.get(col,row));
                    } else if (row == height()) {
                        //row在最后一行只取上方像素
                        newOne.set(col,row,pic.get(col,row - 1));
                    } else {
                        Color up = pic.get(col,row);
                        Color down = pic.get(col,row + 1);
                        newOne.set(col,row,aveColor(up,down));
                    }
                    newOne.set(col,row + 1,pic.get(col,row));

                } else if (row > seam[col]){
                    //seam对应行下面所有行设定为原图上一行的像素
                    newOne.set(col,row,pic.get(col,row - 1));
                }
            }
        }
        height = height + 1;
        pic = new Picture(newOne);
    }
    private boolean isValidVerticalSeam(int[] seam){
        boolean flag = true;
        if (seam == null || seam.length != pic.height()) {
            flag = false;
        }
        return flag;
    }
    private boolean isValidHorizontalSeam(int[] seam){
        boolean flag = true;
        if (seam == null || seam.length != pic.width()) {
            flag = false;
        }
        return flag;
    }

    public void addVerticalSeam(int[] seam){
        //首先判断seam是否有效
        if (!isValidVerticalSeam(seam)){
            throw new IllegalArgumentException("Seam is invalid.");
        }
        //采用平均的方法添加seam
        //仿照remove的方式创建比原来大一行的图片对象
        Picture newOne = new Picture(width() + 1, height());
        //将seam复制到下一行
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width() + 1; col++) {
                if (col < seam[row]){
                    //seam对应列及左边所有列的像素保持不变
                    newOne.set(col,row,pic.get(col,row));
                    //通过平均的方法添加seam
                } else if (col == seam[row]) {

                    if (col == 0){
                        //col在第一列只取右边像素
                        newOne.set(col,row,pic.get(col,row));
                    } else if (col == width()) {
                        //col在最后一列只取左边像素
                        newOne.set(col,row,pic.get(col - 1,row));
                    }else {
                        //采取平均的方式
                        Color left = pic.get(col, row);
                        Color right = pic.get(col + 1, row);
                        newOne.set(col,row,aveColor(left, right));
                    }
                    newOne.set(col + 1,row,pic.get(col, row));

                } else if (col > seam[row]){
                    //seam对应行右边所有列设定为原图上一列的像素
                    newOne.set(col,row,pic.get(col - 1,row));
                }
            }
        }
        width = width + 1;
        pic = new Picture(newOne);
    }
    private Color aveColor(Color c1, Color c2){
        int red = (c1.getRed() + c2.getRed()) / 2;
        int green = (c1.getGreen() + c2.getGreen()) / 2;
        int blue = (c1.getBlue() + c2.getBlue()) / 2;
        return new Color(red, green, blue);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam){
        Picture newOne = new Picture(width(),height() - 1);
        //使seam上的所有色素块往下移动一格
        //首先选中seam上方得区域

        for (int col = 0; col < width(); col++)
            for (int row = 0; row < height() - 1; row++) {

                if (row < seam[col])
                    newOne.set(col, row, pic.get(col, row));
                else
                    newOne.set(col, row, pic.get(col, row + 1));

            }
        height = height - 1;
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