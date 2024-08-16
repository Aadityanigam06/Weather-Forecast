import numpy as np
import pandas as pd
import pickle

# Load the trained model and scaler
with open('weather_model.pkl', 'rb') as f:
    model = pickle.load(f)

with open('scaler.pkl', 'rb') as f:
    scaler = pickle.load(f)

# Example input data (initial day's weather conditions)
initial_data = pd.DataFrame({
    'Temperature (C)': [20],
    'Humidity': [100],
    'Wind Speed (km/h)': [17],
    'Visibility (km)': [66]
})

# Number of days to predict
days_to_predict = 5

# Store predictions
predictions = []

# Loop to predict for the next 5 days
for day in range(days_to_predict):
    # Scale the input data
    initial_data_scaled = scaler.transform(initial_data)

    # Make a prediction
    predicted_temperature = model.predict(initial_data_scaled)[0]
    predictions.append(predicted_temperature)

    # Print the prediction for the current day
    print(f'Day {day + 1} - Predicted Apparent Temperature (C): {predicted_temperature}')

    # Update the input data for the next day's prediction
    # Here, we assume the predicted temperature is used as the next day's temperature
    initial_data['Temperature (C)'] = predicted_temperature

    # Optionally, you can also adjust other parameters like humidity, wind speed, etc.
    # For example:
    # initial_data['Humidity'] = max(0, initial_data['Humidity'] - 1)  # Slightly decrease humidity
    # initial_data['Wind Speed (km/h)'] = min(20, initial_data['Wind Speed (km/h)'] + 1)  # Slightly increase wind speed

# Print all predictions
print("\nPredicted Temperatures for the next 5 days:")
for i, temp in enumerate(predictions, 1):
    print(f"Day {i}: {temp}°C")
