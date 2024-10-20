# Sensor Monitoring Dashboard

A web-based application for real-time monitoring of sensor data, displaying average temperatures and identifying malfunctioning sensors.

## Table of Contents

- [Assumptions](#assumptions)
- [Modules](#modules)
- [Architecture](#architecture)
- [Database](#database)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Usage](#usage)
- [API](#api)
- [Environment Variables](#environment-variables)


## Assumptions

1. **Data Flow Management**: It is assumed that the server can handle the incoming data flow efficiently, and that data will arrive without significant delays. For production-level deployment, it is crucial to implement additional mechanisms to verify that the received data corresponds to the current or previous time periods.
2. **Crash Handling**: The initial development did not account for scenarios involving program crashes or the processing of previously accumulated data after extended downtime. To address this, the implementation of additional database processing during application startup is recommended.

## Modules

1. **Sensor Monitoring**: This module provides hourly analytics for temperature data collected from sensors. It calculates the average temperature for each cardinal direction (North, South, East, West) and identifies malfunctioning sensors, alerting when they deviate from expected parameters.
2. **Sensor Imitator**: This module generates random sensor data based on predefined thresholds, simulating real sensor activity. It is used primarily for integration testing to validate system behavior under various conditions.

## Architecture

This project considers two possible architectures for handling data:

1. **Standalone Application**
- Receives sensor data via an HTTP endpoint and stores it in a database or file system for a relevant time period.
- Performs periodic data analysis.
- Can scale using a load balancer to distribute incoming traffic if data volume increases.
- This architecture was chosen due to the project’s requirements and the manageable data flow (up to 10,000 samples per second), making it sufficient for this task.
2. **Stream-Based Application**
- Receives data through a data stream (e.g., Kafka) and aggregates it in memory (using Spark). Only the aggregated results are stored in the database, reducing storage needs.
- Provides high scalability and fault tolerance (Kafka ensures data safety if any processing instance crashes).
- This option was deemed unnecessary for the current requirements and time constraints but is a potential future improvement for higher data loads.

## Database

PostgreSQL was selected for this project as it handles the current data volume well. For larger-scale projects with extended data retention, a time-series database would be more appropriate for efficient querying and storage.

The database uses the following tables:

1. **sensor_data**: Stores raw sensor data, cleared at 02 minutes past each hour for more precise data management.
2. **sensor_face_data**: Stores hourly averages of sensor data categorized by cardinal directions.
3. **sensor_deviated_data**: Stores hourly records of malfunctioning sensors.

If more advanced analytics are needed in the future, an additional table can be introduced:

4. **sensor_avg_data**: Stores historical average data for each sensor, enabling more flexible analytics.

## Technologies Used

- **Java 17**: Backend logic and sensor data processing.
- **Spring Boot**: Framework for building RESTful APIs and managing application components.
- **Thymeleaf**: Template engine for rendering dynamic HTML content in the frontend.
- **PostgreSQL**: Relational database for storing sensor data and analytics.
- **Docker**: Containerization for simplified deployment and scalability.
- **JUnit**: Unit testing framework.
- **Maven**: Build and dependency management tool.

## Installation

1. Clone the repository:

```bash
 git clone https://github.com/allancrowley/sensor-monitoring-test.git
```

2. Navigate to the project directory:

```bash
  cd sensor-monitoring-test
```

3. Run the containers (sensor-monitoring, sensor-imitator, and PostgreSQL) via Docker Compose:

```bash
   docker-compose up --build
```

## Usage

Once the server is running, you can access the sensor data dashboard at:

`http://localhost:8082/sensors/view`

## API

- Frontend Dashboard:

`GET /sensors/view`

Displays the current sensor data and analytics.
- Sensor Data Endpoint:
  
`POST /sensors/data`
Receives and processes raw sensor data.

## Environment Variables

### sensor-monitoring
- `DB_HOST=postgres` - The hostname for the PostgreSQL database container within the Docker network. The program will access the database using this host.
- `DB_PORT=5432` - The port used to communicate with the PostgreSQL database inside the Docker network.
- `DB_NAME=sensor_db` - The name of the database where sensor data will be stored.
- `DB_USERNAME=postgres` - The username for authenticating access to the database.
- `DB_PASSWORD=password` - The password for authenticating access to the database.
- `SERVER_PORT=8082` - The port on which the application will listen for incoming requests.
- `INPUT_PATH=/sensors/data` - The endpoint path for receiving sensor data payloads.
- `OUTPUT_PATH=/sensors/view` - The endpoint path for accessing the dashboard and viewing sensor data.
- `BATCH_SIZE=25000` - The maximum number of sensor data points that will be written to the database in a single transaction.
- `BATCH_FREQUENCY=2` - The frequency (in seconds) for writing incoming sensor data to the database in batches.
- `DEVIATION=0.2` - The acceptable percentage of temperature data deviation when analyzing malfunctioning sensors. If a sensor’s temperature data deviates by more than this percentage from the average, it is flagged as malfunctioning.

### sensor-imitator

- `SERVER_PORT=8081` - The port on which the data generator application will run.
- `SENSORS_COUNT=3000` - The number of sensors for which random temperature data will be generated.
- `SENSORS_MIN_TEMPERATURE=0` - The lower threshold for the randomly generated normal temperature values.
- `SENSORS_MAX_TEMPERATURE=50` - The upper threshold for the randomly generated normal temperature values.
- `SENSORS_MIN_DEVIATION_TEMPERATURE=-1000` - The lower threshold for the randomly generated temperature values when simulating malfunctioning sensors.
- `SENSORS_MAX_DEVIATION_TEMPERATURE=2000` - The upper threshold for the randomly generated temperature values when simulating malfunctioning sensors.
- `SENSORS_DEVIATION_INTERVAL=1000` - Defines how often malfunctioning sensors appear. For example, if SENSORS_COUNT % SENSORS_DEVIATION_INTERVAL == 0, the corresponding sensor will be flagged as malfunctioning.
- `SENSORS_ENDPOINT_HOST=http://sensor-monitoring` - The host to which the sensor data will be sent. This is typically the backend service responsible for processing sensor data.
- `SENSORS_ENDPOINT_PORT=8082` - The port on the target system to which sensor data will be sent.
- `SENSORS_ENDPOINT_PATH=sensors/data` - The API endpoint path for sending sensor data.
- `SENSORS_SENDING_RATE=1000` - The frequency (in milliseconds) at which data from each sensor is sent to the endpoint.

### postgres

- `POSTGRES_USER: postgres` - The username for authenticating access to the database.
- `POSTGRES_PASSWORD: password` - The password for authenticating access to the database.
- `POSTGRES_DB: sensor_db` - The name of the database where sensor data will be stored.

If you need to change the settings of one of the services, you should update the corresponding environment variable in the `docker-compose.yaml` file or in the related `Dockerfile`, especially if the module is launched independently from the main cluster.

