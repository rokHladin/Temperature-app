# Temperature App
This Android app lets you check the current temperature based on the latitude and longitude you provide. You can also set it to automatically refresh the temperature and location display at regular intervals.

## How to Use
1. **Manual Temperature Check:** Enter latitude and longitude, then tap "Refresh" to see the temperature.
2. **Location-Based Check:** Tap "Update Location" to fetch your current location and then tap "Refresh".
3. **Auto-Refresh:** Toggle the switch to auto-update the temperature and location display every minute.

## Notes
- You'll need a valid API key from OpenWeather.
  - Then create `apikeys.properties` file in the root of your project and put your key in the file like that: `API_KEY = "api_key_string_from_OpenWeather"`
- Grant location permissions when prompted, so app can get your location