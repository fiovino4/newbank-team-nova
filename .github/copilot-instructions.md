# NewBank Team Nova - AI Coding Agent Instructions

## Architecture Overview

**NewBank** is a client-server banking application in Java demonstrating multi-threaded socket communication. The system consists of:

- **Server** (`src/newbank/server/`): Multi-threaded socket server that handles authentication and banking operations
  - `NewBankServer`: Accepts socket connections on port 14002, spawns a handler thread per client
  - `NewBankClientHandler`: Per-connection thread handling login loop and session management
  - `CommandProcessor`: Parses and routes banking commands (HELP, SHOWMYACCOUNTS, CREATEACCOUNT, TRANSFER, LOGOUT)
  - `NewBank`: Singleton managing customer authentication and account operations
  - `Customer` / `Account` / `CustomerID`: Domain models for accounts and customer identity

- **Client** (`src/newbank/client/`): Console-based client connecting via socket
  - `ClientConnection`: Wraps socket I/O for server communication
  - `ConsoleUI`: Handles login loop and interactive command input
  - `CommandParser`: Validates user input before sending to server

## Key Architectural Patterns

### Client-Server Protocol
Communication is text-based over TCP sockets (port 14002):
1. Server prompts for username/password in a login loop
2. After successful authentication, server returns a `CustomerID` token (wrapper around username string)
3. Client sends commands as space-delimited strings (e.g., `SHOWMYACCOUNTS`, `CREATEACCOUNT Savings`)
4. Server processes via `CommandProcessor` and returns text responses
5. Client exits when server responds with "Session terminated"

**Example command flow**: Client sends `"SHOWMYACCOUNTS"` → Server's `CommandProcessor` calls `bank.showMyAccounts(customer)` → Returns formatted account list

### State Management
- **Server-side**: `NewBankClientHandler` maintains session in a single `while(true)` loop; each connection is stateless once authenticated
- **Multi-threading**: Each client connection runs on its own thread; use `synchronized` on `NewBank.checkLogInDetails()` for thread safety
- **Singleton pattern**: `NewBank.getBank()` returns the shared bank instance across all handler threads

### Sensitive Operations Pattern
See `starter-original-code/SimplifiedNewBankClientHandler.java` for the confirmation pattern:
- Some commands (e.g., `NEWACCOUNT`) require explicit user confirmation
- Implement by tracking `awaitingConfirmation` flag in handler; intercept commands before delegating to `CommandProcessor`
- Route only YES/EXIT responses during confirmation; other input prompts retry

## Development Workflow

### Compilation & Execution
- **No build tool** (no Maven/Gradle); compile with `javac` or IDE
- Server entry point: `java newbank.server.NewBankServer` (listens on port 14002)
- Client entry point: `java newbank.client.ClientApp` (connects to localhost:14002)
- Test credentials in `NewBank.addTestData()`: 
  - Bhagy/1234, Christina/abcd, John/pass, Test/Test

### Adding New Commands
1. Add case in `CommandProcessor.process()` to handle the command name
2. Extract and validate arguments; return error messages for invalid syntax
3. Call appropriate `NewBank` or `Customer` method
4. Return response as formatted String (already set up for socket transmission)

**Example**: Adding TRANSFER command
```java
case "TRANSFER":
    if (args.size() != 3) return "Usage: TRANSFER <fromAccount> <toAccount> <amount>";
    // Validate and implement
    return bank.transfer(customer, args.get(0), args.get(1), Double.parseDouble(args.get(2)));
```

### Design Considerations for New Features
- **Thread safety**: `NewBank` singleton is accessed by multiple handler threads; use `synchronized` for shared state modifications
- **Confirmations**: Extend `SimplifiedNewBankClientHandler` pattern; do NOT create new state classes unless essential
- **Response format**: All responses are single Strings (line-based); no multi-line responses from `CommandProcessor`
- **Error handling**: Return "FAIL: <reason>" for failures; clients check response prefix to determine success

## Project Structure Notes

- `src/newbank/` — Production code (client & server implementations)
- `starter-original-code/` — Reference implementation with simplified confirmation pattern
- `docs/PROTOCOL.md` — (placeholder, not yet populated)
- Test data populated in `NewBank.addTestData()` on singleton initialization

## Common Pitfalls

- **Socket blocking**: `readLine()` blocks until newline received; always send complete commands
- **Thread safety**: Multiple handlers share single `NewBank` instance; synchronize bank methods if adding state changes
- **Command parsing**: `CommandProcessor` splits on whitespace; account names with spaces are not supported
- **Client disconnection**: Check for `null` from `readLine()` to detect client disconnect; gracefully close resources in try-finally
