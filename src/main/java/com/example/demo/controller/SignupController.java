package com.example.demo.controller;

import com.example.demo.domain.MemberVO;
import com.example.demo.form.SignupForm;
import com.example.demo.service.MemberService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SignupController {

    @Autowired
    private MemberService memberService;
    
    /**
     * ユーザー登録画面のGETメソッド用処理.
     */
    @GetMapping("/signup")
    public String getSignUp(@ModelAttribute SignupForm form, Model model) {

        // signup.htmlに画面遷移
        return "login/signup";
    }

    /**
     * ユーザー登録画面のPOSTメソッド用処理.
     */
    @PostMapping("/signup")
    public String postSignUp(@ModelAttribute @Validated SignupForm form, BindingResult bindingResult, Model model, RedirectAttributes rda) {

        if (bindingResult.hasErrors()) {
            return getSignUp(form, model);
        }

        MemberVO newMemberVO = new MemberVO();
        BeanUtils.copyProperties(form, newMemberVO);
        
        if(memberService.signUp(newMemberVO)) {
            rda.addFlashAttribute("result", "success");
        }

        // login.htmlにリダイレクト
        return "redirect:/login";
    }
}