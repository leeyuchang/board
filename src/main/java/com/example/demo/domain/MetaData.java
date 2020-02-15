package com.example.demo.domain;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MetaData {

	public String name;
    public String mimeType;
    public List<String> parents;
}
