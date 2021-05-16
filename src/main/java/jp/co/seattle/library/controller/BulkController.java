package jp.co.seattle.library.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
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
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;

/**
 * 一括登録コントローラー
 */
@Controller //APIの入り口
public class BulkController {
    final static Logger logger = LoggerFactory.getLogger(BulkController.class);

    @Autowired
    private BooksService booksService;

    @RequestMapping(value = "/bulkBook", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String login(Model model) {
        return "bulkBook";
    }

    /**
     * 書籍情報を一括登録する 
     * @param file CSVファイル
     * @param model モデル
     * @return 遷移先画面
     */
    @Transactional
    @RequestMapping(value = "/bulkinsertBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
            @RequestParam("bulk_form") MultipartFile file,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

        //リストを作る
        List<BookDetailsInfo> bookcsv = new ArrayList<BookDetailsInfo>();
        //try-with-resourcesを使う
        try (InputStream stream = file.getInputStream();
                Reader reader = new InputStreamReader(stream);
                BufferedReader buf = new BufferedReader(reader);) {
            String line = null;
            String errorMessage = "";//箱を作る
            int rowCount = 0; //csvファイルの行の番号（何行目）
            boolean errorFlag = false;//変数に初期値としてfalseを入れる

            // ファイルを行単位で読む
            //while文では読んで値を保持していく作業が行われている。
            while ((line = buf.readLine()) != null) {
                // 読み込んだ行を、「,」で分割してbrにいれる
                String[] br = line.split(",", 6);
                rowCount++;
                //必須項目に値が入っているかチェック
                //if文はtrueの時に括弧の中の処理がされる
                //isEmptyは文字の長さが０の状態
                if (br[0].isEmpty() || br[1].isEmpty() || br[2].isEmpty() || br[3].isEmpty()) {
                    errorMessage += rowCount + "行目で必要な情報がありません"; //空だった時にエラーの処理する
                    errorFlag = true;

                }
                //出版日のバリデーションチェック
                if (br[3] != null)
                    ;
                {
                    try {
                        SimpleDateFormat d1 = new SimpleDateFormat("yyyyMMdd");
                        d1.setLenient(false);
                        d1.parse(br[3]);
                    } catch (Exception e) {
                        errorMessage += rowCount + "行目の出版日は半角数字のYYYYMMDD形式で入力してください"; //空だった時にエラーの処理する
                        errorFlag = true;

                    }
                }
                //ISBNのバリデーションチェック
                if (!(br[4].isEmpty())
                        && !(br[4].matches("([0-9]{10}|[0-9]{13})?")))
                    ;
                {
                    errorMessage += rowCount + "行目のISBNの桁数または半角数字が正しくありません"; //空だった時にエラーの処理する
                    errorFlag = true;//trueの時にif文は実行される。エラーが起きたことを示すためにtrueを代入する

                }

                // 書籍情報をDtoに格納する。
                BookDetailsInfo bookInfo = new BookDetailsInfo();
                bookInfo.setTitle(br[0]);
                bookInfo.setAuthor(br[1]);
                bookInfo.setPublisher(br[2]);
                bookInfo.setPublishDate(br[3]);
                bookInfo.setIsbn(br[4]);
                bookInfo.setDescription(br[5]);

                bookcsv.add(bookInfo);

            }
            //エラーがあった場合の処理
            if (errorFlag) {
                model.addAttribute("error", errorMessage);
                return "bulkBook";
            }

            // 書籍情報を新規登録する、拡張for文
            for (BookDetailsInfo book : bookcsv) {
                booksService.registBook(book);
            }
            model.addAttribute("resultMessage", "登録完了");

            model.addAttribute("bookDetailsInfo", booksService.getBookInfo(booksService.getBookId()));
            //  一括登録画面に遷移する
            return "bulkBook";

        } catch (IOException e1) {
            model.addAttribute("errorMessage", "CSVファイル読み込みでエラーが発生しました。");
            return "bulkBook";

        } catch (Exception e2) {
            model.addAttribute("errorMessage", "CSVファイル読み込みでエラーが発生しました。");
            return "bulkBook";

        }

    }
}