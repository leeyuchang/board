package com.example.demo.security;

import com.example.demo.domain.CustomUser;
import com.example.demo.domain.MemberVO;
import com.example.demo.mapper.MemberMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.extern.java.Log;

@Log
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        
        log.info("Load User by username : " + userName);

        MemberVO member = memberMapper.read(userName);

        if(member == null) {
            throw new UsernameNotFoundException("Request User Not Found");
        }

        return new CustomUser(member);
    }
    
}