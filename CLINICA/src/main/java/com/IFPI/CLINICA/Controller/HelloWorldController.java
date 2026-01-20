package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Service.clinicaTeste.ClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello-world")
 //pasta para mexer nas funções do spring
public class HelloWorldController {

    @Autowired
    private ClinicaService helloWorldService;

    /*public HelloWorldController(ClinicaService helloWorldService){
        this.helloWorldService = helloWorldService;
    }*/ //PODE-SE INICIAR A CLASSE USANDO CONSTRUTOR OU USANDO @Autowired


    @GetMapping
    public String helloworld() {
        return helloWorldService.helloWorld("Ryan");
    }
}
