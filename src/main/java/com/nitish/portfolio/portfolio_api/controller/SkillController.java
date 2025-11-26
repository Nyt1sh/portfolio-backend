package com.nitish.portfolio.portfolio_api.controller;

import com.nitish.portfolio.portfolio_api.dto.SkillDto;
import com.nitish.portfolio.portfolio_api.dto.SkillRequest;
import com.nitish.portfolio.portfolio_api.model.Skill;
import com.nitish.portfolio.portfolio_api.repository.SkillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class SkillController {

    private final SkillRepository skillRepository;

    public SkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    // ---------- helpers ----------

    private SkillDto toDto(Skill s) {
        List<String> chipsList;

        if (s.getChips() == null || s.getChips().isBlank()) {
            chipsList = List.of();
        } else {
            chipsList = Arrays.stream(s.getChips().split(","))
                    .map(String::trim)
                    .filter(str -> !str.isEmpty())
                    .collect(Collectors.toList());
        }

        return SkillDto.builder()
                .id(s.getId())
                .title(s.getTitle())
                .description(s.getDescription())
                .chips(chipsList)
                .build();
    }

    private void applyRequestToEntity(Skill s, SkillRequest req) {
        s.setTitle(req.getTitle());
        s.setDescription(req.getDescription());
        String chipsStr = (req.getChips() == null || req.getChips().isEmpty())
                ? ""
                : String.join(",", req.getChips());
        s.setChips(chipsStr);
    }

    // ---------- PUBLIC: read-only ----------

    // Public endpoint that portfolio uses
    @GetMapping("/content/skills")
    public ResponseEntity<List<SkillDto>> getPublicSkills() {
        List<SkillDto> skills = skillRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(skills);
    }

    // ---------- ADMIN: full CRUD (protected by SecurityConfig /api/admin/**) ----------

    // list for admin dashboard (same as public but under /admin)
    @GetMapping("/admin/skills")
    public ResponseEntity<List<SkillDto>> getAdminSkills() {
        List<SkillDto> skills = skillRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(skills);
    }

    // create skill
    @PostMapping("/admin/skills")
    public ResponseEntity<SkillDto> createSkill(@RequestBody SkillRequest req) {
        Skill s = new Skill();
        applyRequestToEntity(s, req);
        Skill saved = skillRepository.save(s);
        return ResponseEntity.ok(toDto(saved));
    }

    // update skill
    @PutMapping("/admin/skills/{id}")
    public ResponseEntity<?> updateSkill(@PathVariable Long id,
                                         @RequestBody SkillRequest req) {
        Optional<Skill> opt = skillRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Skill not found");
        }

        Skill s = opt.get();
        applyRequestToEntity(s, req);
        Skill saved = skillRepository.save(s);

        return ResponseEntity.ok(toDto(saved));
    }

    // delete skill
    @DeleteMapping("/admin/skills/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        if (!skillRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Skill not found");
        }
        skillRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
