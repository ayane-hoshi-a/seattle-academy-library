package jp.co.seattle.library.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.rowMapper.BookDetailsInfoRowMapper;
import jp.co.seattle.library.rowMapper.BookInfoRowMapper;

/**
 * 書籍サービス
 * 
 *  booksテーブルに関する処理を実装する
 */
@Service
public class BooksService {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 書籍リストを取得する
     *
     * @return 書籍リスト
     */
    public List<BookInfo> getBookList() {

        // TODO 取得したい情報を取得するようにSQLを修正
        List<BookInfo> getedBookList = jdbcTemplate.query(
                "select id,title,author,publisher,publish_date,thumbnail_url from books order by title asc",
                new BookInfoRowMapper());

        return getedBookList;
    }

    /**
     * 書籍IDに紐づく書籍詳細情報を取得する
     *
     * @param bookId 書籍ID
     * @return 書籍情報
     */
    public BookDetailsInfo getBookInfo(int bookId) {

        // JSPに渡すデータを設定する
        String sql = "SELECT * FROM books where id ="
                + bookId;

        BookDetailsInfo bookDetailsInfo = jdbcTemplate.queryForObject(sql, new BookDetailsInfoRowMapper());

        return bookDetailsInfo;
    }



    /**
     * 書籍を登録する
     *
     * @param bookInfo 書籍情報
     */
    public void registBook(BookDetailsInfo bookInfo) {

        String sql = "INSERT INTO books (title, description, author,publisher,publish_date,thumbnail_name,thumbnail_url,isbn, reg_date,upd_date) VALUES ('"
                + bookInfo.getTitle() + "','" + bookInfo.getDescription() + "','" + bookInfo.getAuthor() + "','"
                + bookInfo.getPublisher() + "','"
                + bookInfo.getPublishDate() + "','"
                + bookInfo.getThumbnailName() + "','"
                + bookInfo.getThumbnailUrl() + "','" + bookInfo.getIsbn() + "',"
                + "sysdate(),"
                + "sysdate())";

        jdbcTemplate.update(sql);
    }

    /**
     * 書籍を編集する
     *
     * @param bookInfo 書籍情報
     */
    public void editBook(BookDetailsInfo bookInfo) {

        String sql = "UPDATE books SET title ='" + bookInfo.getTitle() +
                "',description ='" + bookInfo.getDescription() +
                "',author='" + bookInfo.getAuthor() +
                "',publisher='" + bookInfo.getPublisher() +
                "',publish_date='" + bookInfo.getPublishDate() +
                "',thumbnail_name='" + bookInfo.getThumbnailName() +
                "',thumbnail_url='" + bookInfo.getThumbnailUrl() +
                "',isbn='" + bookInfo.getIsbn() +
                "',upd_date=sysdate() where id =" + bookInfo.getBookId();

        jdbcTemplate.update(sql);
    }

    /**
     * 書籍ID取得
     * 
     * @return 書籍情報
     */
    public int getBookId() {
        String sql = "select max(id) from books ";

        int bookId = jdbcTemplate.queryForObject(sql, Integer.class);

        return bookId;
    }

    /**
     * 書籍情報を削除する
     * 
     * @param bookId 書籍ID
     */
    public void deleteBook(int bookId) {
        String sql = "delete from books where id='" + bookId + "'";
        jdbcTemplate.update(sql);
    }

    /**
     * 検索機能
     * 部分一致
     * @return 書籍リスト
     */
    public List<BookInfo> partSearchBookList(String searchTitle) {

        // TODO 取得したい情報を取得するようにSQLを修正
        List<BookInfo> getedBookList = jdbcTemplate.query(
                "select * from books where title like '%" + searchTitle + "%' order by title asc",
                new BookInfoRowMapper());

        return getedBookList;
    }

    /**
     * 検索機能
     * 完全一致
     * @return 書籍リスト
     */
    public List<BookInfo> perfectSearchBook(String searchTitle) {

        // TODO 取得したい情報を取得するようにSQLを修正
        List<BookInfo> getedBookList = jdbcTemplate.query(
                "select * from books where title like '" + searchTitle + "' order by title asc",
                new BookInfoRowMapper());

        return getedBookList;
    }

}
