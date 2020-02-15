package com.example.demo.controller;

import com.example.demo.form.LoginForm;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.extern.java.Log;

@Log
@Controller
public class LoginController {

    @GetMapping("/login")
    public String getLogin(@ModelAttribute("formData") LoginForm form) {
        log.info("get /login");
        return "login/login";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute("formData") @Validated LoginForm form, BindingResult result) {
        log.info("post /login");

        log.info("★★"+form + "");

        if (result.hasErrors()) {
            return getLogin(form);
        }
        return "redirect:/";
    }
}