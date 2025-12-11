package io.github.ninedots0.core.auth;

import java.io.*;
import java.nio.file.*;

public class UserRepository {

    private static final String FILE_PATH = "src/main/resources/users.txt";

    public UserRepository() {
        try {
            Path p = Paths.get(FILE_PATH);
            if (!Files.exists(p)) {
                Files.createFile(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(user.getUsername() + "," + user.getPassword());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User findUser(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",");
                if (arr.length == 2 && arr[0].equals(username)) {
                    return new User(arr[0], arr[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

