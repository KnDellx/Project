
import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {

    // The representation of the given image
    private int[][] color;

    // The energy of each pixel in the image
    private double[][] energy;

    // Arrays and sinks for finding the shortest path through the image energy
    private double[][] distTo;
    private double distToSink;
    private int[][] edgeTo;
    private int edgeToSink;

    // The current width and height
    private int w;
    private int h;
    // 一个二维数组，用于标记保护区域
    public Boolean[][] protectedArea;

    // 一个二维数组，用于标记易于移除的区域
    public Boolean[][] removalArea;

    // False if finding or removing a vertical seam,
    // true if finding or removing a horizontal seam.
    private boolean transposed;
    public void initMarkedArea() {
        this.protectedArea = new Boolean[width()][height()];
        this.removalArea = new Boolean[width()][height()];
    }
    // 用户调用此方法来保护图像中的某些区域
    public void protectArea(Boolean[][] area) {
        for (int x = 0; x < area.length; x++) {
            for (int y = 0; y < area[0].length; y++) {
                this.protectedArea[x][y] = area[x][y];
            }
        }
    }
    // 用户调用此方法来标记易于移除的区域
    public void markRemovalArea(Boolean[][] area) {
        for (int x = 0; x < area.length; x++) {
            for (int y = 0; y < area[0].length; y++) {
                this.removalArea[x][y] = area[x][y];
            }
        }
    }

    public SeamCarver(Picture picture) {
        if (picture == null) throw new java.lang.NullPointerException();

        // Initialize the dimensions of the picture
        w = picture.width();
        h = picture.height();

        // Store the picture's color information in an int array,
        // using the RGB coding described at:
        // http://docs.oracle.com/javase/8/docs/api/java/awt/Color.html#getRGB()
        color = new int[h][w];

        // Set the dimensions of the energy array
        energy = new double[h][w];

        // Store color information
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                color[i][j] = picture.get(j, i).getRGB();
            }
        }

        // Pre-calculate the energy array
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                energy[i][j] = calcEnergy(j, i);
            }
        }
    }

    public Picture picture() {

        // Create and return a new pic with the stored color information
        Picture pic = new Picture(width(), height());
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                pic.set(j, i, new Color(color[i][j]));
            }
        }
        return new Picture(pic);
    }
    public int width() {
        return w;
    }
    public int height() {
        return h;
    }
    private double calcEnergy(int x, int y) {
        //考虑保护和易于移除的情况
        //首先当protectedArea没有被初始化时，则忽略
        if(protectedArea != null) {
            if(protectedArea[x][y] != null && protectedArea[x][y]) {
                return Double.MAX_VALUE;
            }
        }
        //和保护区域逻辑一样
        if(removalArea != null) {
            if(removalArea[x][y] != null && removalArea[x][y]) {
                return -Double.MAX_VALUE;
            }
        }
        if (x >= width() || y >= height() || x < 0 || y < 0)
            throw new java.lang.IndexOutOfBoundsException();

        // Return 1000.0 for border pixels
        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1)
            return (double) 1000;

        // Store pixel values in Color objects.
        Color up = new Color(color[y - 1][x]);
        Color down = new Color(color[y + 1][x]);
        Color left = new Color(color[y][x - 1]);
        Color right = new Color(color[y][x + 1]);

        return Math.sqrt(gradient(up, down) + gradient(left, right));
    }
    private double gradient(Color a, Color b) {
        return Math.pow(a.getRed() - b.getRed(), 2) +
                Math.pow(a.getBlue() - b.getBlue(), 2) +
                Math.pow(a.getGreen() - b.getGreen(), 2);
    }
    public int[] findHorizontalSeam() {
        transposed = true;

        // Reset our distTo and edgeTo values for a new search
        distToSink = Double.POSITIVE_INFINITY;
        edgeToSink = Integer.MAX_VALUE;
        distTo = new double[h][w];
        edgeTo = new int[h][w];
        for (double[] r: distTo) Arrays.fill(r, Double.POSITIVE_INFINITY);
        for (int[] r: edgeTo) Arrays.fill(r, Integer.MAX_VALUE);

        // Relax the entire left column, since this is our starting column
        for (int i = 0; i < height(); i++) {
            distTo[i][0] = (double) 1000;
            edgeTo[i][0] = -1;
        }

        // Visit all pixels from the left side, diagonally to the right,
        // in keeping with topological order.
        // The topological order is the reverse of the DFS post-order,
        // which visits the top-most adjacent pixel first, before it visits
        // the pixels below.
        for (int depth = height() - 1; depth > 0; depth--) {
            for (int out = 0; out < width() && depth + out < height(); out++) {
                visit(depth + out, out);
            }
        }

        // Visit all pixels from the top, diagonally to the right,
        // in keeping with the topological order described above.
        for (int top = 0; top < width(); top++) {
            for (int depth = 0;
                 depth + top < width() && depth < height();
                 depth++) {
                visit(depth, depth + top);
            }
        }

        // Populate seam[] with the shortest path
        int[] seam = new int[width()];
        seam[width() - 1] = edgeToSink;

        for (int j = width() - 1; j > 0; j--) {
            seam[j - 1] = edgeTo[seam[j]][j];
        }

        // null out our shortest-path arrays for garbage collection
        distTo = null;
        edgeTo = null;

        return seam;

    }

    public int[] findVerticalSeam() {
        transposed = false;

        // Reset our distTo and edgeTo values for a new search
        distToSink = Double.POSITIVE_INFINITY;
        edgeToSink = Integer.MAX_VALUE;
        distTo = new double[h][w];
        edgeTo = new int[h][w];
        for (double[] r: distTo) Arrays.fill(r, Double.POSITIVE_INFINITY);
        for (int[] r: edgeTo) Arrays.fill(r, Integer.MAX_VALUE);

        // Relax the entire top row, since this is our starting row
        Arrays.fill(distTo[0], (double) 1000);
        Arrays.fill(edgeTo[0], -1);

        // Visit all pixels from the top, diagonally to the right,
        // in keeping with topological order.
        // The topological order is the reverse of the DFS post-order,
        // which visits the left-most adjacent pixel first, before it visits
        // pixels to the right.
        for (int top = width() - 1; top >= 0; top--) {
            for (int depth = 0;
                 depth + top < width() && depth < height();
                 depth++) {
                visit(depth, depth + top);
            }
        }
        // Visit all pixels from the left side, diagonally to the right,
        // in keeping with the topological order described above.
        for (int depth = 1; depth < height(); depth++) {
            for (int out = 0;
                 out < width() && depth + out < height();
                 out++) {
                visit(depth + out, out);
            }
        }

        // Populate seam[] with the shortest path
        int[] seam = new int[height()];
        seam[height() - 1] = edgeToSink;

        for (int i = height() - 1; i > 0; i--) {
            seam[i - 1] = edgeTo[i][seam[i]];
        }

        // null out our shortest-path arrays for garbage collection
        distTo = null;
        edgeTo = null;

        return seam;
    }

    private void visit(int i, int j) {
        if (transposed) {
            // Only relax the sink
            if (j == width() - 1) {
                relax(i, j);
            }

            // Bottom edge; relax to the right and above
            else if (i == height() - 1) {
                relax(i, j, i, j + 1);
                relax(i, j, i - 1, j + 1);
            }

            // Top edge; relax to the right and below
            else if (i == 0) {
                relax(i, j, i, j + 1);
                relax(i, j, i + 1, j + 1);
            }

            // Middle pixel; relax right, below, and above
            else {
                relax(i, j, i - 1, j + 1);
                relax(i, j, i, j + 1);
                relax(i, j, i + 1, j + 1);
            }
        }

        else {
            // Only relax the sink
            if (i == height() - 1) {
                relax(i, j);
            }

            // Right edge; relax below and to the left
            else if (j == width() - 1) {
                relax(i, j, i + 1, j - 1);
                relax(i, j, i + 1, j);
            }

            // Left edge; relax below and to the right
            else if (j == 0) {
                relax(i, j, i + 1, j);
                relax(i, j, i + 1, j + 1);
            }

            // Middle pixel; relax left, below, and right
            else {
                relax(i, j, i + 1, j - 1);
                relax(i, j, i + 1, j);
                relax(i, j, i + 1, j + 1);
            }
        }
    }

    private void relax(int i, int j) {
        if (validIndex(i, j)) {
            if (distToSink > distTo[i][j]) {
                distToSink = distTo[i][j];
                if (transposed) edgeToSink = i;
                else edgeToSink = j;
            }
        }
    }

    private void relax(int i1, int j1, int i2, int j2) {
        if (validIndex(i1, j1) && validIndex(i2, j2)) {
            if (distTo[i2][j2] > distTo[i1][j1] + energy[i2][j2]) {
                distTo[i2][j2] = distTo[i1][j1] + energy[i2][j2];
                if (transposed) edgeTo[i2][j2] = i1;
                else edgeTo[i2][j2] = j1;
            }
        }
    }

    private boolean validIndex(int i, int j) {
        return (i >= 0 && i < height() && j >= 0 && j < width());
    }

    public void removeHorizontalSeam(int[] seam) {

        // Check for bad input
        if (height() <= 1)
            throw new java.lang.IllegalArgumentException("Picture too short");
        if (seam == null) throw new java.lang.NullPointerException();
        if (seam.length != width())
            throw new java.lang.IllegalArgumentException("Invalid seam length");

        int yLast = seam[0];
        for (int y: seam) {
            if (y >= height() || y < 0)
                throw new java.lang.IllegalArgumentException("Index out of bounds");
            if (Math.abs(y - yLast) > 1)
                throw new java.lang.IllegalArgumentException("Index not adjacent");
            yLast = y;
        }
        // Create replacement arrays
        int[][] newColor = new int[height() - 1][width()];
        double[][] newEnergy = new double[height() - 1][width()];

        // Populate replacement arrays, skipping pixels in the seam
        for (int j = 0; j < width(); j++) {
            int s = seam[j];
            for (int i = 0; i < s; i++) {
                newColor[i][j] = color[i][j];
                newEnergy[i][j] = energy[i][j];
            }

            for (int i = s + 1; i < height(); i++) {
                newColor[i - 1][j] = color[i][j];
                newEnergy[i - 1][j] = energy[i][j];
            }
        }

        color = newColor;
        energy = newEnergy;
        h--;

        // Recalculate the energy along the seam
        for (int j = 0; j < width(); j++) {
            int s = seam[j];
            // Top edge removed
            if (s == 0) {
                energy[s][j] = calcEnergy(j, s);
            }

            // Bottom edge removed
            else if (s == height()) {
                energy[s - 1][j] = calcEnergy(j, s - 1);
            }

            // Middle pixel removed
            else {
                energy[s][j] = calcEnergy(j, s);
                energy[s - 1][j] = calcEnergy(j, s - 1);
            }
        }
    }

    public void removeVerticalSeam(int[] seam) {

        // Check for bad input
        if (width() <= 1)
            throw new java.lang.IllegalArgumentException("Picture too narrow");
        if (seam == null) throw new java.lang.NullPointerException();
        if (seam.length != height())
            throw new java.lang.IllegalArgumentException("Invalid seam length");

        int xLast = seam[0];
        for (int x: seam) {
            if (x >= width() || x < 0)
                throw new java.lang.IllegalArgumentException("Index out of bounds");
            if (Math.abs(x - xLast) > 1)
                throw new java.lang.IllegalArgumentException("Index not adjacent");
            xLast = x;
        }

        // Create replacement arrays
        int[][] newColor = new int[height()][width() - 1];
        double[][] newEnergy = new double[height()][width() - 1];

        // Populate replacement arrays, skipping pixels in the seam
        for (int i = 0; i < height(); i++) {
            int s = seam[i];

            for (int j = 0; j < s; j++) {
                newColor[i][j] = color[i][j];
                newEnergy[i][j] = energy[i][j];
            }

            for (int j = s + 1; j < width(); j++) {
                newColor[i][j - 1] = color[i][j];
                newEnergy[i][j - 1] = energy[i][j];
            }
        }

        color = newColor;
        energy = newEnergy;
        w--;

        // Recalculate the energy along the seam
        for (int i = 0; i < height(); i++) {
            int s = seam[i];

            // Left edge removed
            if (s == 0) {
                energy[i][s] = calcEnergy(s, i);
            }

            // Right edge removed
            else if (s == width()) {
                energy[i][s - 1] = calcEnergy(s - 1, i);
            }

            // Middle pixel removed
            else {
                energy[i][s] = calcEnergy(s, i);
                energy[i][s - 1] = calcEnergy(s - 1, i);
            }
        }
    }
    public Picture shrinkImage(int wid, int het){
        int rowShrinked = picture().height() - het;
        int colShrinked = picture().width() - wid;
        //先缩小高度
        for (int col = 0; col < rowShrinked; col++) {
            removeHorizontalSeam(findHorizontalSeam());}
        //再缩小高度
        for (int row = 0; row < colShrinked; row++) {
            removeVerticalSeam(findVerticalSeam());
        }
        return picture();
    }
    //仿照缩小写一个放大的方法
    public void addVerticalSeam(int[] seam) {
        // Check for bad input
        //ToDo: 1. Check if the picture is too large to add a seam
//        if (width() <= 1)
//            throw new java.lang.IllegalArgumentException("Picture too narrow");
        if (seam == null) throw new java.lang.NullPointerException();
        if (seam.length != height())
            throw new java.lang.IllegalArgumentException("Invalid seam length");

        int xLast = seam[0];
        for (int x: seam) {
            if (x >= width() || x < 0)
                throw new java.lang.IllegalArgumentException("Index out of bounds");
            if (Math.abs(x - xLast) > 1)
                throw new java.lang.IllegalArgumentException("Index not adjacent");
            xLast = x;
        }

        // Create replacement arrays
        int[][] newColor = new int[height()][width() + 1];
        double[][] newEnergy = new double[height()][width() + 1];

        // Populate replacement arrays, inserting pixels in the seam
        for (int i = 0; i < height(); i++) {
            int s = seam[i];

            for (int j = 0; j <= s; j++) {
                newColor[i][j] = color[i][j];
                newEnergy[i][j] = energy[i][j];
            }

            // Insert the new seam pixel (use average of left and right if possible)
            if (s == 0) {
                newColor[i][s + 1] = color[i][s];
            } else if (s == width() - 1) {
                newColor[i][s + 1] = color[i][s];
            } else {
                newColor[i][s + 1] = (color[i][s] + color[i][s + 1]) / 2;
            }

            for (int j = s + 1; j < width(); j++) {
                newColor[i][j + 1] = color[i][j];
                newEnergy[i][j + 1] = energy[i][j];
            }
        }

        color = newColor;
        energy = newEnergy;
        w++;

        // Recalculate the energy along the seam
        for (int i = 0; i < height(); i++) {
            int s = seam[i];

            // Left edge added
            if (s == 0) {
                energy[i][s] = calcEnergy(s, i);
                energy[i][s + 1] = calcEnergy(s + 1, i);
            }

            // Right edge added
            else if (s == width() - 1) {
                energy[i][s] = calcEnergy(s, i);
                energy[i][s - 1] = calcEnergy(s - 1, i);
            }

            // Middle pixel added
            else {
                energy[i][s] = calcEnergy(s, i);
                energy[i][s + 1] = calcEnergy(s + 1, i);
            }
        }
    }

    //仿照放大竖直写一个放大水平的方法
    public void addHorizontalSeam(int[] seam){
        // Check for bad input

        if (seam == null) throw new java.lang.NullPointerException();
        if (seam.length != width())
            throw new java.lang.IllegalArgumentException("Invalid seam length");

        int yLast = seam[0];
        for (int y: seam) {
            if (y >= height() || y < 0)
                throw new java.lang.IllegalArgumentException("Index out of bounds");
            if (Math.abs(y - yLast) > 1)
                throw new java.lang.IllegalArgumentException("Index not adjacent");
            yLast = y;
        }

        // Create replacement arrays
        int[][] newColor = new int[height() + 1][width()];
        double[][] newEnergy = new double[height() + 1][width()];

        // Populate replacement arrays, inserting pixels in the seam
        for (int j = 0; j < width(); j++) {
            int s = seam[j];
            for (int i = 0; i <= s; i++) {
                newColor[i][j] = color[i][j];
                newEnergy[i][j] = energy[i][j];
            }

            // Insert the new seam pixel (use average of up and down if possible)
            if (s == 0) {
                newColor[s + 1][j] = color[s][j];
            } else if (s == height() - 1) {
                newColor[s + 1][j] = color[s][j];
            } else {
                newColor[s + 1][j] = (color[s][j] + color[s + 1][j]) / 2;
            }

            for (int i = s + 1; i < height(); i++) {
                newColor[i + 1][j] = color[i][j];
                newEnergy[i + 1][j] = energy[i][j];
            }
        }

        color = newColor;
        energy = newEnergy;
        h++;

        // Recalculate the energy along the seam
        for (int j = 0; j < width(); j++) {
            int s = seam[j];

            // Top edge added
            if (s == 0) {
                energy[s][j] = calcEnergy(j, s);
                energy[s + 1][j] = calcEnergy(j, s + 1);
            }

            // Bottom edge added
            else if (s == height() - 1) {
                energy[s][j] = calcEnergy(j, s);
                energy[s - 1][j] = calcEnergy(j, s - 1);
            }

            // Middle pixel added
            else {
                energy[s][j] = calcEnergy(j, s);
                energy[s + 1][j] = calcEnergy(j, s + 1);
            }
        }
    }
    //写一个放大图片的代码
    public Picture enlargeImage(int wid, int het){
        int rowEnlarged = het - picture().height();
        int colEnlarged = wid - picture().width();
        //先放大高度
        for (int col = 0; col < rowEnlarged; col++) {
            addHorizontalSeam(findHorizontalSeam());
        }
        //再放大宽度
        for (int row = 0; row < colEnlarged; row++) {
            addVerticalSeam(findVerticalSeam());
        }
        return picture();
    }


}
