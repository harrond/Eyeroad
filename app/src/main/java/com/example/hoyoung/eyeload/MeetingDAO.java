package kr.soen.mypart;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Jin on 2016-11-17.
 */

public class MeetingDAO extends DAO{
    //싱글톤 클래스
    //private static MeetingDAO meetingDAO = new MeetingDAO();
    private ArrayList<MeetingDTO> arrayListMeetingDTO = new ArrayList<>();
    private MeetingDTO meetingDTOSelected = new MeetingDTO();

    public MeetingDTO getMeetingDTOSelected()
    {
        return meetingDTOSelected;
    }


    public boolean insert(MeetingDTO dto)
    {

        try{

            //String meetingKey = (String)params[0];
            String title = dto.getTitle();
            String placeName = dto.getPlaceName();
            String meetingInfo = dto.getMeetingInfo();
            String publisher = dto.getPublisher();
            String password = dto.getPassword();

            String link="http://210.94.194.201/insertMeeting.php";
            //String data  = URLEncoder.encode("meetingKey", "UTF-8") + "=" + URLEncoder.encode(meetingKey, "UTF-8");
            //meetingKey는 자동으로 설정됨
            String data  = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8");
            data += "&" + URLEncoder.encode("placeName", "UTF-8") + "=" + URLEncoder.encode(placeName, "UTF-8");
            data += "&" + URLEncoder.encode("meetingInfo", "UTF-8") + "=" + URLEncoder.encode(meetingInfo, "UTF-8");
            data += "&" + URLEncoder.encode("publisher", "UTF-8") + "=" + URLEncoder.encode(publisher, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            if(sb.toString().equals("success"))
                return true;
            else
                return false;
        }
        catch(Exception e){
            return false;
        }

    }

    public boolean deleteInfo(String key, String password){

        try{

            String link="http://210.94.194.201/deleteMeeting.php";
            String data  = URLEncoder.encode("meetingKey", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }

            if(sb.toString().equals("success"))
                return true;
            else
                return false;
        }
        catch(Exception e){
           return false;
        }
    }

    public MeetingDTO select(int key)
    {
        try {
            String meetingKey = String.valueOf(key);

            String link = "http://210.94.194.201/selectMeeting.php";
            String data = URLEncoder.encode("meetingKey", "UTF-8") + "=" + URLEncoder.encode(meetingKey, "UTF-8");


            URL url = new URL(link);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

            wr.write(data);
            wr.flush();

            StringBuilder sb = new StringBuilder();

            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(con.getInputStream()));

            String json;
            while((json = bufferedReader.readLine())!= null){
                sb.append(json+"\n");
            }

            String queryJson = sb.toString().trim();
            JSONObject jsonObj = new JSONObject(queryJson);
            JSONArray meetingInfo = jsonObj.getJSONArray("result");

            JSONObject c = meetingInfo.getJSONObject(0);

            MeetingDTO selectedMeetingDTO=new MeetingDTO();

            selectedMeetingDTO.setTitle(c.getString("title"));
            selectedMeetingDTO.setPlaceName(c.getString("placeName"));
            selectedMeetingDTO.setMeetingInfo(c.getString("meetingInfo"));
            selectedMeetingDTO.setPublisher("publisher");
            selectedMeetingDTO.setPassword("password");

            return selectedMeetingDTO;


        }catch (Exception e) {
            return null;
        }
    }


    public ArrayList<MeetingDTO> selectAll()
    {

        try {

            BufferedReader bufferedReader = null;
            String link = "http://210.94.194.201/selectAllMeeting.php";

            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            StringBuilder sb = new StringBuilder();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String json;
            while ((json = bufferedReader.readLine()) != null) {
                sb.append(json + "\n");
            }

            String result = sb.toString().trim();
            arrayListMeetingDTO.clear();//업데이트를 위한 초기화부분

            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArrayMeetingDTO = null;
            jsonArrayMeetingDTO = jsonObj.getJSONArray("result");

            for (int i = 0; i < jsonArrayMeetingDTO.length(); i++) {

                //MeetingDTO 객체를 생성
                MeetingDTO meetingDTO = new MeetingDTO();

                JSONObject c = jsonArrayMeetingDTO.getJSONObject(i);
                //MeetingDTO 객체에 정보 삽입
                meetingDTO.setKey(Integer.parseInt(c.getString("meetingKey")));
                meetingDTO.setTitle(c.getString("title"));
                meetingDTO.setPlaceName(c.getString("placeName"));
                meetingDTO.setMeetingInfo(c.getString("meetingInfo"));
                meetingDTO.setPublisher(c.getString("publisher"));
                meetingDTO.setPassword(c.getString("password"));

                //MeetingDTO 객체를 ArrayList에 삽입
                arrayListMeetingDTO.add(meetingDTO);

            }
        }catch(Exception e){
                return null;
            }
        return arrayListMeetingDTO;

    }

}
