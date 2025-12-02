# Contributing to NewBank

Thank you for contributing to **NewBank**!  
This guide explains our workflow, branching strategy, coding standards.

---

# 🚀 Workflow

We keep things straightforward:

- **All feature branches must be created from `develop`.**
- `main` is reserved for stable releases.

## 1️⃣ Clone the Repository

```bash
git clone https://github.com/fiovino4/newbank-team-nova.git
cd newbank-team-nova
```

## 2️⃣ Switch to the `develop` Branch

```bash
git checkout develop
git pull
```


### 3️⃣ Create a feature branch from `develop`

```bash
git checkout -b feature/<short-description>
```

Examples:

- `feature/add-paybill-command`
- `feature/loan-validation`
- `feature/cli-ux-improvements`

or
- 
- `feature/<your_initials>/<short-description>`

Do **all** your work for that change in this feature branch.

---

# 🧱 Project Architecture Overview

```
src/
 ├── newbank/
 │    ├── server/
 │    │     ├── NewBank.java
 │    │     ├── NewBankServer.java
 │    │     ├── NewBankClientHandler.java
 │    │     ├── CommandProcessor.java
 │    │     └── security/
 │    │            └── PasswordManagerService.java
 │    └── client/
 │          ├── ClientApp.java
 │          ├── ClientConnection.java
 │          ├── ConsoleUI.java
 │          ├── CommandParser.java
 │          └── ParsedCommand.java
```

---

# ⭐ Adding a New Command





You must update **four layers**:

```
Client Input
  → CommandParser
  → ConsoleUI - (interactive mode)
  → Server  - CommandProcessor
```

Below is the *complete, correct example*.

---

# 1️⃣ Client - CommandParser
File: `src/newbank/client/CommandParser.java`

This ensures the command name and argument count are valid.

```java
    private int expectedArgumentCount(String name) {
    switch (name) {
        // General / existing commands
        case "HELP": return 0;
        case "SHOWMYACCOUNTS": return 0;


        private String usageFor(String name) {
            switch (name) {
                case "HELP":
                    return "HELP";
                case "SHOWMYACCOUNTS":
                    return "SHOWMYACCOUNTS";


                private String buildHelpMessage() {
                    return String.join("\n",
                            "Available commands:",
                            "  SHOWMYACCOUNTS",
                            "  BALANCE",
                            "  CREATEACCOUNT <accountName>",
```

---

# 2️⃣ Client - ConsoleUI (Interactive Mode)
File: `src/newbank/client/ConsoleUI.java`

If the user types only `CREATEACCOUNT`, the UI triggers a multi‑step flow:

### (A) The interactive builder

```java
private String buildInteractiveCreateAccountCommand() throws IOException {
    String name = promptForOrExit(
            "name for the new account",
            "e.g. Savings"
    );
    if (name == null) {
        return cancelCommand("CREATEACCOUNT");
    }

    String trimmedName = name.trim();
    if (trimmedName.isEmpty()) {
        System.out.println("Account name cannot be empty.");
        return cancelCommand("CREATEACCOUNT");
    }

    boolean confirmed = confirmYesOrExit(
            "Confirm a new account with the name '" + trimmedName +
            "' by typing YES (or EXIT to cancel):"
    );

    if (!confirmed) {
        return cancelCommand("CREATEACCOUNT");
    }

    return "CREATEACCOUNT " + trimmedName;
}
```

### (B) Triggering the interactive flow

Add this inside the input loop:

```java
if (trimmed.equalsIgnoreCase("CREATEACCOUNT")) {
    input = buildInteractiveCreateAccountCommand();
}
```

This enables:

```
> CREATEACCOUNT
Enter name for the new account (e.g. Savings):
> Holiday
Confirm a new account with the name 'Holiday' by typing YES (or EXIT to cancel):
> YES
```

---

# 3️⃣ Server - CommandProcessor
File: `src/newbank/server/CommandProcessor.java`

Add the command to the switch:

```java
case "CREATEACCOUNT":
    if (args.size() != 1) {
        return "Usage: CREATEACCOUNT <accountName>";
    }
    return bank.createAccount(customer, args.get(0));
```

This forwards the work to **NewBank**.

---


---

# 🧪 Tests

Suggested location:

```
src/test/java/newbank/
```

Recommended tests:

- CommandParser: bad args, correct args
- NewBank: account creation rules

---

# 📦 Submitting Your Contribution

## 1️⃣ Commit Your Work

## 2️⃣ Push your branch

## 3️⃣ Open a PR into `develop`

Your PR description must include:

- Summary of changes
- Commands added
- Trello card or user story link

---

# 📜 Updating CHANGELOG.md

Each PR must add entries under:

```
## [Unreleased]
### Added
### Changed
### Fixed
```
