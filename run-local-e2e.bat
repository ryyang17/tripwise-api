@echo off
echo Starting TripWise API with test profile for E2E testing...
echo.
echo This will start the backend with H2 in-memory database
echo Press Ctrl+C to stop the server
echo.

gradlew.bat bootRun --args="--spring.profiles.active=test"
