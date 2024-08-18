import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherForecastApp {

    public static void main(String[] args) {
        // Create the main frame with a fun background color
        JFrame frame = new JFrame("Weather Forecast");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.getContentPane().setBackground(new Color(135, 206, 250)); // Light sky blue color

        // Create a panel for displaying the weather data with padding and border
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false); // Make the panel transparent

        // Create labels for the data with custom font and color
        JLabel dateTimeLabel = createCustomLabel("Date & Time:");
        JLabel temperatureLabel = createCustomLabel("Predicted Temperature:");
        JLabel windLabel = createCustomLabel("Predicted Wind:");

        // Create labels for displaying the actual values with custom font and color
        JLabel dateTimeValue = createCustomValueLabel();
        JLabel temperatureValue = createCustomValueLabel("Fetching...");
        JLabel windValue = createCustomValueLabel("Fetching...");

        // Add the labels to the panel
        panel.add(dateTimeLabel);
        panel.add(dateTimeValue);
        panel.add(temperatureLabel);
        panel.add(temperatureValue);
        panel.add(windLabel);
        panel.add(windValue);

        // Add the panel to the frame
        frame.add(panel, BorderLayout.CENTER);

        // Add a refresh button in the corner
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(30, 144, 255)); // Dodger blue color
        refreshButton.setBorder(new LineBorder(Color.WHITE, 2));
        refreshButton.setFocusPainted(false);

        // Position the button in the top-right corner
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        frame.add(buttonPanel, BorderLayout.NORTH);

        // Set the current date and time
        updateWeatherData(dateTimeValue, temperatureValue, windValue);

        // Add action listener to refresh the data when the button is clicked
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateWeatherData(dateTimeValue, temperatureValue, windValue);
            }
        });

        // Display the frame
        frame.setVisible(true);
    }

    private static JLabel createCustomLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 18)); // Fun font style
        label.setForeground(Color.DARK_GRAY); // Dark gray text color
        return label;
    }

    private static JLabel createCustomValueLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Comic Sans MS", Font.PLAIN, 18)); // Fun font style
        label.setForeground(Color.BLACK); // Black text color
        label.setBorder(new LineBorder(Color.WHITE, 2)); // White border around value
        label.setHorizontalAlignment(SwingConstants.CENTER); // Center align text
        return label;
    }

    private static JLabel createCustomValueLabel(String text) {
        JLabel label = createCustomValueLabel();
        label.setText(text);
        return label;
    }

    private static void updateWeatherData(JLabel dateTimeValue, JLabel temperatureValue, JLabel windValue) {
        // Set the current date and time
        dateTimeValue.setText(getCurrentDateTime());

        // Fetch the weather prediction from the Python model
        String[] prediction = getWeatherPredictionFromPythonModel();

        // Update the labels with the prediction data
        temperatureValue.setText(prediction[0] + " °C");
        windValue.setText(prediction[1] + " km/h");
    }

    private static String getCurrentDateTime() {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    private static String[] getWeatherPredictionFromPythonModel() {
        String[] prediction = {"Unavailable", "Unavailable"};

        try {
            // Run the Python script that generates the prediction
            ProcessBuilder pb = new ProcessBuilder("python", "weather_prediction.py");
            Process process = pb.start();

            // Capture the output of the Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();

            if (line != null) {
                // Assuming the Python script outputs the temperature and wind speed separated by a comma
                prediction = line.split(",");
            }

            // Wait for the process to complete
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return prediction;
    }
}
