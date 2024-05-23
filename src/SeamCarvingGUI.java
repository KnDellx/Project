
import edu.princeton.cs.algs4.Picture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.Arrays;

public class SeamCarvingGUI extends JFrame {
    private ImageIcon imageIcon;
    //创建一个图标
    private JLabel imageLabel = new JLabel();
    private src.SeamCarver SEAMCARVER ;

    //设定默认文件夹路径和图标大小
    private static final String ICONS_FOLDER = "icons";
    private static final int ICON_SIZE = 20;

    //创建要保护/移除区域的矩阵
    private Boolean[][] protectArea;
    private Boolean[][] removeArea;
    private Picture pic;
    private Picture originalPic;

    public SeamCarvingGUI() {

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //创建处理图片的按钮
        JButton processButton = new JButton("process");
        processButton.setIcon(icon("add.png"));
        processButton.addActionListener(e -> processImage());

        //创建一个加载图像的按钮
        JButton loadImageButton = new JButton("Load Image");
        loadImageButton.addActionListener(e -> loadImage());

        //创建一个保护区域的按钮
        JButton protectButton = new JButton("Protect Area");
        protectButton.addActionListener(e -> protectArea());
        //创建一个易于清除区域的按钮
        JButton removeButton = new JButton("Remove Area");
        removeButton.addActionListener(e -> removeArea());

        //先创建button的集合的实例进行集成化处理
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));

        buttonPanel.add(protectButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(processButton);
        buttonPanel.add(loadImageButton);
        getContentPane().add(buttonPanel, BorderLayout.EAST);

        // 创建一个标签并设置文本
        JLabel titleLabel = new JLabel("Seam Carving GUI", SwingConstants.CENTER);
        // 将标签添加到窗口的底部
        getContentPane().add(titleLabel, BorderLayout.SOUTH);
        getContentPane().add(imageLabel, BorderLayout.CENTER);

    }
    public void removeArea() {
        PicturePainter painter = new PicturePainter(pic, 10, Color.RED);
        SEAMCARVER.initMarkedArea();

        // 初始化 removeArea 数组
        removeArea = new Boolean[pic.width()][pic.height()];
        for (Boolean[] row : removeArea) {
            Arrays.fill(row, Boolean.FALSE);
        }

        JFrame paintPanel = new JFrame("Paint Area You Want to Remove");
        paintPanel.setSize(pic.width(), pic.height());
        paintPanel.setLocationRelativeTo(null);
        paintPanel.setVisible(true);

        // 将 painter 添加到 paintPanel 中
        paintPanel.add(painter);

        // 将鼠标事件处理器添加到 painter 对象上
        painter.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                removeArea[x][y] = true;
                painter.paintAt(x, y);


            }
        });
        painter.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                removeArea[x][y] = true;
                painter.paintAt(x, y);
            }
        });


        paintPanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    int response = JOptionPane.showConfirmDialog(paintPanel, "Are you sure you want to save removal area and leave?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (response == JOptionPane.YES_OPTION) {
                        //把removeArea传回seamCarver中的removeArea
                        SEAMCARVER.markRemovalArea(removeArea);
                        //更新SEAMCARVER类中能量计算的方法

                        paintPanel.dispose();
                    }
                });
            }
        });

    }

    private void protectArea() {
        //和removeArea一样，只是颜色不同
        PicturePainter painter = new PicturePainter(pic, 10, Color.GREEN);
        SEAMCARVER.initMarkedArea();

        // 初始化 protectArea 数组
        protectArea = new Boolean[pic.width()][pic.height()];
        for (Boolean[] row : protectArea) {
            Arrays.fill(row, Boolean.FALSE);
        }

        JFrame paintPanel = new JFrame("Paint Area You Want to Protect");
        paintPanel.setSize(pic.width(), pic.height());
        paintPanel.setLocationRelativeTo(null);
        paintPanel.setVisible(true);

        // 将 painter 添加到 paintPanel 中
        paintPanel.add(painter);

        // 将鼠标事件处理器添加到 painter 对象上
        painter.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                protectArea[x][y] = true;
                painter.paintAt(x, y);
            }
        });
        painter.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                protectArea[x][y] = true;
                painter.paintAt(x, y);
            }
        });




        paintPanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    int response = JOptionPane.showConfirmDialog(paintPanel, "Are you sure you want to save protection area and leave?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (response == JOptionPane.YES_OPTION) {
                        //把protectArea传回seamCarver中的protectArea
                        SEAMCARVER.protectArea(protectArea);
                        paintPanel.dispose();
                    }
                });
            }
        });
    }


    private void processImage() {
        //开始前检查是否有图片，没有就弹窗报错
        if (pic == null) {
            JOptionPane.showMessageDialog(null, "No image loaded!");
            return;
        }
        try {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                protected Void doInBackground() throws Exception {
                    JFrame jFrame = new JFrame();
                    JLabel picture = new JLabel();
                    picture.setIcon(pic.getJLabel().getIcon());
                    jFrame.add(picture);
                    jFrame.setSize(pic.width(), pic.height());
                    jFrame.setVisible(true);
                    jFrame.setDefaultCloseOperation(2);
                    jFrame.addComponentListener(new ComponentAdapter() {//让窗口响应大小改变事件
                        @Override
                        public void componentResized(ComponentEvent e) {

                            int fraWidth = jFrame.getWidth();//获取面板宽度
                            int fraHeight = jFrame.getHeight();//获取面板高度
                            Picture image;
                            if (fraWidth < pic.width() || fraHeight < pic.height()) {
                                image = SEAMCARVER.shrinkImage(fraWidth, fraHeight);
                            } else {
                                image = SEAMCARVER.enlargeImage(fraWidth, fraHeight);
                            }

                            picture.setIcon(image.getJLabel().getIcon());

                        }
                    });
                    jFrame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            SwingUtilities.invokeLater(() -> {
                                //加一个关闭窗口监听器，若关闭窗口则弹窗提示是否保存图片

                                //若选择保存，则当前处理后的图片显示在主窗口中
                                //若不选择保存，则主窗口显示原图片originalPic

                                int response = JOptionPane.showConfirmDialog(null, "Do you want to save the processed image?", "Save Image", JOptionPane.YES_NO_OPTION);
                                if (response == JOptionPane.YES_OPTION) {
                                    pic.save("processed.jpg");
                                } else {
                                    imageLabel.setIcon(originalPic.getJLabel().getIcon());
                                    pic = originalPic;
                                }
                            });
                        }
                    });
                    return null;
                }

                protected void done() {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loadImage() {
        // Load an image from a file
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String imagePath = file.getAbsolutePath();
            // Update the image label
            imageIcon = new ImageIcon(imagePath);
            imageLabel.setIcon(imageIcon);
            // Load the image into the SeamCarver
            pic = new Picture(imagePath);
            originalPic = new Picture(imagePath);
            SEAMCARVER = new src.SeamCarver(pic);
        }

    }
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
