package chapters.testserver.controllers;

import chapters.testserver.*;
import chapters.testserver.repositories.*;
import java.util.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserRepository userRepo;

    @GetMapping("/menu/user-list")
    public String listUsers(Model model) {

        List<User> users = userRepo.findAll();
        model.addAttribute("userList", users);

        return "menu/list-users";
    }

    @ResponseBody
    @PostMapping("/menu/user-list")
    public NewUserCreatedResponse createNewUser(
            AddNewUserRequest request
    ) {

        return NewUserCreatedResponse.of(
                userRepo.save(
                        request.toEntity()
                )
        );
    }
}
