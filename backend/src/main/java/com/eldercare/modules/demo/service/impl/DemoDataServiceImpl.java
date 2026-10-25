package com.eldercare.modules.demo.service.impl;

import com.eldercare.modules.demo.dto.response.SeedDemoDataResponse;
import com.eldercare.modules.demo.export.DemoExportService;
import com.eldercare.modules.demo.seeder.DemoDataSeeder;
import com.eldercare.modules.demo.service.DemoDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DemoDataServiceImpl implements DemoDataService {

    private final DemoDataSeeder demoDataSeeder;
    private final DemoExportService demoExportService;

    @Override
    public SeedDemoDataResponse seedDemoData(MultipartFile fixtureFile) {
        return demoDataSeeder.seed(fixtureFile);
    }

    @Override
    public byte[] exportData(String type, String format) {
        return demoExportService.export(type, format);
    }
}
