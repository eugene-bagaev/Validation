package repositories;

import google.GoogleHelper;
import salesforce.User;

import java.util.*;

public class UserRepository implements UserRepositoryInterface {

    private static Map<String, User> users = new HashMap<>();
//    private static

    static {

    }

    public List<User> getAllUsers() {
        List<User> users = new LinkedList<>();

        users.add(new User("Eugene", "eygene@gmail.com", "12345"));
        users.add(new User("Alex", "alex@gmail.com", "12345"));

        return users;
    }

    @Override
    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    @Override
    public User getUserByUserName(String username) {
        return null;
    }

    @Override
    public void removeUserFromListByUsername(String username) {

    }
}
