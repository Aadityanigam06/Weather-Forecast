import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_absolute_error, mean_squared_error
import requests
import pickle

# Fetch historical data or a large dataset from the API (simulated here with a loop for multiple requests)
data_records = []

for i in range(100):  # Assuming we simulate fetching data 100 times for training
    api_url = 'https://api.open-meteo.com/v1/forecast?latitude=13.0878&longitude=80.2785&hourly=temperature_2m,relative_humidity_2m,visibility,wind_speed_10m&timezone=auto'
    response = requests.get(api_url)
    data = response.json()

    hourly_data = data['hourly']
    temperature = hourly_data['temperature_2m']
    humidity = hourly_data['relative_humidity_2m']
    visibility = hourly_data['visibility']
    wind_speed = hourly_data['wind_speed_10m']

    for temp, hum, vis, wind in zip(temperature, humidity, visibility, wind_speed):
        data_records.append([temp, hum, wind, vis])

# Convert the collected data into a DataFrame
df = pd.DataFrame(data_records, columns=['Temperature (C)', 'Humidity', 'Wind Speed (km/h)', 'Visibility (km)'])
df['Apparent Temperature (C)'] = df['Temperature (C)']  # In a real scenario, this should be a more complex calculation

# Step 2: Preprocess the Data
df.fillna(method='ffill', inplace=True)
features = df[['Temperature (C)', 'Humidity', 'Wind Speed (km/h)', 'Visibility (km)']]
target = df['Apparent Temperature (C)']

X_train, X_test, y_train, y_test = train_test_split(features, target, test_size=0.2, random_state=42)

scaler = StandardScaler()
X_train = scaler.fit_transform(X_train)
X_test = scaler.transform(X_test)

# Step 3: Train the AI Model
model = RandomForestRegressor(n_estimators=100, random_state=42)
model.fit(X_train, y_train)

# Step 4: Evaluate the Model
y_pred = model.predict(X_test)
mae = mean_absolute_error(y_test, y_pred)
rmse = np.sqrt(mean_squared_error(y_test, y_pred))

print(f'Mean Absolute Error: {mae}')
print(f'Root Mean Squared Error: {rmse}')

# Step 5: Save the Model and Scaler
with open('weather_model.pkl', 'wb') as file:
    pickle.dump(model, file)

with open('scaler.pkl', 'wb') as file:
    pickle.dump(scaler, file)
