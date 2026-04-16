package com.xound.controller;

import com.xound.dto.BandCreateRequest;
import com.xound.dto.BandJoinRequest;
import com.xound.model.Band;
import com.xound.model.BandMember;
import com.xound.service.BandService;
import jakarta.validation.Valid;
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
    public ResponseEntity<Band> createBand(@Valid @RequestBody BandCreateRequest request,
                                            Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        Band band = bandService.createBand(userId, request.getName().trim());
        return ResponseEntity.ok(band);
    }

    @GetMapping("/{bandId}/members")
    public ResponseEntity<List<BandMember>> getMembers(@PathVariable Long bandId) {
        return ResponseEntity.ok(bandService.getMembers(bandId));
    }

    @PostMapping("/{bandId}/members/{userId}")
    public ResponseEntity<Map<String, String>> addMember(@PathVariable Long bandId,
                                                          @PathVariable Long userId,
                                                          Authentication auth) {
        Long adminUserId = (Long) auth.getCredentials();
        bandService.addMember(bandId, adminUserId, userId);
        return ResponseEntity.ok(Map.of("message", "Miembro agregado"));
    }

    @DeleteMapping("/{bandId}/members/{userId}")
    public ResponseEntity<Map<String, String>> removeMember(@PathVariable Long bandId,
                                                             @PathVariable Long userId,
                                                             Authentication auth) {
        Long adminUserId = (Long) auth.getCredentials();
        bandService.removeMember(bandId, adminUserId, userId);
        return ResponseEntity.ok(Map.of("message", "Miembro eliminado"));
    }

    @PostMapping("/leave")
    public ResponseEntity<Map<String, String>> leaveBand(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        bandService.leaveBand(userId);
        return ResponseEntity.ok(Map.of("message", "Saliste de la banda exitosamente"));
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, String>> joinBand(@Valid @RequestBody BandJoinRequest request,
                                                         Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        bandService.addMemberByInviteCode(request.getInviteCode().trim().toUpperCase(), userId);
        return ResponseEntity.ok(Map.of("message", "Te uniste a la banda exitosamente"));
    }

    @PostMapping("/regenerate-code")
    public ResponseEntity<Map<String, String>> regenerateCode(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        String newCode = bandService.regenerateInviteCode(userId);
        return ResponseEntity.ok(Map.of("inviteCode", newCode));
    }
}
