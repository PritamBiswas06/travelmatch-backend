package com.pvp.travelmatch.repository;

import com.pvp.travelmatch.entity.TravelPartner;
import com.pvp.travelmatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelPartnerRepository extends JpaRepository<TravelPartner, Long> {
    boolean existsByUserOneAndUserTwoOrUserTwoAndUserOne(
            User userOne1, User userTwo1,
            User userOne2, User userTwo2
    );
}