package zerobase.weather.domain;

/*
데이터베이스에 데이터를 넣기 위한 클래스
 */

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

/*
@NoArgsConstructor : 파라미터가 없는 기본 생성자를 생성
@AllArgsConstructor : 모든 필드값을 파라미터로 받는 생성자를 만들어줌
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Diary {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
    private String weather;
    private String icon;
    private double temperature;
    private String text;
    private LocalDate date;

    public void setDateWeather(DateWeather dateWeather){
        this.date = dateWeather.getDate();
        this.weather = dateWeather.getWeather();
        this.icon = dateWeather.getWeather();
    }
}
