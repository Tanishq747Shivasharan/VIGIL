# VIGIL: Personal Cybersecurity Android Toolkit

VIGIL is a comprehensive, offline cybersecurity application designed for Android. It empowers users to monitor their network environment, detect phishing attempts in real-time, and audit their digital credentials through a suite of integrated security tools.

---

## 🚀 Core Features

### 📶 WiFi Security Shield
- **Real-time Analysis**: Scans the connected WiFi network for security protocols (WPA2, WEP, etc.).
- **Risk Assessment**: Categorizes networks as Safe, Medium Risk, or High Risk based on encryption strength.
- **Network Metadata**: Displays SSID, BSSID, frequency, and signal strength.

### 🔍 LAN Device Discovery
- **Network Mapping**: Scans the local area network to identify all connected devices.
- **Security Audit**: Lists IP addresses, MAC addresses, and device names (if available).
- **Intruder Detection**: Helps users identify unauthorized devices on their home or office network.

### 🛡️ SMS Phishing Guard
- **Inbox Scanner**: Analyzes existing SMS messages for known phishing patterns and malicious links.
- **Live Monitoring**: Uses a BroadcastReceiver to intercept incoming SMS and alert the user instantly if a threat is detected.
- **Risk Scoring**: Provides a 0–100 risk score and detailed explanation for flagged messages.

### 🔑 Password Auditor & Generator
- **Strength Analysis**: Real-time feedback on password complexity using entropy-based calculations.
- **Secure Generator**: Creates cryptographically secure, customizable passwords (length, symbols, numbers).
- **Audit Checklist**: Provides actionable tips to improve weak passwords.

### 📜 Centralized Scan History
- **Unified Database**: All security scans (WiFi, LAN, SMS, Password) are stored locally using SQLite.
- **Filtered View**: Easily browse history by category or date.
- **Detailed Reports**: View full analysis details for any past scan record.

---

## 🛠️ Technology Stack

- **Language**: Java
- **Architecture**: Fragment-based UI with a Repository pattern for data.
- **Database**: Room Persistence Library (SQLite).
- **Networking**: InetAddress & Socket for LAN scanning.
- **System**: BroadcastReceiver for real-time SMS interception.
- **UI Components**: ConstraintLayout, RecyclerView, CardView, Material Design 3.

---

## 📋 Installation

1. Clone the repository.
2. Open the project in **Android Studio**.
3. Sync Gradle and ensure permissions (SMS, Location, WiFi State) are granted.
4. Build and Run on an Android device (API 24+).

---

## 🤝 Team
- Tanishq Shivasharan (3533)
- Aryan Jakkal (3558)
- Dhairyashil Sarwade (3583)

*Made for MSBTE Micro Project Submission — Empowering users through proactive security.*
