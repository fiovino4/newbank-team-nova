# Changelog 📜

All notable changes to the NewBank project are documented here.

---
## [1.1.2] - 
### Added
### Changed
### Fixed

## [1.1.1] -Loan Marketplace + Core Banking Enhancements
### Added
-Loan marketplace system
    -Loan data model with auto-generated IDs.
    -OFFERLOAN command for creating micro-loan offers.
    -SHOWAVAILABLELOANS displays all open loan offers.
    -MYLOANS shows loans created by the logged-in user.

-Account management utilities
    -Added Customer.getAccount, hasAccount, removeAccount.
    -Expanded Account model with balance, getters/setters, and formatted output.

### Changed
-Improved CommandProcessor:
    -Added scoped case blocks to eliminate duplicate variable issues.
    -Added argument validation and clearer error messages.
    -Updated HELP output to include loan-related commands.

### Fixed
-SHOWMYACCOUNTS server crash
    -Replaced invalid StringBuilder.isEmpty() with length check.
    -Improved handling of unknown customers in NewBank.showMyAccounts.

### Implemented
-CREATEACCOUNT
    -Prevents duplicates.
    -Uses NewBank.createAccount for consistent logic.

-TRANSFER
    -Full intra-customer transfer system.
    -Validates:
    -both accounts exist,
    -amount is numeric and positive,
    -sufficient funds.
    -Updates balances via Account.setBalance.

-CLOSEACCOUNT
-Ensures:
    -customer exists,
    -account exists,
    -balance must be zero before closing.
    -Removes account cleanly from customer profile.

---

## [1.1.0] – UX & Command Improvements (2025-12-02)

### Added
- Multi-step interactive flows for:
    - `TRANSFER`
    - `CREATEACCOUNT`
    - `OFFERLOAN`
    - `REQUESTLOAN`
- Support for `EXIT` to cancel workflows.
- Confirmation using `YES` for sensitive actions.
- Updated `HELP` command to explain:
    - Direct command mode
    - Interactive multi-step mode
- New consolidated command table with “(in development)” status markers.
- Improved login + command prompts per CLEAN-401 requirements.

### Changed
- `BALANCE` / `SHOWMYACCOUNTS` now show all accounts at once.
- Server responses standardised for clarity.
- Help text output now ends with `END_OF_HELP` for client parsing.
- Improved readability & structure of README.md.

### Fixed
- Missing prompts after HELP output.
- Duplicate login messages in ConsoleUI.

---

## [0.9.0] – Client Command Parsing & Project Restructure (PR #5)

### Added
- Full **CommandParser** implementation:
    - Case-insensitive command names.
    - Command + argument splitting.
    - Argument-count validation for all supported commands.
    - Clear error messages for invalid or incomplete input.
- `ParsedCommand` class representing parsed commands with:
    - Valid/invalid state
    - Argument list
    - Error message routing
- Unknown command detection on the client:
    - Shows: *“Unknown command: X. Type HELP for a list of commands.”*
- ConsoleUI now:
    - Uses the parser for *all* commands after login.
    - Blocks invalid commands from reaching the server.
    - Normalises commands to uppercase before sending.
    - Displays consistent error messages.

### Added – Command Support
Parser now supports:
- `HELP`
- `SHOWMYACCOUNTS`
- `CREATEACCOUNT <name>`
- `CLOSEACCOUNT <name>`
- `TRANSFER <from> <to> <amount>`
- `VIEWTRANSACTIONS <account>`
- `OFFERLOAN <from> <amount> <rate> <term>`
- `REQUESTLOAN <to> <amount> <maxRate> <term>`
- `SHOWAVAILABLELOANS`
- `ACCEPTLOAN <loanId> <to>`
- `MYLOANS`
- `REPAYLOAN <loanId> <amount>`
- Initial password hashing service:
    - PBKDF2WithHmacSHA256
    - 65,536 iterations
    - 16-byte random salt

### Changed
- Replaced outdated ExampleClient with:
    - `ClientApp`
    - `ConsoleUI`
    - `ClientConnection`
- Restructured entire project into a proper `src/` layout.
- Improved file naming and project organisation for maintainability.

### Notes
- Work linked to Trello cards:
    - *Parse user input commands*
    - *Display “command not recognised”*

---

