package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

/*
Controller : 클라이언트와 맞닿아 있는 부분
             어떤 AIP가 필요할지 생각하여 구현

RestController : http 응답을 보낼 때 상태코드 응답을 보내는데(404, 503 등) 지정을 해서 보낼 수 있음
 */
@RestController
public class DiaryController {
    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    /*
    날씨일기 쓰기
    보통 get형식은 조회를 할 때 사용
    저장할 때는 post를 사용
     */
    @ApiOperation(value = "일기 텍스트와 날씨를 이용해서 DB에 일기 저장", notes = "이것은 노트")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text){
        /*
        @RequestParam : header를 통해 전달받을 데이터, 여기서는 날짜
        @DateTimeFormat : date 형식 포멧 지정
        @RequestBody : post형식이니까 body를 사용할 수 있음, body에는 text를 저장하겠다.
         */
        
        // 서비스로 전달
        diaryService.createDiary(date, text);
    }

    /*
    날씨 일기 조회
     */
    @ApiOperation("선택한 날짜의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return diaryService.readDiary(date);
    }

    /*
    여러개 조회
     */

    @ApiOperation("선택한 기간 중의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "조회할 기간의 첫번째 날", example = "2023-07-01") LocalDate startDate,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "조회할 기간의 마지막 날", example = "2023-07-02") LocalDate endDate){

        return diaryService.readDiaries(startDate, endDate);
    }

    /*
    수정
     */
    @ApiOperation("선택한 날짜의 일기 텍스트를 수정합니다.")
    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        diaryService.updateDiary(date, text);
    }

    /*
    삭제
     */
    @ApiOperation("선택한 날짜의 일기 데이터를 삭제합니다.")
    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        diaryService.deleteDiary(date);

    }

}
