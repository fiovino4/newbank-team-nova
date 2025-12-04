# Changelog 📜

All notable changes to the NewBank project are documented here.

---

## [1.1.1] - 
### Added
### Changed
### Fixed

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
