package cn.gy.test.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class LoginController {

    @RequestMapping(value = "/verifycode", method = RequestMethod.GET, produces = "image/jpg")
    @ResponseBody
    public byte[] getVerificationCode(HttpServletRequest request, HttpServletResponse response) {

        return null;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(HttpServletRequest request) {

        return "";
    }
}
