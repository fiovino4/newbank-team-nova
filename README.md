# NewBank – CLI Banking Application 🚀

NewBank is a lightweight educational banking platform built using a **client/server architecture** and a **command-line interface (CLI)**.  
It demonstrates key software engineering concepts:

- Clean console-based UX
- Command parsing and validation
- Networked client/server communication
- Secure password hashing (PBKDF2)
- Multi-step guided workflows
- Consistent, intuitive command structure

This version includes improvements from:

- **CLEAN-401 – Improve User Prompts and Instructions**
- **CLI-201 – Intuitive Command Structure**

---

# 📦 Project Structure

```
newbank-team-nova/
├── src/
│   └── newbank/
│       ├── client/
│       │   ├── ClientApp.java
│       │   ├── ClientConnection.java
│       │   ├── CommandParser.java
│       │   ├── ConsoleUI.java
│       │   ├── NetworkClient.java
│       │   └── ParsedCommand.java
│       │
│       ├── server/
│       │   ├── NewBank.java
│       │   ├── NewBankClientHandler.java
│       │   ├── NewBankServer.java
│       │   ├── CommandProcessor.java
│       │   │
│       │   ├── model/
│       │   │   ├── Account.java
│       │   │   ├── Customer.java
│       │   │   ├── CustomerID.java
│       │   │   ├── Loan.java
│       │   │   ├── LoanStatus.java
│       │   │   └── Notification.java
│       │   │
│       │   ├── service/
│       │   │   ├── AccountService.java
│       │   │   ├── CustomerService.java
│       │   │   ├── LoanService.java
│       │   │   ├── NotificationService.java
│       │   │   └── security/
│       │   │       └── PasswordManagerService.java
│       │
│       └── tests/
│           ├── AccountTest.java
│           ├── CommandParserTest.java
│           ├── CustomerTest.java
│           ├── LoanServiceTest.java
│           ├── NewBankClientHandlerTest.java
│           ├── NewBankLoginTest.java
│           └── NewBankTest.java
│
├── README.md
├── CONTRIBUTING.md
└── CHANGELOG.md
```

---

# 🧰 Requirements
- Java **17+**
- Terminal

---

# ⚙️ How to Run

### **1️⃣ Start the Server**
```bash
java -cp out newbank.server.NewBankServer
```

Output:
```
New Bank Server listening on 14002
```

### **2️⃣ Start the Client**
```bash
java -cp out newbank.client.ClientApp
```

---

# 🔐 Login Flow

```
Enter Username (case-sensitive):
> Test
Enter Password (case-sensitive):
> Test
Checking Details...
Log In Successful. Welcome Test!
```

---

# 🧭 Using the CLI

NewBank supports **two usage styles**:

---

## **1️⃣ Direct Mode (Full Commands)**

Examples:

### View balances
```
> BALANCE
Main: 1000.0
Savings: 1000.0
Bonds: 1000.0
```

### Create account
```
> CREATEACCOUNT Holiday
```

### Transfer
```
> TRANSFER Main Savings 100.00
```

---

## **2️⃣ Interactive Mode (Step‑by‑Step Prompts)**

Type:
```
TRANSFER
CREATEACCOUNT
REQUESTLOAN
OFFERLOAN
```

### Example: TRANSFER
```
> TRANSFER
Enter source account name:
> Main
Enter destination account name:
> Savings
Enter amount:
> 25
Confirm transfer? (YES / EXIT):
> YES
```

### Example: CREATEACCOUNT
```
> CREATEACCOUNT
Enter new account name:
> Holiday
Confirm 'Holiday'? (YES / EXIT):
> YES
```

---

# 📚 Command Reference

| Command | Description |
|--------|------------|
| `BALANCE` / `SHOWMYACCOUNTS` | Show all accounts |
| `CREATEACCOUNT <name>` | Create account |
| `CLOSEACCOUNT <name>` | Close account |
| `TRANSFER <from> <to> <amount>` | Move funds |
| `VIEWTRANSACTIONS <name>` | View history *(in development)* |
| `OFFERLOAN <from> <amount> <rate> <term>` | Offer loan |
| `REQUESTLOAN <to> <amount> <maxRate> <term>` | Request loan *(in development)* |
| `SHOWAVAILABLELOANS` | View active loans |
| `ACCEPTLOAN <id>` | Accept loan *(in development)* |
| `MYLOANS` | Show user loans |
| `REPAYLOAN <id> <amount>` | Repay loan *(in development)* |
| `HELP` | Show help |
| `LOGOUT` / `EXIT` / `QUIT` | End session |

---

# 🧱 Architecture

```
                         +----------------------------+
                         |         ClientApp          |
                         |         (main entry)       |
                         +-------------+--------------+
                                       |
                                       v
                         +----------------------------+
                         |          ConsoleUI         |
                         |  (login / prompts / flows) |
                         +-------------+--------------+
                                       |
                                       v
                     +-----------------+-----------------+
                     |        CommandParser              |
                     |        ParsedCommand              |
                     | (parse + validate user commands)  |
                     +-----------------+-----------------+
                                       |
                                       v
                         +----------------------------+
                         |    NetworkClient /         |
                         |    ClientConnection        |
                         | (socket connect + I/O)     |
                         +-------------+--------------+
                                       |
                              Socket connection
                                       |
                                       v
+--------------------------------------------------------------------+
|                           NewBankServer                            |
|--------------------------------------------------------------------|
|  Listens on port → accepts Socket → spawns NewBankClientHandler    |
+-------------------------------+------------------------------------+
                                |
                                v
                     +----------------------------+
                     |     NewBankClientHandler   |
                     |   (login + request loop)   |
                     +-------------+--------------+
                                       |
                                       v
                             +-------------------+
                             |  CommandProcessor |
                             | (server commands) |
                             +---------+---------+
                                       |
                                       v
                               +---------------+
                               |    NewBank    |
                               |  (facade API) |
                               +---+-----+-----+
                                   |     |
        +--------------------------+     +--------------------------+
        v                                                    v
+------------------------+                     +------------------------+
|    CustomerService     |                     |    AccountService      |
| - register / authenticate                  |  - create / close       |
| - lookup Customer                          |  - deposit / withdraw   |
| - hasCustomer                              |  - transfer             |
+------------------------+                     +------------------------+
        |
        v
+------------------------+
| PasswordManagerService |
| (hash + verify pw)     |
+------------------------+

        +------------------------+          +---------------------------+
        |      LoanService       |          |   NotificationService     |
        | - offer / list loans   |          | - create notifications    |
        | - validate accounts    |          | - (future delivery, etc.) |
        +------------+-----------+          +-------------+-------------+
                     |                                      |
                     v                                      v
        +---------------------------+        +---------------------------+
        |        model.*            |        |          model.*          |
        |  Account, Customer,       |        |  Notification, CustomerID |
        |  Loan, LoanStatus         |        |                           |
        +---------------------------+        +---------------------------+
```

---

# 🔒 Security

- PBKDF2WithHmacSHA256
- 65,536 iterations
- 16-byte random salt
- Base64-encoded hash + salt
- No plaintext passwords stored

---

# 📄 License
No license Requirements 