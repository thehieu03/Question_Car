package interfa;

import java.util.List;
import model.User;

public interface IUserDAO {
    User login(String username, String password);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    boolean register(String username, String email, String password);
    int getTotalUsers();
    List<User> getAllUsers(int offset, int limit);
    List<User> searchUsers(String keyword, int offset, int limit);
    int getTotalUsersBySearch(String keyword);
    boolean banUser(int userId);
    boolean unbanUser(int userId);
    boolean updateUser(int userId, String username, String email, int role);
    User getUserById(int userId);
}

