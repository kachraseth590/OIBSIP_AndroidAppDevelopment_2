# 📝 To-Do App

A clean, Firebase-backed To-Do list Android app built with **Java and XML** in Android Studio. Users can securely log in and manage their personal tasks, events, meetings, and notes — all synced in real-time to the cloud.

---

## ✨ Features

- 🔐 **Login & Sign-Up** — Secure email/password authentication via Firebase Auth
- 📋 **Task List** — View all your tasks in a clean card-based layout, sorted newest first
- ➕ **Add Task** — Create tasks with a title, description, and date & time picker
- ✏️ **Edit Task** — Tap any task to update it instantly
- 🗑️ **Delete Task** — Permanently remove tasks from Firebase with one tap
- 🚪 **Logout** — One-tap logout always accessible from the toolbar
- ☁️ **Real-time Sync** — All data stored and synced via Firebase Realtime Database
- 🔒 **Data Isolation** — Each user only ever sees their own tasks

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI | XML Layouts |
| IDE | Android Studio |
| Authentication | Firebase Auth (Email/Password) |
| Database | Firebase Realtime Database |
| UI Components | Material Design 3, RecyclerView, CardView |

---

## 📱 Screens

| Screen | Description |
|---|---|
| Login | Email/password sign-in, auto-skips if already logged in |
| Sign Up | New account creation with name, email, and password |
| Task List | RecyclerView of all tasks with delete button per card |
| Add/Edit Task | Title, description, and date-time picker form |

---

## 🚀 Getting Started

### 1. Clone / Open the Project
Open the `TodoApp` folder in Android Studio.

### 2. Connect Firebase
- Go to [Firebase Console](https://console.firebase.google.com/) and create a project
- Add an Android app with package name `com.kktdeveloper.todoapp`
- Download `google-services.json` and place it inside the `app/` folder
- Enable **Authentication → Sign-in method → Email/Password**
- Enable **Realtime Database** and set rules (see below)

### 3. Set Firebase Database Rules
```json
{
  "rules": {
    "tasks": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    }
  }
}
```

### 4. Run the App
Sync Gradle and run on an emulator or physical device (minSdk 26).

---

## 📂 Project Structure

```
app/src/main/
├── java/com/kktdeveloper/todoapp/
│   ├── LoginActivity.java
│   ├── SignupActivity.java
│   ├── MainActivity.java
│   ├── AddEditTaskActivity.java
│   ├── adapter/
│   │   └── TaskAdapter.java
│   └── model/
│       └── Task.java
└── res/
    ├── layout/
    │   ├── activity_login.xml
    │   ├── activity_signup.xml
    │   ├── activity_main.xml
    │   ├── activity_add_edit_task.xml
    │   └── item_task.xml
    ├── values/
    │   ├── strings.xml
    │   ├── colors.xml
    │   └── themes.xml
    └── menu/
        └── menu_main.xml
```

---

## 👨‍💻 Developer

**Kamal Kumar Thakur**  
Package: `com.kktdeveloper.todoapp`

---

## 📄 License

This project is built for educational and portfolio purposes.
