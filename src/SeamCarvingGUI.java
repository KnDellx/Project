//import edu.princeton.cs.algs4.Picture;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//
//public class SeamCarvingGUI extends JFrame{
//    private ImageIcon imageIcon;
//    private JLabel imageLabel = new JLabel();
//    private SeamCarver seamcarver; // 将SeamCarver声明为成员变量
//
//    private TimerExample timerExample;
//
//
//    public SeamCarvingGUI() {
//        //设置初始面板的名称和大小
//        setTitle("Seam Carving GUI");
//        setSize(600, 400);
//        //设置初始界面的位置和关闭操作
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        setLayout(new BorderLayout());
//        timerExample =new TimerExample(this);
//        //创建放大的按钮
//        // Create buttons
//        JButton enlargeButton = new JButton("Enlarge");
//        enlargeButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                //为输入创建一个对话框
//                String inputWidth = JOptionPane.showInputDialog("Enter the target width: ");
//                String inputHeight = JOptionPane.showInputDialog("Enter the target height: ");
//                //检查输入是否为空
//                //isBlank?
//                if (inputWidth != null && !inputWidth.isEmpty() && inputHeight != null && !inputHeight.isEmpty()) {
//                    try {
//                        int targetWidth = Integer.parseInt(inputWidth);
//                        int targetHeight = Integer.parseInt(inputHeight);
//                       timerExample.timeStart();
//                        seamcarver.enlargeImage(targetWidth,targetHeight);
//                        timerExample.timeStop();
//                    }catch (NumberFormatException ex) {
//                        JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid number.");
//                    }
//                }
//
//            }
//        });
//
//        //创建导入图像的按钮
//        // Create buttons
//        JButton loadImageButton = new JButton("Load Image");
//        loadImageButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                loadImage();
//            }
//        });
//
//
//        //创建缩小的按钮
//        JButton shrinkButton = new JButton("Shrink");
//        shrinkButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                //为输入创建一个对话框
//                String inputWidth = JOptionPane.showInputDialog("Enter the target width: ");
//                String inputHeight = JOptionPane.showInputDialog("Enter the target height: ");
//                //检查输入是否为空
//                //isBlank?
//                if (inputWidth != null && !inputWidth.isEmpty() && inputHeight != null && !inputHeight.isEmpty()) {
//                    try {
//                        int targetWidth = Integer.parseInt(inputWidth);
//                        int targetHeight = Integer.parseInt(inputHeight);
//                        timerExample.timeStart();
//                        Picture shrinkedImage = seamcarver.shrinkImage(targetWidth,targetHeight);
//                        timerExample.timeStop();
//                        //弹出shrink后的图像
//                        shrinkedImage.show();
//                    }catch (NumberFormatException ex) {
//                        JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid number.");
//                    }
//                }
//
//            }
//        });
//
//
//        // Add buttons to a panel
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.add(enlargeButton);
//        buttonPanel.add(shrinkButton);
//        buttonPanel.add(loadImageButton);
//
//
//        // 向frame中添加元素，并且设置位置
//        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
//        getContentPane().add(imageLabel,BorderLayout.CENTER);
//
//    }
//
//    private void loadImage() {
//        // 创建一个文件选择器对象
//        JFileChooser fileChooser = new JFileChooser();
//        // 显示一个打开文件的对话框，并捕获用户的响应
//        int returnValue = fileChooser.showOpenDialog(null);
//        // 如果用户选择了一个文件并点击了“打开”按钮
//        if (returnValue == JFileChooser.APPROVE_OPTION) {
//            // 获取用户选择的文件
//            File selectedFile = fileChooser.getSelectedFile();
//            // 获取该文件的绝对路径
//            String imagePath = selectedFile.getAbsolutePath();
//            // 根据图像路径创建一个图像图标对象
//            imageIcon = new ImageIcon(imagePath);
//            // 将图像图标设置到标签上，以便显示图像
//            imageLabel.setIcon(imageIcon);
//            //导入SeamCarving中的方法
//            Picture pic = new Picture(imagePath);
//            seamcarver = new SeamCarver(pic); // 在这里实例化SeamCarver
//        }
//    }
//
//
//
//}


import edu.princeton.cs.algs4.Picture;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SeamCarvingGUI extends JFrame {
    private ImageIcon imageIcon;
    private JLabel imageLabel = new JLabel();
    private SeamCarver seamcarver;
    private TimerExample timerExample;

    public SeamCarvingGUI() {
        setTitle("Seam Carving GUI");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        timerExample = new TimerExample(this);

        JButton enlargeButton = new JButton("Enlarge");
        enlargeButton.addActionListener(e -> processImage(true));

        JButton loadImageButton = new JButton("Load Image");
        loadImageButton.addActionListener(e -> loadImage());

        JButton shrinkButton = new JButton("Shrink");
        shrinkButton.addActionListener(e -> processImage(false));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(enlargeButton);
        buttonPanel.add(shrinkButton);
        buttonPanel.add(loadImageButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(imageLabel, BorderLayout.CENTER);
    }

    private void processImage(boolean enlarge) {
        String action = enlarge ? "enlarge" : "shrink";
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
                            seamcarver.enlargeImage(targetWidth, targetHeight);
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
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getAbsolutePath();
            imageIcon = new ImageIcon(imagePath);
            imageLabel.setIcon(imageIcon);
            Picture pic = new Picture(imagePath);
            seamcarver = new SeamCarver(pic);
        }
    }


}
