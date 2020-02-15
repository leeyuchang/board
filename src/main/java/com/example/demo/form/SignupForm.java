package com.example.demo.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SignupForm {

    @NotBlank
    @Email
    private String userid;
    @NotBlank
    private String userpw;
    @NotBlank
    private String userName;

}