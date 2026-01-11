import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

enum ExecuteResult {
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

enum NodeType {
    LEAF,
    INTERNAL
}

class Statement {
    StatementType type;
}

class Row {
    int id;
    String name;
    String email;
}

abstract class Node {
    NodeType type;
}

class LeafNode extends Node {
    List<Row> rows = new ArrayList<>();

    LeafNode() {
        this.type = NodeType.LEAF;
    }
}

class InternalNode extends Node {
    int key;
    LeafNode left;
    LeafNode right;

    InternalNode(int key, LeafNode left, LeafNode right) {
        this.type = NodeType.INTERNAL;
        this.key = key;
        this.left = left;
        this.right = right;
    }

}

/*
class Cursor {
    Table table;
    int rowIndex;
    int pageIndex;

    Cursor(Table table) {
        this.table = table;
        this.rowIndex = 0;
        this.pageIndex = 0;
    }

    boolean endOfTable() {
        return pageIndex >= table.pages.size();
    }

    Row value() {
        return table.pages
                .get(pageIndex).rows
                .get(rowIndex);
    }

    void advance() {
        rowIndex++;
        if (rowIndex >= table.pages.get(pageIndex).rows.size()) {
            pageIndex++;
            rowIndex = 0;
        }
    }
}
*/

class Cursor{
    LeafNode leaf;
    int rowIndex;

    Cursor(LeafNode leaf){
        this.leaf = leaf;
        this.rowIndex = 0;        
    }

    boolean endOfTable(){
        return rowIndex >= leaf.rows.size();
    }

    Row value(){
        return leaf.rows.get(rowIndex);
    }

    void advance(){
        rowIndex++;
    }
}

class Table {
    static final int MAX_PAGES = 10;
    static final int ROWS_PER_PAGE = 2;
    // List<LeafNode> pages = new ArrayList<>();
    Node root;

    Table() {
        this.root = new LeafNode();
    }

    static int findInsertIndex(List<Row> rows, int id) {
        int i = 0;
        while (i < rows.size() && rows.get(i).id < id) {
            i++;
        }

        return i;
    }

    /*
     * void insert(Row row) {
     * LeafNode page;
     * 
     * if (pages.isEmpty() || pages.get(pages.size() - 1).rows.size() >=
     * ROWS_PER_PAGE) {
     * page = new LeafNode();
     * pages.add(page);
     * } else {
     * page = pages.get(pages.size() - 1);
     * }
     * 
     * int index = findInsertIndex(page.rows, row.id);
     * page.rows.add(index, row);
     * }
     */

    void insert(Row row) {
        if (root.type == NodeType.LEAF) {
            LeafNode leaf = (LeafNode) root;

            int index = findInsertIndex(leaf.rows, row.id);
            leaf.rows.add(index, row);

            if (leaf.rows.size() > ROWS_PER_PAGE) {
                splitLeaf(leaf);
            }
            return;
        }

        InternalNode internalNode = (InternalNode) root;

        if(row.id < internalNode.key){
            insertIntoLeaf(internalNode.left, row);
        }else{
            insertIntoLeaf(internalNode.right, row);
        }

    }

    void insertIntoLeaf(LeafNode leaf, Row row){
        int index = findInsertIndex(leaf.rows, row.id);
        leaf.rows.add(index, row);
    }

    void splitLeaf(LeafNode leaf){
        int mid = leaf.rows.size() / 2;

        LeafNode left = new LeafNode();
        LeafNode right = new LeafNode();

        for(int i = 0; i < mid; i++){
            left.rows.add(leaf.rows.get(i));
        }

        for(int i = mid; i < leaf.rows.size(); i++){
            right.rows.add(leaf.rows.get(i));
        }

        int separatorKey = right.rows.get(0).id;

        root = new InternalNode(separatorKey, left, right);
    }

    /*
    int totalRows() {
        int count = 0;

        for (LeafNode p : pages) {
            count += p.rows.size();
        }

        return count;
    }
    */

    //current tree height is 1, will evolve in next implementations
    int totalRows(){
        if(root.type == NodeType.LEAF){
            return ((LeafNode) root).rows.size();
        }

        InternalNode internalNode = (InternalNode) root;
        return internalNode.left.rows.size() + internalNode.right.rows.size();
    }

    Cursor startCursor() {
        if(root.type == NodeType.LEAF){
            return new Cursor((LeafNode) root);
        }else{
            InternalNode internal = (InternalNode) root;
            return new Cursor(internal.left);
        }
    }

}


class RootNode_04 {
    static final int MAX_ROWS = 20;
    static final String DB_FILE = "database.txt";

    static Table table = new Table();

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

    /*
    public static boolean idExists(int id) {
        Cursor cursor = table.startCursor();

        while (!cursor.endOfTable()) {
            Row row = cursor.value();
            if (row.id == id) {
                return true;
            }
            cursor.advance();
        }

        return false;
    }

    */

    public static boolean idExists(int id){
        Node root = table.root;

        if(root.type == NodeType.LEAF){
            LeafNode leaf = (LeafNode) root;
            for(Row r : leaf.rows){
                if(r.id == id){
                    return true;
                }
            }

            return false;
        }

        InternalNode internalNode = (InternalNode) root;

        for(Row r : internalNode.left.rows){
            if(r.id == id) return true;
        }

        for(Row r : internalNode.right.rows){
            if(r.id == id) return true;
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
            Cursor cursor = table.startCursor();

            while (!cursor.endOfTable()) {
                Row row = cursor.value();
                writer.write(serializeRow(row));
                writer.newLine();
                cursor.advance();
            }
            System.out.println("Data saved to file successfully.");
        } catch (IOException e) {
            System.err.println("Error saving database.");
        }
    }

    public static void loadTable() {
        File file = new File(DB_FILE);
        if (!file.exists()) {
            System.out.println("Error: Loading Database file.File not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DB_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Row row = deserializeRow(line);
                table.insert(row);
            }
            System.out.println("Data loaded from file successfully.");
        } catch (IOException e) {
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

                if (table.totalRows() >= MAX_ROWS) {
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

                table.insert(row);
                result = ExecuteResult.SUCCESS;
                break;
            case SELECT:
                System.out.println("id\t| name\t| email");

                Cursor cursor = table.startCursor();

                while (!cursor.endOfTable()) {
                    Row r = cursor.value();
                    System.out.println(r.id + "\t| " + r.name + "\t|" + r.email);
                    cursor.advance();
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
