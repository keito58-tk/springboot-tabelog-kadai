package com.example.tabelog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.tabelog.entity.Term;
import com.example.tabelog.service.TermService;

@Controller
@RequestMapping("/terms")
public class TermController {
	private final TermService termService;
	 
    public TermController(TermService termService) {
        this.termService = termService;
    }

    @GetMapping
    public String index(Model model) {
        Term term = termService.findFirstTermByOrderByIdDesc();

        model.addAttribute("term", term);

        return "terms/index";
    }
}
