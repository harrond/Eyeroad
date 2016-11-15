package com.example.hoyoung.test;
public class MeetingDTO extends DTO{
    // listView에 접목시키기 위한 test용 필드
    // 구현 시 실제 DTO의 필드를 listview에 적용해야되는 수정필요
    ////////////////////////////////////////////////////////////////

    private String desc ;

        public void setDesc(String desc) {
            this.desc = desc ;
        }

        public String getDesc() {
            return this.desc ;
        }
    //////////////////////////////////////////////////////////////////

    private int key;
    private String title ;
    private String placeName;
    private String meetingInfo;
    private String publisher;
    private String password;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setTitle(String title) {
        this.title = title ;
    }

    public String getTitle() {
        return this.title ;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getMeetingInfo() {
        return meetingInfo;
    }

    public void setMeetingInfo(String meetingInfo) {
        this.meetingInfo = meetingInfo;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}