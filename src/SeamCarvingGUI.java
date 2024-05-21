import edu.princeton.cs.algs4.Picture;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;

public class SeamCarvingGUI extends JFrame implements MouseListener, MouseMotionListener {
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
        setDefaultLookAndFeelDecorated(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //为动作进行计时
        timerExample = new TimerExample(this);
        addMouseListener(this);
        addMouseMotionListener( this );

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

        //创建一个标记的按钮
        JButton markButton = new JButton("Protected");
        markButton.addActionListener(e -> Protected());

        //创建一个清除按钮
        JButton clearButton = new JButton("Removal");
        clearButton.addActionListener(e -> Removal());

        //先创建button的集合的实例进行集成化处理
        JPanel buttonPanel = new JPanel(new GridLayout(5,1));
        buttonPanel.add(enlargeButton);
        buttonPanel.add(shrinkButton);
        buttonPanel.add(loadImageButton);
        buttonPanel.add(markButton);
        buttonPanel.add(clearButton);
        getContentPane().add(buttonPanel, BorderLayout.EAST);

        // 创建一个标签并设置文本
        JLabel titleLabel = new JLabel("Seam Carving GUI", SwingConstants.CENTER);
        // 将标签添加到窗口的底部
        getContentPane().add(titleLabel, BorderLayout.SOUTH);

        //给图片组件加一个边框
        // 创建一个边框
        Border border = BorderFactory.createLineBorder(Color.BLACK, 5); // 黑色边框，宽度为5
        // 将边框设置到JLabel
        imageLabel.setBorder(border);

        getContentPane().add(imageLabel, BorderLayout.CENTER);
    }
    public void Protected(){
        //先把图片弹窗的形式弹出
        PicturePainter painter = new PicturePainter(pic);
        JFrame contentAwarePanel = new JFrame("Content Aware");
        contentAwarePanel.add(imageLabel);
        contentAwarePanel.setSize(pic.width(), pic.height());
        contentAwarePanel.setLocationRelativeTo(null);
        contentAwarePanel.setVisible(true);
        //在上面标记区域
        contentAwarePanel.add(painter);
        //将鼠标拖动时标记坐标传回RemoveArea


    }
    public void Removal(){

    }

    private void processImage(boolean enlarge) {
        String inputWidth = JOptionPane.showInputDialog("Enter the target width:");
        String inputHeight = JOptionPane.showInputDialog("Enter the target height:");
        if (inputWidth != null && !inputWidth.isEmpty() && inputHeight != null && !inputHeight.isEmpty()) {
            try {
                int targetWidth = Integer.parseInt(inputWidth);
                int targetHeight = Integer.parseInt(inputHeight);
                timerExample.timeStart();


                //draggedScreen();
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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    Picture pic;

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        //若用户点击了打开或保存
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getAbsolutePath();
            imageIcon = new ImageIcon(imagePath);
            //设定图片标签的图标为imageIcon
            imageLabel.setIcon(imageIcon);
            //设定图片放置在imageLabel中央
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
        seamcarver=new SeamCarver(shrinkedImage);
        //用弹窗显示裁剪后的图片
        // 创建一个包含图片的 JLabel
        JLabel imageLabel = shrinkedImage.getJLabel();

        // 在弹窗中显示图片
        JOptionPane.showMessageDialog(null, imageLabel, "Processed Image", JOptionPane.PLAIN_MESSAGE);
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
//    private void addMouseListener() {
//        PicturePainter painter = new PicturePainter(pic);
//        // 通过鼠标左右键标记图像决定是保护还是易于移除的区域
//        imageLabel.addMouseMotionListener(new MouseAdapter() {
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                super.mouseDragged(e);
//                // 由于坐标计算是基于组件的左上角所以需要计算偏移量来进行坐标转换
//                int xOffset = (imageLabel.getWidth() - pic.width()) / 2;
//                int yOffset = (imageLabel.getHeight() - pic.height()) / 2;
//                int x = e.getX() - xOffset;
//                int y = e.getY() - yOffset;
//                if (x >= 0 && x < pic.width() && y >= 0 && y < pic.height()) {
//                    if (SwingUtilities.isLeftMouseButton(e)) {
//                        seamcarver.markRemovalArea(new int[x][y]);
//                        // 左键点击标记为易于移除的区域
//                        painter.paintAtRemove(x, y);
//                    } else if (SwingUtilities.isRightMouseButton(e)) {
//                        seamcarver.protectArea(new int[x][y]);
//                        // 右键点击标记为需要保护的区域
//                        painter.paintAtProtected(x, y);
//                    }
//                    imageLabel.repaint(); // 重绘界面以更新颜色
//                }
//            }
//        });
//    }
//
//    private void contentAware() {
//        JFrame contentAwarePanel = new JFrame("Content Aware");
//        PicturePainter painter = new PicturePainter(pic);
//
//        contentAwarePanel.add(painter);
//        contentAwarePanel.setSize(pic.width(), pic.height());
//        contentAwarePanel.setLocationRelativeTo(null);
//        contentAwarePanel.setVisible(true);
//        // 当窗口关闭时，将新的图片显示在imageLabel上
//        contentAwarePanel.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                // 将新的图片显示在imageLabel上
//                imageLabel.setIcon(new ImageIcon(painter.getNewBufferedImage()));
//                seamcarver = new SeamCarver(painter.getNewPicture());
//            }
//        });
//    }
//}
//
//    //写一个清楚标记的函数
//    private void contentAwareClear() {
//        imageLabel.setIcon(imageIcon);
//        seamcarver = new SeamCarver(pic);
//    }













}

