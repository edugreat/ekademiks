package com.edugreat.akademiksresource.chat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.model.MiscellaneousNotifications;
@Repository
@RestResource(exported = false)
public interface MiscellaneousNotificationsDao extends JpaRepository<MiscellaneousNotifications, Integer> {



}
