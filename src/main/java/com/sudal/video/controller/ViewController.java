package com.sudal.video.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author SUDAL
 */
@Controller
public class ViewController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

}
