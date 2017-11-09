package com.longlongago.roomManager.model;


/**
 * Created by Jojo on 25/07/2017.
 */

@org.parceler.Parcel
public class Room {
    public String roomId;

    public String userId;

    public Integer peopleNo;

    public Integer roomStatus;

    public Integer currentPeopleNo;

    public String ownerId;

    public String roomName;

    public String roomPicUrl;

    public String roomPic;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId == null ? null : roomId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public Integer getPeopleNo() {
        return peopleNo;
    }

    public void setPeopleNo(Integer peopleNo) {
        this.peopleNo = peopleNo;
    }

    public Integer getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(Integer roomStatus) {
        this.roomStatus = roomStatus;
    }

    public Integer getCurrentPeopleNo() {
        return currentPeopleNo;
    }

    public void setCurrentPeopleNo(Integer currentPeopleNo) {
        this.currentPeopleNo = currentPeopleNo;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId == null ? null : ownerId.trim();
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName == null ? null : roomName.trim();
    }

    public String getRoomPicUrl() {
        return roomPicUrl;
    }

    public void setRoomPicUrl(String roomPicUrl) {
        this.roomPicUrl = roomPicUrl == null ? null : roomPicUrl.trim();
    }

    public String getRoomPic() {
        return roomPic;
    }

    public void setRoomPic(String roomPic) {
        this.roomPic = roomPic == null ? null : roomPic.trim();
    }
}
