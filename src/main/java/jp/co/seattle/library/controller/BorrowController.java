package jp.co.seattle.library.controller;

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

import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.BorrowService;

/**
 * 貸出コントローラー
 */
@Controller //APIの入り口
public class BorrowController {
    final static Logger logger = LoggerFactory.getLogger(BorrowController.class);

    @Autowired
    private BooksService booksService;
    @Autowired
    private BorrowService borrowService;

    /**
     * 対象書籍を借りる
     *
     * @param locale ロケール情報
     * @param bookId 書籍ID
     * @param model モデル情報
     * @return 遷移先画面名
     */
    @Transactional
    @RequestMapping(value = "/rentBook", method = RequestMethod.POST)
    public String borrowBook(
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome borrow! The client locale is {}.", locale);

        borrowService.borrowBook(bookId);

        int count = borrowService.count(bookId);

        if (count == 0) {
            //テーブルにコードが入っていない時
            //借りるボタンは使える　返すボタンは使えない
            model.addAttribute("returnDisabled", "disabled");
            model.addAttribute("borrowStatus", "貸出可");
        } else {
            //テーブルにコードがある時
            //借りるボタンは使えない　返すボタンは使える
            model.addAttribute("borrowDisabled", "disabled");
            model.addAttribute("borrowStatus", "貸出し中");
        }
        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        return "details";

    }

    /**
     * 対象書籍を返す
     *
     * @param locale ロケール情報
     * @param bookId 書籍ID
     * @param model モデル情報
     * @return 遷移先画面名
     */
    @Transactional
    @RequestMapping(value = "/returnBook", method = RequestMethod.POST)
    public String returnBook(
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome return! The client locale is {}.", locale);

        borrowService.returnBook(bookId);

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
        //書籍詳細情報再取得
        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        return "details";

    }
}

