import urllib.request
import json
import pickle

# Load the trained model and scaler
with open('C:/Users/aadit/OneDrive/Desktop/Code/weather_model.pkl', 'rb') as model_file:
    model = pickle.load(model_file)

with open('scaler.pkl', 'rb') as scaler_file:
    scaler = pickle.load(scaler_file)

def fetch_weather_data():
    url = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=relative_humidity_2m,rain&hourly=temperature_2m,visibility,wind_speed_80m&timezone=auto"
    
    try:
        # Send the request to the API
        with urllib.request.urlopen(url) as response:
            # Read and decode the response
            data = response.read().decode()
            # Parse JSON data
            weather_data = json.loads(data)

            # Extract current weather data
            current_weather = weather_data.get('current', {})
            humidity = current_weather.get('relative_humidity_2m', 'N/A')
            rain = current_weather.get('rain', 'N/A')
            
            # Extract hourly weather data
            hourly_data = weather_data.get('hourly', {})
            temperature = hourly_data.get('temperature_2m', ['N/A'])[0]
            visibility = hourly_data.get('visibility', ['N/A'])[0]
            wind_speed = hourly_data.get('wind_speed_80m', ['N/A'])[0]

            # Prepare data for prediction
            features = [temperature, humidity, wind_speed, visibility]
            features = scaler.transform([features])  # Apply scaling

            # Predict using the trained model
            predicted_temperature = model.predict(features)[0]
            predicted_wind_speed = wind_speed  # Use the wind speed from the API response

            # Print the relevant outputs
            print(f"Predicted Temperature: {predicted_temperature:.2f}°C")
            print(f"Predicted Wind Speed: {predicted_wind_speed:.2f} km/h")
            print(f"Current Humidity: {humidity:.2f}%")
            print(f"Current Visibility: {visibility:.2f} meters")
            print(rain)

    except Exception as e:
        # Handle any exceptions (e.g., network issues, invalid responses)
        print(f"Error fetching weather data: {e}")

if __name__ == "__main__":
    fetch_weather_data()
