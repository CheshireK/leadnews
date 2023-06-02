package com.myapp.controller;

import com.myapp.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/fm")
public class FreeMarkerController {
    @GetMapping("/t1")
    public String test01(Model model){
        model.addAttribute("name", "FreeMarker");
        Student student = new Student();
        student.setAge(18);
        student.setName("小红");
        student.setBirthday(new Date());
        model.addAttribute("stu", student);
        return "01-basic";
    }
}
