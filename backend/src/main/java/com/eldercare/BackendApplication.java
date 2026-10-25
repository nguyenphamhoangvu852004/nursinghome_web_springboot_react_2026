package com.eldercare;

import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.admin.user_management.RoleEntity;
import com.eldercare.modules.admin.user_management.role.repository.RoleRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CareGoalRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CarePlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		loadEnv();
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner testRepository(CareGoalRepository repo, CarePlanRepository carePlanRepository) {
			return args -> {
//				repo.findAll().forEach(System.out::println);
//				System.out.println(carePlanRepository.findById((long)5).get().toString());
			};
	}



//	@Bean
	@Profile("!test")
	public CommandLineRunner activateAdmin(UserRepository userRepository, RoleRepository roleRepository) {
		return args -> {
			// Ensure CNA role exists
			if (roleRepository.findByRoleName("CNA").isEmpty()) {
				RoleEntity cnaRole = RoleEntity.builder()
						.roleName("CNA")
						.description("Certified Nursing Assistant")
						.isDeleted(false)
						.createdAt(OffsetDateTime.now())
						.updatedAt(OffsetDateTime.now())
						.build();
				roleRepository.save(cnaRole);
				System.out.println(">>> [STARTUP] Seeded CNA role");
			}

			Optional<UserEntity> userOpt = userRepository.findByEmailAndIsDeletedFalse("daniel.brooks@nhms-demo.local");
			if (userOpt.isPresent()) {
				UserEntity user = userOpt.get();
				if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
					user.setStatus("ACTIVE");
					userRepository.save(user);
					System.out.println(">>> [STARTUP] Activated daniel.brooks@nhms-demo.local");
				}
			}
		};
	}

	private static void loadEnv() {
		String[] paths = {".env", "backend/.env", "../.env"};
		for (String envPath : paths) {
			Path path = Paths.get(envPath);
			if (Files.exists(path)) {
				try {
					List<String> lines = Files.readAllLines(path);
					for (String line : lines) {
						line = line.trim();
						if (line.isEmpty() || line.startsWith("#")) {
							continue;
						}
						int eqIdx = line.indexOf('=');
						if (eqIdx > 0) {
							String key = line.substring(0, eqIdx).trim();
							String value = line.substring(eqIdx + 1).trim();
							if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
								value = value.substring(1, value.length() - 1);
							} else if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
								value = value.substring(1, value.length() - 1);
							}
							System.setProperty(key, value);
						}
					}
					System.out.println("Loaded environment variables from " + path.toAbsolutePath());
					return;
				} catch (IOException e) {
					System.err.println("Failed to read environment file: " + path.toAbsolutePath() + " - " + e.getMessage());
				}
			}
		}
		System.out.println("No .env file found. Relying on system environment variables.");
	}

}
