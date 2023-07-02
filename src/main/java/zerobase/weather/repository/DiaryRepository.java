package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer>{
    List<Diary> findAllByDate(LocalDate date);


    List<Diary> findAllByDateBetween(LocalDate startDatem, LocalDate endDate);
    
    // date로 조회했을 떄, 첫번째 데이터
    Diary getFirstByDate(LocalDate date);


    // 삭제
    @Transactional
    void deleteAllByDate(LocalDate date);


}