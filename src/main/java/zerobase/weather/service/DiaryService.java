package zerobase.weather.service;

import net.bytebuddy.asm.Advice;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DiaryService {
    
    /*
    @Value : openweathermap.key에 지정되어있는 값을 가져와서 apiKey 객체에 넣어주기
     */
    @Value("${openweathermap.key}")
    private String apiKey;
    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *") // 크론표현식 : 초, 분, 시, 일, 월, 년 (매일 1시
    public void saveWeatherDate(){
        dateWeatherRepository.save(getWeatherFromApi());
        logger.info("오늘도 날씨 데이터 잘 가져옴");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        logger.info("started to create diary");

        // 날씨 데이터 가져오기(API? DB?)
        DateWeather dateWeather = getDataWeather(date);

        // 3. 파싱된 데이터 + 일기값 db에 넣기
        // 파라미터가 없는 기본 생성자 사용이 가능한 이유 >> @NoArgsConstructor
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);

        // DiaryRepository를 사용하여 db에 저장
        diaryRepository.save(nowDiary);

        logger.info("end to create diary");
    }

    private DateWeather getWeatherFromApi(){
        // 1. Open weather map에서 날씨 데이터 가져오기(API)
        String weatherData = getWeatherString();

        // 2. 받아온 날씨 json 파싱하기
        Map<String, Object> parseWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parseWeather.get("main").toString());
        dateWeather.setIcon(parseWeather.get("icon").toString());
        dateWeather.setTemperature((Double)parseWeather.get("temp"));

        // 3. 파싱된 데이터 반환
        return dateWeather;

    }

    private DateWeather getDataWeather(LocalDate date){
        List<DateWeather> dateWeathersListFromDB = dateWeatherRepository.findAllByDate(date);
        if (dateWeathersListFromDB.size() == 0) {
            // DB에 없다면 api에서 정보 가져오기
            return getWeatherFromApi();

        } else {
            return dateWeathersListFromDB.get(0);

        }
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        /**
         if (date.isAfter(LocalDate.ofYearDay(3050, 1))) {
         throw new InvalidDate();
         }
         */

        logger.debug("read diary");

        return diaryRepository.findAllByDate(date);
    }


    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate){
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }


    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }


    private String getWeatherString(){
        // https://api.openweathermap.org/data/2.5/weather?q={city Name}&appid={apikey}
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;


        // 자바의 URL클래스로 만들어주기
        try{
            URL url = new URL(apiUrl);
            // apiUrl을 http 형식으로 연결
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // get방식으로 연결
            connection.setRequestMethod("GET"); 
            // 응답코드에 따른 응답객체를 받아옴
            int responseCode = connection.getResponseCode();

            BufferedReader br;
            if (responseCode == 200) { 
                // 정상일 때
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream())); // 오류 스트림 받아옴

            }
            
            // br로 받아온 응답객체를 다시 StringBuilder에 옮겨줌
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return response.toString();
            

        } catch (Exception e) {
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString){
        // json 데이터 parsing

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            // input 데이터인 jsonString를 넣어줌
            // JSONObject 타입으로 parse
            jsonObject = (JSONObject) jsonParser.parse(jsonString);

        } catch (ParseException e){
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherDate = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherDate.get("main"));
        resultMap.put("icon", weatherDate.get("icon"));


        return resultMap;
    }



}
