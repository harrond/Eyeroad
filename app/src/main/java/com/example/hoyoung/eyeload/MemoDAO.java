package com.example.hoyoung.eyeload;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jin on 2016-11-17.
 */

public class MemoDAO extends DAO{

    private ArrayList<MemoDTO> arrayListMemoDTO;
    private MemoDTO memoDTOSelected;

    public ArrayList<MemoDTO> getArrayListMemoDTO() {
        return arrayListMemoDTO;
    }

    public MemoDTO getMemoDTOSelected() {
        return memoDTOSelected;
    }

    public MemoDAO()
    {
        arrayListMemoDTO = new ArrayList<>();
        memoDTOSelected = new MemoDTO();
    }

    //안드로이드->DB로 값을 삽입하기 위한 함수
    public boolean insert(MemoDTO dto) {

        //매개변수로 받은 객체의 정보를 빼오는 부분
        String title = dto.getTitle();
        String x = String.valueOf(dto.getX());
        String y = String.valueOf(dto.getY());
        String z = String.valueOf(dto.getZ());
        String content = dto.getContent();
        Date date = dto.getDate();
        String image = dto.getImage();
        String iconId = String.valueOf(dto.getIconId());
        String deviceID = dto.getDeviceID();
        String visibility = String.valueOf(dto.getVisibility());

        //date->string 처리
        DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
        String stringDate = sdFormat.format(date);

        try {

            String link = "http://210.94.194.201/insertMemo.php";//변수들을 보낼 link
            //String data  = URLEncoder.encode("memoKey", "UTF-8") + "=" + URLEncoder.encode(memoKey, "UTF-8");
            //memoKey는 자동으로 설정됨
            //PHP를 통해 변수들을 Mapping하는 부분
            String data = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8");
            data += "&" + URLEncoder.encode("x", "UTF-8") + "=" + URLEncoder.encode(x, "UTF-8");
            data += "&" + URLEncoder.encode("y", "UTF-8") + "=" + URLEncoder.encode(y, "UTF-8");
            data += "&" + URLEncoder.encode("z", "UTF-8") + "=" + URLEncoder.encode(z, "UTF-8");
            data += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8");
            data += "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(stringDate, "UTF-8");
            data += "&" + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8");
            data += "&" + URLEncoder.encode("iconId", "UTF-8") + "=" + URLEncoder.encode(iconId, "UTF-8");
            data += "&" + URLEncoder.encode("deviceID", "UTF-8") + "=" + URLEncoder.encode(deviceID, "UTF-8");
            data += "&" + URLEncoder.encode("visibility", "UTF-8") + "=" + URLEncoder.encode(visibility, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);//Mapping된 데이터를 PHP를 통해 처리하는 부분
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            //서버로부터 반환된 값 읽어옴
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            if(sb.toString().equals("success"))//성공적으로 불러왔을 시
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    //key값을 이용하여 DB의 튜플을 삭제
    public boolean delete(int key){

        try{
            String memoKey =  String.valueOf(key);

            String link="http://210.94.194.201/deleteMemo.php";//key값을 보낼 link
            String data  = URLEncoder.encode("memoKey", "UTF-8") + "=" + URLEncoder.encode(memoKey, "UTF-8");//PHP를 통해 변수들을 Mapping하는 부분

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
    public boolean select(int key)
    {
        String memoKey = String.valueOf(key);

        try{
            String link="http://210.94.194.201/selectMemo.php";//튜플에 대한 데이터를 받아올 link

            //PHP를 통해 변수들을 Mapping하는 부분
            String data  = URLEncoder.encode("memoKey", "UTF-8") + "=" + URLEncoder.encode(memoKey, "UTF-8");


            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );//Mapping된 데이터를 PHP를 통해 처리하는 부분
            wr.flush();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();

            String json;
            while((json = bufferedReader.readLine())!= null){//서버로부터 반환된 값 읽어옴
                sb.append(json+"\n");
            }

            //PHP를 통해 받아온 변수들을 JSON을 이용하여 처리하는 부분
            String queryJson = sb.toString().trim();
            JSONObject jsonObj = new JSONObject(queryJson);
            JSONArray memoInfo = jsonObj.getJSONArray("result");

            JSONObject c = memoInfo.getJSONObject(0);

            //string->date 변환
            DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = sdFormat.parse(c.getString("date"));

            //JSON을 이용하여 DTO에 데이터 삽입하는 부분
            MemoDTO selectedMemoDTO=new MemoDTO();

            selectedMemoDTO.setTitle(c.getString("title"));
            selectedMemoDTO.setX(Double.valueOf(c.getString("x")));
            selectedMemoDTO.setY(Double.valueOf(c.getString("y")));
            selectedMemoDTO.setZ(Double.valueOf(c.getString("z")));
            selectedMemoDTO.setContent(c.getString("content"));
            selectedMemoDTO.setDate(date);
            selectedMemoDTO.setImage(c.getString("image"));
            selectedMemoDTO.setIconId(Integer.valueOf(c.getString("iconId")));
            selectedMemoDTO.setDeviceID(c.getString("deviceID"));
            selectedMemoDTO.setVisibility(Integer.valueOf(c.getString("visibility")));

            memoDTOSelected = selectedMemoDTO;

            return true;

        }catch (Exception e) {
            return false;
        }

    }

    //deviceID를 이용하여 개인이 작성한 메모를 DB로부터 불러오는 함수
    public boolean selectAllPersonal(String deviceID) {
        try {

            BufferedReader bufferedReader = null;

            String link = "http://210.94.194.201/selectAllPersonalMemo.php";//모든 메모에 대한 데이터를 받아올 link
            //PHP를 통해 변수들을 Mapping하는 부분
            String data  = URLEncoder.encode("deviceID", "UTF-8") + "=" + URLEncoder.encode(deviceID, "UTF-8");

            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

            wr.write( data );//Mapping된 데이터를 PHP를 통해 처리하는 부분
            wr.flush();

            StringBuilder sb = new StringBuilder();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String json;
            while ((json = bufferedReader.readLine()) != null) {//서버로부터 반환된 값 읽어옴
                sb.append(json + "\n");

            }

            String result = sb.toString().trim();
            arrayListMemoDTO.clear();//업데이트를 위한 초기화부분

            //PHP를 통해 받아온 변수들을 JSON을 이용하여 처리하는 부분
            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArrayMemoDTO = null;
            jsonArrayMemoDTO = jsonObj.getJSONArray("result");

            //DTO에 정보를 삽입하고 LIst에 DTO를 삽입하는 부분
            for (int i = 0; i < jsonArrayMemoDTO.length(); i++) {

                //MeetingDTO 객체를 생성
                MemoDTO memoDTO = new MemoDTO();

                JSONObject c = jsonArrayMemoDTO.getJSONObject(i);

                //string -> date 변환
                DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = sdFormat.parse(c.getString("date"));

                //JSON을 이용하여 DTO에 데이터 삽입하는 부분
                memoDTO.setKey(Integer.parseInt(c.getString("memoKey")));
                memoDTO.setTitle(c.getString("title"));
                memoDTO.setX(Double.parseDouble(c.getString("x")));
                memoDTO.setY(Double.parseDouble(c.getString("y")));
                memoDTO.setZ(Double.parseDouble(c.getString("z")));
                memoDTO.setContent(c.getString("content"));
                memoDTO.setDate(date);
                memoDTO.setImage(c.getString("image"));
                memoDTO.setIconId(Integer.parseInt(c.getString("iconId")));
                memoDTO.setDeviceID(c.getString("deviceID"));
                memoDTO.setVisibility(Integer.parseInt(c.getString("visibility")));

                arrayListMemoDTO.add(memoDTO);//DTO를 MemoList에 추가
            }
            return true;

        }catch(Exception e){
            return false;
        }

    }

    //타인이 작성한 공개된 메모와 개인이 작성한 모든 메로를 DB로부터 불러오는 함수
    public boolean selectAll() {
        try {

            String deviceID;
            deviceID = String.valueOf(Build.class.getField("SERIAL").get(null));//deviceID를 가져옴옴

            BufferedReader bufferedReader = null;

            String link = "http://210.94.194.201/selectAllMemo.php";//모든 메모에 대한 데이터를 받아올 link
            //PHP를 통해 변수들을 Mapping하는 부분
            String data  = URLEncoder.encode("deviceID", "UTF-8") + "=" + URLEncoder.encode(deviceID, "UTF-8");

            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

            wr.write( data );//Mapping된 데이터를 PHP를 통해 처리하는 부분
            wr.flush();

            StringBuilder sb = new StringBuilder();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String json;
            while ((json = bufferedReader.readLine()) != null) {//서버로부터 반환된 값 읽어옴
                sb.append(json + "\n");

            }

            String result = sb.toString().trim();
            arrayListMemoDTO.clear();//업데이트를 위한 초기화부분

            //PHP를 통해 받아온 변수들을 JSON을 이용하여 처리하는 부분
            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArrayMemoDTO = null;
            jsonArrayMemoDTO = jsonObj.getJSONArray("result");

            //DTO에 정보를 삽입하고 LIst에 DTO를 삽입하는 부분
            for (int i = 0; i < jsonArrayMemoDTO.length(); i++) {

                //MeetingDTO 객체를 생성
                MemoDTO memoDTO = new MemoDTO();

                JSONObject c = jsonArrayMemoDTO.getJSONObject(i);

                //string -> date 변환
                DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = sdFormat.parse(c.getString("date"));

                //JSON을 이용하여 DTO에 데이터 삽입하는 부분
                memoDTO.setKey(Integer.parseInt(c.getString("memoKey")));
                memoDTO.setTitle(c.getString("title"));
                memoDTO.setX(Double.parseDouble(c.getString("x")));
                memoDTO.setY(Double.parseDouble(c.getString("y")));
                memoDTO.setZ(Double.parseDouble(c.getString("z")));
                memoDTO.setContent(c.getString("content"));
                memoDTO.setDate(date);
                memoDTO.setImage(c.getString("image"));
                memoDTO.setIconId(Integer.parseInt(c.getString("iconId")));
                memoDTO.setDeviceID(c.getString("deviceID"));
                memoDTO.setVisibility(Integer.parseInt(c.getString("visibility")));

                arrayListMemoDTO.add(memoDTO);//DTO를 MemoList에 추가
            }
        }catch(Exception e){
            return false;
        }

        return true;

    }

}
