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

        model.addAttribute("bookList", booksService.getBookList());
        return "home";
    }

    /**
     * 検索機能
     * @param locale
     * @param searchTitle 検索
     * @param model
     * @return
     */
    @Transactional
    @RequestMapping(value = "/searchBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String SearchBook(Locale locale,
            @RequestParam("search") String searchTitle,
            Model model) {

        model.addAttribute("bookList", booksService.getSearchBookList(searchTitle));
        return "home";
    }
}
