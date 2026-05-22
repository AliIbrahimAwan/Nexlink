package nexlink.client.networking;

import java.net.Socket;
import java.util.Scanner;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import nexlink.client.auth.LoginScreen;
import nexlink.client.auth.RegisterScreen;

public class Client {

    public static void main() {

        Socket socket = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;

        try {
            socket = new Socket("localhost", 2222);

            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // pass bufferedReader to ClientListener — shared, no conflict
            Scanner scanner = new Scanner(System.in);
            String userName = null;
            boolean authenticated = false;
            
            while (!authenticated) {
                System.out.println("Enter 1 for Login, 2 for Registration:");
                int option = scanner.nextInt();
                scanner.nextLine(); // consume leftover newline

                bufferedWriter.write(String.valueOf(option));
                bufferedWriter.newLine();
                bufferedWriter.flush();

                if (option == 1) {
                    // LOGIN LOOP
                    while (true) {
                        LoginScreen loginScreen = new LoginScreen();
                        loginScreen.getLoginInfo();
                        userName = loginScreen.getUsername();
                        String password = loginScreen.getPassword();

                        bufferedWriter.write(userName);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        bufferedWriter.write(password);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        String check = bufferedReader.readLine();

                        if (check.equals("11")) {
                            System.out.println("Login successful! Welcome " + userName);
                            
                            authenticated = true;
                            break;
                        }
                        else{System.out.println("Incorrect credentials, try again!");
                        };
                    }

                } else if (option == 2) {
                    // REGISTER
                    RegisterScreen registerScreen = new RegisterScreen();
                    registerScreen.getLoginInfo();
                    userName = registerScreen.getUsername();
                    String password = registerScreen.getPassword();

                    bufferedWriter.write(userName);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    bufferedWriter.write(password);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    String response = bufferedReader.readLine();
                    if (response.equalsIgnoreCase("1")) {
                        System.out.println("Registration successful! Please login.");
                    } else {
                        System.out.println("Username already taken. Try again.");
                    }
                }
            }

            ClientListener clientListener = new ClientListener(bufferedReader);
            Thread listenerThread = new Thread(clientListener);
            listenerThread.start();
            
            // get receiver name
            System.out.print("Enter receiver name: ");
            String receiverName = scanner.nextLine();
            bufferedWriter.write(receiverName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // message loop
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
