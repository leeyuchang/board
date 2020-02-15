package com.example.demo.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RefreshTokenDTO {
	
	public String client_secret;
	public String grant_type;
	public String refresh_token;
	public String client_id;

}
