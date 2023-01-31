package com.example.relayRun.user.oauth2;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class OAuth2Controller {

    @GetMapping("/login")
    @ResponseBody
    public Map<String, String> login(@RequestParam(value = "auth") String access,
                                     @RequestParam(value = "refresh") String refresh) {
        Map<String, String> map = new HashMap<>();
        map.put("access", access);
        map.put("refresh", refresh);

        return map;
    }
}
