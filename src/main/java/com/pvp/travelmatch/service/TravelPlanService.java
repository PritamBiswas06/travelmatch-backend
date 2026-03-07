package com.pvp.travelmatch.service;

import com.pvp.travelmatch.dto.MatchResponse;
import com.pvp.travelmatch.dto.TravelPlanRequest;
import com.pvp.travelmatch.entity.TravelPlan;
import com.pvp.travelmatch.entity.User;
import com.pvp.travelmatch.repository.TravelPlanRepository;
import com.pvp.travelmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelPlanService {

    private final TravelPlanRepository travelPlanRepository;
    private final UserRepository userRepository;

    public TravelPlan createPlan(TravelPlanRequest request) {

        // 🔥 Get logged-in user email from JWT
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TravelPlan plan = TravelPlan.builder()
                .fromLocation(request.getFromLocation())
                .destination(request.getDestination())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .budget(request.getBudget())
                .travelType(request.getTravelType())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        return travelPlanRepository.save(plan);
    }

//    public List<TravelPlan> findMatches(Long planId) {
//
//        TravelPlan myPlan = travelPlanRepository.findById(planId)
//                .orElseThrow(() -> new RuntimeException("Plan not found"));
//
//        List<TravelPlan> candidates = travelPlanRepository.findMatchingPlans(
//                myPlan.getDestination(),
//                myPlan.getStartDate(),
//                myPlan.getEndDate(),
//                myPlan.getUser().getId()
//        );
//
//        // 🔥 Budget Filtering (within 30% difference)
//        return candidates.stream()
//                .filter(plan -> {
//                    double myBudget = myPlan.getBudget();
//                    double otherBudget = plan.getBudget();
//
//                    double difference = Math.abs(myBudget - otherBudget);
//                    return difference <= myBudget * 0.3;
//                })
//                .toList();
//    }




    public List<MatchResponse> findMatches(Long planId) {

        TravelPlan myPlan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        List<TravelPlan> candidates = travelPlanRepository.findMatchingPlans(
                myPlan.getDestination(),
                myPlan.getStartDate(),
                myPlan.getEndDate(),
                myPlan.getUser().getId()
        );

        return candidates.stream()
                .map(plan -> {

                    int score = 0;

                    // 1️⃣ Destination match
                    score += 40;

                    // 2️⃣ Date overlap %
                    long totalDays = myPlan.getStartDate().until(myPlan.getEndDate()).getDays();
                    long overlapStart =
                            plan.getStartDate().isAfter(myPlan.getStartDate())
                                    ? plan.getStartDate().toEpochDay()
                                    : myPlan.getStartDate().toEpochDay();

                    long overlapEnd =
                            plan.getEndDate().isBefore(myPlan.getEndDate())
                                    ? plan.getEndDate().toEpochDay()
                                    : myPlan.getEndDate().toEpochDay();

                    long overlapDays = overlapEnd - overlapStart;

                    if (overlapDays > 0 && totalDays > 0) {
                        double overlapPercent = (double) overlapDays / totalDays;
                        score += (int) (overlapPercent * 30);
                    }

                    // 3️⃣ Budget similarity
                    double budgetDiff = Math.abs(myPlan.getBudget() - plan.getBudget());
                    double budgetPercent = 1 - (budgetDiff / myPlan.getBudget());
                    score += (int) (budgetPercent * 20);

                    // 4️⃣ Travel type
                    if (myPlan.getTravelType().equalsIgnoreCase(plan.getTravelType())) {
                        score += 10;
                    }

                    return new MatchResponse(plan, score);
                })
                .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                .toList();
    }
}