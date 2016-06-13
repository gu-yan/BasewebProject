package cn.gy.test.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/web")
public class WebController {

    @RequestMapping(value = "/book", method = RequestMethod.GET)
    public ResponseEntity<String> getBook() {
        ResponseEntity<String> res = new ResponseEntity<String>("", HttpStatus.OK);
        return res;
    }
}
