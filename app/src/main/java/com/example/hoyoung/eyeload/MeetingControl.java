package kr.soen.mypart;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Jin on 2016-10-8.
 */

public class MeetingControl extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList 및 서버로부터 불러온 DTO list
    private ArrayList<MeetingDTO> meetingList = new ArrayList<>();
    private MeetingDTO meetingDTOSelected = new MeetingDTO();
    //private MeetingDAO meetingDAO = MeetingDAO.getInstance();//싱글톤 DAO불러오기
    private MeetingDAO meetingDAO = new MeetingDAO();

    private static MeetingControl meetingControl = new MeetingControl();

    //생성자
    private MeetingControl()
    {

    }

    //싱글톤 return
    public static MeetingControl getInstance(){
        return meetingControl;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return meetingList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.meeting_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView titleTextView = (TextView) convertView.findViewById(R.id.meetingTextView1) ;
        TextView placeNameTextView = (TextView) convertView.findViewById(R.id.meetingTextView2) ;
        // Data Set(meetingList)에서 position에 위치한 데이터 참조 획득
        MeetingDTO listViewItem = meetingList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        titleTextView.setText(listViewItem.getTitle());
        placeNameTextView.setText(listViewItem.getPlaceName());

        return convertView;
    }

    public ArrayList<MeetingDTO> getMeetingList()
    {
        return meetingList;
    }
    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return meetingList.get(position) ;
    }

    //DB에서 MeetingKey값이 key인 DTO를 불러오는 함수
    public MeetingDTO getMeeting(int key)
    {
        meetingDTOSelected = meetingDAO.select(key);
        return meetingDTOSelected;
    }

    //DB에서 모든 DTO를 가져오는 함수
    public void getAllMeeting()
    {
        meetingList = meetingDAO.selectAll();
    }

    //매개변수의 값을 가진 DTO를 DB에 저장하는 함수
    public boolean setInfo(String title,String placeName,String meetingInfo,String publisher, String password)
    {
        MeetingDTO meetingDTO = new MeetingDTO();

        meetingDTO.setTitle(title);
        meetingDTO.setPlaceName(placeName);
        meetingDTO.setMeetingInfo(meetingInfo);
        meetingDTO.setPublisher(publisher);
        meetingDTO.setPassword(password);

        return meetingDAO.insert(meetingDTO);
    }

    //DB에서 MeetingKey값이 key인 DTO를 삭제하는 함수
    public boolean deleteInfo(String key,String password)
    {
        return meetingDAO.deleteInfo(key,password);
    }
}