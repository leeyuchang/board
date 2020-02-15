package com.example.demo.controller;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import com.example.demo.domain.BoardAttachVO;
import com.example.demo.domain.BoardVO;
import com.example.demo.domain.Criteria;
import com.example.demo.domain.PageDTO;
import com.example.demo.domain.Response;
import com.example.demo.form.ModifyForm;
import com.example.demo.form.RegisterForm;
import com.example.demo.service.BoardService;
import com.example.demo.service.FileDeleteService;
import com.example.demo.service.FileDeletedException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.java.Log;

@Log
@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private FileDeleteService filedeleteService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/board/list")
    public String getListForm(@ModelAttribute("cri") Criteria cri, Model model, HttpSession session) {

        log.info(" /board/list start with sessionId :" + session.getId()); // TODO Session
                                                                           // ID取得に問題があり、Clientに対し、ここでSession開始する。

        log.info("★/board/list：" + cri);
        model.addAttribute("list", boardService.getList(cri));
        int total = boardService.getTotol(cri);
        log.info("total : " + total);
        model.addAttribute("pageMaker", new PageDTO(cri, total));
        return "board/list";
    }

    // @PreAuthorize("isAuthenticated()")
    @GetMapping("/board/register")
    public String getRegisterForm(@ModelAttribute("formData") RegisterForm form, @ModelAttribute("cri") Criteria cri) {
        return "board/register";
    }

    // @PreAuthorize("isAuthenticated()")
    @PostMapping("/board/register")
    public String postRegister(@ModelAttribute("formData") @Validated RegisterForm form, BindingResult result,
            RedirectAttributes rda, Locale locale, @ModelAttribute("cri") Criteria cri) {

        log.info("Post:/board/register" + form);

        if (result.hasErrors()) {
            return getRegisterForm(form, cri);
        }

        BoardVO boardVO = new BoardVO();
        BeanUtils.copyProperties(form, boardVO);

        boardService.register(boardVO);
        if (boardVO.getBno() != null) {
            Response response = new Response();
            response.setResult(messageSource.getMessage("result.message.regist",
                    new String[] { String.valueOf(boardVO.getBno()) }, locale));
            response.setCode("code");
        }

        return "redirect:/board/list" + cri.getListLink();
    }

    @GetMapping("/board/get")
    public void getForm(@RequestParam("bno") Long bno, @ModelAttribute("cri") Criteria cri, Model model) {

        model.addAttribute("formData", boardService.get(bno));
    }

    @GetMapping("/board/modify")
    public void modifyForm(@RequestParam("bno") Long bno, @ModelAttribute("cri") Criteria cri, Model model) {
        model.addAttribute("formData", boardService.get(bno));
    }

    public String modifyValidated(ModifyForm form) {
        return "/board/modify";
    }

    @PostMapping("/board/modify")
    public String postModify(@ModelAttribute("formData") @Validated ModifyForm form, BindingResult result,
            @ModelAttribute("cri") Criteria cri, RedirectAttributes rda, Locale locale) {

        log.info("Post:/board/modify" + form);

        if (result.hasErrors()) {
            return modifyValidated(form);
        }

        BoardVO boardVO = new BoardVO();
        BeanUtils.copyProperties(form, boardVO);

        Response response = new Response();
        if (boardService.modify(boardVO)) {
            String msg = messageSource.getMessage("result.message.update.success",
                    new String[] { String.valueOf(boardVO.getBno()) }, locale);
            response.setResult(msg);
            response.setCode("code");
        } else {
            String msg = messageSource.getMessage("result.message.update.failure",
                    new String[] { String.valueOf(boardVO.getBno()) }, locale);
            response.setResult(msg);
            response.setCode("code");
        }
        rda.addFlashAttribute("response", response);

        return "redirect:/board/list" + cri.getListLink();
    }

    @PostMapping("/board/remove")
    public String postRemove(@RequestParam("bno") Long bno, @ModelAttribute("cri") Criteria cri,
            RedirectAttributes rda) {

        log.info("remove..." + bno);
        List<BoardAttachVO> attachList = boardService.getAttachList(bno);

        Response response = new Response();

        if (boardService.remove(bno) == 1) {

            try {
                deleteFiles(attachList);
                response.setResult("success");
                response.setCode("");
                rda.addFlashAttribute("response", response);
            } catch (FileDeletedException e) {
                response.setResult(e.getMessage());
                response.setCode("");
                rda.addFlashAttribute("response", response);
            }
        } else {
            response.setResult("error");
            response.setCode("");
            rda.addFlashAttribute("response", response);
        }
        return "redirect:/board/list" + cri.getListLink();
    }

    private void deleteFiles(List<BoardAttachVO> attachList) {

        log.info("delete attach file....");

        if (attachList == null || attachList.isEmpty()) {
            throw new IllegalArgumentException();
        }

       var list = attachList.parallelStream()
                            .map(o -> CompletableFuture.runAsync(()->filedeleteService.delete(o.getFileId())))
                            .collect(Collectors.toList());

       CompletableFuture.allOf(list.toArray(new CompletableFuture[list.size()])).join();
    }

    @GetMapping("/board/getAttachList")
    public ResponseEntity<List<BoardAttachVO>> getAttachList(Long bno) {
        log.info("★ getAttachList :" + bno);
        return new ResponseEntity<>(boardService.getAttachList(bno), HttpStatus.OK);
    }

}