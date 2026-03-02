================================================================================
                            VIGIL
          CryptoLab & Security Dashboard — A Browser-Based Toolkit
================================================================================

PROJECT INFORMATION
-------------------
Name:           VIGLE
Type:           Micro Project Submission
Course:         Diploma in Computer Engineering (Semester VI)
Board:          Maharashtra State Board of Technical Education (MSBTE)
Scheme:         K-Scheme
Subjects:       Network and Information Security (NIS) — 316317
                Mobile Application Development (MAD) — 316006
Difficulty:     Medium

================================================================================

DESCRIPTION
-----------

VIGIL is a comprehensive offline cybersecurity toolkit designed for
educational purposes and practical security analysis. It runs entirely in the
browser without requiring any server, npm packages, or installation procedures.

This application serves as the web companion to the VIGIL Android app
(MAD project), together forming a complete dual-platform personal cybersecurity
ecosystem. Simply open index.html in any modern browser to begin.

================================================================================

SCREENSHOTS
-----------

Landing Page:           screenshots/index.png
CryptoLab:              screenshots/cryptolab.png
Steganography Tool:     screenshots/steganography.png
Password Audit Tool:    screenshots/password-audit.png
Security Dashboard:     screenshots/dashboard.png
Email Header Analyzer:  screenshots/email-analyzer.png

================================================================================

FEATURES
--------

1. LANDING PAGE (index.html)
   - Hero section with project overview and call-to-action
   - Live statistics bar displaying tool capabilities
   - Quick navigation to all five security tools

2. CRYPTOLAB (cryptolab.html)
   
   Caesar Cipher
   - Encrypt and decrypt text using shift-based substitution
   - Step-by-step letter shift visualization table
   
   Vernam Cipher
   - XOR-based encryption with one-time pad
   - Interactive XOR truth table visualization
   
   Rail Fence Cipher
   - Zigzag pattern transposition cipher
   - Visual grid showing encryption/decryption process
   
   Columnar Transposition
   - Keyword-based columnar cipher
   - Column order visualization with grid layout

3. STEGANOGRAPHY TOOL (steganography.html)
   - Hide secret messages inside PNG images using LSB technique
   - Extract hidden messages from stego-images
   - Pure browser-based implementation using Canvas API
   - Download stego-images directly to device

4. PASSWORD AUDIT TOOL (password-audit.html)
   - Real-time password strength meter with animated progress bar
   - Seven-point security criteria checklist
     * Minimum length requirement
     * Uppercase letter presence
     * Lowercase letter presence
     * Numeric digit presence
     * Special character presence
     * No common patterns
     * No dictionary words
   - Shannon entropy calculation in bits
   - Estimated crack time computation
   - Cryptographically secure random password generator
   - Customizable generation options
   - TOTP/MFA simulator with live 30-second countdown timer

5. SECURITY DASHBOARD (dashboard.html)
   - Import JSON data exported from VIGIL Android app
   - Summary statistics cards
     * Total Wi-Fi scans performed
     * Total devices discovered
     * Total SMS messages flagged
   - Three interactive Chart.js visualizations
     * Bar chart showing security type distribution
     * Doughnut chart displaying risk level breakdown
     * Line chart tracking devices discovered over time
   - Complete scan history table with color-coded risk badges
   - One-click PDF report generation using jsPDF

6. EMAIL HEADER ANALYZER (email-analyzer.html)
   - Paste raw email headers for instant phishing analysis
   - Parse and validate SPF, DKIM, DMARC authentication results
   - Detect Reply-To and Return-Path domain mismatches
   - Received hops timeline with internal vs external IP detection
   - Security verdict classification
     * LIKELY SAFE
     * SUSPICIOUS
     * LIKELY PHISHING

================================================================================

NIS CURRICULUM COVERAGE
------------------------

