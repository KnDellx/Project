import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ImageReader {
    public String loadFilePath = "doc/processing/origin.jpg";
    public String saveFilePath = "doc/processing/";
    private int[][][] colorMatrix;
    private int[][] grayMatrix;
    private int[][] edgeMatrix;
    private int[][] normalizeEdgeMatrix;
    public BufferedImage image;
    private int height;
    private int width;



    public int[][][] getColorMatrix() {
        int[][][] copy = new int[height][width][colorMatrix[0][0].length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                copy[y][x] = Arrays.copyOf(colorMatrix[y][x], colorMatrix[y][x].length);
            }
        }
        return copy;
    }

    public int[][] getGrayMatrix() {
        int[][] copy = new int[height][width];
        for (int y = 0; y < height; y++) {
            copy[y] = Arrays.copyOf(grayMatrix[y], grayMatrix[y].length);
        }
        return copy;
    }

    public int[][] getEdgeMatrix() {
        int[][] copy = new int[height][width];
        for (int y = 0; y < height; y++) {
            copy[y] = Arrays.copyOf(edgeMatrix[y], edgeMatrix[y].length);
        }
        return copy;
    }



    // 读取文件并生成图像
    public void ImageRead() throws IOException {
        this.colorMatrix = imageToColorScale(loadFilePath);
        this.grayMatrix = toGrayScale(colorMatrix);
        saveMatrixAsImage(grayMatrix, saveFilePath + "gray.jpg");
        this.edgeMatrix = sobelEdgeDetection(grayMatrix);
        this.normalizeEdgeMatrix = normalizeEdgeMatrix(edgeMatrix);
        saveMatrixAsImage(normalizeEdgeMatrix, saveFilePath + "edge.jpg");
    }



    // 导入图像文件，并返回色彩值矩阵
    public int[][][] imageToColorScale(String filePath) throws IOException {
        File file = new File(filePath);
        this.image = ImageIO.read(file);

        this.width = image.getWidth();
        this.height = image.getHeight();

        int[][][] colorMatrix = new int[height][width][3]; // 三维数组，存储每个像素点的 RGB 色彩值

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                colorMatrix[y][x][0] = red; // 存储红色分量
                colorMatrix[y][x][1] = green; // 存储绿色分量
                colorMatrix[y][x][2] = blue; // 存储蓝色分量
            }
        }

        return colorMatrix;
    }

    // 将RGB色彩值矩阵转换为灰度图像
    private int[][] toGrayScale(int[][][] matrix) {
        int height = matrix.length;
        int width = matrix[0].length;
        int[][] grayMatrix = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 使用加权平均法将RGB值转换为灰度值
                int gray = (int) (0.2126 * matrix[y][x][0] + 0.7152 * matrix[y][x][1] + 0.0722 * matrix[y][x][2]);
                grayMatrix[y][x] = gray;
            }
        }
        return grayMatrix;
    }

    // 应用Sobel算子进行边缘检测
    private static int[][] sobelEdgeDetection(int[][] matrix) {
        // Sobel算子卷积核
        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

        int height = matrix.length;
        int width = matrix[0].length;
        int[][] edgeMatrix = new int[height][width];

        // 应用Sobel算子
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0;
                int gy = 0;
                // 计算梯度
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        gx += matrix[y + j][x + i] * sobelX[j + 1][i + 1];
                        gy += matrix[y + j][x + i] * sobelY[j + 1][i + 1];
                    }
                }
                // 计算边缘强度
                int edge = (int) Math.sqrt(gx * gx + gy * gy);
                edgeMatrix[y][x] = edge;
            }
        }
        return edgeMatrix;
    }

    // 将矩阵保存为图像文件
    private static void saveMatrixAsImage(int[][] matrix, String filename) {
        int height = matrix.length;
        int width = matrix[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 将矩阵中的灰度值或边缘值设置为图像像素的RGB值
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayValue = matrix[y][x];
                // 在灰度图像中，RGB三个通道的值相同
                int rgb = new Color(grayValue, grayValue, grayValue).getRGB();
                image.setRGB(x, y, rgb);
            }
        }

        // 将图像写入文件
        try {
            File output = new File(filename);
            ImageIO.write(image, "jpg", output);
            System.out.println("图像已保存为：" + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 线性归一化处理边缘矩阵
    private int[][] normalizeEdgeMatrix(int[][] edgeMatrix) {
        int maxEdgeValue = Integer.MIN_VALUE;
        int minEdgeValue = Integer.MAX_VALUE;

        // 找到边缘矩阵中的最大值和最小值
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (edgeMatrix[y][x] > maxEdgeValue) {
                    maxEdgeValue = edgeMatrix[y][x];
                }
                if (edgeMatrix[y][x] < minEdgeValue) {
                    minEdgeValue = edgeMatrix[y][x];
                }
            }
        }

        // 线性映射到0到255的范围内
        int[][] normalizedMatrix = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                normalizedMatrix[y][x] = (int) (255 * (edgeMatrix[y][x] - minEdgeValue) / (double) (maxEdgeValue - minEdgeValue));
            }
        }
        return normalizedMatrix;
    }
}
