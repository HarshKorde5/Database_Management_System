import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

enum MetaCommandResult {
    SUCCESS,
    UNRECOGNIZED_COMMAND
}

enum PrepareResult {
    SUCCESS,
    UNRECOGNIZED_STATEMENT
}

enum StatementType {
    INSERT,
    SELECT,
}

class Statement {
    StatementType type;
}

class PrepareStatement_01 {
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

            if(input.startsWith(".")){
                switch (doMetaCommand(input)) {
                    case SUCCESS:
                        continue;
                    case UNRECOGNIZED_COMMAND:
                        System.out.println("Unrecognized command '"+input+"'.");
                        continue;                        
                }
            }

            Statement statement = new Statement();
            switch (prepareStatement(input, statement)) {
                case SUCCESS:                    
                    break;
            
                case UNRECOGNIZED_STATEMENT:
                    System.out.println("Unrecognized keyword at start of '"+input+"'.");
                    continue;
            }

            executeStatement(statement);
            System.out.println("Executed.");
        }
    }

    public static MetaCommandResult doMetaCommand(String input) {
        if (input.equals(".exit")) {
            System.exit(0);
            return MetaCommandResult.SUCCESS; // dead code : not reached after exit
        } else {
            return MetaCommandResult.UNRECOGNIZED_COMMAND;
        }
    }

    public static PrepareResult prepareStatement(String input, Statement statement) {
        input = input.toLowerCase();
        if (input.startsWith("insert")) {
            statement.type = StatementType.INSERT;
            return PrepareResult.SUCCESS;
        }

        if (input.equals("select")) {
            statement.type = StatementType.SELECT;
            return PrepareResult.SUCCESS;
        }

        return PrepareResult.UNRECOGNIZED_STATEMENT;
    }

    public static void executeStatement(Statement statement){
        switch (statement.type) {
            case INSERT:
                System.out.println("This is where we will do INSERT.");
                break;
            case SELECT:
                System.out.println("This is where we will do SELECT.");
                break;
            default:
                System.out.println("Won't reach here, but this is default unrecognized statement.");
                break;
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
