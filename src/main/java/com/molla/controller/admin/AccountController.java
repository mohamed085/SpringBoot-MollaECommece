package com.molla.controller.admin;

import com.molla.exciptions.UserNotFoundException;
import com.molla.model.MollaUserDetails;
import com.molla.model.Role;
import com.molla.model.User;
import com.molla.service.RoleService;
import com.molla.service.UserService;
import com.molla.util.FileUpload;
import lombok.extern.slf4j.Slf4j;import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/account")
public class AccountController {

    private final UserService userService;
    private final RoleService roleService;

    public AccountController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("")
    public String viewAccount(@AuthenticationPrincipal MollaUserDetails loggerUSer, Model model)
            throws UserNotFoundException {

        log.debug("AccountController | try to get view account : " + loggerUSer.toString());

        User user = userService.findByEmail(loggerUSer.getUsername());

        model.addAttribute("user", user);

        return "admin/account/index";
    }

    @GetMapping("/password")
    public String viewEditPassword(@AuthenticationPrincipal MollaUserDetails loggerUSer, Model model)
            throws UserNotFoundException {
        log.debug("AccountController | try to get view edit password : " + loggerUSer.toString());

        User user = userService.findByEmail(loggerUSer.getUsername());

        model.addAttribute("user", user);

        return "admin/account/accountChangePassword";
    }

    @PostMapping("/password")
    public String editPassword(@AuthenticationPrincipal MollaUserDetails loggerUSer, User user,
                               RedirectAttributes redirectAttributes) throws UserNotFoundException {
        log.debug("AccountController | try to get edit account : " + loggerUSer.toString());

        User user1 = userService.findByEmail(loggerUSer.getUsername());

        user1.setPassword(user.getPassword());

        userService.save(user1);

        redirectAttributes.addFlashAttribute("message", "Your password changed successfully.");
        return "redirect:/admin/account";
    }

    @GetMapping ("/edit")
    public String getEditAccount(@AuthenticationPrincipal MollaUserDetails loggerUSer, Model model)
            throws UserNotFoundException {

        log.debug("AccountController | try to get account : " + loggerUSer.toString());

        User user = userService.findByEmail(loggerUSer.getUsername());
        List<Role> roles = roleService.findAll();

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);

        return "admin/account/editAccount";
    }

    @PostMapping("/edit")
    public String editAccount(User user,
                              @RequestParam("image") MultipartFile file,
                              RedirectAttributes redirectAttributes) throws IOException {

        log.debug("AccountController | try to edit account : " + user.toString());

        if (!file.isEmpty()) {

            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            user.setPhotos(fileName);
            User savedUser = userService.save(user);

            String uploadDir = "user-photos/" + savedUser.getId();

            FileUpload.cleanDir(uploadDir);
            FileUpload.saveFile(uploadDir, fileName, file);

        } else {
            if (user.getPhotos().isEmpty())
                user.setPhotos(null);

            userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The account has been updated saved successfully.");


        return "redirect:/admin/account";
    }


}
