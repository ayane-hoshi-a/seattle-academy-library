package jp.co.seattle.library.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 貸し出しサービス
 * 
 *  borrowテーブルに関する処理を実装する
 */
@Service
public class BorrowService {
    final static Logger logger = LoggerFactory.getLogger(BorrowService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 書籍の貸し出し
     *
     * @param bookId 
     */
    public void borrowBook(int bookId) {

        String sql = "INSERT INTO borrow (bookid) VALUES (" + bookId + ")";

        jdbcTemplate.update(sql);
    }

    public int count(int bookId) {
        String sql = "select count(*) from borrow where bookid=" + bookId + ";";
        int count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count;
    }

    /**
    * 書籍情報を返却する
    * 
    * @param bookId 
    */
    public void returnBook(int bookId) {
        String sql = "delete from borrow where bookid=" + bookId + ";";
        jdbcTemplate.update(sql);
    }


}
