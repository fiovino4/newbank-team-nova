package newbank.server;

import newbank.server.CommandParser;
import newbank.server.ParsedCommand;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTest {

    private final CommandParser parser = new CommandParser();

    @Test
    public void shouldParseValidCommandWithNoArguments() {
        ParsedCommand cmd = parser.parse("HELP");

        assertNotNull(cmd);
        assertTrue(cmd.isValid(), "HELP should be valid");
        assertEquals("HELP", cmd.getName(), "Command name should be HELP");
        assertTrue(cmd.getArguments().isEmpty(), "HELP should have no arguments");
    }
    // Verifies that a command with no arguments is parsed correctly.

    @Test
    public void shouldParseValidCommandWithArguments() {
        ParsedCommand cmd = parser.parse("CREATEACCOUNT Savings");

        assertNotNull(cmd);
        assertTrue(cmd.isValid(), "CREATEACCOUNT with one argument should be valid");
        assertEquals("CREATEACCOUNT", cmd.getName(), "Command name should be CREATEACCOUNT");
        assertEquals(1, cmd.getArguments().size(), "CREATEACCOUNT should have exactly one argument");
        assertEquals("Savings", cmd.getArguments().get(0), "First argument should be 'Savings'");
    }
    // Checks that a command with arguments is parsed correctly.

    @Test
    public void shouldRejectCommandWithWrongArgumentCount() {
        ParsedCommand cmd = parser.parse("CREATEACCOUNT");

        assertNotNull(cmd);
        assertFalse(cmd.isValid(), "CREATEACCOUNT with no arguments should be invalid");
        assertNotNull(cmd.getMessage(), "Invalid command should include a message");
        assertTrue(cmd.getMessage().contains("Invalid number of arguments"),
                "Error message should mention invalid number of arguments");
    }
    // Ensures that commands with incorrect argument counts are rejected.

    @Test
    public void shouldRejectEmptyInput() {
        ParsedCommand cmd = parser.parse("");

        assertNotNull(cmd);
        assertFalse(cmd.isValid(), "Empty input should be invalid");
        assertNotNull(cmd.getMessage(), "Invalid command should include a message");
        assertTrue(cmd.getMessage().contains("Please enter a command"),
                "Error message should ask user to enter a command");
    }
    // Verifies that empty input is handled as an invalid command.

    @Test
    public void shouldParseUnknownCommandAsValid() {
        ParsedCommand cmd = parser.parse("FOOBAR arg1 arg2");

        assertNotNull(cmd);
        assertTrue(cmd.isValid(), "Unknown commands should still be parsed as valid");
        assertEquals("FOOBAR", cmd.getName(), "Command name should be FOOBAR");
        List<String> args = cmd.getArguments();
        assertEquals(2, args.size(), "FOOBAR should have two arguments");
        assertEquals("arg1", args.get(0));
        assertEquals("arg2", args.get(1));
    }
    // Checks that unknown commands are treated as valid with arguments.

    @Test
    public void shouldTrimWhitespaceAroundInput() {
        ParsedCommand cmd = parser.parse("   HELP   ");

        assertNotNull(cmd);
        assertTrue(cmd.isValid(), "HELP with extra spaces should still be valid");
        assertEquals("HELP", cmd.getName());
        assertTrue(cmd.getArguments().isEmpty());
    }
    // Ensures leading/trailing spaces do not break parsing.

    @Test
    public void shouldNormaliseCommandCaseToUppercase() {
        ParsedCommand cmd = parser.parse("createaccount Savings");

        assertNotNull(cmd);
        assertTrue(cmd.isValid(), "Lowercase command should still be valid");
        assertEquals("CREATEACCOUNT", cmd.getName(), "Command name should be normalised to uppercase");
        assertEquals(1, cmd.getArguments().size());
        assertEquals("Savings", cmd.getArguments().get(0));
    }
    // Ensures command names are case-insensitive but stored in a normalised form.
}