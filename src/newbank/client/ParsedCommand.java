package newbank.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParsedCommand {

    private final String name;
    private final List<String> arguments;
    private final boolean valid;
    private final String errorMessage;

    private ParsedCommand(String name, List<String> arguments, boolean valid, String errorMessage) {
        this.name = name;
        this.arguments = arguments == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(arguments));
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public static ParsedCommand valid(String name, List<String> arguments) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Command name must not be empty.");
        }
        return new ParsedCommand(name.toUpperCase(), arguments, true, null);
    }

    public static ParsedCommand invalid(String errorMessage) {
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = "Invalidcommand.";
        }
        return new ParsedCommand(null, Collections.emptyList(), false, errorMessage);
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
