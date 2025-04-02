package com.example.locvoiturefx.services;

import com.example.locvoiturefx.dao.UserDAO;
import com.example.locvoiturefx.models.User;
import com.example.locvoiturefx.utils.PasswordUtils;
import java.util.List;

public class AuthService {
    private UserDAO userDAO;

    public AuthService() {
        userDAO = new UserDAO();
    }

    // Lors de l'authentification, on compare le mot de passe hashé
    public User authenticate(String username, String password) {
        List<User> users = userDAO.getAllUsers();
        String hashedPassword = PasswordUtils.hashPassword(password);
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(hashedPassword)) {
                return u;
            }
        }
        return null;
    }

    // Lors de la création d'un nouvel utilisateur, on hash le mot de passe avant de l'insérer
    public boolean addUser(User newUser) {
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            if (u.getUsername().equals(newUser.getUsername())) {
                return false;
            }
        }
        newUser.setPassword(PasswordUtils.hashPassword(newUser.getPassword()));
        return userDAO.addUser(newUser);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    // Lors de la mise à jour, on hash également le mot de passe
    public boolean updateUser(User user) {
        user.setPassword(PasswordUtils.hashPassword(user.getPassword()));
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(int id) {
        return userDAO.deleteUser(id);
    }
}
