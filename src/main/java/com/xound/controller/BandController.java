package com.xound.controller;

import com.xound.model.Band;
import com.xound.model.BandMember;
import com.xound.service.BandService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bands")
public class BandController {

    private final BandService bandService;

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBand(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        Band band = bandService.getBandByAdmin(userId);
        if (band == null) {
            return ResponseEntity.ok(Map.of("band", (Object) "null"));
        }
        return ResponseEntity.ok(band);
    }

    @GetMapping("/member")
    public ResponseEntity<?> getMyBandAsMember(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        Band band = bandService.getBandByMember(userId);
        if (band == null) {
            return ResponseEntity.ok(Map.of("band", (Object) "null"));
        }
        return ResponseEntity.ok(band);
    }

    @PostMapping
    public ResponseEntity<?> createBand(@RequestBody Map<String, String> body, Authentication auth) {
        try {
            Long userId = (Long) auth.getCredentials();
            String name = body.get("name");
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio"));
            }
            Band band = bandService.createBand(userId, name.trim());
            return ResponseEntity.ok(band);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{bandId}/members")
    public ResponseEntity<List<BandMember>> getMembers(@PathVariable Long bandId) {
        return ResponseEntity.ok(bandService.getMembers(bandId));
    }

    @PostMapping("/{bandId}/members/{userId}")
    public ResponseEntity<?> addMember(@PathVariable Long bandId, @PathVariable Long userId, Authentication auth) {
        try {
            Long adminUserId = (Long) auth.getCredentials();
            bandService.addMember(bandId, adminUserId, userId);
            return ResponseEntity.ok(Map.of("message", "Miembro agregado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{bandId}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long bandId, @PathVariable Long userId, Authentication auth) {
        try {
            Long adminUserId = (Long) auth.getCredentials();
            bandService.removeMember(bandId, adminUserId, userId);
            return ResponseEntity.ok(Map.of("message", "Miembro eliminado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leaveBand(Authentication auth) {
        try {
            Long userId = (Long) auth.getCredentials();
            bandService.leaveBand(userId);
            return ResponseEntity.ok(Map.of("message", "Saliste de la banda exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinBand(@RequestBody Map<String, String> body, Authentication auth) {
        try {
            Long userId = (Long) auth.getCredentials();
            String inviteCode = body.get("inviteCode");
            if (inviteCode == null || inviteCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Código de invitación requerido"));
            }
            bandService.addMemberByInviteCode(inviteCode.trim().toUpperCase(), userId);
            return ResponseEntity.ok(Map.of("message", "Te uniste a la banda exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/regenerate-code")
    public ResponseEntity<?> regenerateCode(Authentication auth) {
        try {
            Long userId = (Long) auth.getCredentials();
            String newCode = bandService.regenerateInviteCode(userId);
            return ResponseEntity.ok(Map.of("inviteCode", newCode));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
