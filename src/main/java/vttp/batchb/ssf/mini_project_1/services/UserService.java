package vttp.batchb.ssf.mini_project_1.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.batchb.ssf.mini_project_1.models.Registration;
import vttp.batchb.ssf.mini_project_1.models.User;
import vttp.batchb.ssf.mini_project_1.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;

    public boolean hasUser(String username){
        return userRepo.hasUser(username);
    }

    public boolean isValidUser(String username, String password){
        Optional<User> opt = getUserByUsername(username);

        if (opt.isEmpty()){
            return false;
        }

        User retrievedUser = opt.get();
        // check if password is correct
        if (retrievedUser.getPassword().equals(password))
            return true;
        return false;
        
    }

    public Optional<User> getUserByUsername(String username){
        return userRepo.getUserByUsername(username);
    }

    public boolean validPassword(String password, String passwordAgain){
        if (password.equals(passwordAgain)){
            return true;
        }
        return false;
    }

    public void register(Registration registration){
        User newUser = new User();

        newUser.setUsername(registration.getUsername());
        newUser.setPassword(registration.getPassword());
        userRepo.addUser(newUser);
    }

}
