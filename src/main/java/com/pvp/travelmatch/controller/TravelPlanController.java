package com.pvp.travelmatch.controller;

import com.pvp.travelmatch.dto.MatchResponse;
import com.pvp.travelmatch.dto.TravelPlanRequest;
import com.pvp.travelmatch.entity.TravelPlan;
import com.pvp.travelmatch.service.TravelPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelPlanController {

    private final TravelPlanService travelPlanService;

    @PostMapping
    public TravelPlan createPlan(@RequestBody TravelPlanRequest request) {
        return travelPlanService.createPlan(request);
    }

    @GetMapping("/{planId}/matches")
    public List<MatchResponse> getMatches(@PathVariable Long planId) {
        return travelPlanService.findMatches(planId);
    }

    @GetMapping("/my")
    public List<TravelPlan> getMyPlans() {
        return travelPlanService.getMyPlans();
    }
}