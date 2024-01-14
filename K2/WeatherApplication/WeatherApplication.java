package Napredno.K2.WeatherApplication;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

interface IWeatherObserver {
    void update(float temperature, float humidity, float pressure);

    int priority();
}

class ForecastDisplay implements IWeatherObserver {
    private float lastPressure;

    public ForecastDisplay(WeatherDispatcher weatherDispatcher) {
        this.lastPressure = 0;
        weatherDispatcher.register(this);
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        if (pressure > lastPressure) {
            System.out.println("Forecast: Improving");
        } else if (pressure == lastPressure) {
            System.out.println("Forecast: Same");
        } else {
            System.out.println("Forecast: Cooler");
        }

        lastPressure = pressure;
    }

    @Override
    public int priority() {
        return 1;
    }
}

class CurrentConditionsDisplay implements IWeatherObserver {
    public CurrentConditionsDisplay(WeatherDispatcher wd) {
        wd.register(this);
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        System.out.printf("Temperature: %.1fF\nHumidity: %.1f%%%n", temperature, humidity);
    }

    @Override
    public int priority() {
        return 0;
    }
}


class WeatherDispatcher {
    Set<IWeatherObserver> observerSet;

    public WeatherDispatcher() {
        observerSet = new HashSet<>();
    }

    public void remove(IWeatherObserver weatherObserver) {
        observerSet.remove(weatherObserver);
    }

    public void register(IWeatherObserver weatherObserver) {
        observerSet.add(weatherObserver);
    }

    public void setMeasurements(float temp, float humidity, float pressure) {
        observerSet.stream().sorted(Comparator.comparing(IWeatherObserver::priority)).forEach(weatherObserver -> weatherObserver.update(temp, humidity, pressure));
        System.out.println();
    }
}

public class WeatherApplication {

    public static void main(String[] args) {
        WeatherDispatcher weatherDispatcher = new WeatherDispatcher();

        CurrentConditionsDisplay currentConditions = new CurrentConditionsDisplay(weatherDispatcher);
        ForecastDisplay forecastDisplay = new ForecastDisplay(weatherDispatcher);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            weatherDispatcher.setMeasurements(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
            if (parts.length > 3) {
                int operation = Integer.parseInt(parts[3]);
                if (operation == 1) {
                    weatherDispatcher.remove(forecastDisplay);
                }
                if (operation == 2) {
                    weatherDispatcher.remove(currentConditions);
                }
                if (operation == 3) {
                    weatherDispatcher.register(forecastDisplay);
                }
                if (operation == 4) {
                    weatherDispatcher.register(currentConditions);
                }

            }
        }
    }
}

