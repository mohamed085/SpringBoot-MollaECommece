package com.molla.controller.admin;

import com.molla.exciptions.UserNotFoundException;
import com.molla.exporter.UserCsvExporter;
import com.molla.exporter.UserExcelExporter;
import com.molla.exporter.UserPdfExporter;
import com.molla.model.Role;
import com.molla.model.User;
import com.molla.service.RoleService;
import com.molla.service.UserService;
import com.molla.service.serviceImp.UserServiceImp;
import com.molla.util.FileUpload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("")
    public String getAll(Model model) {
        log.debug("UserController | get all users");

        return getAllByPage(1, "firstName", "asc", null, model);
    }

    @GetMapping("/page/{pageNum}")
    public String getAllByPage(@PathVariable(name = "pageNum") int pageNum,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword,
                             Model model) {

        log.debug("UserController | listByPage is started");

        Page<User> page = userService.findAllByPage(pageNum, sortField, sortDir, keyword);
        List<User> users = page.getContent();

        long startCount = (pageNum - 1) * UserServiceImp.USERS_PER_PAGE + 1;
        long endCount = startCount + UserServiceImp.USERS_PER_PAGE - 1;

        if (endCount > page.getTotalPages()) {
            endCount = page.getTotalPages();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("users", users);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/admin/users");

        return "admin/user/index";
    }


    @GetMapping("/new")
    public String getNew(Model model) {
        log.debug("UserController | newUser is called");

        List<Role> listRoles = roleService.findAll();

        log.info("UserController | newUser | listRoles.size() : " + listRoles.size());

        User user = new User();
        user.setEnabled(true);

        log.debug("UserController | newUser | user : " + user.toString());

        model.addAttribute("user", user);
        model.addAttribute("roles", listRoles);
        model.addAttribute("pageTitle", "Create New User");

        return "admin/user/user_form";
    }

    @PostMapping("/save")
    public String saveUser(User user,
                           @RequestParam("fileImage") MultipartFile file,
                           RedirectAttributes redirectAttributes) throws IOException {

        log.debug("UserController | try to save user : " + user.toString());

        if (!file.isEmpty()) {

            log.debug("UserController | try to save user photo");

            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            user.setPhotos(fileName);
            User savedUser = userService.save(user);

            String uploadDir = "user-photos/" + savedUser.getId();

            FileUpload.cleanDir(uploadDir);
            FileUpload.saveFile(uploadDir, fileName, file);

        }

        userService.save(user);

        redirectAttributes.addFlashAttribute("messageSuccess", "The user has been added saved successfully.");

        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        log.debug("UserController | editUser is called");

        try {
            User user = userService.findById(id);

            log.debug("UserController | editUser | user : " + user.toString());

            List<Role> listRoles = roleService.findAll();

            log.debug("UserController | editUser | listRoles.size() : " + listRoles.size());

            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User (Name: " + user.getFullName() + ")");
            model.addAttribute("roles", listRoles);

            return "admin/user/user_form";

        } catch (UserNotFoundException ex) {

            log.debug("UserController | editUser | ex.getMessage() : " + ex.getMessage());

            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
            return "redirect:/admin/users";
        }

    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        log.debug("UserController | deleteUser is called");

        try {
            User deletedUser = userService.deleteById(id);

            log.debug("UserController | deleteUser | delete completed");

            redirectAttributes.addFlashAttribute("messageSuccess",
                    "User: " + deletedUser.getFullName() + " has been deleted successfully");
        } catch (UserNotFoundException ex) {

            log.debug("UserController | deleteUser | ex.getMessage() : " + ex.getMessage());

            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id,
                                          @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {

        log.debug("UserController | updateUserEnabledStatus is called");

        try {
            userService.updateEnabledStatus(id, enabled);
            log.debug("UserController | updateUserEnabledStatus completed");

            String status = enabled ? "enabled" : "disabled";

            log.debug("UserController | updateUserEnabledStatus | status : " + status);

            String message = "The user ID " + id + " has been " + status;

            log.debug("UserController | updateUserEnabledStatus | message : " + message);

            if (message.contains("enabled")) {
                redirectAttributes.addFlashAttribute("messageSuccess", message);
            } else {
                redirectAttributes.addFlashAttribute("messageError", message);
            }


        } catch (UserNotFoundException ex) {
            log.debug("UserController | deleteUser | ex.getMessage() : " + ex.getMessage());

            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
        }


        return "redirect:/admin/users";
    }

    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {

        log.debug("UserController | exportToCSV is called");

        List<User> listUsers = userService.findAll();

        log.debug("UserController | exportToCSV | listUsers.size() : " + listUsers.size());

        UserCsvExporter exporter = new UserCsvExporter();


        log.debug("UserController | exportToCSV | export is starting");

        exporter.export(listUsers, response);

        log.debug("UserController | exportToCSV | export completed");
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {

        log.debug("UserController | exportToExcel is called");

        List<User> listUsers = userService.findAll();

        log.debug("UserController | exportToExcel | listUsers.size() : " + listUsers.size());

        UserExcelExporter exporter = new UserExcelExporter();

        log.debug("UserController | exportToExcel | export is starting");

        exporter.export(listUsers, response);

        log.debug("UserController | exportToExcel | export completed");
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {

        log.debug("UserController | exportToPDF is called");

        List<User> listUsers = userService.findAll();

        log.debug("UserController | exportToPDF | listUsers.size() : " + listUsers.size());

        UserPdfExporter exporter = new UserPdfExporter();

        log.debug("UserController | exportToPDF | export is starting");

        exporter.export(listUsers, response);

        log.debug("UserController | exportToPDF | export completed");
    }

}
