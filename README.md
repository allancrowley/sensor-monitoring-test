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

1. **sensor_data**: Stores raw sensor data, cleared every hour.
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

