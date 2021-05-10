package jp.co.seattle.library.controller;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.BorrowService;
import jp.co.seattle.library.service.ThumbnailService;

//AddBooksControlを移してきた。（内容は似ているから）
/**
 * Handles requests for the application home page.
 */
//EditBookに直す
@Controller //APIの入り口
public class EditBookController {
    final static Logger logger = LoggerFactory.getLogger(EditBookController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private BorrowService borrowService;

    @RequestMapping(value = "/editBook", method = RequestMethod.POST) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String login(Model model,
            @RequestParam("bookId") Integer bookId) {
        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        return "editBook";
    }

    /**
     * 書籍情報を編集する
     * @param locale ロケール情報
     * @param bookId 
     * @param title 書籍名
     * @param description 説明文
     * @param author 著者名
     * @param publisher 出版社
     * @param publish_date 出版日
     * @param isbn ISBN 
     * @param file サムネイルファイル
     * @param model モデル
     * @return 遷移先画面
     */
    @Transactional
    @RequestMapping(value = "/reinsertBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
            @RequestParam("bookId") int bookId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("publish_date") String publishDate,
            @RequestParam("isbn") String isbn,
            @RequestParam("thumbnail") MultipartFile file,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

        // パラメータで受け取った書籍情報をDtoに格納する。
        BookDetailsInfo bookInfo = new BookDetailsInfo();
        bookInfo.setBookId(bookId);
        bookInfo.setTitle(title);
        bookInfo.setDescription(description);
        bookInfo.setAuthor(author);
        bookInfo.setPublisher(publisher);
        bookInfo.setPublishDate(publishDate);
        bookInfo.setIsbn(isbn);

        // クライアントのファイルシステムにある元のファイル名を設定する
        String thumbnail = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                // サムネイル画像をアップロード
                String fileName = thumbnailService.uploadThumbnail(thumbnail, file);
                // URLを取得
                String thumbnailUrl = thumbnailService.getURL(fileName);

                bookInfo.setThumbnailName(fileName);
                bookInfo.setThumbnailUrl(thumbnailUrl);

            } catch (Exception e) {

                // 異常終了時の処理
                logger.error("サムネイルアップロードでエラー発生", e);
                model.addAttribute("bookDetailsInfo", bookInfo);
                return "editBook";
            }
        }

        try {
            SimpleDateFormat d1 = new SimpleDateFormat("yyyyMMdd");
            d1.setLenient(false);
            d1.parse(publishDate);
        } catch (Exception e) {
            model.addAttribute("errorDate", "出版日は半角数字のYYYYMMDD形式で入力してください");
            return "editBook";
        }
    

    if(!(bookInfo.getIsbn().matches("([0-9]{10}|[0-9]{13})?"))) {
        model.addAttribute("errorIsbn", "ISBNの桁数または半角数字が正しくありません");
        return "editBook";
    }

    // 書籍情報を編集する
    booksService.editBook(bookInfo);

    model.addAttribute("resultMessage", "登録完了");

    // TODO 登録した書籍の詳細情報を表示するように実装
    model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
    //貸出ステータス表示、ボタンの変更
    int count = borrowService.count(bookId);

    if (count == 0) {
        //借りるボタンは使える　返すボタンは使えない
        model.addAttribute("returnDisabled", "disabled");
        model.addAttribute("borrowStatus", "貸出可");
    } else {
        //借りるボタンは使えない　返すボタンは使える
        model.addAttribute("borrowDisabled", "disabled");
        model.addAttribute("borrowStatus", "貸出し中");
    }
        //  詳細画面に遷移する
        return "details";
    }
}



