package com.pvp.travelmatch.service;

import com.pvp.travelmatch.entity.*;
import com.pvp.travelmatch.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchRequestService {

    private final MatchRequestRepository matchRequestRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final UserRepository userRepository;
    private final TravelPartnerRepository travelPartnerRepository;

    // Send Match Request
    public MatchRequest sendRequest(Long travelPlanId) {

        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TravelPlan plan = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new RuntimeException("Travel plan not found"));

        User receiver = plan.getUser();

        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("You cannot send request to yourself");
        }

        // 🔥 CHECK DUPLICATE
        Optional<MatchRequest> existing =
                matchRequestRepository.findBySenderIdAndTravelPlanId(
                        sender.getId(),
                        travelPlanId
                );

        if (existing.isPresent()) {

            MatchRequest request = existing.get();

            if (request.getStatus().equals("PENDING")) {
                throw new RuntimeException("Request already sent and pending");
            }

            if (request.getStatus().equals("ACCEPTED")) {
                throw new RuntimeException("You are already matched with this user");
            }

            if (request.getStatus().equals("REJECTED")) {
                throw new RuntimeException("Your previous request was rejected");
            }
        }

        MatchRequest request = MatchRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .travelPlan(plan)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        return matchRequestRepository.save(request);
    }

    // Accept / Reject
    public MatchRequest updateStatus(Long requestId, String status) {

        MatchRequest request = matchRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(status);

        MatchRequest updated = matchRequestRepository.save(request);

        // 🔥 If ACCEPTED → create TravelPartner
        if (status.equals("ACCEPTED")) {

            TravelPartner partner = TravelPartner.builder()
                    .userOne(request.getSender())
                    .userTwo(request.getReceiver())
                    .travelPlan(request.getTravelPlan())
                    .createdAt(LocalDateTime.now())
                    .build();

            travelPartnerRepository.save(partner);
        }

        return updated;
    }

    // View Incoming Requests
    public List<MatchRequest> getMyRequests() {

        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return matchRequestRepository.findByReceiver(user);
    }
}