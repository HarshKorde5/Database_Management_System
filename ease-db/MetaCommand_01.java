import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

enum MetaCommandResult {
    SUCCESS,
    UNRECOGNIZED_COMMAND
}

public class MetaCommand_01 {
    public static void printPrompt() {
        System.out.print("ease-db> ");
    }

    public static void handleInput(String input) {
        if (input.equals(".exit")) {
            System.exit(0);
        } else {
            System.out.println("Unrecognized command '" + input + "'.");
        }
    }

    public static void replLoop(BufferedReader reader) throws IOException {
        while (true) {
            printPrompt();
            String input = reader.readLine();

            if (input == null) {
                break;
            }

            if (input.startsWith(".")) {
                switch (doMetaCommand(input)) {
                    case SUCCESS:
                        continue;
                    case UNRECOGNIZED_COMMAND:
                        System.out.println("Unrecognized command '" + input + "'.");
                        continue;
                }
            }
            
            handleInput(input);
        }
    }

    static MetaCommandResult doMetaCommand(String input) {
        if (input.equals(".exit")) {
            System.exit(0);
            return MetaCommandResult.SUCCESS; // dead code : not reached after exit
        } else {
            return MetaCommandResult.UNRECOGNIZED_COMMAND;
        }
    }

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            replLoop(reader);
        } catch (IOException e) {
            System.err.println("Error reading input.");
        }
    }
}
