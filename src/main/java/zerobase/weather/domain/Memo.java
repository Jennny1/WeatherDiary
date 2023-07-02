package zerobase.weather.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "memo") // 엔티티 지정
public class Memo {
    /*
    JPA에서 Entity 객체를 정의할 때 필요한 항목들
    @Id와 @GeneratedValue를 같이 사용한다.
    @Id : PK ; primary key를 나타낸다.
    @GeneratedValue : PK에 대한 생성전략
    strategy = GenerationType.IDENTITY : 기본키 생성을 DB에게 위임한다
    strategy = GenerationType.AUTO(default)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String text;
}
