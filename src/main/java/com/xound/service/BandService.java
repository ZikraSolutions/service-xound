package com.xound.service;

import com.xound.model.Band;
import com.xound.model.BandMember;
import com.xound.model.User;
import com.xound.repository.BandRepository;
import com.xound.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BandService {

    private final BandRepository bandRepository;
    private final UserRepository userRepository;

    public BandService(BandRepository bandRepository, UserRepository userRepository) {
        this.bandRepository = bandRepository;
        this.userRepository = userRepository;
    }

    public Band getOrCreateBand(Long adminUserId, String bandName) {
        return bandRepository.findByAdminUserId(adminUserId)
                .orElseGet(() -> {
                    Band band = new Band();
                    band.setName(bandName);
                    band.setAdminUserId(adminUserId);
                    band.setInviteCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    bandRepository.save(band);
                    return bandRepository.findByAdminUserId(adminUserId)
                            .orElseThrow(() -> new RuntimeException("Error al crear banda"));
                });
    }

    public Band getBandByAdmin(Long adminUserId) {
        return bandRepository.findByAdminUserId(adminUserId).orElse(null);
    }

    public Band getBandByMember(Long userId) {
        return bandRepository.findByMemberUserId(userId).orElse(null);
    }

    public Band createBand(Long adminUserId, String name) {
        if (bandRepository.findByAdminUserId(adminUserId).isPresent()) {
            throw new RuntimeException("Ya tienes una banda creada");
        }
        Band band = new Band();
        band.setName(name);
        band.setAdminUserId(adminUserId);
        band.setInviteCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bandRepository.save(band);
        return bandRepository.findByAdminUserId(adminUserId)
                .orElseThrow(() -> new RuntimeException("Error al crear banda"));
    }

    public List<BandMember> getMembers(Long bandId) {
        return bandRepository.findMembers(bandId);
    }

    public void leaveBand(Long userId) {
        Band band = bandRepository.findByMemberUserId(userId)
                .orElseThrow(() -> new RuntimeException("No perteneces a ninguna banda"));
        bandRepository.removeMember(band.getId(), userId);
    }

    public void addMemberByInviteCode(String inviteCode, Long userId) {
        Band band = bandRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("Código de invitación inválido"));

        // Only musicians can join a band - admins/super_admins already have their own
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!"MUSICIAN".equals(user.getRoleName())) {
            throw new RuntimeException("Solo los músicos pueden unirse a una banda");
        }

        if (bandRepository.isMember(band.getId(), userId)) {
            throw new RuntimeException("El usuario ya es miembro de la banda");
        }
        bandRepository.addMember(band.getId(), userId);
    }

    public void addMember(Long bandId, Long adminUserId, Long userId) {
        Band band = bandRepository.findByAdminUserId(adminUserId)
                .orElseThrow(() -> new RuntimeException("No tienes una banda"));
        if (!band.getId().equals(bandId)) {
            throw new RuntimeException("No tienes permiso para esta banda");
        }
        bandRepository.addMember(bandId, userId);
    }

    public void removeMember(Long bandId, Long adminUserId, Long userId) {
        Band band = bandRepository.findByAdminUserId(adminUserId)
                .orElseThrow(() -> new RuntimeException("No tienes una banda"));
        if (!band.getId().equals(bandId)) {
            throw new RuntimeException("No tienes permiso para esta banda");
        }
        bandRepository.removeMember(bandId, userId);
    }

    public String regenerateInviteCode(Long adminUserId) {
        Band band = bandRepository.findByAdminUserId(adminUserId)
                .orElseThrow(() -> new RuntimeException("No tienes una banda"));
        String newCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        bandRepository.updateInviteCode(band.getId(), newCode);
        return newCode;
    }
}
