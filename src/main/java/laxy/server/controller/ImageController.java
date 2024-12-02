package laxy.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import laxy.server.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "이미지 관련 API")
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "이미지 업로드. Header: {Content-Type: multipart/form-data}, Body(form-data): {image: IMAGE_FILE}")
    @PostMapping("/image")
    public ResponseEntity<String> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image){
        String imageId = imageService.upload(image);
        return ResponseEntity.ok(imageId);
    }
    @Operation(summary = "이미지 삭제. Params: {addr: IMAGE_URL}")
    @DeleteMapping("/image")
    public ResponseEntity<String> s3delete(@RequestParam String addr){
        imageService.deleteImageFromS3(addr);
        return ResponseEntity.ok("success");
    }
}