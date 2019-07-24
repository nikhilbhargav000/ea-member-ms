package com.easyapper.member.model.group;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class Group {
    @Id
    private ObjectId id;
    private String groupId;
    private String name;
    private String type;
    private Set<GroupMember> members = new HashSet<GroupMember>();
    private String createdBy;
    private long createdAt;
    private long expiresAt;
    private String status; ///??????
    private boolean isActive;


//    public ObjectId getId() {
//        return id;
//    }
//
//    public void setId(ObjectId id) {
//        this.id = id;
//    }
//
//    public String getGroupId() {
//        return groupId;
//    }
//
//    public void setGroupId(String groupId) {
//        this.groupId = groupId;
//    }
//
//    public List<GroupMember> getMembers() {
//        return members;
//    }
//
//    public void setMembers(List<GroupMember> members) {
//        this.members = members;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(String createdBy) {
//        this.createdBy = createdBy;
//    }
//
//    public long getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(long createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public boolean isActive() {
//        return isActive;
//    }
//
//    public void setActive(boolean active) {
//        isActive = active;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return getCreatedAt() == group.getCreatedAt() &&
                Objects.equals(getId(), group.getId()) &&
                Objects.equals(getGroupId(), group.getGroupId()) &&
                Objects.equals(getName(), group.getName()) &&
                Objects.equals(getCreatedBy(), group.getCreatedBy());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getGroupId(), getName(), getCreatedBy(), getCreatedAt());
    }

//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public long getExpiresAt() {
//        return expiresAt;
//    }
//
//    public void setExpiresAt(long expiresAt) {
//        this.expiresAt = expiresAt;
//    }
}
