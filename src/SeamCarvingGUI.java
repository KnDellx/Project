
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
    private SeamCarver SEAMCARVER ;

    //设定默认文件夹路径和图标大小
    private static final String ICONS_FOLDER = "icons";
    private static final int ICON_SIZE = 30;

    //创建要保护/移除区域的矩阵
    private Boolean[][] protectArea;
    private Boolean[][] removeArea;
    private Picture pic;
    private Picture originalPic;

    public SeamCarvingGUI() {

        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle(" Carve whatever you want！");

        //创建处理图片的按钮
        JButton processButton = new JButton("process");
        processButton.addActionListener(e -> processImage());
        //美化按钮
        beautifyButton(processButton);


        //创建一个加载图像的按钮
        JButton loadImageButton = new JButton("Load Image");
        loadImageButton.addActionListener(e -> loadImage());
        beautifyButton(loadImageButton);

        //创建一个保护区域的按钮
        JButton protectButton = new JButton("Protect Area");
        protectButton.setIcon(icon("add.png"));
        protectButton.addActionListener(e -> protectArea());
        beautifyButton(protectButton);
        //创建一个易于清除区域的按钮
        JButton removeButton = new JButton("Remove Area");
        removeButton.setIcon(icon("remove.png"));
        removeButton.addActionListener(e -> removeArea());
        beautifyButton(removeButton);

        //先创建button的集合的实例进行集成化处理
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
        //加长buttonPanel的宽度
        buttonPanel.setPreferredSize(new Dimension(150, 400));

        buttonPanel.add(protectButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(processButton);
        buttonPanel.add(loadImageButton);
        getContentPane().add(buttonPanel, BorderLayout.EAST);
        //改变panel背景风格
        buttonPanel.setBackground(Color.DARK_GRAY);

        // 创建一个标签并设置文本
        //能否把以下字体设置为好看的字体并且放大
        JLabel titleLabel = new JLabel("@ Seam Carving GUI", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Calibri Light", Font.ITALIC, 20));
        // 将标签添加到窗口的底部
        getContentPane().add(titleLabel, BorderLayout.SOUTH);
        getContentPane().add(imageLabel, BorderLayout.CENTER);

        //在最上方添加一列菜单栏，有清空，保存，退出选项
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        //清除功能
        JMenuItem clearMenuItem = new JMenuItem("Clear");
        clearMenuItem.addActionListener(e -> {
            imageLabel.setIcon(null);
            pic = null;
        });
        //退出功能
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(clearMenuItem);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        //能否通过图片大小自适应更改窗口大小
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (pic != null) {
                    imageLabel.setIcon(pic.getJLabel().getIcon());
                }
            }
        });

    }

    private void beautifyButton(JButton button) {
        button.setForeground(Color.WHITE); // 设置按钮文字颜色
        button.setBackground(new Color(59, 89, 182)); // 设置按钮背景颜色
        button.setFocusPainted(false); // 去除按钮焦点边框
        button.setBorder(BorderFactory.createRaisedBevelBorder()); // 设置按钮边框
        button.setPreferredSize(new Dimension(120, 40)); // 设置按钮大小
        button.setFont(new Font("Arial", Font.BOLD, 16)); // 设置按钮字体和大小
    }
    public void removeArea() {
        //写一个监听是否有图像，若没有就弹窗报错
        if (pic == null) {
            JOptionPane.showMessageDialog(null, "No image loaded!");
            return;
        }
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
                        SEAMCARVER.reCalculateEnergy();

                        paintPanel.dispose();
                    }
                });
            }
        });

    }

    private void protectArea() {
        //写一个监听是否有图像，若没有就弹窗报错
        if (pic == null) {
            JOptionPane.showMessageDialog(null, "No image loaded!");
            return;
        }
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
                        SEAMCARVER.reCalculateEnergy();
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
                            //通过辅助图片对象储存当前图片，以便在窗口关闭时恢复原图片

                            int fraWidth = jFrame.getWidth();//获取面板宽度
                            int fraHeight = jFrame.getHeight();//获取面板高度
                            Picture image;
                            if (fraWidth < pic.width() || fraHeight < pic.height()) {
                                image = SEAMCARVER.shrinkImage(fraWidth, fraHeight);
                            } else {
                                image = SEAMCARVER.enlargeImage(fraWidth, fraHeight);
                            }

                            picture.setIcon(image.getJLabel().getIcon());
                            pic = image;

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
                                    //弹窗可自由选择保存路径
                                    JFileChooser fileChooser = new JFileChooser();
                                    int result = fileChooser.showSaveDialog(null);
                                    if (result == JFileChooser.APPROVE_OPTION) {
                                        File file = fileChooser.getSelectedFile();
                                        pic.save(file.getAbsolutePath());
                                        imageLabel.setIcon(pic.getJLabel().getIcon());
                                    }
                                } else {
                                    imageLabel.setIcon(originalPic.getJLabel().getIcon());
                                    pic = new Picture(originalPic);
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
        //如果已经选择文件就弹窗报错
        if (pic != null) {
            JOptionPane.showMessageDialog(null, "Image already loaded!");
            return;
        }
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
            SEAMCARVER = new SeamCarver(pic);
            //创建原图像的深拷贝
            originalPic = new Picture(pic);
        }
        //若没有选择文件则弹窗报错
        else {
            JOptionPane.showMessageDialog(null, "No file selected!");
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
