package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

@Repository
public interface JpaMemoRepository extends JpaRepository<Memo, Integer> {
    // JpaRepository는 자바 표준 ORM 명세를 그대로 가져와서 사용하기 위해
    // 가져올 떄, 어떤 클래스를 가져다 쓸지 (Memo)
    // key의 형태 (Integer)를 표시한다.

}
