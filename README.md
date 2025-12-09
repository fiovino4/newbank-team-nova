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
 │   ├── newbank/client/
 │   ├── newbank/server/
 │   ├── newbank/server/security
 ├── README.md
 ├── CONTRIBUTING.md
 ├── CHANGELOG.md
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
|--------|-------------|
| `BALANCE` / `SHOWMYACCOUNTS` | Show all accounts |
| `CREATEACCOUNT <name>` | Create account *(in development)* |
| `CLOSEACCOUNT <name>` | Close account *(in development)* |
| `TRANSFER <from> <to> <amount>` | Move funds |
| `VIEWTRANSACTIONS <name>` | View history *(in development)* |
| `OFFERLOAN <from> <amount> <rate> <term>` | Offer loan *(in development)* |
| `REQUESTLOAN <to> <amount> <maxRate> <term>` | Request loan *(in development)* |
| `SHOWAVAILABLELOANS` | View active loans *(in development)* |
| `ACCEPTLOAN <id>` | Accept loan *(in development)* |
| `MYLOANS` | Show user loans *(in development)* |
| `REPAYLOAN <id> <amount>` | Repay loan *(in development)* |
| `HELP` | Show help |
| `LOGOUT` / `EXIT` / `QUIT` | End session |

---

# 🧱 Architecture

```
           +----------------------------+
           |         ClientApp          |
           |  (User input + UI logic)   |
           +-------------+--------------+
                         |
                         v
              ConsoleUI + CommandParser
                         |
                         v   Socket Connection
+----------------------------------------------------------+
|                    NewBankServer                         |
|----------------------------------------------------------|
|   Accepts connections → Creates NewBankClientHandler     |
+-----------------------+----------------------------------+
                        |
                        v
              +-------------------------+
              | NewBankClientHandler    |
              | (login + request loop)  |
              +------------+------------+
                           |
                           v
                 +------------------+
                 | CommandProcessor |
                 +------------------+
                           |
                           v
                +----------------------+
                |      NewBank         |
                | Accounts, Customers  |
                +----------------------+
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