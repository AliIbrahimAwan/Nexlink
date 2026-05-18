package nexlink.client.networking;

/**
 *
 * @author ALI
 */
import java.net.Socket;
import java.util.Scanner;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class Client {

    public static void main() {
        //decleartion
        Socket socket = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;

        try {
            //trys connection to port 1234 at localhost

            socket = new Socket("localhost", 1919);
            ClientListener clientListener = new ClientListener(socket);
            Thread listenerThread = new Thread(clientListener);

            listenerThread.start();

            // chars "outputStreamWriter" ----> byteData"socket.getOutputStream()" ----> network
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

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

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
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
