# Backend Setup for E2E Testing - Complete Guide

## 🎯 What Has Been Done

Your backend is now configured to support the frontend E2E tests that use real API calls.


####  **Already Configured (No Changes Needed)**
- ✅ **Dockerfile** - Already has HEALTHCHECK using `/actuator/health`
- ✅ **Spring Actuator** - Already in dependencies
- ✅ **CORS** - Already configured to allow all origins
- ✅ **Docker Image Build** - Already configured in `.gitlab-ci.yml`

## 🚀 Next Steps - What You Need to Do

### Step 1: Test Locally (Optional but Recommended)

```bash
# Build the Docker image
docker build -t tripwise-api:test .

# Run with environment variables
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/TripWise \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=201461 \
  tripwise-api:test

# Test the health endpoint
curl http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP"}
```

### Step 2: Commit and Push Changes

```bash
git add src/main/resources/application.properties
git commit -m "Configure backend for E2E testing with environment variables"
git push origin main
```

### Step 3: Verify CI Pipeline Builds Docker Image

Your `.gitlab-ci.yml` already has the `docker-image` job that:
1. Builds the Docker image
2. Tags it as `latest`
3. Pushes to GitLab Container Registry at:
   - `registry.gitlab.com/ruyi-group/s3-ip-tripwise-api:latest`

**Watch the pipeline:**
- Go to GitLab → CI/CD → Pipelines
- Wait for `docker-image` job to complete successfully
- Verify the image is pushed to Container Registry

### Step 4: Verify Docker Image in GitLab Registry

1. Go to GitLab: **Your Backend Project → Packages & Registries → Container Registry**
2. You should see: `s3-ip-tripwise-api` with tag `latest`
3. Image path should be: `registry.gitlab.com/ruyi-group/s3-ip-tripwise-api:latest`

### Step 5: Test the Setup with Frontend E2E

Once the backend Docker image is available:

1. Go to your **frontend project**
2. Push the E2E test changes
3. GitLab CI will:
   - Start PostgreSQL service
   - Start backend service (using your Docker image)
   - Build frontend
   - Run E2E tests against real backend

## 🔍 How It Works in CI

### Frontend CI Pipeline (when E2E tests run):

```yaml
playwright-e2e-job:
  services:
    # ⬇️ This pulls YOUR backend image
    - name: registry.gitlab.com/ruyi-group/s3-ip-tripwise-api:latest
      alias: backend
    # ⬇️ Database for backend
    - name: postgres:16
      alias: postgres
  variables:
    # ⬇️ Backend connects to postgres service
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/TripWise
    SPRING_DATASOURCE_USERNAME: postgres
    SPRING_DATASOURCE_PASSWORD: postgres
    SPRING_PROFILES_ACTIVE: default
    # ⬇️ Frontend connects to backend service
    REACT_APP_API_URL: http://backend:8080
```

### Execution Flow:

1. **GitLab starts services:**
   - PostgreSQL: Available at `postgres:5432`
   - Backend: Available at `backend:8080`

2. **Backend initializes:**
   - Connects to PostgreSQL using environment variables
   - Runs Flyway migrations
   - Starts Spring Boot application
   - Health endpoint responds at `http://backend:8080/actuator/health`

3. **Frontend tests run:**
   - API calls go to `http://backend:8080`
   - Backend processes real requests
   - Database stores real data
   -  **Full integration test!**

