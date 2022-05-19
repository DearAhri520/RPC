package ren.irene.white.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import provider.HiService;

/**
 * @author DearAhri520
 */
@Controller
@RestController
public class HiController {

    @Autowired
    private HiService hiService;

    @GetMapping("/hi")
    public ResponseEntity<String> hi(@RequestParam("name") String name){
        return ResponseEntity.ok(hiService.sayHi(name));
    }
}
