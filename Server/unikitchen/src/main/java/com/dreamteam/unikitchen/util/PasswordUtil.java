package com.dreamteam.unikitchen.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hashes a plain text password using BCrypt and returns the hashed password
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Checks if a plain text password matches the hashed password
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
