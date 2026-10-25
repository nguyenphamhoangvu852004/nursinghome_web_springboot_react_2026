package com.eldercare.modules.demo.controller;

import com.eldercare.modules.demo.dto.response.SeedDemoDataResponse;
import com.eldercare.modules.demo.service.DemoDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class DemoDataController {

    private final DemoDataService demoDataService;

    @PostMapping(
            value = "/demo-data-seeder",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<SeedDemoDataResponse> seedDemoData(
            @RequestParam("fixture_file") MultipartFile fixtureFile
    ) {
        System.out.println(">>> seedDemoData called");
        return ResponseEntity.ok(demoDataService.seedDemoData(fixtureFile));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportData(
            @RequestParam String type,
            @RequestParam String format
    ) {
        String normalizedFormat = format.toLowerCase();

        byte[] fileData = demoDataService.exportData(type, normalizedFormat);

        MediaType mediaType = "pdf".equals(normalizedFormat)
                ? MediaType.APPLICATION_PDF
                : MediaType.parseMediaType("text/csv; charset=UTF-8");

        String filename = type + "." + normalizedFormat;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""
                )
                .body(fileData);
    }
}
