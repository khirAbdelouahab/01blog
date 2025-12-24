#!/bin/bash

# Function to cleanup background processes
cleanup() {
    echo ""
    echo "Shutting down applications..."
    
    # Kill all background jobs
    jobs -p | xargs -r kill
    
    # Alternative: Kill by process group
    # kill 0
    
    echo "Applications stopped."
    exit 0
}

# Set trap to catch Ctrl+C (SIGINT) and call cleanup
trap cleanup SIGINT SIGTERM

echo "Starting Spring Boot application..."
cd /home/abouchik/Desktop/01-blogger/backend
#cd /c/Users/KHIRI/Desktop/01-blogger/backend
./mvnw spring-boot:run &
SPRING_PID=$!

echo "Starting Angular application..."
cd /home/abouchik/Desktop/01-blogger/frontend
#cd /c/Users/KHIRI/Desktop/01-blogger/frontend

npm start &
ANGULAR_PID=$!

echo "Both applications are starting..."
echo "Press Ctrl+C to stop both applications"

# Wait for all background jobs to complete
# This will keep the script running until interrupted
wait