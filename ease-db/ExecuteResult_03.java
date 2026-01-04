import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

enum ExecuteResult{
    SUCCESS,
    TABLE_FULL,
    DUPLICATE_KEY
}

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

class ExecuteResult_03 {
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

            ExecuteResult result = executeStatement(statement, input);

            switch (result) {
                case SUCCESS:
                    System.out.println("Executed.");
                    break;
                case TABLE_FULL:
                    System.out.println("Error: Table full.");
                    break;
                case DUPLICATE_KEY:
                    System.out.println("Error: Duplicate ID.");
                    break;
            }
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

    public static void saveTable() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DB_FILE))) {
            for (Row row : table.rows) {
                writer.write(serializeRow(row));
                writer.newLine();
            }
            System.out.println("Data saved to file successfully.");
        } catch (IOException e) {
            System.err.println("Error saving database.");
        }
    }

    public static void loadTable() {
        File file = new File(DB_FILE);
        if(!file.exists()){
            System.out.println("Error: Loading Database file.File not found.");
            return;
        }

        try(BufferedReader reader = new BufferedReader(new FileReader(DB_FILE))){
            String line;
            while((line = reader.readLine()) != null){
                Row row = deserializeRow(line);
                table.rows.add(row);
            }
            System.out.println("Data loaded from file successfully.");
        }catch(IOException e){
            System.err.println("Error: Loading Database.");
        }

    }
    // public static void executeStatement(Statement statement, String input) {

    public static ExecuteResult executeStatement(Statement statement, String input) {
        ExecuteResult result = null;

        switch (statement.type) {
            case INSERT:
                String[] statementSplit = input.split(" ", 4);

                if (!isInsertValid(statementSplit)) {
                    return ExecuteResult.SUCCESS;
                }

                if (table.rows.size() >= MAX_ROWS) {
                    return ExecuteResult.TABLE_FULL;
                }

                int id = Integer.parseInt(statementSplit[1]);
                if (idExists(id)) {
                    return ExecuteResult.DUPLICATE_KEY;
                }

                Row row = new Row();
                row.id = id;
                row.name = statementSplit[2];
                row.email = statementSplit[3];

                table.rows.add(row);
                result = ExecuteResult.SUCCESS;
                break;
            case SELECT:
                System.out.println("id\t| name\t| email");
                for (Row r : table.rows) {
                    System.out.println(r.id + "\t| " + r.name + "\t| " + r.email);
                }
                result = ExecuteResult.SUCCESS;
                break;
        }
        return result;
    }

    public static void main(String[] args) {
        loadTable();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            replLoop(reader);
        } catch (IOException e) {
            System.err.println("Error reading input.");
        }
    }
}