Practical No.   NIS Practical Title              VIGIL Feature
-------------   -------------------------------- ---------------------------
P2              Multi-Factor Authentication      TOTP/MFA Simulator
P4              Strong Password Utility          Password Audit + Generator
P5              Caesar Cipher                    CryptoLab — Caesar
P6              Vernam Cipher                    CryptoLab — Vernam
P7              Rail Fence Cipher                CryptoLab — Rail Fence
P8              Columnar Transposition           CryptoLab — Columnar
P9              Hash Code Generation             Password Entropy Calculator
P11             Steganography                    Steganography Tool
P15 & P16       Secure Email / Email Tracker     Email Header Analyzer
CO1–CO5         All Course Outcomes              Covered across all 6 pages

================================================================================

TECHNICAL SPECIFICATIONS
------------------------

Frontend:           HTML5, CSS3, Vanilla JavaScript (ES6+)
Styling:            CSS Custom Properties, Grid, Flexbox
Charting Library:   Chart.js (CDN) — Dashboard only
PDF Generation:     jsPDF (CDN) — Dashboard only
Image Processing:   Canvas API — Steganography
Typography:         Google Font: Inter
Framework:          None — Pure vanilla JavaScript
Dependencies:       Zero local dependencies

================================================================================

DIRECTORY STRUCTURE
-------------------

VIGIL-web/
|
|-- index.html
|-- cryptolab.html
|-- steganography.html
|-- password-audit.html
|-- dashboard.html
|-- email-analyzer.html
|
|-- css/
|   `-- style.css
|
`-- js/
    |-- caesar.js
    |-- vernam.js
    |-- railfence.js
    |-- columnar.js
    |-- stego.js
    |-- password.js
    |-- dashboard.js
    `-- email.js

================================================================================

INSTALLATION AND USAGE
----------------------

No installation required. Follow these steps:

1. Download or clone this repository to your local machine

2. Navigate to the project directory

3. Open index.html in any modern web browser
   (Tested on Chrome, Firefox, Edge, Safari)

4. All tools are immediately available and work completely offline

NOTE: The Security Dashboard page requires internet connection only for
      loading Chart.js and jsPDF libraries from CDN. All other pages
      function entirely offline.

================================================================================

ANDROID APP INTEGRATION
------------------------

VIGIL Web integrates seamlessly with the VIGIL Android application
(MAD Project — Course Code: 316006).

WORKFLOW:

1. The Android app performs security scans:
   - Wi-Fi network security analysis
   - LAN device discovery
   - SMS phishing detection

2. Scan results are exported as a JSON file from the Android app

3. Import the JSON file into the Security Dashboard page

4. Visualize all scan data with interactive charts and tables

5. Download a comprehensive PDF security report

JSON FILE FORMAT:

{
  "wifi_scans": [...],
  "lan_scans": [...],
  "sms_scans": [...]
}

This dual-platform approach provides a complete cybersecurity ecosystem:
mobile scanning combined with web-based analysis and reporting.

================================================================================

FUTURE ENHANCEMENTS
-------------------

The following features are planned for future releases:

- Dark Mode Theme
  Add theme toggle for improved user experience in low-light environments

- Extended Hash Algorithm Support
  Expand to include MD5, SHA-256, SHA-512, and bcrypt implementations

- Real TOTP Integration
  Connect with actual authenticator applications via QR code generation

- Additional Cipher Implementations
  Add Playfair, Vigenère, and modern AES encryption demonstrations

================================================================================

AUTHOR INFORMATION
------------------

Name:           [Your Name]
Roll Number:    [Your Roll Number]
Course:         Diploma in Computer Engineering (Semester VI)
Board:          Maharashtra State Board of Technical Education (MSBTE)
Scheme:         K-Scheme

Subjects Covered:
- Network and Information Security (NIS) — 316317
- Mobile Application Development (MAD) — 316006

================================================================================

LICENSE
-------

This project is licensed under the MIT License.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

================================================================================

                    Made for MSBTE Micro Project Submission
              Empowering students with practical cybersecurity skills

================================================================================
