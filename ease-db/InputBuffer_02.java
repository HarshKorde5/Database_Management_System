import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputBuffer_02 {
    public static void printPrompt() {
        System.out.print("ease-db> ");
    }

    public static void handleInput(String input){
        if(input.equals(".exit")){
            System.exit(0);
        }else{
            System.out.println("Unrecognized command '"+input+"'.");
        }
    }

    public static void replLoop(BufferedReader reader) throws IOException {
        while(true){
            printPrompt();
            String input = reader.readLine();

            if(input == null){
                break;
            }

            handleInput(input);
        }
    }

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try{
            replLoop(reader);
        }catch(IOException e){
            System.err.println("Error reading input.");
        }
    }
}
