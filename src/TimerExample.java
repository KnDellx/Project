import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerExample extends JLabel {
    private JLabel timerLabel;
    private long startTime;
    private SeamCarvingGUI jFrame;
    private Timer timer;

    public TimerExample(SeamCarvingGUI seamCarvingGUI) {
        this.jFrame = seamCarvingGUI;
        timerLabel = new JLabel("00:00:00", SwingConstants.RIGHT);
        timerLabel.setFont(new Font("Serif", Font.BOLD, 32));

        // Initialize the timer with a 1000ms delay (1 second)
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsedTime / 1000) % 60;
                int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
                int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);
                timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            }
        });
    }

    public void timeStart() {
        startTime = System.currentTimeMillis(); // Set the start time before starting the timer
        jFrame.add(timerLabel);
        timerLabel.setVisible(true);
        jFrame.revalidate(); // Revalidate the frame to refresh the layout
        jFrame.repaint(); // Repaint the frame to ensure the label is displayed
        timer.restart(); // Restart the timer to ensure it starts fresh
        timer.start();
    }

    public void timeStop() {
        timer.stop();
    }
}