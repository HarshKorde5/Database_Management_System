import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

class Row {
    int id;
    String name;
    String email;
}

class Table {
    List<Row> rows = new ArrayList<>();
}

class Persistence_02 {
    static final String DB_FILE = "database.txt";
    static Table table = new Table();
    static final int MAX_ROWS = 100;

    public static void printPrompt() {
        System.out.print("ease-db> ");
    }

    public static MetaCommandResult doMetaCommand(String input) {
        if (input.equals(".exit")) {
            saveTable();
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

            Statement statement = new Statement();
            switch (prepareStatement(input, statement)) {
                case SUCCESS:
                    break;

                case UNRECOGNIZED_STATEMENT:
                    System.out.println("Unrecognized keyword at start of '" + input + "'.");
                    continue;
            }

            executeStatement(statement, input);
        }
    }

    public static boolean isInsertValid(String[] splitStatement) {
        if (splitStatement.length < 4) {
            System.out.println("Syntax Error. Usage: insert <id> <name> <email>");
            return false;
        }

        try {
            Integer.parseInt(splitStatement[1]);
        } catch (NumberFormatException e) {
            System.out.println("ID must be a number.");
            return false;
        }

        return true;

    }

    public static boolean idExists(int id) {
        for (Row r : table.rows) {
            if (r.id == id) {
                return true;
            }
        }
        return false;
    }

    public static String serializeRow(Row row) {
        return row.id + "|" + row.name + "|" + row.email;
    }

    public static Row deserializeRow(String line) {
        String[] parts = line.split("\\|", 3);

        Row row = new Row();
        row.id = Integer.parseInt(parts[0]);
        row.name = parts[1];
        row.email = parts[2];

        return row;
    }

    public static void saveTable(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(DB_FILE))){
            for( Row row : table.rows){
                writer.write(serializeRow(row));
                writer.newLine();
            }
            System.out.println("Data saved to file successfully.");
        }catch(IOException e){
            System.err.println("Error saving database.");
        }
    }

    public static void executeStatement(Statement statement, String input) {
        switch (statement.type) {
            case INSERT:
                String[] statementSplit = input.split(" ", 4);

                if (!isInsertValid(statementSplit)) {
                    return;
                }

                if (table.rows.size() >= MAX_ROWS) {
                    System.out.println("Error: Table full.");
                    return;
                }

                int id = Integer.parseInt(statementSplit[1]);
                if (idExists(id)) {
                    System.out.println("Error: Duplicate ID. Record with " + id + " already exists.");
                    return;
                }

                Row row = new Row();
                row.id = id;
                row.name = statementSplit[2];
                row.email = statementSplit[3];
                table.rows.add(row);
                System.out.println("Inserted 1 row.\nExecuted.\n");
                break;
            case SELECT:
                System.out.println("id\t| name\t| email");
                for (Row r : table.rows) {
                    System.out.println(r.id + "\t| " + r.name + "\t| " + r.email);
                }
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
