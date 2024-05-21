import edu.princeton.cs.algs4.Picture;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;

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
        JButton removeButton = new JButton("Remove area");
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

    private void removeArea(){
        PicturePainter painter = new PicturePainter(pic, 10, Color.RED);
        try {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                protected Void doInBackground() throws Exception {
                    // 初始化protectedArea
                    SeamCarver seamCarver = new SeamCarver(pic);
                    seamCarver.initMarkedArea();
                    // 创建一个新的窗口
                    JFrame paintPanel = new JFrame("Paint Area You Want to Remove");
                    JLabel picture = new JLabel();
                    picture.setIcon(pic.getJLabel().getIcon());
                    paintPanel.add(painter);
                    paintPanel.setSize(pic.width(), pic.height());
                    paintPanel.setLocationRelativeTo(null);
                    paintPanel.setVisible(true);

                    // 在跳出的图窗先进行标记，然后再进行处理，并将标记的区域传回seamCarver中的protectedArea
                    paintPanel.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            int response = JOptionPane.showConfirmDialog(paintPanel, "Are you sure you want to save removal area and leave?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (response == JOptionPane.YES_OPTION) {
                                paintPanel.dispose();
                                protectArea = painter.getPaintArea();
                            }
                        }
                    });

                    return null;
                }

                protected void done() {
                    try {
                        get();  // Call get to rethrow exceptions from doInBackground
                        // 更新原始的imageLabel
                        imageLabel.repaint();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Error processing image: " + e.getMessage());
                    }
                }
            };
            worker.execute();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error initializing protection: " + e.getMessage());
        }

    }

    private void protectArea() {
        PicturePainter painter = new PicturePainter(pic, 10, Color.GREEN);
        try {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                protected Void doInBackground() throws Exception {
                    // 初始化protectedArea
                    SeamCarver seamCarver = new SeamCarver(pic);
                    seamCarver.initMarkedArea();
                    // 创建一个新的窗口
                    JFrame paintPanel = new JFrame("Paint Area You Want to Protect");
                    JLabel picture = new JLabel();
                    picture.setIcon(pic.getJLabel().getIcon());
                    paintPanel.add(painter);
                    paintPanel.setSize(pic.width(), pic.height());
                    paintPanel.setLocationRelativeTo(null);
                    paintPanel.setVisible(true);

                    // 在跳出的图窗先进行标记，然后再进行处理，并将标记的区域传回seamCarver中的protectedArea
                    paintPanel.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            int response = JOptionPane.showConfirmDialog(paintPanel, "Are you sure you want to save protected area and leave?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (response == JOptionPane.YES_OPTION) {
                                paintPanel.dispose();
                                protectArea = painter.getPaintArea();
                            }
                        }
                    });

                    return null;
                }

                protected void done() {
                    try {
                        get();  // Call get to rethrow exceptions from doInBackground
                        // 更新原始的imageLabel
                        imageLabel.repaint();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Error processing image: " + e.getMessage());
                    }
                }
            };
            worker.execute();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error initializing protection: " + e.getMessage());
        }
    }



    private void processImage() {
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
                                image = seamcarver.shrinkImage(fraWidth, fraHeight);
                            }
                            else {
                                image = seamcarver.enlargeImage(fraWidth, fraHeight);
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