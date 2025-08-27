package com.gillianbc.pensionstracker.repo;

import com.gillianbc.pensionstracker.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepo extends JpaRepository<Provider,Long>{}
