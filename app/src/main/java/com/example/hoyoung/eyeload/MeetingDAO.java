package com.example.hoyoung.eyeload;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Jin on 2016-11-17.
 */

public class MeetingDAO extends DAO{

    private ArrayList<MeetingDTO> arrayListMeetingDTO;
    private MeetingDTO meetingDTOSelected;

    public MeetingDTO getMeetingDTOSelected()
    {
        return meetingDTOSelected;
    }

    public MeetingDAO()
    {
        arrayListMeetingDTO = new ArrayList<>();
        meetingDTOSelected = new MeetingDTO();
    }

    //안드로이드->DB로 값을 삽입하기 위한 함수
    public boolean insert(MeetingDTO dto)
    {

        try{

            //매개변수로 받은 객체의 정보를 빼오는 부분
            //String meetingKey = (String)params[0];
            String title = dto.getTitle();
            String placeName = dto.getPlaceName();
            String meetingInfo = dto.getMeetingInfo();
            String publisher = dto.getPublisher();
            String password = dto.getPassword();

            String link="http://210.94.194.201/insertMeeting.php";//변수들을 보낼 link
            //String data  = URLEncoder.encode("meetingKey", "UTF-8") + "=" + URLEncoder.encode(meetingKey, "UTF-8");
            //meetingKey는 자동으로 설정됨
            //PHP를 통해 변수들을 Mapping하는 부분
            String data  = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8");
            data += "&" + URLEncoder.encode("placeName", "UTF-8") + "=" + URLEncoder.encode(placeName, "UTF-8");
            data += "&" + URLEncoder.encode("meetingInfo", "UTF-8") + "=" + URLEncoder.encode(meetingInfo, "UTF-8");
            data += "&" + URLEncoder.encode("publisher", "UTF-8") + "=" + URLEncoder.encode(publisher, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );//Mapping된 데이터를 PHP를 통해 처리하는 부분
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            //서버로부터 반환된 값 읽어옴
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            if(sb.toString().equals("success"))//성공적으로 불러왔을 시
                return true;
            else
                return false;
        }
        catch(Exception e){
            return false;
        }

    }

    //key값과 password를 이용하여 DB의 튜플을 삭제
    public boolean deleteInfo(String key, String password){

        try{

            String link="http://210.94.194.201/deleteMeeting.php";//key값과 password을 보낼 link

            //PHP를 통해 변수들을 Mapping하는 부분
            String data  = URLEncoder.encode("meetingKey", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );//Mapping된 데이터를 PHP를 통해 처리하는 부분
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            //서버로부터 반환된 값 읽어옴
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }

            if(sb.toString().equals("success"))//성공적으로 삭제했을 시
                return true;
            else
                return false;
        }
        catch(Exception e){
            return false;
        }
    }

    //key값을 이용하여 선택된 튜플을 DB로부터 받아오는 함수
    public MeetingDTO select(int key)
    {
        try {
            String meetingKey = String.valueOf(key);

            String link = "http://210.94.194.201/selectMeeting.php";//튜플에 대한 데이터를 받아올 link
            //PHP를 통해 변수들을 Mapping하는 부분
            String data = URLEncoder.encode("meetingKey", "UTF-8") + "=" + URLEncoder.encode(meetingKey, "UTF-8");


            URL url = new URL(link);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

            wr.write(data);//Mapping된 데이터를 PHP를 통해 처리하는 부분
            wr.flush();

            StringBuilder sb = new StringBuilder();

            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(con.getInputStream()));

            String json;
            while((json = bufferedReader.readLine())!= null){//서버로부터 반환된 값 읽어옴
                sb.append(json+"\n");
            }

            //PHP를 통해 받아온 변수들을 JSON을 이용하여 처리하는 부분
            String queryJson = sb.toString().trim();
            JSONObject jsonObj = new JSONObject(queryJson);
            JSONArray meetingInfo = jsonObj.getJSONArray("result");

            JSONObject c = meetingInfo.getJSONObject(0);

            //JSON을 이용하여 DTO에 데이터 삽입하는 부분
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

    //개설된 모든 모임을 가져오는 함수
    public ArrayList<MeetingDTO> selectAll()
    {

        try {

            BufferedReader bufferedReader = null;
            String link = "http://210.94.194.201/selectAllMeeting.php";//모든 메모에 대한 데이터를 받아올 link

            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            StringBuilder sb = new StringBuilder();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String json;
            while ((json = bufferedReader.readLine()) != null) {//서버로부터 반환된 값 읽어옴
                sb.append(json + "\n");
            }

            String result = sb.toString().trim();

            arrayListMeetingDTO.clear();//업데이트를 위한 초기화부분

            //PHP를 통해 받아온 변수들을 JSON을 이용하여 처리하는 부분
            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArrayMeetingDTO = null;
            jsonArrayMeetingDTO = jsonObj.getJSONArray("result");

            //JSON을 이용하여 DTO에 데이터 삽입하는 부분
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

                arrayListMeetingDTO.add(meetingDTO);//MeetingDTO 객체를 ArrayList에 삽입

            }
        }catch(Exception e){
            return null;
        }
        return arrayListMeetingDTO;//MeetingDTO의 객체가 담긴 List를 반환

    }

}
