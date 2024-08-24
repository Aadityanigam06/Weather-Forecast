import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherForecastApp extends JFrame {
    private JLabel dateTimeLabel;
    private JLabel temperatureLabel;
    private JLabel windSpeedLabel;
    private JButton refreshButton;

    public WeatherForecastApp() {
        setTitle("Weather Forecast App");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Background image panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // North panel for date and time
        JPanel northPanel = new JPanel();
        northPanel.setOpaque(false);
        dateTimeLabel = new JLabel("Date and Time: ");
        dateTimeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dateTimeLabel.setForeground(Color.WHITE);
        northPanel.add(dateTimeLabel);
        backgroundPanel.add(northPanel, BorderLayout.NORTH);

        // Center panel for temperature and wind speed
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setOpaque(false);

        temperatureLabel = new JLabel("Predicted Temperature: ");
        temperatureLabel.setFont(new Font("Arial", Font.BOLD, 20));
        temperatureLabel.setForeground(Color.WHITE);
        centerPanel.add(temperatureLabel);

        windSpeedLabel = new JLabel("Wind Speed: ");
        windSpeedLabel.setFont(new Font("Arial", Font.BOLD, 20));
        windSpeedLabel.setForeground(Color.WHITE);
        centerPanel.add(windSpeedLabel);

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // South panel for refresh button
        JPanel southPanel = new JPanel();
        southPanel.setOpaque(false);
        refreshButton = new JButton("Refresh");
        southPanel.add(refreshButton);
        backgroundPanel.add(southPanel, BorderLayout.SOUTH);

        // Load and display the data
        loadData();

        // Refresh button action
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
    }

    private void loadData() {
        try {
            // Run the Python script and read the output
            ProcessBuilder pb = new ProcessBuilder("python", "Prediction.py");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    
            // Read the output
            String predictedTemperatureLine = reader.readLine();
            String windSpeedLine = reader.readLine();
    
            // Close the reader
            reader.close();
    
            // Extract numeric values from the strings
            double predictedTemperature = Double.parseDouble(predictedTemperatureLine.replaceAll("[^0-9.]", ""));
            double windSpeed = Double.parseDouble(windSpeedLine.replaceAll("[^0-9.]", ""));
    
            // Update the labels
            temperatureLabel.setText(String.format("Predicted Temperature: %.2f°C", predictedTemperature));
            windSpeedLabel.setText(String.format("Wind Speed: %.2f km/h", windSpeed));
    
            // Update date and time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy - h:mm a");
            dateTimeLabel.setText("Date and Time: " + now.format(formatter));
    
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // BackgroundPanel class to handle background image
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            // Load the background image
            String imagePath;
            int hour = java.time.LocalTime.now().getHour();
            if (hour >= 6 && hour < 18) {
                imagePath = "sun_background.jpg"; // Image for day time
            } else {
                imagePath = "moon_background.jpg"; // Image for night time
            }
            backgroundImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WeatherForecastApp().setVisible(true);
            }
        });
    }
}
