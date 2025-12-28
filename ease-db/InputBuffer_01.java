import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class InputBuffer_01 {

    public static void printPrompt(){
        System.out.print("ease-db> ");
    }

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        while(true){
            printPrompt();

            String input;
            try{
                input = reader.readLine();
                if(input == null){
                    System.exit(0);
                }
            }catch(IOException e){
                System.err.println("Error reading input");
                return;
            }


            if(input.equals(".exit")){
                System.exit(0);
            }else{
                System.out.println("Unrecognized command '"+input+"'.");
            }
        }

    }
}
