import edu.princeton.cs.algs4.Picture;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class SeamCarvingGUI extends JFrame {
    private ImageIcon imageIcon;
    //创建一个图标
    private JLabel imageLabel = new JLabel();
    private SeamCarver seamcarver;
    //计时器实例
    private TimerExample timerExample;
    //设定默认文件夹路径和图标大小
    private static final String ICONS_FOLDER = "icons";
    private static final int ICON_SIZE = 20;

    public SeamCarvingGUI() {

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //为动作进行计时
        timerExample = new TimerExample(this);

        //创建放大的按钮
        JButton enlargeButton = new JButton("Enlarge");
        enlargeButton.setIcon(icon("add.png"));
        enlargeButton.addActionListener(e -> processImage(true));

        //创建一个加载图像的按钮
        JButton loadImageButton = new JButton("Load Image");
        loadImageButton.addActionListener(e -> loadImage());

        //创建一个缩小的按钮
        JButton shrinkButton = new JButton("Shrink");
        shrinkButton.setIcon(icon("remove.png"));
        shrinkButton.addActionListener(e -> processImage(false));

        //先创建button的集合的实例进行集成化处理
        JPanel buttonPanel = new JPanel(new GridLayout(3,1));
        buttonPanel.add(enlargeButton);
        buttonPanel.add(shrinkButton);
        buttonPanel.add(loadImageButton);
        getContentPane().add(buttonPanel, BorderLayout.EAST);

        // 创建一个标签并设置文本
        JLabel titleLabel = new JLabel("Seam Carving GUI", SwingConstants.CENTER);
        // 将标签添加到窗口的底部
        getContentPane().add(titleLabel, BorderLayout.SOUTH);

        getContentPane().add(imageLabel, BorderLayout.CENTER);

    }

    private void processImage(boolean enlarge) {
        String inputWidth = JOptionPane.showInputDialog("Enter the target width:");
        String inputHeight = JOptionPane.showInputDialog("Enter the target height:");
        if (inputWidth != null && !inputWidth.isEmpty() && inputHeight != null && !inputHeight.isEmpty()) {
            try {
                int targetWidth = Integer.parseInt(inputWidth);
                int targetHeight = Integer.parseInt(inputHeight);
                timerExample.timeStart();
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    protected Void doInBackground() throws Exception {
                        if (enlarge) {
                            Picture enlargedImage = seamcarver.enlargeImage(targetWidth, targetHeight);
                            enlargedImage.show();
                        } else {
                            Picture shrinkedImage = seamcarver.shrinkImage(targetWidth, targetHeight);
                            shrinkedImage.show();
                        }
                        return null;
                    }

                    protected void done() {
                        timerExample.timeStop();
                        try {
                            get();  // Call get to rethrow exceptions from doInBackground
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "Error processing image: " + e.getMessage());
                        }
                    }
                };
                worker.execute();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid number.");
            }
        }
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        //若用户点击了打开或保存
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getAbsolutePath();
            imageIcon = new ImageIcon(imagePath);
            //设定图片标签的图标为imageIcon，并且大小为原来的1/2
            imageLabel.setIcon(imageIcon);
            Picture pic = new Picture(imagePath);
            seamcarver = new SeamCarver(pic);
        }
    }
    /*
    创建一个缩放图像的函数，使图像显示时不会超出窗口并且为合适的大小
     */
    private ImageIcon icon(String filename, int... dims) {
        URL url = getClass().getResource(ICONS_FOLDER + "/" + filename);
        ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
        int width, height;
        if (dims.length == 0) {
            width = height = ICON_SIZE;
        } else {
            width = icon.getIconWidth() / dims[0];
            height = icon.getIconHeight() / dims[0];
        }
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_FAST));
    }


}
