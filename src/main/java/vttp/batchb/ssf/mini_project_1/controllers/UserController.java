package vttp.batchb.ssf.mini_project_1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp.batchb.ssf.mini_project_1.models.Registration;
import vttp.batchb.ssf.mini_project_1.models.User;
import vttp.batchb.ssf.mini_project_1.services.UserService;

@Controller
@RequestMapping
public class UserController {

    // handle user-related operations (login and registration)
    @Autowired
    private UserService userSvc;
    
    // landing page
    @GetMapping(path = {"/", "/index.html"})
    public String getIndex(Model model){

        model.addAttribute("user", new User());
        return "landing";

    }

    @PostMapping("/login")
    public String postLogin(@Valid @ModelAttribute User user, BindingResult bindings, HttpSession sess){

        // validation
        if (bindings.hasErrors())
            return "landing";

        // check if account exists
        if (!userSvc.hasUser(user.getUsername())){
            FieldError err = new FieldError("user", "username", "You do not have an account!");
            bindings.addError(err);
            return "landing";
        }

        // check if password matches
        if (!userSvc.isValidUser(user.getUsername(), user.getPassword())){
            FieldError err = new FieldError("user", "password", "Incorrect password!");
            bindings.addError(err);
            return "landing";
        }

        // if user is successfully authenticated, add the user to the session
        if (sess.getAttribute("user") == null){
            sess.setAttribute("user", user);
        }
        
        // redirect to user's homepage
        return "redirect:/" + user.getUsername() + "/home";
    }

    // registration view
    @GetMapping("/register")
    public String getRegister(Model model){
        model.addAttribute("registration", new Registration());
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(@Valid @ModelAttribute Registration registration, BindingResult bindings, Model model, RedirectAttributes redirectAttributes){
        
        // validation
        if (bindings.hasErrors()){
            return "register";
        }

        // check if username exists
        if (userSvc.hasUser(registration.getUsername())){
            FieldError err = new FieldError("registration", "username", "This username has been used.");
            bindings.addError(err);
            return "register";
        }

        // check if the two entered passwords match
        if (!userSvc.validPassword(registration.getPassword(), registration.getPasswordAgain())){
            FieldError err = new FieldError("registration", "passwordAgain", "Passwords do not match.");
            bindings.addError(err);
            return "register";
        }

        // register the user if all validations pass
        userSvc.register(registration);
        redirectAttributes.addFlashAttribute("successMessage", "You have been registered successfully. Please log in!");
        
        model.addAttribute("user", new User());
        // redirect to login page
        return "redirect:/";

    }

    @GetMapping("/logout")
    public String logout(HttpSession sess){
        
        sess.invalidate();
        return "redirect:/";
    }

}
