package main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import repositories.UserRepository;
import salesforce.User;

@Controller
public class MainController {

    @GetMapping("/")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("users", new UserRepository().getAllUsers());

        return "main";
    }

    @GetMapping("/delete/{un}")
    public String deleteUser(@PathVariable String username) {
        UserRepository repo = new UserRepository();
        repo.removeUserFromListByUsername(username);

        return "redirect:/";
    }

    @PostMapping("/submit")
    public String addProduct(@RequestBody String postPayload) {
        System.out.println(postPayload);
        return null;
    }

}