package com.example.demo.domain;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class CustomUser extends User {

    private static final long serialVersionUID = 1L;
    private MemberVO member;

    public CustomUser(MemberVO member) {
        super(
            member.getUserid(), 
            member.getUserpw(), 
            member.getAuthList().stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getAuth()))
                                .collect(Collectors.toList()));
        this.member = member; 
    }
    
}