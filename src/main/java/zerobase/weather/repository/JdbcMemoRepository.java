package zerobase.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

// Repository 클래스는
@Repository
public class JdbcMemoRepository {
    // jdbc 객체 만들어주기
    private final JdbcTemplate jdbcTemplate;

    // 생성자
    @Autowired
    public JdbcMemoRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);

    }

    public Memo save(Memo memo) {
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());
        return memo;
    }

    private RowMapper<Memo> memoRowMapper(){
        // jdbcTemplate의 결과물 -> ResultSet 형태
        // 예: {id = 1, text = 'this is memo'}
        // ResultSet -> memo 형태로 바꿔주는 과정

        return (rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        );
    }


    public List<Memo> findAll(){
        String sql = "select * from memo";
        // ResultSet 형태의 객체를 memoRowMapper()함수를 통하여 Memo 객체로 가져오는 방식
        return jdbcTemplate.query(sql, memoRowMapper());
    }

    public Optional<Memo> findById(int id){
        String sql = "select * from memo where id = ?";
        // 여러개 중 첫벗째 id값을 가져오겠다.
        // 혹시 찾는 id가 없는 경우, 결과를 null로 가져오는 경우가 있을 수 있는데, 
        // 이 때 null값을 처리하기 쉽게 해주기 위해 Optional로 처리
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
    }

}