## [1.0.0] – Initial Release

### Added
- Basic client/server connection using sockets.
- Initial command set:
    - `BALANCE`
    - `SHOWMYACCOUNTS`
    - `TRANSFER` (placeholder)
    - `LOGIN` flow (username + password)
- Starter README.


## [2.0.0] – Loan Module Refactor 

### Overview

This refactor introduces a clear separation between the Loan domain model and the LoanService business logic. The redesign enforces a cleaner architecture by isolating data representation from business rules, making the loan subsystem more maintainable, testable, and extensible.

1. Introduction of model/loan Package (Domain Layer)

    The model/loan folder now contains all domain entities related to loans.

    Loan

  Rewritten as a pure data model.
  
  Contains only fields, getters, and a string representation.
  
  No validation or business logic remains inside this class.
  
  Fields include:
  
  id
  
  lender (CustomerID)
  
  fromAccount
  
  amount
  
  interestRate
  
  termMonths
  
  extraTerms
  
  loanStatus (LoanStatus)
  
  This ensures that the model is immutable with respect to business rules, and all logic is delegated to the service layer.
  
  LoanStatus
  
  A dedicated enum defining the lifecycle states of a loan:
  
  AVAILABLE
  
  REQUESTED
  
  ACTIVE
  
  REPAID
  
  CANCELLED

2. Addition of LoanService (Service Layer)
    
    A dedicated service class has been created to handle all loan-related business operations.
    
    Responsibilities
    
    Validate loan creation requests.
    
    Verify lender identity and account ownership.
    
    Ensure sufficient account balance.
    
    Enforce positive values for amount, interest rate, and term.
    
    Generate unique loan IDs using AtomicInteger.
    
    Store loans in an internal Map (Map<Integer, Loan>).
    
    Manage loan state transitions and expose query methods.
    
    The method offerLoan now performs all validation and constructs domain model objects only after ensuring the request is valid.

2.Architectural Separation of Concerns
    
   The refactor establishes a clearer boundary between different layers:
    
   Layer	Responsibility
   model.loan	Domain entities; data only, no logic
   service.LoanService	Business logic, validation, state management
   NewBank	Orchestration and dependency wiring
   CommandProcessor	Parsing client commands and returning formatted responses


3.Recap of Architectural Separation of Concerns

| Layer               | Responsibility                                            |
| ------------------- | --------------------------------------------------------- |
| model.loan          | Domain entities; data only, no logic                      |
| service.LoanService | Business logic, validation, state management              |
| NewBank             | Orchestration and dependency wiring                       |
| CommandProcessor    | Parsing client commands and returning formatted responses |



4. Improved Query Methods

Two methods now provide structured output for loan listings:

showUserLoan

Returns a formatted, multi-line list of all loans created by the logged-in customer.

showAvailableLoans

Returns a formatted, multi-line list of all loans currently in the AVAILABLE state.

Both methods now provide consistent, readable output and do not perform any business mutations.

5. Test Cases, in tests

Added Account Test:
Tests for negative balance
Checks when account opens it is empty


Added CommandParser Test:
Verifies that a command with no arguments is parsed correctly.
Checks that a command with arguments is parsed correctly.
Verifies that empty input is handled as an invalid command.
Checks that unknown commands are treated as valid with arguments.
Ensures leading/trailing spaces do not break parsing.
Ensures command names are case-insensitive but stored in a normalised form.
Verifies that blank input is handled as an invalid command.
Ensures that invalid commands do not crash the system.

Added Customer Test
Verifies that checkPassword returns true for the original password.
Verifies that checkPassword returns false for an incorrect password.

Added NewBankClientHandler Test
This test opens a ServerSocket, connects a client Socket, accepts it and hands the accepted socket to NewBankClientHandler. The client socket is closed immediately to simulate an intentional disconnect. The test then waits for the handler thread to finish and asserts it terminates without crashing.

Addded NewBank Test:
erifies that NewBank follows the singleton pattern.
Checks that hasCustomer reports true for a known customer.
Checks that valid login details return a CustomerID.
Ensures that invalid login details return null.
Verifies that SHOWMYACCOUNTS returns the correct account information for a known customer.
Verifies successful login with correct credentials.

LoanService Test:
Verifies that offering a negative loan amount is rejected and does not alter the loan marketplace.

