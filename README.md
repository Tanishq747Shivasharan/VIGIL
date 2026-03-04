# VIGIL
### CryptoLab & Security Dashboard — A Browser-Based Cybersecurity Toolkit

---

## Overview

VIGIL is a comprehensive offline cybersecurity toolkit designed for educational purposes and practical security analysis. It runs entirely in the browser with no server, no npm packages, and no installation required.

VIGIL Web serves as the companion to the VIGIL Android app (MAD project), together forming a complete dual-platform personal cybersecurity ecosystem. Open `index.html` in any modern browser to get started.

---

## Features

### Landing Page
- Hero section with project overview and call-to-action
- Live statistics bar displaying tool capabilities
- Quick navigation to all five security tools

### CryptoLab
Four classical cipher implementations with interactive visualizations:

| Cipher | Description |
|---|---|
| Caesar | Shift-based substitution with step-by-step letter visualization |
| Vernam | XOR-based encryption with one-time pad and truth table |
| Rail Fence | Zigzag transposition with visual grid display |
| Columnar Transposition | Keyword-based cipher with column order visualization |

### Steganography Tool
- Hide secret messages inside PNG images using the LSB technique
- Extract hidden messages from stego-images
- Pure browser-based implementation using the Canvas API
- Download stego-images directly to device

### Password Audit Tool
- Real-time password strength meter with animated progress bar
- Seven-point security criteria checklist
- Shannon entropy calculation and estimated crack time
- Cryptographically secure random password generator with customizable options
- TOTP/MFA simulator with live 30-second countdown timer

### Security Dashboard
- Import JSON data exported from the VIGIL Android app
- Summary statistics: Wi-Fi scans, devices discovered, SMS flags
- Three interactive Chart.js visualizations (bar, doughnut, line)
- Complete scan history table with color-coded risk badges
- One-click PDF report generation via jsPDF

### Email Header Analyzer
- Paste raw email headers for instant phishing analysis
- SPF, DKIM, and DMARC authentication validation
- Reply-To and Return-Path domain mismatch detection
- Received hops timeline with internal vs. external IP detection
- Security verdict: **Likely Safe**, **Suspicious**, or **Likely Phishing**

---

## NIS Curriculum Coverage

| Practical | Title | VIGIL Feature |
|---|---|---|
| P2 | Multi-Factor Authentication | TOTP/MFA Simulator |
| P4 | Strong Password Utility | Password Audit + Generator |
| P5 | Caesar Cipher | CryptoLab — Caesar |
| P6 | Vernam Cipher | CryptoLab — Vernam |
| P7 | Rail Fence Cipher | CryptoLab — Rail Fence |
| P8 | Columnar Transposition | CryptoLab — Columnar |
| P9 | Hash Code Generation | Password Entropy Calculator |
| P11 | Steganography | Steganography Tool |
| P15–P16 | Secure Email / Email Tracker | Email Header Analyzer |
| CO1–CO5 | All Course Outcomes | Covered across all 6 pages |

---

## Technical Specifications

| Category | Details |
|---|---|
| Frontend | HTML5, CSS3, Vanilla JavaScript (ES6+) |
| Styling | CSS Custom Properties, Grid, Flexbox |
| Charting | Chart.js via CDN (Dashboard only) |
| PDF Generation | jsPDF via CDN (Dashboard only) |
| Image Processing | Canvas API (Steganography only) |
| Typography | Inter (Google Fonts) |
| Framework | None |
| Local Dependencies | Zero |

---

## Installation and Usage

No installation required.

1. Download or clone this repository
2. Navigate to the project directory
3. Open `index.html` in any modern web browser

All tools are immediately available and work completely offline.

> **Note:** The Security Dashboard requires an internet connection only to load the Chart.js and jsPDF libraries from CDN. All other pages function entirely offline.

Tested on Chrome, Firefox, Edge, and Safari.

---

## Android App Integration

VIGIL Web integrates with the VIGIL Android application (MAD Project — `316006`).

**Workflow:**
1. The Android app performs Wi-Fi security analysis, LAN device discovery, and SMS phishing detection
2. Scan results are exported as a JSON file from the Android app
3. Import the JSON file into the Security Dashboard
4. Visualize scan data with interactive charts and a complete history table
5. Download a comprehensive PDF security report

**Expected JSON format:**
```json
{
  "wifi_scans": [...],
  "lan_scans":  [...],
  "sms_scans":  [...]
}
```

---

## Screenshots

| Page | File |
|---|---|
| Landing Page | `screenshots/index.png` |
| CryptoLab | `screenshots/cryptolab.png` |
| Steganography Tool | `screenshots/steganography.png` |
| Password Audit Tool | `screenshots/password-audit.png` |
| Security Dashboard | `screenshots/dashboard.png` |
| Email Header Analyzer | `screenshots/email-analyzer.png` |

---

## Planned Enhancements

- Dark mode theme toggle
- Extended hash algorithm support (MD5, SHA-256, SHA-512, bcrypt)
- Real TOTP integration via QR code generation
- Additional cipher implementations: Playfair, Vigenere, AES

---

## Team

| Name | Roll Number |
|---|-------------|
| Tanishq Shivasharan | 3533        |
| Aryan Jakkal | 3558        |
| Dhairyashil Sarwade | 3583        |

---

## License

This project is licensed under the **MIT License**.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is provided to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

---

*Made for MSBTE Micro Project Submission — Empowering students with practical cybersecurity skills.*
