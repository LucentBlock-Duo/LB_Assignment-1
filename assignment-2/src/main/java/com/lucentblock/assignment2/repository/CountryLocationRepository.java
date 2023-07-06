package com.lucentblock.assignment2.repository;


import com.lucentblock.assignment2.entity.CountryLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryLocationRepository extends JpaRepository<CountryLocation,Long> { }
