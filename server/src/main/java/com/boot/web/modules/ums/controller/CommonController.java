package com.boot.web.modules.ums.controller;

import com.boot.web.common.api.CommonResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import okhttp3.*;

@RestController
@Api(tags = "CommonController")
@Tag(name = "CommonController",description = "公共API")
@RequestMapping("/common")
public class CommonController {

    @Value("${upload.directory}")
    private String uploadDirectory;

    @Value("${baidu.api.key}")
    private String apiKey;

    @Value("${baidu.secret.key}")
    private String secretKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @ApiOperation("上传图片")
    @PostMapping("/upload")
    public CommonResult<String> handleImageUpload(@RequestParam("file") MultipartFile file) {
        try {
            // 生成文件名
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // 构建目标路径
            Path targetPath = Paths.get(uploadDirectory, fileName);

            // 如果目标目录不存在，则创建目录
            if (!Files.exists(targetPath.getParent())) {
                Files.createDirectories(targetPath.getParent());
            }

            // 将文件保存到指定目录
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 获取文件相对路径
            String relativePath = fileName;

            return CommonResult.success(relativePath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return CommonResult.failed("上传失败");
        }
    }

    @ApiOperation("上传文件")
    @PostMapping("/uploadFile")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            // 生成文件名
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // 构建目标路径
            Path targetPath = Paths.get(uploadDirectory + "/file", fileName);

            // 如果目标目录不存在，则创建目录
            if (!Files.exists(targetPath.getParent())) {
                Files.createDirectories(targetPath.getParent());
            }

            // 将文件保存到指定目录
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 获取文件相对路径
            return fileName;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "上传失败";
        }
    }

//    https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=PteXuiZlsm9Q3pE1hZJfnrZt&client_secret=bb09UXEZbpJdWN4tMWcjbT2TsQunI387
//    https://tsn.baidu.com/text2audio?tex=slogan&tok=24.b89164623af43faec631cb6056b47e99.2592000.1745593526.282335-118233772&cuid=test_device_aidenz233&ctp=1&lan=zh
    @GetMapping("/synthesize")
    public ResponseEntity<byte[]> synthesize(@RequestParam String text) throws IOException {
        // Step 1: 获取 Access Token
        String accessToken = getAccessToken(apiKey, secretKey);

        // Step 2: 调用百度语音合成 API
        String url = "https://tsn.baidu.com/text2audio";
        RequestBody requestBody = new FormBody.Builder()
                .add("tex", text)
                .add("tok", accessToken)
                .add("cuid", "test_device_aidenz233") // 可以是设备唯一标识符
                .add("ctp", "1")
                .add("lan", "zh")
                .build();

        Request request = new Request.Builder().url(url).post(requestBody).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            byte[] audioBytes = response.body().bytes();
            return ResponseEntity.ok()
                    .header("Content-Type", "audio/mp3")
                    .body(audioBytes);
        }
    }

    private String getAccessToken(String apiKey, String secretKey) throws IOException {
        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + apiKey + "&client_secret=" + secretKey;
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            return objectMapper.readTree(responseBody).get("access_token").asText();
        }
    }

}
