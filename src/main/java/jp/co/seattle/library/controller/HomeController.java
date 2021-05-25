package jp.co.seattle.library.controller;

import java.util.ArrayList;
import java.util.List;
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

import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.service.BooksService;

/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class HomeController {
    final static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private BooksService booksService;

    /**
     * Homeボタンからホーム画面に戻るページ
     * @param model
     * @return
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String transitionHome(Model model) {
        List<BookInfo> list = new ArrayList<>(booksService.getBookList());
        if (list.size() == 0) {
            model.addAttribute("errorList", "書籍データがありません。");
            return "home";
        }

        model.addAttribute("bookList", list);
        return "home";
    }

    /**
     * 検索機能
     * @param locale
     * @param search 検索書籍名
     * @param radio ラジオボタンの情報
     * @param model
     * @return
     */
    @Transactional
    @RequestMapping(value = "/searchBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String SearchBook(Locale locale,
            @RequestParam("search") String searchTitle,
            @RequestParam("radio") String radio,
            Model model) {

        if (searchTitle.isEmpty()) {
            model.addAttribute("errorSearch", "検索結果が一致していません。");
            return "home";
        }
        //検索結果の表示
        if (radio.equals("part")) {
            if (booksService.partSearchBookList(searchTitle).isEmpty()) {
                model.addAttribute("errorSearch", "検索結果が一致していません。");
                return "home";
            }
            model.addAttribute("bookList", booksService.partSearchBookList(searchTitle));
            return "home";
        }
        if (radio.equals("perfect")) {
            if (booksService.perfectSearchBook(searchTitle).isEmpty()) {
                model.addAttribute("errorSearch", "検索結果が一致していません。");
                return "home";
            }
            model.addAttribute("bookList", booksService.perfectSearchBook(searchTitle));
            return "home";
        }
        return "home";
    }
}
