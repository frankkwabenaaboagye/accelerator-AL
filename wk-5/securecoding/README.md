# Secure E-Banking Application: Secure Coding and Cryptography

## Overview
This project demonstrates secure coding practices and cryptographic techniques in a sample Java e-banking application. The goal is to mitigate common security vulnerabilities and protect sensitive user data, such as account information and financial transactions.

## Scenario
You are developing a secure e-banking application for a financial institution. Security is paramount to protect sensitive user data and financial transactions.

## Exercises
### A. Secure Coding Fundamentals
- **Common Vulnerabilities Addressed:**
  - SQL Injection
  - Cross-Site Scripting (XSS)
- **Secure Coding Practices:**
  - Input validation and sanitization
  - Use of prepared statements for database access
  - Output encoding to prevent XSS
  - Proper error handling
  - Principle of least privilege

### B. Securing User Data with Cryptography
- **Cryptographic Concepts:**
  - Encryption and decryption of sensitive data
  - Use of strong cryptographic libraries (e.g., BouncyCastle)
- **Application:**
  - Encrypting user account information and transactions
  - Secure password storage

## Technologies Used
- Java 17+
- Maven
- BouncyCastle (for cryptography)
- JUnit (for testing)

## Project Structure
```
securecoding/
  ├── pom.xml
  ├── README.md
  └── src/
      ├── main/
      │   ├── java/
      │   │   └── (application code)
      │   └── resources/
      └── test/
          └── java/
```

## Setup Instructions
1. **Clone the repository:**
   ```sh
   git clone <repo-url>
   cd securecoding
   ```
2. **Build the project:**
   ```sh
   mvn clean install
   ```
3. **Run the application:**
   ```sh
   mvn exec:java -Dexec.mainClass="com.securebank.Main"
   ```
4. **Run tests:**
   ```sh
   mvn test
   ```

## Secure Coding and Cryptography References
- [OWASP Top Ten](https://owasp.org/www-project-top-ten/) (Open Worldwide Application Security Project Top Ten)
- [BouncyCastle Java API](https://www.bouncycastle.org/java.html)
- [Java Secure Coding Guidelines](https://www.oracle.com/java/technologies/javase/seccodeguide.html)

## Implemented Features
- **Input Validation & XSS Prevention:**
  - `InputValidator` class for username validation and HTML sanitization.
- **Secure Database Access:**
  - `DatabaseHelper` demonstrates use of prepared statements to prevent SQL injection.
- **Cryptography:**
  - `CryptoUtils` class for AES encryption/decryption using BouncyCastle.
- **Testing:**
  - JUnit tests for input validation and cryptography utilities.

## Usage
- The main entry point is `com.securebank.Main`.
- To run tests:
  ```sh
  mvn test
  ```
- To build and run the application:
  ```sh
  mvn clean install
  mvn exec:java -Dexec.mainClass="com.securebank.Main"
  ```

## Security Notes & Code Explanations

### 1. SQL Injection
**What is it?**
SQL Injection (Structured Query Language Injection) is a vulnerability that allows attackers to manipulate Structured Query Language (SQL) queries by injecting malicious input, potentially exposing or corrupting sensitive data.

**How this project prevents it:**
- The `DatabaseHelper` class uses **prepared statements** (see `fetchUserByUsername`) to safely insert user input into Structured Query Language (SQL) queries. This ensures user data is treated as a value, not executable code.
- **Code Example:**
  ```java
  String sql = "SELECT * FROM users WHERE username = ?";
  try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      // ...
  }
  ```
- **Why it works:** Prepared statements separate SQL logic from data, so even if a user enters SQL keywords, they are not executed as part of the query.

### 2. Cross-Site Scripting (XSS)
**What is it?**
Cross-Site Scripting (XSS) allows attackers to inject malicious scripts into web pages viewed by other users, potentially stealing data or hijacking sessions.

**How this project prevents it:**
- The `InputValidator` class provides a `sanitizeForHtml` method that escapes HyperText Markup Language (HTML) special characters, neutralizing scripts in user input.
- **Code Example:**
  ```java
  public static String sanitizeForHtml(String input) {
      if (input == null) return null;
      return input.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#x27;");
  }
  ```
- **Why it works:** By converting special characters to their HTML entities, any embedded scripts are rendered harmless in the browser.

### 3. Input Validation
**Why it's important:**
- Prevents malformed or malicious data from entering the system.
- The `isValidUsername` method in `InputValidator` only allows alphanumeric usernames (plus underscores) of 3-20 characters.
- **Code Example:**
  ```java
  public static boolean isValidUsername(String username) {
      return username != null && username.matches("^[a-zA-Z0-9_]{3,20}$");
  }
  ```
  (Only letters, numbers, and underscores are allowed. No special characters or spaces.)

### 4. Cryptography
**Why it's important:**
- Protects sensitive data (e.g., account info, transactions) from unauthorized access.

**How this project implements it:**
- The `CryptoUtils` class uses **Advanced Encryption Standard in Galois/Counter Mode (AES-GCM)** (a modern, secure encryption mode) with the BouncyCastle provider.
- **Key points:**
  - Random 256-bit Advanced Encryption Standard (AES) keys are generated for strong encryption.
  - Initialization vectors (IVs) (random values used to ensure uniqueness for each encryption) are used for each encryption to ensure uniqueness.
  - Data is encrypted and encoded in Base64 (a binary-to-text encoding scheme) for safe storage/transmission.
- **Code Example:**
  ```java
  SecretKey key = CryptoUtils.generateAESKey();
  byte[] iv = new byte[12];
  new SecureRandom().nextBytes(iv);
  String encrypted = CryptoUtils.encrypt("Sensitive data", key, iv);
  String decrypted = CryptoUtils.decrypt(encrypted, key, iv);
  ```
- **Why it works:** Advanced Encryption Standard in Galois/Counter Mode (AES-GCM) provides both confidentiality and integrity. BouncyCastle is a trusted cryptography provider.

### 5. Testing Security Features
- JUnit (Java Unit) tests (`InputValidatorTest`, `CryptoUtilsTest`) ensure that input validation, sanitization, and cryptography work as expected.
- **Example:**
  - XSS test checks that `<script>` tags are neutralized.
  - Crypto test checks that encrypted data can be decrypted back to the original.

---
This project is for educational purposes and demonstrates best practices for secure coding and cryptography in Java. 