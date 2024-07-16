package com.taishow.controller.cms;

import com.taishow.dto.Result;
import com.taishow.service.cms.StillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/still")
public class StillController {
    StillService stillService;

    public StillController(StillService stillService) {
        this.stillService = stillService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadStills(@RequestParam("files")List<MultipartFile> files,
                                       @RequestParam("movieId") Integer movieId) {
        try {
            List<String> stills = files.stream().map(file -> {
                        try {
                            String mimeType = file.getContentType();
                            String base64 = Base64.getEncoder().encodeToString(file.getBytes()); // 必須有IO例外儲儷
                            return "data:" + mimeType + ";base64," + base64;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
            stillService.insertStills(stills, movieId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getStills/{movieId}")
    public Result getStillsByMovieId(@PathVariable Integer movieId) {
        return stillService.getStillsByMovieId(movieId);
    }
}
