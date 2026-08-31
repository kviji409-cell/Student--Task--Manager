# Student Task Manager 📚

A simple Android application for managing student assignments, tasks, and deadlines.

## Features
- Add tasks with title and description
- Select a due date
- Mark tasks as completed
- Undo completed tasks
- Delete tasks
- Shows total, completed, and pending task counts
- Stores data locally using SQLite
- Simple student-friendly interface

## Technology
- Java
- Android Studio
- XML/Android UI
- SQLite
- Gradle

## How to Run
1. Open the project in Android Studio.
2. Allow Gradle to sync.
3. Connect an Android phone with USB debugging enabled, or create an Android Emulator.
4. Click **Run ▶**.
5. The app will install on the device.

## Project Structure
- `MainActivity.java` – application interface and task operations
- `Task.java` – task model
- `TaskDatabaseHelper.java` – SQLite database
- `AndroidManifest.xml` – application configuration

## Future Enhancements
- Task categories such as College, Personal and Exams
- Search and filtering
- Notifications for deadlines
- Cloud synchronization
- User login
