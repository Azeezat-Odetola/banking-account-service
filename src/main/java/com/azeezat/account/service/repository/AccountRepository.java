package com.azeezat.account.service.repository;

import com.azeezat.account.service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository <Account, Long> {



}
