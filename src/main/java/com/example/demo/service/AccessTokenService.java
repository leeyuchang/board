package com.example.demo.service;

import com.example.demo.domain.AccessTokenDTO;

public interface AccessTokenService {

    public AccessTokenDTO get();
    public void save(AccessTokenDTO token);

}