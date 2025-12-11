package io.github.ninedots0.core.auth;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();
    private User currentUser = null;
    
    public User getCurrentUser() {
        return currentUser;
    }
    /** 注册 */
    public int register(String username, String password){
        // 已存在
        if (username.equals("")|| password.equals(""))
            return 0;
        User exist = userRepository.findUser(username);
        if (exist != null) return 1;

        userRepository.saveUser(new User(username, password));
        
        try{
            String path = "saves/" + username + "/" + "num.txt", num = "0";
            File f = new File(path);
            f.getParentFile().mkdirs();
            Files.write(f.toPath(), num.getBytes());
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return 2;
    }
    /** 登录 */
    public boolean login(String username, String password) {
        User user = userRepository.findUser(username);
        if (user == null) return false;
        if (user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }
}
