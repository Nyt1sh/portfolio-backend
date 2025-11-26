package com.nitish.portfolio.portfolio_api.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.nitish.portfolio.portfolio_api.dto.ContentUpdateRequest;
import com.nitish.portfolio.portfolio_api.model.HeroContent;
import com.nitish.portfolio.portfolio_api.repository.HeroContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.nitish.portfolio.portfolio_api.dto.AboutContentRequest;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;


import java.util.Map;

//@RestController
//@RequestMapping("/api")
//@CrossOrigin
//public class ContentController {
//
//    private static final Long HERO_CONTENT_ID = 1L;
//
//    @Autowired
//    private HeroContentRepository heroContentRepository;
//
//    // PUBLIC ROUTE: Used by the main portfolio to load the content
//    @GetMapping("/content/hero")
//    public ResponseEntity<?> getHeroContent() {
//        return heroContentRepository.findById(HERO_CONTENT_ID)
//                .map(content -> ResponseEntity.ok(content.getContent()))
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // SECURE ROUTE: Used by the admin dashboard to update the content
//    @PutMapping("/admin/content/hero")
//    public ResponseEntity<?> updateHeroContent(@RequestBody ContentUpdateRequest request) {
//        HeroContent content = heroContentRepository.findById(HERO_CONTENT_ID)
//                .orElseGet(() -> HeroContent.builder()
//                        .id(HERO_CONTENT_ID)
//                        .contentKey("hero_description")
//                        .build());
//
//        content.setContent(request.getContent());
//        heroContentRepository.save(content);
//
//        return ResponseEntity.ok("Hero content updated successfully.");
//    }
//}

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ContentController {

    private static final Long HERO_CONTENT_ID = 1L;

    @Autowired
    private HeroContentRepository heroContentRepository;

    // EXISTING: GET /api/content/hero  (you already have)
    @GetMapping("/content/hero")
    public ResponseEntity<?> getHeroContent() {
        return heroContentRepository.findById(HERO_CONTENT_ID)
                .map(content -> ResponseEntity.ok(content.getContent()))
                .orElse(ResponseEntity.notFound().build());
    }

    // NEW: GET /api/content/hero/description  -> returns hero_description (id 1)
    @GetMapping("/content/hero/description")
    public ResponseEntity<?> getHeroDescription() {
        return heroContentRepository.findById(HERO_CONTENT_ID)
                .map(content -> ResponseEntity.ok(content.getContent()))
                .orElse(ResponseEntity.notFound().build());
    }

    // NEW: GET /api/content/hero/image-url  -> returns profile_image_url (search by contentKey)
    @GetMapping("/content/hero/image-url")
    public ResponseEntity<?> getHeroImageUrl() {
        return heroContentRepository.findByContentKey("profile_image_url")
                .map(content -> ResponseEntity.ok(content.getContent()))
                .orElse(ResponseEntity.notFound().build());
    }

    // EXISTING secure update for hero text (but only updates id=1)
    @PutMapping("/admin/content/hero")
    public ResponseEntity<?> updateHeroContent(@RequestBody ContentUpdateRequest request) {
        HeroContent content = heroContentRepository.findById(HERO_CONTENT_ID)
                .orElseGet(() -> HeroContent.builder()
                        .id(HERO_CONTENT_ID)
                        .contentKey("hero_description")
                        .build());

        content.setContent(request.getContent());
        heroContentRepository.save(content);

        return ResponseEntity.ok("Hero content updated successfully.");
    }

    // NEW secure endpoint to update profile image URL (admin)
    @PutMapping("/admin/content/hero/image-url")
    public ResponseEntity<?> updateHeroImageUrl(@RequestBody ContentUpdateRequest request) {
        HeroContent content = heroContentRepository.findByContentKey("profile_image_url")
                .orElseGet(() -> HeroContent.builder()
                        .id(2L) // keep same id you seeded (or generate new logic)
                        .contentKey("profile_image_url")
                        .build());

        content.setContent(request.getContent());
        heroContentRepository.save(content);

        return ResponseEntity.ok("Hero image URL updated successfully.");
    }

    @Autowired
    private Cloudinary cloudinary;

    @PutMapping(value = "/admin/content/hero/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadHeroImage(@RequestPart("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file provided");
            }

            // Define a fixed public_id so uploads overwrite the same resource
            final String PUBLIC_ID = "portfolio/hero_profile"; // folder "portfolio", public id "hero_profile"

            Map uploadOptions = ObjectUtils.asMap(
                    "public_id", PUBLIC_ID,
                    "overwrite", true,
                    "invalidate", true,           // invalidate CDN cache so new image shows immediately
                    "resource_type", "image"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            String url = (String) uploadResult.get("secure_url");

            // Persist the URL in DB (profile_image_url)
            HeroContent content = heroContentRepository.findByContentKey("profile_image_url")
                    .orElseGet(() -> HeroContent.builder()
                            .id(2L)
                            .contentKey("profile_image_url")
                            .build());

            content.setContent(url);
            heroContentRepository.save(content);

            return ResponseEntity.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/content/hero/delete-by-public-id")
    public ResponseEntity<?> deleteHeroImage(@RequestParam String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Delete failed: " + e.getMessage());
        }
    }


    //about section

    // --- helper to save/update by contentKey ---
    private void saveOrUpdateKey(String key, String contentStr) {
        HeroContent content = heroContentRepository.findByContentKey(key)
                .orElseGet(() -> HeroContent.builder().contentKey(key).build());
        content.setContent(contentStr == null ? "" : contentStr);
        heroContentRepository.save(content);
    }

    // GET /api/content/about  (returns JSON)
    @GetMapping("/content/about")
    public ResponseEntity<?> getAboutContent() {
        Map<String, Object> result = new HashMap<>();
        heroContentRepository.findByContentKey("about_headline").ifPresent(h -> result.put("headline", h.getContent()));
        heroContentRepository.findByContentKey("about_subtitle").ifPresent(h -> result.put("subtitle", h.getContent()));
        heroContentRepository.findByContentKey("about_paragraph1").ifPresent(h -> result.put("paragraph1", h.getContent()));
        heroContentRepository.findByContentKey("about_paragraph2").ifPresent(h -> result.put("paragraph2", h.getContent()));
        heroContentRepository.findByContentKey("about_chips").ifPresent(h -> result.put("chips", h.getContent()));
        heroContentRepository.findByContentKey("about_image_url").ifPresent(h -> result.put("imageUrl", h.getContent()));
        heroContentRepository.findByContentKey("about_cv_url").ifPresent(h -> result.put("cvUrl", h.getContent()));
        return ResponseEntity.ok(result);
    }

    // PUT /api/admin/content/about  (save fields)
    @PutMapping("/admin/content/about")
    public ResponseEntity<?> updateAboutContent(@RequestBody AboutContentRequest req) {
        saveOrUpdateKey("about_headline", req.getHeadline());
        saveOrUpdateKey("about_subtitle", req.getSubtitle());
        saveOrUpdateKey("about_paragraph1", req.getParagraph1());
        saveOrUpdateKey("about_paragraph2", req.getParagraph2());
        saveOrUpdateKey("about_chips", req.getChips() == null ? "" : String.join(",", req.getChips()));
        saveOrUpdateKey("about_cv_url", req.getCvUrl());
        return ResponseEntity.ok("About updated");
    }

    // PUT /api/admin/content/about/upload-image  (multipart -> Cloudinary overwrite)
    // PUT /api/admin/content/about/upload-image  (multipart -> Cloudinary overwrite)
    @PutMapping(value = "/admin/content/about/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAboutImage(@RequestPart("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file provided");
            }

            // --- 1) Determine if there's an existing public_id to delete ---
            String oldPublicId = null;

            // 1a. Prefer an explicit stored public_id
            Optional<HeroContent> pubIdRec = heroContentRepository.findByContentKey("about_image_public_id");
            if (pubIdRec.isPresent() && pubIdRec.get().getContent() != null && !pubIdRec.get().getContent().isBlank()) {
                oldPublicId = pubIdRec.get().getContent().trim();
            }

            // 1b. If no public_id stored, try to extract it from stored about_image_url
            if (oldPublicId == null) {
                Optional<HeroContent> urlRec = heroContentRepository.findByContentKey("about_image_url");
                if (urlRec.isPresent() && urlRec.get().getContent() != null && !urlRec.get().getContent().isBlank()) {
                    String existingUrl = urlRec.get().getContent().trim();
                    String extracted = extractPublicIdFromCloudinaryUrl(existingUrl);
                    if (extracted != null && !extracted.isBlank()) {
                        oldPublicId = extracted;
                        // also persist the extracted public_id so future deletes are straightforward
                        saveOrUpdateKey("about_image_public_id", oldPublicId);
                    }
                }
            }

            // --- 2) Attempt to destroy old resource if we found a public_id ---
            if (oldPublicId != null && !oldPublicId.isBlank()) {
                try {
                    Map destroyOptions = ObjectUtils.asMap(
                            "resource_type", "image",
                            "invalidate", true
                    );
                    Map destroyResult = cloudinary.uploader().destroy(oldPublicId, destroyOptions);
                    System.out.println("Cloudinary destroy result for [" + oldPublicId + "]: " + destroyResult);
                } catch (Exception ex) {
                    // Log and continue with new upload — don't fail the entire request
                    System.err.println("Failed to destroy old Cloudinary image [" + oldPublicId + "]: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

            // --- 3) Upload new image with unique public_id ---
            final String newPublicId = "portfolio/about_image_" + System.currentTimeMillis();

            Map uploadOptions = ObjectUtils.asMap(
                    "public_id", newPublicId,
                    "overwrite", true,
                    "invalidate", true,
                    "resource_type", "image"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            String url = (String) uploadResult.get("secure_url");
            String returnedPublicId = (String) uploadResult.get("public_id");

            // --- 4) Persist secure_url and public_id into hero_content (two keys) ---
            saveOrUpdateKey("about_image_url", url);
            saveOrUpdateKey("about_image_public_id", returnedPublicId);

            // --- 5) Return uploaded url ---
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    /**
     * Helper: try to extract Cloudinary public_id from a typical Cloudinary URL.
     * Example Cloudinary URL:
     * https://res.cloudinary.com/<cloud>/image/upload/v1234567890/folder/subfolder/name_ext.jpg
     * => public_id: folder/subfolder/name_ext
     *
     * Returns null if it cannot parse a plausible id.
     */
    private String extractPublicIdFromCloudinaryUrl(String url) {
        if (url == null) return null;
        try {
            // find the "/image/upload/" marker
            int idx = url.indexOf("/image/upload/");
            if (idx == -1) return null;
            String after = url.substring(idx + "/image/upload/".length());

            // remove possible version prefix like v12345/
            if (after.startsWith("v")) {
                int slash = after.indexOf('/');
                if (slash > 0) {
                    after = after.substring(slash + 1);
                }
            }

            // strip file extension if present (.jpg, .png, .webp, etc.)
            int dot = after.lastIndexOf('.');
            String withoutExt = (dot > 0) ? after.substring(0, dot) : after;

            // Return as Cloudinary public_id (e.g. "portfolio/about_image_12345")
            return withoutExt;
        } catch (Exception ex) {
            System.err.println("Failed to extract public id from url [" + url + "]: " + ex.getMessage());
            return null;
        }
    }



}
