package com.example.demo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.demo.domain.UploadPathVO;
import com.example.demo.mapper.BoardAttachMapper;
import com.example.demo.mapper.UploadPathMapper;
import com.example.demo.service.DriveFileDelete;
import com.example.demo.service.FileListDTO;
import com.example.demo.service.FileListService;
import com.example.demo.service.Files;
import com.example.demo.service.UploadPathService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.java.Log;

@Log
@SpringBootTest
class DemoApplicationTests {

	@Autowired
	UploadPathMapper uploadPathMapper;

	@Test
	public void uploadPathMapperTest() {
		// root https://drive.google.com/drive/folders/1eC9veHgZjYVY1xacm8l8rpNrXZF0z-Ho
		// year https://drive.google.com/drive/folders/1ggC_MjqSV69brxoz99X2is5r99aXlfuZ
		// month
		// https://drive.google.com/drive/folders/1dZpAoHGLymd2KD85nMF-nvuce8CB691e
		// day https://drive.google.com/file/d/10xjdCZbcWlTxn0-RMlAcB8rao-X5CTsz

		final UploadPathVO uploadPathVO = new UploadPathVO(LocalDateTime.of(2020, 2, 2, 10, 10, 10),
				"1eC9veHgZjYVY1xacm8l8rpNrXZF0z-Ho", "1ggC_MjqSV69brxoz99X2is5r99aXlfuZ",
				"1dZpAoHGLymd2KD85nMF-nvuce8CB691e", "10xjdCZbcWlTxn0-RMlAcB8rao-X5CTsz");
		uploadPathMapper.insert(uploadPathVO);
	}

	@Test
	public void uploadPathMapperTruncateTest() {
		uploadPathMapper.truncate();
	}

	@Test
	public void uploadPathMapperSelectTest() {
		final Optional<UploadPathVO> optUploadPathVO = Optional.ofNullable(uploadPathMapper.select());
		optUploadPathVO.ifPresent(vo -> {
			for (final String date : vo.getDateArr()) {
				log.info(date);
			}
			return;
		});
	}

	@Autowired
	UploadPathService service;

	@Test
	public void ServiceTest() {
		final String result = service.getPath();
		log.info(result);
	}

	@Autowired
	DriveFileDelete driveFileDelete;

	@Test
	public void deleteFileTest() {
		try {
			driveFileDelete.remove("1vrTWFW4VwID0gFMzwnpxjA4CdILuZFIm");
		} catch (final Exception e) {
			log.info("★" + e.toString());
		}
	}

	@Test
	public void getThumbnailTest() {

		final HttpClient client = HttpClient.newHttpClient();

		final HttpRequest httpRequest = HttpRequest
				.newBuilder(URI.create("https://drive.google.com/thumbnail?id=" + "12ffZOURDn6rPWWR3DRmKTZyEmnZZ0BCL"))
				.build();
		try {
			final HttpResponse<byte[]> res = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
			log.info(res.body().clone() + "★");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Autowired
	FileListService fileListWebService;

	@Autowired
	BoardAttachMapper boardAttachMapper;

	@Test
	public void testFileListService() {

		var path = uploadPathMapper.getYesterDayFolder();

		var db = boardAttachMapper.findFilesbyFolderName(path.getDay());
		var drive = fileListWebService.getList(path.getDay());

		var driveFileIds = drive.asList().stream()
										.map(o -> o.getId())
										.collect(Collectors.toSet());

		var dbFileIds = db.stream()
						 .map(o -> o.getFileId())
						 .collect(Collectors.toSet());

		var deleteListFromDrive = driveFileIds.stream()
											  .filter(id -> !dbFileIds.contains(id))
											  .collect(Collectors.toList());

		deleteListFromDrive.stream().forEach(e -> log.info("To be deleted : " + e));
	}

	@Test
	public void testFileListDTO() {
		var fileDTO = new FileListDTO(List.of(new Files()));
		log.info(fileDTO.asList().size() + "");
	}


	@Test
	public void testUploadPathMapper() {
		
		log.info("TEST");
		// uploadPathMapper.select();
	}

}
