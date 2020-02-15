package com.example.demo.domain;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class AccessTokenDTO {

	@SerializedName("access_token")
	public String accessToken;
	@SerializedName("expires_in")
	public String expiresIn;
	public String scope;
	@SerializedName("token_type")
	public String tokenType;
	
}
