// package: com.nitish.portfolio.portfolio_api.controller

package com.nitish.portfolio.portfolio_api.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.nitish.portfolio.portfolio_api.dto.ProjectDto;
import com.nitish.portfolio.portfolio_api.dto.ProjectRequest;
import com.nitish.portfolio.portfolio_api.model.Project;
import com.nitish.portfolio.portfolio_api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final Cloudinary cloudinary;

    // ---------- helpers ----------

    private ProjectDto toDto(Project p) {
        List<String> tagList;
        if (p.getTags() == null || p.getTags().isBlank()) {
            tagList = List.of();
        } else {
            tagList = Arrays.stream(p.getTags().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        return ProjectDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .tags(tagList)
                .imageUrl(p.getImageUrl())
                .liveUrl(p.getLiveUrl())
                .githubUrl(p.getGithubUrl())
                .displayOrder(p.getDisplayOrder())
                .build();
    }

    private void applyRequestToEntity(Project p, ProjectRequest req) {
        p.setTitle(req.getTitle());
        p.setDescription(req.getDescription());
        p.setDisplayOrder(req.getDisplayOrder());

        p.setLiveUrl(req.getLiveUrl());
        p.setGithubUrl(req.getGithubUrl());

        String tagsStr = (req.getTags() == null || req.getTags().isEmpty())
                ? ""
                : String.join(",", req.getTags());
        p.setTags(tagsStr);
    }

    // ---------- PUBLIC: projects for portfolio ----------

    @GetMapping("/content/projects")
    public ResponseEntity<List<ProjectDto>> getPublicProjects() {
        List<Project> all = projectRepository.findAllByOrderByDisplayOrderAscIdAsc();
        List<ProjectDto> dtos = all.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ---------- ADMIN: CRUD (secured by SecurityConfig /api/admin/**) ----------

    @GetMapping("/admin/projects")
    public ResponseEntity<List<ProjectDto>> getAdminProjects() {
        List<Project> all = projectRepository.findAllByOrderByDisplayOrderAscIdAsc();
        List<ProjectDto> dtos = all.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/admin/projects")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectRequest req) {
        Project p = new Project();
        applyRequestToEntity(p, req);
        Project saved = projectRepository.save(p);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/admin/projects/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id,
                                           @RequestBody ProjectRequest req) {
        Optional<Project> opt = projectRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Project not found");
        }
        Project p = opt.get();
        applyRequestToEntity(p, req);
        Project saved = projectRepository.save(p);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/admin/projects/reorder")
    public ResponseEntity<?> reorderProjects(@RequestBody List<Long> orderedIds) {

        List<Project> all = projectRepository.findAll();
        Map<Long, Project> map = all.stream()
                .collect(Collectors.toMap(Project::getId, p -> p));

        for (int i = 0; i < orderedIds.size(); i++) {
            Long id = orderedIds.get(i);
            Project p = map.get(id);
            if (p != null) {
                p.setDisplayOrder(i);
            }
        }

        projectRepository.saveAll(all);
        return ResponseEntity.ok("Order updated");
    }



    @DeleteMapping("/admin/projects/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        Optional<Project> opt = projectRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Project not found");
        }
        Project p = opt.get();

        // delete image from Cloudinary if exists
        if (p.getImagePublicId() != null && !p.getImagePublicId().isBlank()) {
            try {
                Map destroyOptions = ObjectUtils.asMap(
                        "resource_type", "image",
                        "invalidate", true
                );
                cloudinary.uploader().destroy(p.getImagePublicId(), destroyOptions);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        projectRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ---------- ADMIN: image upload (delete old image first, like About) ----------

    @PutMapping(
            value = "/admin/projects/{id}/upload-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadProjectImage(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            Optional<Project> opt = projectRepository.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(404).body("Project not found");
            }

            Project p = opt.get();

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file provided");
            }

            // 1) Delete old image if exists
            if (p.getImagePublicId() != null && !p.getImagePublicId().isBlank()) {
                try {
                    Map destroyOptions = ObjectUtils.asMap(
                            "resource_type", "image",
                            "invalidate", true
                    );
                    cloudinary.uploader().destroy(p.getImagePublicId(), destroyOptions);
                } catch (Exception ex) {
                    System.err.println("Failed to delete old project image: " + ex.getMessage());
                }
            }

            // 2) Upload new image
            final String newPublicId = "portfolio/projects/project_" + id + "_" + System.currentTimeMillis();

            Map uploadOptions = ObjectUtils.asMap(
                    "public_id", newPublicId,
                    "overwrite", true,
                    "invalidate", true,
                    "resource_type", "image"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            String url = (String) uploadResult.get("secure_url");
            String returnedPublicId = (String) uploadResult.get("public_id");

            // 3) Save URL + public_id in DB
            p.setImageUrl(url);
            p.setImagePublicId(returnedPublicId);
            projectRepository.save(p);

            // Return URL for frontend
            return ResponseEntity.ok(url);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}
