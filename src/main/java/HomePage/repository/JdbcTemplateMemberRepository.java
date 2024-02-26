package HomePage.repository;

import HomePage.domain.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcTemplateMemberRepository implements MemberRepository{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateMemberRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public Member save(Member member) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("members").usingGeneratedKeyColumns("member_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("member_name", member.getName());
        parameters.put("member_password", member.getPassword());
        parameters.put("member_email", member.getEmail());
        parameters.put("member_phoneNumber", member.getPhoneNumber());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        member.setId(key.longValue());

        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        List<Member> result = jdbcTemplate.query("select * from members where member_id = ?", memberRowMapper(), id);
        // 리스트의 병렬 검색을 위해서 stream()을 사용
        // Optional<>을 사용하는 이유로는 null 값이 오면 발생하는 Null Pointer Exception을 막기 위함이며. 결국 허용함에 따라 메모리를 필요로 하는데
        // 여러개의 null 객체를 생성하더라도 static 키워드를 통해서 빈 객체를 공유함으로써 메모리를 절약할 수 있도록 설계가 되어있음.
        return result.stream().findAny();
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = jdbcTemplate.query("select * from members where member_name = ?", memberRowMapper(), name);
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return jdbcTemplate.query("select * from members", memberRowMapper());
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        List<Member> result = jdbcTemplate.query("select * from members where member_email = ?", memberRowMapper(), email);
        return result.stream().findAny();
    }

    @Override
    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        List<Member> result = jdbcTemplate.query("select * from members where member_phoneNumber = ?", memberRowMapper(), phoneNumber);
        return result.stream().findAny();
    }

    private RowMapper<Member> memberRowMapper(){
        return (rs, rowNum) -> {
            // 멤버 인스턴스 생성, 검색 용도로만 사용하고 이는 저장하려는 것이 아님.
            Member member = new Member();
            member.setId(rs.getLong("member_id"));
            member.setName(rs.getString("member_name"));
            member.setPassword(rs.getString("member_password"));
            member.setEmail(rs.getString("member_email"));
            member.setPhoneNumber(rs.getString("member_phoneNumber"));
            return member;
        };
    }
}
