package repositories;

import salesforce.User;
import java.util.List;

public interface UserRepositoryInterface {

    List<User> getAllUsers();

    User getUserByUserName(String username);

    void removeUserFromListByUsername(String username);

    void addUser(User user);

}
