package com.easyapper.member.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Member {
	@Id
	private ObjectId id;
	private String userId;
	private String name;
	private Set<String> groupIds = new HashSet<>();

	private long createdAt;
	private boolean isActive;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Set<String> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Set<String> groupIds) {
		this.groupIds = groupIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Member member = (Member) o;
		return getCreatedAt() == member.getCreatedAt() && Objects.equals(getId(), member.getId())
				&& Objects.equals(getUserId(), member.getUserId()) && Objects.equals(getName(), member.getName());
	}

	@Override
	public int hashCode() {

		return Objects.hash(getId(), getUserId(), getName(), getCreatedAt());
	}
}
