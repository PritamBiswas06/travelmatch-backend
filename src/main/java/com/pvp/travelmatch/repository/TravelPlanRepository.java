package com.pvp.travelmatch.repository;

import com.pvp.travelmatch.entity.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {

    @Query("""
        SELECT t FROM TravelPlan t
        WHERE t.destination = :destination
        AND t.user.id <> :userId
        AND t.startDate <= :endDate
        AND t.endDate >= :startDate
    """)
    List<TravelPlan> findMatchingPlans(
            String destination,
            LocalDate startDate,
            LocalDate endDate,
            Long userId
    );
}