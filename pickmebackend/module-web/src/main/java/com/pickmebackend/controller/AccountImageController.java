package com.pickmebackend.controller;

import com.pickmebackend.service.AccountImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/images")
@RequiredArgsConstructor
public class AccountImageController {

    private final AccountImageService accountImageService;

    @PostMapping
    public ResponseEntity<?> saveImage(@RequestParam("image") MultipartFile image, HttpServletRequest request) {
        return accountImageService.saveImage(image, request);
    }

    @GetMapping(value = "/{imageName:.+}")
    public ResponseEntity<?> getImage(@PathVariable String imageName, HttpServletRequest request) {
        return accountImageService.loadAsResource(imageName, request);
    }
}
