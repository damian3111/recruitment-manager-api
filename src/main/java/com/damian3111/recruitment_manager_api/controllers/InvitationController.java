package com.damian3111.recruitment_manager_api.controllers;

import com.damian3111.recruitment_manager_api.persistence.entities.InvitationEntity;
import com.damian3111.recruitment_manager_api.services.InvitationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.openapitools.api.InvitationsApi;
import org.openapitools.model.DeleteInvitation200Response;
import org.openapitools.model.InvitationDto;
import org.openapitools.model.UpdateInvitationStatusRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class InvitationController implements InvitationsApi {

    private final InvitationService invitationService;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<List<InvitationDto>> getAllInvitations() {
        return ResponseEntity.ok(invitationService.getAllInvitations().stream().map(i -> modelMapper.map(i, InvitationDto.class)).collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<List<InvitationDto>> getUserRelatedInvitations(Long userId, String email) {
        return ResponseEntity.ok(invitationService.getUserRelatedInvitations(userId, email).stream().map(i -> modelMapper.map(i, InvitationDto.class)).collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<List<InvitationDto>> getInvitationsByCandidateAndRecruiter(Long candidateId, Long recruiterId) {
        return ResponseEntity.ok(invitationService.getInvitationsByCandidateAndRecruiter(candidateId, recruiterId).orElseThrow()
                        .stream().map(i -> modelMapper.map(i, InvitationDto.class)).collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<List<InvitationDto>> getInvitationsByRecruiterId(Long recruiterId) {
        return ResponseEntity.ok(invitationService.getInvitationsByRecruiter(recruiterId).orElseThrow()
                .stream().map(i -> modelMapper.map(i, InvitationDto.class)).collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<InvitationDto> sendInvitation(InvitationDto invitationDto) {
        InvitationEntity invitation = invitationService.createInvitation(invitationDto);
        return ResponseEntity.ok(modelMapper.map(invitation, InvitationDto.class));
    }

    @Override
    public ResponseEntity<InvitationDto> updateInvitationStatus(Long id, UpdateInvitationStatusRequest updateInvitationStatusRequest) {
        InvitationEntity invitationEntity = invitationService.updateInvitationStatusById(id, updateInvitationStatusRequest);
        return ResponseEntity.ok(modelMapper.map(invitationEntity, InvitationDto.class));
    }

    @Override
    public ResponseEntity<DeleteInvitation200Response> deleteInvitation(Long id) {
        invitationService.deleteInvitation(id);
        return ResponseEntity.ok(new DeleteInvitation200Response());
    }

    @Override
    public ResponseEntity<List<InvitationDto>> getInvitationsByJobUserId(Long userId, String email) {
        List<InvitationEntity> invitationEntities = invitationService.getAcceptedInvitations(userId, email).orElseThrow();
        return ResponseEntity.ok(invitationEntities.stream().map(i -> modelMapper.map(i, InvitationDto.class)).collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<List<InvitationDto>> getInvitationsByEmail(Long userId, String email) {
        List<InvitationEntity> invitationEntities = invitationService.getInvitationsReceivedByRecruited2(userId, email).orElseThrow();
        return ResponseEntity.ok(invitationEntities.stream().map(i -> modelMapper.map(i, InvitationDto.class)).collect(Collectors.toList()));
    }

}