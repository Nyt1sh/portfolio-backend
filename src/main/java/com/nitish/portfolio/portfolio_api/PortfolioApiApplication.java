//package com.nitish.portfolio.portfolio_api;
//
//import com.nitish.portfolio.portfolio_api.model.Admin;
//import com.nitish.portfolio.portfolio_api.model.HeroContent;
//import com.nitish.portfolio.portfolio_api.repository.AdminRepository;
//import com.nitish.portfolio.portfolio_api.repository.HeroContentRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@SpringBootApplication
//@EnableAsync
//@EnableScheduling   // ⬅️ add this
//public class PortfolioApiApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(PortfolioApiApplication.class, args);
//	}
//
//	@Bean
//	public CommandLineRunner createDefaultData(AdminRepository adminRepository,
//											   PasswordEncoder encoder,
//											   HeroContentRepository contentRepository) {
//		return args -> {
//			// --- 1. Admin Creation ---
//			String defaultUsername = "admin";
//
//			if (!adminRepository.findByUsername(defaultUsername).isPresent()) {
//
//				Admin admin = Admin.builder()
//						.username(defaultUsername)
//						.password(encoder.encode("Test")) // hashed
//						.notificationsEnabled(true)
//						.notificationEmail("nyt1sh.dev@gmail.com")
//						.build();
//
//				adminRepository.save(admin);
//
//				System.out.println("\n✨ DEFAULT ADMIN CREATED");
//				System.out.println("Username: admin");
//				System.out.println("Password: Test (but encrypted)\n");
//			} else {
//				System.out.println("✔ Admin already exists. Skipping creation.");
//			}
//
//			// --- 2. Hero Content Initialization ---
//			Long heroDescId = 1L;
//			if (!contentRepository.findById(heroDescId).isPresent()) {
//				HeroContent initialDesc = HeroContent.builder()
//						.id(heroDescId)
//						.contentKey("hero_description")
//						.content("Versatile developer fluent in C, C++, Java, and web stacks. I architect clean codebases, optimize algorithms, and build responsive UIs—bridging logic and creativity to engineer seamless, scalable systems.")
//						.build();
//
//				contentRepository.save(initialDesc);
//				System.out.println("✔ Initial Hero Description created.");
//			} else {
//				System.out.println("✔ Hero Description already exists. Skipping creation.");
//			}
//
//			// --- 3. Profile Image URL Initialization (New) ---
//			Long heroImageId = 2L;
//			// --- 2. Hero Content Initialization ---
//			// If table is empty, insert initial rows. Otherwise, don't touch it.
//			if (contentRepository.count() == 0) {
//				HeroContent initialDesc = HeroContent.builder()
//						.contentKey("hero_description")
//						.content("Versatile developer fluent in C, C++, Java, and web stacks. I architect clean codebases, optimize algorithms, and build responsive UIs—bridging logic and creativity to engineer seamless, scalable systems.")
//						.build();
//				contentRepository.save(initialDesc);
//
//				HeroContent initialImage = HeroContent.builder()
//						.contentKey("profile_image_url")
//						.content("https://placehold.co/400x400/9333ea/ffffff/svg?text=Your+Profile+Image")
//						.build();
//				contentRepository.save(initialImage);
//
//				System.out.println("✔ Initial Hero Description & Profile Image created.");
//			} else {
//				System.out.println("✔ HeroContent already exists. Skipping initialization.");
//			}
//
//		};
//	}
//}

package com.nitish.portfolio.portfolio_api;

import com.nitish.portfolio.portfolio_api.model.Admin;
import com.nitish.portfolio.portfolio_api.model.HeroContent;
import com.nitish.portfolio.portfolio_api.repository.AdminRepository;
import com.nitish.portfolio.portfolio_api.repository.HeroContentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class PortfolioApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortfolioApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner createDefaultData(AdminRepository adminRepository,
											   PasswordEncoder encoder,
											   HeroContentRepository contentRepository) {
		return args -> {
			// --- 1. Admin Creation ---
			String defaultUsername = "admin";

			if (adminRepository.findByUsername(defaultUsername).isEmpty()) {

				Admin admin = Admin.builder()
						.username(defaultUsername)
						.password(encoder.encode("Test")) // hashed
						.notificationsEnabled(true)
						.notificationEmail("nyt1sh.dev@gmail.com")
						.build();

				adminRepository.save(admin);

				System.out.println("\n✨ DEFAULT ADMIN CREATED");
				System.out.println("Username: admin");
				System.out.println("Password: Test (but encrypted)\n");
			} else {
				System.out.println("✔ Admin already exists. Skipping creation.");
			}

			// --- 2. Hero Content Initialization ---
			// If table is empty, insert initial rows. Otherwise, don't touch it.
			if (contentRepository.count() == 0) {
				HeroContent initialDesc = HeroContent.builder()
						.contentKey("hero_description")
						.content("Versatile developer fluent in C, C++, Java, and web stacks. I architect clean codebases, optimize algorithms, and build responsive UIs—bridging logic and creativity to engineer seamless, scalable systems.")
						.build();
				contentRepository.save(initialDesc);

				HeroContent initialImage = HeroContent.builder()
						.contentKey("profile_image_url")
						.content("https://placehold.co/400x400/9333ea/ffffff/svg?text=Your+Profile+Image")
						.build();
				contentRepository.save(initialImage);

				System.out.println("✔ Initial Hero Description & Profile Image created.");
			} else {
				System.out.println("✔ HeroContent already exists. Skipping initialization.");
			}
		};
	}
}
