package com.eldercare.modules.demo.service;

import com.eldercare.modules.demo.dto.response.SeedDemoDataResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DemoDataService {

    SeedDemoDataResponse seedDemoData(MultipartFile fixtureFile);

    byte[] exportData(String type, String format);
}
