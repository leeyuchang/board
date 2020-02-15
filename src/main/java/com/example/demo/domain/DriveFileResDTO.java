package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DriveFileResDTO {
	public String kind;
	public String id;
	public String name;
	public String mimeType;
	public String folderName;
}
