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
public class RegisterScreenLogic {

    private String username;
    private String password;
    public boolean isRegistered = false;
    // This lock object will manage thread signaling
    public final Object lock = new Object();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

