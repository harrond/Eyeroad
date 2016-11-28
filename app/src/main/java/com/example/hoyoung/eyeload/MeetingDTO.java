package kr.soen.mypart;

/**
 * Created by Jin on 2016-11-5.
 */

public class MeetingDTO extends DTO{

    private int meetingKey;
    private String title ;
    private String placeName;
    private String meetingInfo;
    private String publisher;
    private String password;

    public MeetingDTO()
    {
        title = null;
        placeName = null;
        meetingInfo = null;
        publisher = null;
        password = "1234"; // default 비밀번호 값
    }

    public int getKey() {
        return meetingKey;
    }

    public void setKey(int key) {
        this.meetingKey = key;
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