#!/bin/bash

# Exit immediately if a command fails
set -e

# Déclaration de la variable contenant le nom de l'app
BRANCH="DEV"

# Function to print status messages
print_status() {
  echo "======================================"
  echo "$1"
  echo "======================================"
}

# Saving changes and pulling the latest code
print_status "Saving and pulling the latest changes from git..."
git add .

if git diff --cached --quiet; then
  echo "No changes to commit."
else
  git commit -m "some update"
fi

git fetch --all 
git reset --hard "origin/$BRANCH"
git pull origin "$BRANCH"

# Install dependencies
print_status "Installing dependencies..."
npm install

# Build the Next.js app
print_status "Building the Next.js app..."
if npm run build; then
  echo "Build completed successfully."
else
  echo "Build failed. Exiting..."
  exit 1
fi

print_status "App has been restarted successfully!"