// This script exports data from your local PostgreSQL database and imports it into
the Dockerized Postgres container (TripWise-DB).

// run in Powershell: .\import-to-docker.bat

REM Configure local PostgreSQL bin directory (update if different)
set "PG_BIN=C:\Program Files\PostgreSQL\17\bin"
set "PG_DUMP=%PG_BIN%\pg_dump.exe"

REM Output file (placed next to this script)
set "OUTFILE=%~dp0tripwise-data.sql"

REM Docker container name (from docker-compose.yml)
set "CONTAINER=TripWise-DB"

if not exist "%PG_DUMP%" (
  echo pg_dump not found at "%PG_DUMP%".
  echo Pas de waarde van PG_BIN aan bovenin dit bestand of installeer PostgreSQL.
  pause
  exit /b 1
)

REM If a password was passed as first arg, use it; otherwise prompt the user.
if "%~1"=="" (
  set /p "LOCAL_PW=Voer lokaal Postgres-wachtwoord in voor gebruiker 'postgres' (zichtbaar invoer): "
) else (
  set "LOCAL_PW=%~1"
)

REM Export data from local DB
echo Exporting data from local PostgreSQL database to "%OUTFILE%"...
set "PGPASSWORD=%LOCAL_PW%"
"%PG_DUMP%" -U postgres -d TripWise --data-only --inserts --column-inserts -f "%OUTFILE%"
if errorlevel 1 (
  echo pg_dump mislukte. Controleer dat de lokale database draait en dat de gebruikersnaam/wachtwoord correct zijn.
  pause
  exit /b 1
)

echo Data exported to %OUTFILE%

REM Ensure Docker and container are available
docker inspect -f "{{.State.Running}}" %CONTAINER% >nul 2>&1
if errorlevel 1 (
  echo Container %CONTAINER% niet gevonden of Docker is niet gestart.
  echo Start de containers met: docker-compose up -d
  pause
  exit /b 1
)

REM Copy SQL file into container
echo Copying "%OUTFILE%" into container %CONTAINER%:/tmp/tripwise-data.sql ...
docker cp "%OUTFILE%" %CONTAINER%:/tmp/tripwise-data.sql
if errorlevel 1 (
  echo Fout bij kopieren naar container.
  pause
  exit /b 1
)

REM Import inside container using container's POSTGRES_PASSWORD (from docker-compose.yml)
echo Importing into database inside container...
set "CONTAINER_PW=201461"
docker exec -e PGPASSWORD=%CONTAINER_PW% %CONTAINER% psql -U postgres -d TripWise -f /tmp/tripwise-data.sql
if errorlevel 1 (
  echo Import in container mislukt. Bekijk de foutmelding hierboven.
  pause
  exit /b 1
)

echo Import succesvol voltooid.
pause

