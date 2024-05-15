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
