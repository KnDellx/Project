import edu.princeton.cs.algs4.Picture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PicturePainter extends JPanel {
    private Picture picture;
    private BufferedImage bufferedImage;
    private int brushSize = 20; // 画笔大小
    private Color brushColor = Color.WHITE; // 画笔颜色

    public PicturePainter(Picture picture) {
        // Set the picture and convert it to a BufferedImage
        this.picture = picture;
        this.bufferedImage = pictureToBufferedImage(picture);
        // Set the preferred size of the panel to the size of the picture
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                paintAt(e.getX(), e.getY());
            }
        });
        // Add a mouse motion listener to draw when the mouse is dragged
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                paintAt(e.getX(), e.getY());
            }
        });
    }

    private BufferedImage pictureToBufferedImage(Picture picture) {
        // Create a new BufferedImage with the same dimensions as the picture
        BufferedImage image = new BufferedImage(picture.width(), picture.height(), BufferedImage.TYPE_INT_ARGB);
        // Set the RGB values of the image to the RGB values of the picture
        for (int y = 0; y < picture.height(); y++) {
            for (int x = 0; x < picture.width(); x++) {
                image.setRGB(x, y, picture.get(x, y).getRGB());
            }
        }
        return image;
    }
    // Convert the BufferedImage to a Picture
    public Picture getNewPicture() {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Picture picture = new Picture(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                picture.set(x, y, new Color(bufferedImage.getRGB(x, y)));
            }
        }

        return picture;
    }

    public BufferedImage getNewBufferedImage() {
        return bufferedImage;
    }

    public void paintAt(int x, int y) {
        // Create a Graphics2D object from the BufferedImage
        Graphics2D g = bufferedImage.createGraphics();
        // Set the color and fill an oval at the mouse position
        g.setColor(Color.GREEN);
        //能否把画笔设置为半透明的
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        // 画笔大小
        g.fillOval(x - brushSize / 2, y - brushSize / 2, brushSize, brushSize);
        // Dispose of the Graphics2D object
        g.dispose();
        repaint();
    }

    // Override the paintComponent method to draw the BufferedImage
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bufferedImage, 0, 0, null);
    }
}