package dacd.guillen.control;
import dacd.guillen.model.Weather;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherController {
    private WeatherProvider weatherProvider;
    private WeatherStore weatherStore;

    public WeatherController() {
        this.weatherProvider = new OpenWeatherMapProvider("d90cb8f8df5f809c443f0fd9fb7d85a8");
        this.weatherStore = new SqliteWeatherStore();
    }

    public void startWeatherUpdateScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::updateWeather, 0, 6, TimeUnit.HOURS);
    }

    private void updateWeather() {
        List<Weather> weatherList = weatherProvider.getWeather();
        for (Weather weather : weatherList) {
            try (Connection connection = weatherStore.getConnection()) {
                weatherStore.createTableIfNotExists(connection, weather.getLocation().getName());
                weatherStore.saveWeather(connection, weather.getLocation().getName(), weather);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}