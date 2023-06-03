package com.gangoffive.birdtradingplatform.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gangoffive.birdtradingplatform.enums.Permission.*;
@RequiredArgsConstructor

public enum UserRole {
	ADMIN(
			Set.of(
					ADMIN_READ,
					ADMIN_CREATE,
					ADMIN_UPDATE,
					ADMIN_DELETE
			)
	),
	SHOPOWNER (
			Set.of(
					SHOPOWNER_READ,
					SHOPOWNER_CREATE,
					SHOPOWNER_UPDATE,
					SHOPOWNER_DELETE
			)
	),
	SHOPSTAFF (
			Set.of(
					SHOPSTAFF_READ,
					SHOPSTAFF_UPDATE
			)
	),
	USER (
			Set.of(
					USER_READ,
					USER_CREATE,
					USER_UPDATE,
					USER_DELETE
			)
	);
	@Getter
	private final Set<Permission> permissions;
	public List<SimpleGrantedAuthority> getAuthorities() {
		var authorities = getPermissions()
				.stream()
				.map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
				.collect(Collectors.toList());
		authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
		return authorities;
	}

	public static void main(String[] args) {
		UserRole role = UserRole.USER;
		System.out.println(role);
		List<SimpleGrantedAuthority> list = role.getAuthorities();
		for (SimpleGrantedAuthority a: list) {
			System.out.println(a);
		}
	}
}
