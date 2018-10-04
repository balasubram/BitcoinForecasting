# BitCoin Forecasting

## Build

The project is built using maven. To build the project and build a jar with dependencies, run the following command

```mvn clean compile package
```
The jar is found in the target package. The jar name is BitcoinForecasting-0.0.1-SNAPSHOT.jar. All the dependent jars are available in target/jars directory. For running the application the base directory will be target

To run the application, run the command 

```
java -cp .:BitcoinForecasting-0.0.1-SNAPSHOT.jar:jars/* com.bala.bitcoin.webserver.Application

```

The application will be started and the jetty webserver is listening on port 8900.

All the API's are exposed using the REST API. To use the API, the URL's needs to be accessed either using a browser or a command line utility like CURL.

The BTC prices for the past month is retrieved and displayed as a JSON. The command to run is given below

```
curl http://localhost:8900/priceMovement?type=month
```

The BTC prices for the past week is retrieved and displayed as a JSON. The command to run is given below

```
curl http://localhost:8900/priceMovement?type=week
```

The BTC prices for a custom dqte is retrieved and displayed as a JSON. If the date parameter is not given, the price movement is retrieved for the current date. If the date parameter is given, the price information for that date is provided. The date format should be in yyyy-MM-dd format. If any other format is provided, a message "Invalid date" is sent as response. The command to run is given below

```
curl http://localhost:8900/priceMovement?type=date

curl http://localhost:8900/priceMovement?type=date?date=2018-10-02
```


To retrieve the moving average for the BitCoin price, the parameters are startDate and endDate are necessary. If the movingAverageDays parameter is not given, it will compute the moving average for the days between startDate and endDate. The date format should be in yyyy-MM-dd format. The startDate cannot be after endDate. If any other format is provided, a message "Invalid date" is sent as response. The command to run is given below

```
curl http://localhost:8900/movingAverage?startDate=2018-09-01&endDate=2018-09-05&movingAverageDays=3

curl http://localhost:8900/movingAverage?startDate=2018-09-01&endDate=2018-09-05
```

For the next 15 days forecasting, use the command given below. For forecasting, I used ARIMA model based on spark-timeseries API. I don't know much about MI, I got to know about this technique from my friend and I am not sure if my code is correct.

```
curl http://localhost:8900/forecasting
```


