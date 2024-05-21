import edu.princeton.cs.algs4.Picture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Arrays;

public class SeamCarvingGUI extends JFrame implements MouseListener, MouseMotionListener {
    private ImageIcon imageIcon;
    //创建一个图标
    private JLabel imageLabel = new JLabel();
    private SeamCarver seamcarver;

    //设定默认文件夹路径和图标大小
    private static final String ICONS_FOLDER = "icons";
    private static final int ICON_SIZE = 20;

//    创建要保护/移除区域的矩阵
    private Boolean[][] protectArea;
    private Boolean[][] removeArea;
    private BufferedImage bufferedImage;
    private int brushSize; // 画笔大小
    private Color brushColor; // 画笔颜色

    public SeamCarvingGUI() {

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        addMouseListener(this);
        addMouseMotionListener( this );

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
        JPanel buttonPanel = new JPanel(new GridLayout(4,1));
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
    private void removeArea() {
        PicturePainter painter = new PicturePainter(pic, 10, Color.RED);
        SeamCarver seamCarver = new SeamCarver(pic);
        seamCarver.initMarkedArea();

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
        //把removeArea传回seamCarver中的removeArea
        seamCarver.markRemovalArea(removeArea);
        Picture image = seamcarver.shrinkImage(300, 200);


        paintPanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    int response = JOptionPane.showConfirmDialog(paintPanel, "Are you sure you want to save removal area and leave?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (response == JOptionPane.YES_OPTION) {
                        paintPanel.dispose();
                       //用弹窗显示裁剪后的图片
                        JOptionPane.showMessageDialog(null, "Cropped image saved successfully.");
                        imageLabel.setIcon(image.getJLabel().getIcon());
                    }
                });
            }
        });

    }

    private void protectArea() {
        //和removeArea一样，只是颜色不同
        PicturePainter painter = new PicturePainter(pic, 10, Color.GREEN);
        SeamCarver seamCarver = new SeamCarver(pic);
        seamCarver.initMarkedArea();

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
        //把protectArea传回seamCarver中的protectArea
        seamCarver.protectArea(protectArea);
        Picture image = seamcarver.shrinkImage(300, 200);
        imageLabel.setIcon(image.getJLabel().getIcon());


        paintPanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    int response = JOptionPane.showConfirmDialog(paintPanel, "Are you sure you want to save protection area and leave?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (response == JOptionPane.YES_OPTION) {
                        paintPanel.dispose();




                    }
                });
            }
        });
    }



    private void processImage() {
        SeamCarver SEAMCARVER = new SeamCarver(pic);
        try {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                protected Void doInBackground() throws Exception {
                    JFrame jFrame=new JFrame();
                    JLabel picture=new JLabel();
                    picture.setIcon(pic.getJLabel().getIcon());
                    jFrame.add(picture);
                    jFrame.setSize(pic.width(),pic.height());
                    jFrame.setVisible(true);
                    jFrame.setDefaultCloseOperation(2);
                    jFrame.addComponentListener(new ComponentAdapter() {//让窗口响应大小改变事件
                        @Override
                        public void componentResized(ComponentEvent e) {
                            Picture pic2 = pic;
                            int fraWidth = jFrame.getWidth();//获取面板宽度
                            int fraHeight = jFrame.getHeight();//获取面板高度
                            Picture image;
                            if (fraWidth<pic.width()||fraHeight>pic.width()){
                                image = SEAMCARVER.shrinkImage(fraWidth, fraHeight);
                            }
                            else {
                                image = SEAMCARVER.enlargeImage(fraWidth, fraHeight);
                            }

                            picture.setIcon(image.getJLabel().getIcon());
                            pic = pic2;
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

    Picture pic;

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getAbsolutePath();
            imageIcon = new ImageIcon(imagePath);
            imageLabel.setIcon(imageIcon);
            pic = new Picture(imagePath);
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

    int drag_status=0,c1,c2,c3,c4;

    public void draggedScreen()throws Exception
    {
        seamcarver=new SeamCarver(pic);
        int w = c1 - c3;
        int h = c2 - c4;
        System.out.println(c1+" "+c2+" " +c3+" "+c4);
        w = w * -1;
        h = h * -1;
        Picture shrinkedImage = seamcarver.shrinkImage(w, h);
        System.out.println("Cropped image saved successfully.");
        seamcarver=new SeamCarver(shrinkedImage);
        imageLabel.setIcon(shrinkedImage.getJLabel().getIcon());
    }


    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        repaint();
        c1=arg0.getX();
        c2=arg0.getY();
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        repaint();
        if(drag_status==1)
        {
            c3=arg0.getX();
            c4=arg0.getY();
            try
            {
                draggedScreen();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        repaint();
        drag_status=1;
        c3=arg0.getX();
        c4=arg0.getY();
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {

    }

    public void paint(Graphics g)
    {
        super.paint(g);
        int w = c1 - c3;
        int h = c2 - c4;
        w = w* -1;
        h = h * -1;
        if(w<0)
            w = w * -1;
        g.drawRect(c1, c2, w, h);
    }
}