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

    private ArrayList<MemoDTO> arrayListMemoDTO = new ArrayList<>();
    private MemoDTO memoDTOSelected = new MemoDTO();

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

    public MemoDTO select(int key)
    {
        String memoKey = String.valueOf(key);

        try{
            String link="http://210.94.194.201/selectMemo.php";

            //memoKey는 자동으로 설정됨
            String data  = URLEncoder.encode("memoKey", "UTF-8") + "=" + URLEncoder.encode(memoKey, "UTF-8");


            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );
            wr.flush();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();

            String json;
            while((json = bufferedReader.readLine())!= null){
                sb.append(json+"\n");
            }

            String queryJson = sb.toString().trim();
            JSONObject jsonObj = new JSONObject(queryJson);
            JSONArray memoInfo = jsonObj.getJSONArray("result");

            JSONObject c = memoInfo.getJSONObject(0);

            //string->date 변환
            DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = sdFormat.parse(c.getString("date"));

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

            return selectedMemoDTO;


        }catch (Exception e) {
            //return new String("Exception: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<MemoDTO> selectAllPersonalMemo(String deviceID) {
        try {

            BufferedReader bufferedReader = null;
            //Device ID를 가져오는 부분

            String link = "http://210.94.194.201/selectAllPersonalMemo.php";
            String data  = URLEncoder.encode("deviceID", "UTF-8") + "=" + URLEncoder.encode(deviceID, "UTF-8");

            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

            wr.write( data );
            wr.flush();

            StringBuilder sb = new StringBuilder();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String json;
            while ((json = bufferedReader.readLine()) != null) {
                sb.append(json + "\n");

            }

            String result = sb.toString().trim();
            arrayListMemoDTO.clear();//업데이트를 위한 초기화부분

            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArrayMemoDTO = null;
            jsonArrayMemoDTO = jsonObj.getJSONArray("result");

            for (int i = 0; i < jsonArrayMemoDTO.length(); i++) {

                //MeetingDTO 객체를 생성
                MemoDTO memoDTO = new MemoDTO();

                JSONObject c = jsonArrayMemoDTO.getJSONObject(i);

                //string -> date 변환
                DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = sdFormat.parse(c.getString("date"));

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



                arrayListMemoDTO.add(memoDTO);
            }
            return arrayListMemoDTO;
        }catch(Exception e){
            return null;
        }

    }

    public ArrayList<MemoDTO> selectAllMemo(String deviceID) {
        try {

            BufferedReader bufferedReader = null;
            //Device ID를 가져오는 부분

            String link = "http://210.94.194.201/selectAllMemo.php";
            String data  = URLEncoder.encode("deviceID", "UTF-8") + "=" + URLEncoder.encode(deviceID, "UTF-8");

            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

            wr.write( data );
            wr.flush();

            StringBuilder sb = new StringBuilder();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String json;
            while ((json = bufferedReader.readLine()) != null) {
                sb.append(json + "\n");

            }

            String result = sb.toString().trim();
            arrayListMemoDTO.clear();//업데이트를 위한 초기화부분

            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArrayMemoDTO = null;
            jsonArrayMemoDTO = jsonObj.getJSONArray("result");

            for (int i = 0; i < jsonArrayMemoDTO.length(); i++) {

                //MeetingDTO 객체를 생성
                MemoDTO memoDTO = new MemoDTO();

                JSONObject c = jsonArrayMemoDTO.getJSONObject(i);

                //string -> date 변환
                DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = sdFormat.parse(c.getString("date"));

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



                arrayListMemoDTO.add(memoDTO);
            }
                return arrayListMemoDTO;
            }catch(Exception e){
                return null;
            }

        }

}
