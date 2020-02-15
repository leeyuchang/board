package com.example.demo.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginForm {

    @NotBlank
    @Email
    private String userid;
    @NotBlank
    private String userpw;
    @NotBlank
    private String userName;

}