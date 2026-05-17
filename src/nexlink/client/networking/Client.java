package nexlink.client.networking;

/**
 *
 * @author ALI
 */
import java.net.Socket;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Client {

    public static void main() {
        //decleartion
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            //trys connection to port 1234 at localhost

            socket = new Socket("localhost", 1919);

            // char "inputStreamReader" <---- byteData"socket.getInputStream()" <---- network
            inputStreamReader = new InputStreamReader(socket.getInputStream());

            // chars "outputStreamWriter" ----> byteData"socket.getOutputStream()" ----> network
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            //chars "bufferedReader" first stored in buffer <--- char "inputStreamReader"
            bufferedReader = new BufferedReader(inputStreamReader);

            //char "bufferedReader" first stored in buffer ---> chars "outputStreamWriter"
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            Scanner scanner = new Scanner(System.in);

                // After connecting, send sender name first:
            scanner = new Scanner(System.in);

            System.out.print("Enter your name: ");
            String senderName = scanner.nextLine();
            bufferedWriter.write(senderName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

                // Then send receiver name:
            System.out.print("Enter receiver name: ");
            String receiverName = scanner.nextLine();
            bufferedWriter.write(receiverName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

                // Then start messaging loop:
            while (true) {
                String message = scanner.nextLine();
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                if (message.equalsIgnoreCase("bye")) {
                    break;
                }

                String response = bufferedReader.readLine();
                System.out.println("Server: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
