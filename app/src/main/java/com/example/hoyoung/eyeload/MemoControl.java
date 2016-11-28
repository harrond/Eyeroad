package kr.soen.mypart;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
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
import java.util.Date;
import java.util.Map;

/**
 * Created by Jin on 2016-10-8.
 */

public class MemoControl extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList 및 서버로부터 받은 DTO list
    private ArrayList<MemoDTO> memoList = new ArrayList<MemoDTO>();
    private MemoDTO memoDTOSelected = new MemoDTO();
    private MemoDAO memoDAO = new MemoDAO();


    private static MemoControl memoControl = new MemoControl();
    //싱글톤을 위한 생성자
    private MemoControl()
    {

    }

    //싱글톤 return
    public static MemoControl getInstance(){
        return memoControl;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return memoList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.memo_listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView titleTextView = (TextView) convertView.findViewById(R.id.memoTextView1) ;
        TextView contentTextView = (TextView) convertView.findViewById(R.id.memoTextView2) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        MemoDTO listViewItem = memoList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        titleTextView.setText(listViewItem.getTitle());
        contentTextView.setText(listViewItem.getContent());

        return convertView;
    }

    public ArrayList<MemoDTO> getMemoList()
    {
        return memoList;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return memoList.get(position) ;
    }

    //DB에서 MemoKey값이 key인 DTO를 불러오는 함수
    public MemoDTO getMemo(int key)
    {
        memoDTOSelected = memoDAO.select(key);
        return memoDTOSelected;
        //Log.d("TEST","MemoControl getMemo " + memoDTOSelected.getTitle());
    }

    //DB에서 모든 DTO를 가져오는 함수
    public void getAllMemo()
    {
        memoList = memoDAO.selectAll();
    }

    //DB에서 DeviceID값이 같은 모든 DTO를 불러오는 함수
    public boolean getAllPersonalMemo()
    {
        try {
            String deviceID;
            deviceID = String.valueOf(Build.class.getField("SERIAL").get(null));
            memoList = memoDAO.selectAllMemo(deviceID);

            return true;
        }catch (Exception e)
        {
            e.getMessage();
            return false;
        }

    }


    //매개변수의 값을 가진 DTO를 DB에 저장하는 함수
    public boolean setInfo(String title, Double x, Double y, Double z, String content, Date date, String image, int iconId, String deviceID, int visibility)
    {
        MemoDTO memoDTO = new MemoDTO();

        //memoDTO.setKey(memoKey);
        memoDTO.setTitle(title);
        memoDTO.setX(x);
        memoDTO.setY(y);
        memoDTO.setZ(z);
        memoDTO.setContent(content);
        memoDTO.setDate(date);
        memoDTO.setImage(image);
        memoDTO.setIconId(iconId);
        memoDTO.setDeviceID(deviceID);
        memoDTO.setVisibility(visibility);

        return memoDAO.insert(memoDTO);
    }

    //DB에서 MeetingKey값이 key인 DTO를 삭제하는 함수
    public boolean deleteInfo(int key)
    {
        return memoDAO.delete(key);
    }


}