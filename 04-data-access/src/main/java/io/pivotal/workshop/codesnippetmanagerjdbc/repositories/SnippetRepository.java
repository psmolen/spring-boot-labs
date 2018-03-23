package io.pivotal.workshop.codesnippetmanagerjdbc.repositories;

import io.pivotal.workshop.codesnippetmanagerjdbc.models.NewSnippet;
import io.pivotal.workshop.codesnippetmanagerjdbc.models.SnippetRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

import static java.util.UUID.randomUUID;

@Repository
public class SnippetRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String SQL_INSERT = "insert into snippet (id, title, code, created, modified)" +
            " values(?, ?, ?, now(), now())";
    private final String SQL_QUERY_ALL = "select * from snippet";
    private final String SQL_QUERY_BY_ID = "select * from snippet where id = ?";
    private final String SQL_QUERY_BETWEEN = "select * from snippet where created >= CAST(? AS DATE) AND created <=" +
            "CAST(? AS DATE)";

    private final RowMapper<SnippetRecord>
            rowMapper = (ResultSet rs, int row) -> new SnippetRecord(
            rs.getString("id"),
            rs.getString("title"),
            rs.getString("code"),
            rs.getDate("created").toLocalDate(),
            rs.getDate("modified").toLocalDate()
            );

    public SnippetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SnippetRecord save(NewSnippet newSnippet) {
        String newId = randomUUID().toString();
        jdbcTemplate.update(SQL_INSERT, newId,
                newSnippet.title, newSnippet.code);
        return findOne(newId);
    }

    public List<SnippetRecord> findAll() {
        return jdbcTemplate.query(SQL_QUERY_ALL,
                rowMapper);
    }

    public List<SnippetRecord> findAllByDates(String startDate, String endDate) {
        return jdbcTemplate.query(SQL_QUERY_BETWEEN, new Object[]{startDate, endDate}, rowMapper);
    }

    public SnippetRecord findOne(String id) {
        return jdbcTemplate.queryForObject(SQL_QUERY_BY_ID, new Object[]{id}, rowMapper);
    }
}
