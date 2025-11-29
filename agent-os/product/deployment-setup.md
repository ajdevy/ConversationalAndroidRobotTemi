# Deployment & Setup Instructions

This document provides step-by-step instructions for setting up and deploying the Temi Restaurant Assistant PoC.

## Prerequisites

### Required Software
- **Java JDK:** 17 or higher
- **Kotlin:** 1.9.0 or higher
- **Gradle:** 8.0 or higher
- **PostgreSQL:** 14 or higher
- **Android Studio:** Latest stable version (for Android app development)
- **IntelliJ IDEA:** Latest version (for backend development)
- **Git:** For version control
- **Docker:** (Optional) For containerized deployment

### Hardware Requirements
- **Backend Server:** Minimum 4GB RAM, 2 CPU cores, 20GB storage
- **Temi Robot:** Temi robot device with Android OS
- **Development Machine:** 8GB+ RAM for Android Studio and IntelliJ

---

## 1. API Keys & Third-Party Services

### OpenAI API (Required for Cloud LLM)
1. Visit: https://platform.openai.com/signup
2. Create an account or sign in
3. Navigate to: https://platform.openai.com/api-keys
4. Click "Create new secret key"
5. Name it: `temi-restaurant-assistant`
6. **Copy and save the key immediately** (you won't be able to see it again)
7. **Cost:** Pay-as-you-go pricing - $0.005/1K input tokens, $0.015/1K output tokens for GPT-4o

### Speech-to-Text Service (Choose One)

#### Option A: Google Cloud Speech-to-Text (Recommended for noisy environments)
1. Visit: https://console.cloud.google.com/
2. Create a new project or select existing
3. Enable Speech-to-Text API: https://console.cloud.google.com/apis/library/speech.googleapis.com
4. Create credentials:
   - Go to: https://console.cloud.google.com/apis/credentials
   - Click "Create Credentials" → "API Key"
   - Alternatively, create a Service Account for production
5. Download JSON credentials file
6. **Cost:** $0.006/15 seconds for standard recognition, $0.009/15 seconds for enhanced models

#### Option B: Azure Speech Services
1. Visit: https://portal.azure.com/
2. Create a "Speech Services" resource
3. Get your API key and region from the resource's "Keys and Endpoint" section
4. **Cost:** Free tier: 5 audio hours/month, then $1/audio hour

#### Option C: OpenAI Whisper (Local, Free)
1. No API key required
2. Model downloads automatically
3. Runs on device (requires GPU for real-time processing)
4. Repository: https://github.com/openai/whisper

### Text-to-Speech Service (Choose One)

#### Option A: Google Cloud Text-to-Speech
1. Visit: https://console.cloud.google.com/
2. Enable Text-to-Speech API: https://console.cloud.google.com/apis/library/texttospeech.googleapis.com
3. Use the same credentials as Speech-to-Text
4. **Cost:** $4.00/1M characters for standard voices, $16.00/1M for WaveNet/Neural2 voices

#### Option B: Amazon Polly
1. Visit: https://aws.amazon.com/polly/
2. Create AWS account
3. Go to IAM Console and create access keys
4. **Cost:** 1M characters free for 12 months, then $4.00/1M characters (standard), $16.00/1M (neural)

#### Option C: Android TTS (Local, Free)
1. Built into Android OS
2. No API key required
3. May have lower quality than cloud options

### OAuth 2.0 Provider (Optional for production)
For development, use Spring Security's built-in OAuth. For production:
- **Auth0:** https://auth0.com/ (Free tier: 7,000 active users)
- **Keycloak:** Self-hosted, free and open-source
- **Okta:** https://www.okta.com/ (Free tier: up to 100 monthly active users)

---

## 2. Backend Setup

### Step 1: Clone Repository
```bash
git clone <repository-url>
cd temi-restaurant-assistant/backend
```

### Step 2: Configure Environment Variables
Create `.env` file in the backend root directory:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=temi_restaurant_db
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password

# OAuth Configuration
OAUTH_CLIENT_ID=your_client_id
OAUTH_CLIENT_SECRET=your_client_secret
OAUTH_ISSUER_URI=http://localhost:8080

# OpenAI Configuration
OPENAI_API_KEY=sk-proj-xxxxxxxxxxxxxxxxxxxxx
OPENAI_MODEL=gpt-4o

# Speech-to-Text Configuration (Google Cloud example)
GOOGLE_APPLICATION_CREDENTIALS=/path/to/service-account-key.json
SPEECH_TO_TEXT_LANGUAGE_CODES=en-US,es-ES,pt-BR

# Text-to-Speech Configuration
TTS_SERVICE=google  # Options: google, amazon, android
TTS_VOICE_EN=en-US-Neural2-D
TTS_VOICE_ES=es-ES-Neural2-A
TTS_VOICE_PT=pt-BR-Neural2-A

# Server Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# Monitoring
ACTUATOR_ENABLED=true
ACTUATOR_ENDPOINT_HEALTH_ENABLED=true
```

### Step 3: Set Up PostgreSQL Database
```bash
# Start PostgreSQL service
sudo systemctl start postgresql  # Linux
brew services start postgresql   # macOS

# Create database
psql -U postgres
CREATE DATABASE temi_restaurant_db;
CREATE USER temi_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE temi_restaurant_db TO temi_user;
\q
```

### Step 4: Run Database Migrations
```bash
# Using Gradle
./gradlew flywayMigrate

# Or manually load schema
psql -U temi_user -d temi_restaurant_db -f src/main/resources/db/schema.sql
```

### Step 5: Load Test Data
```bash
# Run seed script
./gradlew bootRun --args='--spring.profiles.active=dev --seed-data=true'

# Or manually
psql -U temi_user -d temi_restaurant_db -f src/main/resources/db/test-data.sql
```

### Step 6: Build Backend
```bash
# Build with Gradle
./gradlew clean build

# Run tests with coverage
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```

### Step 7: Run Backend Server
```bash
# Development mode
./gradlew bootRun

# Production mode
java -jar build/libs/temi-restaurant-backend-1.0.0.jar
```

Backend should now be running at: `http://localhost:8080`

### Step 8: Verify Backend
```bash
# Check health endpoint
curl http://localhost:8080/actuator/health

# Check API documentation
open http://localhost:8080/swagger-ui.html
```

---

## 3. Android App Setup

### Step 1: Clone and Open Project
```bash
cd temi-restaurant-assistant/android-app
```
Open the project in Android Studio.

### Step 2: Configure Temi SDK
1. Download Temi SDK from: https://github.com/robotemi/sdk
2. Follow SDK integration instructions
3. Add Temi SDK dependency to `build.gradle`:
```gradle
dependencies {
    implementation 'com.robotemi:sdk:0.11.50'
}
```

### Step 3: Configure Backend Connection
Edit `app/src/main/res/values/config.xml`:
```xml
<resources>
    <string name="backend_base_url">http://YOUR_SERVER_IP:8080</string>
    <string name="backend_api_version">v1</string>
</resources>
```

Or use environment-specific configuration in `local.properties`:
```properties
backend.url=http://192.168.1.100:8080
```

### Step 4: Configure LLM Settings
Edit `app/src/main/assets/llm-config.json`:
```json
{
  "localModel": {
    "enabled": true,
    "modelPath": "gemma-2-2b-it.gguf",
    "maxTokens": 1024
  },
  "cloudModel": {
    "enabled": true,
    "provider": "openai",
    "fallbackToLocal": true
  },
  "supportedLanguages": ["en", "es", "pt"]
}
```

### Step 5: Download Local LLM Model (Optional)
For offline capability:
1. Download Gemma 2 model (2B or 3B variant)
   - From: https://huggingface.co/google/gemma-2-2b-it
   - Convert to GGUF format if needed
2. Place model file in: `app/src/main/assets/models/`
3. Update model path in config

### Step 6: Build APK
```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing)
./gradlew assembleRelease
```

APK location: `app/build/outputs/apk/debug/app-debug.apk`

---

## 4. Temi Robot Configuration

### Step 1: Enable Developer Mode on Temi
1. On Temi robot, go to Settings
2. Tap "About" 7 times to enable developer mode
3. Enable "USB Debugging" in Developer Options

### Step 2: Install App on Temi
```bash
# Connect Temi via USB or WiFi
adb connect TEMI_IP_ADDRESS

# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Or use Android Studio's "Run" button with Temi as target device
```

### Step 3: Configure Robot Registration
1. Launch app on Temi
2. Go to Settings → Robot Configuration
3. Enter:
   - **Robot Serial Number:** (found in Temi settings)
   - **Restaurant Assignment:** Select restaurant from list
   - **Backend URL:** Verify connection
4. Tap "Register Robot"
5. Robot will authenticate with backend and download assigned restaurant's menu

### Step 4: Test Robot Functionality
1. **Test visual status indicator:**
   - Verify grey static circle appears when idle
   - Ask a question and check red pulsating circle appears during thinking
   - Verify green pulsating circle appears when robot speaks
   - Check smooth transitions between states
2. **Test speech recognition:** Say "Hello" and verify response
3. **Test menu query:** Ask "What's on the menu?" and verify menu-based response
4. **Test interruption:** Start asking a question, interrupt mid-sentence, verify:
   - Green pulsating stops immediately
   - Red pulsating appears as it processes interruption
   - Robot responds to the interruption appropriately
5. **Test multilingual:** Switch to Spanish/Portuguese and verify all features work
6. **Test offline mode:** Disable WiFi, verify local LLM works and status indicator still functions

---

## 5. Admin Dashboard Setup (Optional)

If implementing the web admin dashboard:

### Step 1: Set Up Frontend Project
```bash
cd temi-restaurant-assistant/admin-dashboard
npm install  # or yarn install
```

### Step 2: Configure API Endpoint
Edit `.env`:
```bash
REACT_APP_API_URL=http://localhost:8080/api/v1
REACT_APP_OAUTH_CLIENT_ID=your_client_id
```

### Step 3: Run Dashboard
```bash
npm start  # Development
npm run build  # Production build
```

---

## 6. Testing the Complete Setup

### Backend Tests
```bash
# Run all tests with coverage
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests "RestaurantServiceTest"

# Check coverage (should be 90%+)
cat build/reports/jacoco/test/html/index.html
```

### Integration Tests
```bash
# Test restaurant creation
curl -X POST http://localhost:8080/api/v1/restaurants \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"name":"Test Restaurant","address":"123 Main St"}'

# Test menu retrieval
curl http://localhost:8080/api/v1/restaurants/1/menu

# Test user permissions
curl http://localhost:8080/api/v1/users/me/restaurants \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### End-to-End Test
1. Create test restaurant via API or admin dashboard
2. Create test menu items
3. Assign robot to restaurant
4. Launch app on Temi
5. Verify visual status indicator shows grey static circle (idle state)
6. Ask robot: "What dishes do you have?"
7. Observe status transitions:
   - Red pulsating circle while thinking
   - Green pulsating circle while speaking
   - Back to grey static when finished
8. Verify robot searches menu and responds correctly
9. Test in noisy environment and verify status indicator remains visible and clear
10. Test interruption handling:
    - Start a question, interrupt mid-response
    - Verify green pulsating stops immediately
    - Verify red pulsating appears during re-processing
    - Confirm robot handles interruption appropriately
11. Test Spanish/Portuguese with status indicator functioning
12. Test all three status states (grey idle, red thinking, green speaking) work correctly in all languages

---

## 7. Production Deployment

### Backend Deployment

#### Option A: Docker Deployment
```bash
# Build Docker image
docker build -t temi-restaurant-backend:1.0.0 .

# Run with Docker Compose
docker-compose up -d
```

`docker-compose.yml`:
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: temi_restaurant_db
      POSTGRES_USER: temi_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  backend:
    image: temi-restaurant-backend:1.0.0
    environment:
      DB_HOST: postgres
      OPENAI_API_KEY: ${OPENAI_API_KEY}
    ports:
      - "8080:8080"
    depends_on:
      - postgres

volumes:
  postgres_data:
```

#### Option B: Local Server Deployment
1. Set up reverse proxy (Nginx):
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

2. Create systemd service:
```ini
[Unit]
Description=Temi Restaurant Backend
After=network.target

[Service]
Type=simple
User=temi
WorkingDirectory=/opt/temi-restaurant-backend
ExecStart=/usr/bin/java -jar temi-restaurant-backend-1.0.0.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

3. Enable and start:
```bash
sudo systemctl enable temi-restaurant-backend
sudo systemctl start temi-restaurant-backend
```

### Database Backup
```bash
# Create backup
pg_dump -U temi_user temi_restaurant_db > backup_$(date +%Y%m%d).sql

# Restore backup
psql -U temi_user -d temi_restaurant_db < backup_20250117.sql
```

---

## 8. Monitoring & Maintenance

### Check Application Health
```bash
# Backend health
curl http://localhost:8080/actuator/health

# Detailed metrics
curl http://localhost:8080/actuator/metrics
```

### View Logs
```bash
# Backend logs
tail -f logs/temi-restaurant-backend.log

# Docker logs
docker logs -f temi-restaurant-backend
```

### Monitor Database
```bash
# Connect to database
psql -U temi_user -d temi_restaurant_db

# Check active connections
SELECT count(*) FROM pg_stat_activity;

# Check database size
SELECT pg_size_pretty(pg_database_size('temi_restaurant_db'));
```

---

## 9. Troubleshooting

### Backend won't start
- Check PostgreSQL is running: `systemctl status postgresql`
- Verify database credentials in `.env`
- Check port 8080 is not in use: `lsof -i :8080`

### Robot can't connect to backend
- Verify backend URL in robot app settings
- Check network connectivity: `ping YOUR_SERVER_IP`
- Verify firewall allows port 8080
- Check OAuth token is valid

### Speech recognition not working
- Verify API keys are correct
- Check microphone permissions on Temi
- Test in quieter environment first
- Verify service account credentials path

### LLM responses are slow
- Check OpenAI API status: https://status.openai.com/
- Verify network latency
- Consider using local LLM fallback
- Check rate limits on API key

### Tests failing
- Run: `./gradlew clean test`
- Check test database is properly configured
- Verify test data seed scripts are correct

---

## 10. Security Checklist

- [ ] Change default database password
- [ ] Store API keys in secure environment variables (never in code)
- [ ] Enable HTTPS for production backend
- [ ] Configure OAuth properly for production
- [ ] Restrict database access to backend server only
- [ ] Enable Spring Security CSRF protection
- [ ] Implement rate limiting on API endpoints
- [ ] Regular security updates for dependencies
- [ ] Backup database regularly
- [ ] Monitor API key usage and costs

---

## Support & Resources

- **Temi SDK Documentation:** https://github.com/robotemi/sdk/wiki
- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **OpenAI API Documentation:** https://platform.openai.com/docs
- **PostgreSQL Documentation:** https://www.postgresql.org/docs/
- **Kotlin Documentation:** https://kotlinlang.org/docs/home.html

---

## Cost Estimate (Monthly)

### Development/Testing
- OpenAI API: ~$10-50 (depending on usage)
- Google Cloud Speech/TTS: ~$5-20
- Total: **~$15-70/month**

### Production (per location)
- OpenAI API: ~$50-200 (based on customer volume)
- Google Cloud Speech/TTS: ~$20-100
- Server hosting: $20-50 (if cloud hosted)
- Total: **~$90-350/month per restaurant**

### Cost Optimization
- Use local LLM (Gemma) when possible to reduce API costs
- Implement response caching for common questions
- Use cheaper speech services for non-critical interactions
- Monitor usage with Spring Boot Actuator
