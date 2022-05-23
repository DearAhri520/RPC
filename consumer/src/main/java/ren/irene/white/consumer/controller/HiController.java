package ren.irene.white.consumer.controller;

import annotation.RpcAutowired;
import api.HiService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author DearAhri520
 */
@Controller
public class HiController {

    @RpcAutowired
    private HiService hiService;

    @GetMapping("/hi")
    public ResponseEntity<String> hiService(@RequestParam("name") String name) {
        return ResponseEntity.ok(hiService.sayHi(name));
    }
}
