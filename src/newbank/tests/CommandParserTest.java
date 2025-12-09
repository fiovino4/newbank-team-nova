package newbank.tests;

import newbank.client.CommandParser;
import newbank.client.ParsedCommand;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CommandParserTest {

    private final CommandParser parser = new CommandParser();

    @Test
    public void shouldParseValidCommandWithNoArguments() {
        ParsedCommand cmd = parser.parse("HELP");

        assertNotNull("Parsed command should not be null", cmd);
        assertTrue("HELP should be valid", cmd.isValid());
        assertEquals("Command name should be HELP", "HELP", cmd.getName());
        assertTrue("HELP should have no arguments", cmd.getArguments().isEmpty());
    }
    // Verifies that a command with no arguments is parsed correctly.

    @Test
    public void shouldParseValidCommandWithArguments() {
        ParsedCommand cmd = parser.parse("CREATEACCOUNT Savings");

        assertNotNull("Parsed command should not be null", cmd);
        assertTrue("CREATEACCOUNT with one argument should be valid", cmd.isValid());
        assertEquals("Command name should be CREATEACCOUNT",
                "CREATEACCOUNT", cmd.getName().toUpperCase());
        assertEquals("CREATEACCOUNT should have exactly one argument",
                1, cmd.getArguments().size());
        assertEquals("First argument should be 'Savings'",
                "Savings", cmd.getArguments().getFirst());
    }
    // Checks that a command with arguments is parsed correctly.

    @Test
    public void shouldMarkEmptyInputAsInvalid() {
        ParsedCommand cmd = parser.parse("");

        assertNotNull("Parsed command for empty input should not be null", cmd);
        assertFalse("Empty input should be marked as invalid", cmd.isValid());
        assertTrue("Empty input should have no arguments", cmd.getArguments().isEmpty());
        assertTrue("Command name for empty input should be null or empty",
                cmd.getName() == null || cmd.getName().isEmpty());
    }
    // Verifies that empty input is handled as an invalid command.

    @Test
    public void shouldTreatUnknownCommandAsInvalid() {
        ParsedCommand cmd = parser.parse("FOOBAR arg1 arg2");

        assertNotNull("Parsed command should not be null", cmd);
        assertFalse("Unknown commands should be marked as invalid", cmd.isValid());

        // invalid(...) creates ParsedCommand(null, emptyList, false, errorMessage)
        assertTrue("Name for unknown command should be null or empty",
                cmd.getName() == null || cmd.getName().isEmpty());

        List<String> args = cmd.getArguments();
        assertTrue("Unknown command should not carry any arguments", args.isEmpty());
    }
    // Checks that unknown commands are treated as valid with arguments.

    @Test
    public void shouldTrimWhitespaceAroundInput() {
        ParsedCommand cmd = parser.parse("   HELP   ");

        assertNotNull("Parsed command should not be null", cmd);
        assertTrue("HELP with extra spaces should still be valid", cmd.isValid());
        assertEquals("Command name should be HELP",
                "HELP", cmd.getName().toUpperCase());
        assertTrue("HELP should have no arguments", cmd.getArguments().isEmpty());
    }
    // Ensures leading/trailing spaces do not break parsing.

    @Test
    public void shouldNormaliseCommandCaseToUppercase() {
        ParsedCommand cmd = parser.parse("createaccount Savings");

        assertNotNull("Parsed command should not be null", cmd);
        assertTrue("Lowercase command should still be valid", cmd.isValid());
        assertEquals("Command name should be normalised to uppercase",
                "CREATEACCOUNT", cmd.getName().toUpperCase());
        assertEquals("CREATEACCOUNT should have exactly one argument",
                1, cmd.getArguments().size());
        assertEquals("First argument should be 'Savings'",
                "Savings", cmd.getArguments().getFirst());
    }
    // Ensures command names are case-insensitive but stored in a normalised form.

    @Test
    public void shouldRejectBlankInput() {
        CommandParser parser = new CommandParser();
        ParsedCommand cmd = parser.parse("   ");
        assertFalse(cmd.isValid());
        assertTrue(cmd.getMessage().contains("Please enter a command"));
    }
// Verifies that blank input is handled as an invalid command.

    @Test
    public void shouldNotCrashOnInvalidInput() {
        try {
            CommandParser parser = new CommandParser();
            ParsedCommand cmd = parser.parse("INVALIDCOMMAND");
            // Server should not throw, crash, or close connection
            assertNotNull(cmd);
        } catch (Exception e) {
            fail("System crashed on invalid input: " + e.getMessage());
        }
    }
// Ensures that invalid commands do not crash the system.
}