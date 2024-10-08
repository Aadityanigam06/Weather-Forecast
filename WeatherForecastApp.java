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
    private JLabel humidityLabel;
    private JLabel visibilityLabel;
    private JLabel rainLabel;
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

        // Center panel for temperature, wind speed, visibility, humidity, and rain
        JPanel centerPanel = new JPanel(new GridLayout(5, 1));
        centerPanel.setOpaque(false);

        temperatureLabel = new JLabel("Predicted Temperature: ");
        temperatureLabel.setFont(new Font("Arial", Font.BOLD, 20));
        temperatureLabel.setForeground(Color.WHITE);
        centerPanel.add(temperatureLabel);

        windSpeedLabel = new JLabel("Predicted Wind Speed: ");
        windSpeedLabel.setFont(new Font("Arial", Font.BOLD, 20));
        windSpeedLabel.setForeground(Color.WHITE);
        centerPanel.add(windSpeedLabel);

        visibilityLabel = new JLabel("Current Visibility: ");
        visibilityLabel.setFont(new Font("Arial", Font.BOLD, 20));
        visibilityLabel.setForeground(Color.WHITE);
        centerPanel.add(visibilityLabel);

        humidityLabel = new JLabel("Current Humidity: ");
        humidityLabel.setFont(new Font("Arial", Font.BOLD, 20));
        humidityLabel.setForeground(Color.WHITE);
        centerPanel.add(humidityLabel);

        rainLabel = new JLabel("Is it raining? ");
        rainLabel.setFont(new Font("Arial", Font.BOLD, 20));
        rainLabel.setForeground(Color.WHITE);
        centerPanel.add(rainLabel);

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
            // Run the Python script and capture any errors
            ProcessBuilder pb = new ProcessBuilder("C:\\Users\\aadit\\AppData\\Local\\Programs\\Python\\Python312\\python.exe", "Prediction.py");
            Process process = pb.start();

            // Read error stream (to check if the script is failing)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                System.err.println("Error from Python script: " + errorLine);
            }

            // Read the output from the Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String predictedTemperatureLine = reader.readLine();
            String predictedWindSpeedLine = reader.readLine();
            String visibilityLine = reader.readLine();
            String humidityLine = reader.readLine();
            String rainLine = reader.readLine(); // Read rain status

            // Parse and update labels
            double predictedTemperature = Double.parseDouble(predictedTemperatureLine.replaceAll("[^0-9.]", ""));
            double predictedWindSpeed = Double.parseDouble(predictedWindSpeedLine.replaceAll("[^0-9.]", ""));
            double visibility = Double.parseDouble(visibilityLine.replaceAll("[^0-9.]", ""));
            double humidity = Double.parseDouble(humidityLine.replaceAll("[^0-9.]", ""));
            String rainStatus = rainLine.trim();

            temperatureLabel.setText(String.format("Predicted Temperature: %.2f°C", predictedTemperature));
            windSpeedLabel.setText(String.format("Predicted Wind Speed: %.2f km/h", predictedWindSpeed));
            visibilityLabel.setText(String.format("Current Visibility: %.2f meters", visibility));
            humidityLabel.setText(String.format("Current Humidity: %.2f%%", humidity));
            rainLabel.setText("Is it raining? " + rainStatus);

            // Update date and time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy - h:mm a");
            dateTimeLabel.setText("Date and Time: " + now.format(formatter));

            reader.close();
            errorReader.close();
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
        SwingUtilities.invokeLater(() -> {
            WeatherForecastApp app = new WeatherForecastApp();
            app.setVisible(true);
        });
    }
}
