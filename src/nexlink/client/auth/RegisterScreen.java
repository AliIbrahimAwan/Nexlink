/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nexlink.client.auth;

import java.util.Scanner;

/**
 *
 * @author ALI
 */
public class RegisterScreen {
    private String username =null;

    private String password = null;

    public void getLoginInfo() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("enter username: ");        
        username = scanner.nextLine();
        
        System.out.println("enter password: ");
        password = scanner.nextLine();

    }

    public String getUsername() {
        return username;
    }
    
        public String getPassword() {
        return password;
    }
}
