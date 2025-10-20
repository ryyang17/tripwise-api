@echo off
echo Exporting data from local PostgreSQL database...

REM Export data from your local database
REM Replace "your-local-postgres-path" with your actual PostgreSQL bin path
REM For example: "C:\Program Files\PostgreSQL\16\bin\pg_dump"

pg_dump -U postgres -d TripWise --data-only --inserts --column-inserts -f tripwise-data.sql

echo Data exported to tripwise-data.sql
echo Now you can import it to Docker using: import-to-docker.bat
pause

