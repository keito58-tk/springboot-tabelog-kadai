package com.example.tabelog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Integer>{
	public Role findByName(String name);
}
