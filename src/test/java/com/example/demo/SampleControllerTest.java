package com.example.demo;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;

import com.example.demo.domain.BoardVO;
import com.example.demo.domain.Criteria;
import com.example.demo.mapper.BoardMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ModelMap;

import lombok.extern.java.Log;

// @RunWith(SpringRunner.class)
@Log
@SpringBootTest
@AutoConfigureMockMvc
public class SampleControllerTest {

    @Autowired
    MockMvc mock;

    @Autowired
    BoardMapper boardMapper;

    @Test
    public void testHello() throws Exception {
        mock.perform(get("/sample1"))
        .andExpect(content().string(containsString("Hello world")));
    }

    @Test
    public void testPaging() {
        Criteria cri = new Criteria();
        List<BoardVO> list = boardMapper.getListWithPaging(cri);
        list.forEach(action -> log.info(action.toString()));
    }

    @Test
    public void testPagin2nd() {
        Criteria cri = new Criteria();
        cri.setPageNum(1);
        cri.setAmount(100);
        List<BoardVO> list = boardMapper.getListWithPaging(cri);
        list.forEach(each -> log.info(each.toString()));
    }

    @Test
    public void testListPaging() throws Exception {
        ModelMap viewName;
        viewName = mock.perform(
            get("/board/list")
            .param("pageNum", "2")
            .param("amount", "50"))
            .andReturn()
            .getModelAndView()
            .getModelMap();
            log.info("★："+viewName);
    }

    @Value("${page.amount}")
    private int amout;
    @Test
    public void testGetApplicationProperty() {
        log.info("amout : "+amout);
    }

    private String getYesterDayFolder() {
        LocalDate today = LocalDate.now();
        String yesterDay = today.minusDays(1).toString();
        return yesterDay.replace("-", File.separator);
    }

    @Test
    public  void deleteFiles() {
        log.info("★File Delete Task run...");

        // List<Path> fileListPaths = 
        // dBfileList
        // .stream()
        // .map( vo -> Paths.get(vo.getUploadPath(), vo.getUuid() + "_" + vo.getFileName()))
        // .collect(Collectors.toList());
      
        // dBfileList
        // .stream()
        // .filter(vo -> vo.isFileType())
        // .map(vo -> Paths.get(vo.getUploadPath(), "s_" + vo.getUuid() + "_" + vo.getFileName()))
        // .forEach(vo -> fileListPaths.add(vo));

        File targetDir = Paths.get(getYesterDayFolder()).toFile();

        File[] removeFiles = targetDir.listFiles();


        for(File file : removeFiles) {
            log.log(Level.WARNING, file.getAbsolutePath());
            // file.delete();
        }
    }
}
