/**
 * 詳細画面ボタンの非活性
 */

$(function(){
	var status =$("#borrowStatus").text();
     if(status == "貸出可"){
	//返すボタン非活性
	   $("#btn_returnBook").prop('disabled',true);
     }
     if(status == "貸出し中"){
	//借りるボタン非活性
	//削除ボタン非活性
      $("#btn_rentBook").prop('disabled',true);
      $("#btn_deleteBook").prop('disabled',true);
     }
})
