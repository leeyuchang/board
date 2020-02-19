package com.example.demo.service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.annotation.Resource;

import com.example.demo.domain.AccessTokenInfo;
import com.example.demo.domain.BoardAttachVO;
import com.example.demo.domain.DriveFileResDTO;
import com.example.demo.domain.MetaData;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.java.Log;

@Log
@Service
public class FileUploadServiceImpl implements FileUploadService {

        @Resource(name = "accessTokenInfo")
        private AccessTokenInfo accessTokenInfo;

	private final String boundary = "-------314159265358979323846";

	private final String delimiter = "\r\n--" + boundary + "\r\n";
	
	private final String closeDelim = "\r\n--" + boundary + "--";
	
	private final String GOOGLE_DRIVE_URL = "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart";
	
	private final UriComponents uri = UriComponentsBuilder
			.fromHttpUrl(GOOGLE_DRIVE_URL)
			.queryParam("fields", String.join(",", "id", "mimeType", "name"))
			.build();

	@Autowired
	private Gson gson;
	
	@Autowired
	private HttpClient httpClient;

	public BoardAttachVO up(MultipartFile file, String folderId) {

		Objects.requireNonNull(accessTokenInfo);
		Objects.requireNonNull(file);
		
		log.info("★ " + file.getOriginalFilename());

		var metaData = MetaData.builder()
				.name(file.getOriginalFilename())
				.mimeType(file.getContentType())
				.parents(Arrays.asList(folderId))
				.build();

		String base64FileData = null;
		try {
			base64FileData = Base64.getEncoder().encodeToString(file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		var stringBuffer = new StringBuffer();

		var multipartRequestBody = stringBuffer
				.append(delimiter)
				.append("content-type: application/json; charset=UTF-8\r\n\r\n")
				.append(gson.toJson(metaData))
				.append(delimiter)
				.append("content-transfer-encoding: base64\r\n")
				.append("content-type: " + file.getContentType())
				.append("\r\n\r\n")
				.append(base64FileData)
				.append(closeDelim)
				.toString();

		var request = HttpRequest.newBuilder().uri(uri.toUri())
				.header("Authorization", "Bearer " + accessTokenInfo.getAccessToken())
				.header("Content-Type", "multipart/related; boundary=\"" + boundary + "\"")
				.POST(BodyPublishers.ofString(multipartRequestBody))
				.build();

		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			log.info("" + response.body());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		if(!is2xx(response)) {
			throw new RuntimeException(response.statusCode()+"");
		}
		
		return convertDtoToVo(gson.fromJson(response.body(), DriveFileResDTO.class), folderId);
	}
	
	private BoardAttachVO convertDtoToVo(DriveFileResDTO dto, String folderId) {
		BoardAttachVO boardAttachVO = new BoardAttachVO();
		boardAttachVO.setFileId(dto.getId());
		boardAttachVO.setFileName(dto.getName());
		boardAttachVO.setFileType(dto.getMimeType().startsWith("image"));
		boardAttachVO.setFolderName(folderId);
		return boardAttachVO;
	}
	
	private boolean is2xx(HttpResponse<String> res) {
		return res.statusCode() >= 200 && res.statusCode() < 300;
	}

}