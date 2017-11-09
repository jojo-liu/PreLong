package com.longlongago.login.model;

@org.parceler.Parcel
public class UserBaseInfo {
	public String userId;

	public String username;

	public String password;

	public Integer userStatus;

	public String signature;

	public String avatarUrl;

	public String phone;

	public String sex;

	public String roomId;

	public String avatar;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId == null ? null : userId.trim();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username == null ? null : username.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password == null ? null : password.trim();
	}

	public Integer getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature == null ? null : signature.trim();
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl == null ? null : avatarUrl.trim();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone == null ? null : phone.trim();
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex == null ? null : sex.trim();
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId == null ? null : roomId.trim();
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar == null ? null : avatar.trim();
	}

	@Override
	public String toString() {
		return "UserBaseInfo{" +
				"userId='" + userId + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", userStatus=" + userStatus +
				", signature='" + signature + '\'' +
				", avatarUrl='" + avatarUrl + '\'' +
				", phone='" + phone + '\'' +
				", sex='" + sex + '\'' +
				", roomId='" + roomId + '\'' +
				", avatar='" + avatar + '\'' +
				'}';
	}
}