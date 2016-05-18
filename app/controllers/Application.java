package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;
import play.cache.Cache;

import views.html.*;
import models.*;

public class Application extends Controller {

	public static Result index() {
		Member mem = (Member)Cache.get("Member");
		if(mem != null) {
			return ok(index.render(mem.memberName, "/myPage"));
		}
		return ok(index.render("ログイン", "/login"));
	}

	public static Result login() {
		Form<Member> form = Form.form(Member.class);
		return ok(login.render(form));
	}

	/*
	*  ログイン画面から送られてきたフォーム情報を取得しデータベースに保存されているか名前検索をかける
	*  なければlogin画面に戻る,あればpasswordが一致しているか確認
	*  一致していなければlogin画面に戻る,一致していたらログイン処理(1時間の間ログイン情報を保持)をしてindex画面へ
	*/
	public static Result loginEntry() {
		Form<Member> form = Form.form(Member.class).bindFromRequest();
		Member mem = new Member();
		if(!form.hasErrors()) {
			// TODO 名前を取得しデータベース検索

			// TODO パスワード確認

			// TODO ログイン画面

			// 仮処理
			mem.memberName = form.get().memberName;
			Cache.set("Member", mem, 1 * 60 * 60);
		} else {
			return ok(login.render(form));
		}
		return redirect("./");
	}

	/*
	*  新規アカウント登録画面
	*/
	public static Result createAccount() {
		Form<Member> form = Form.form(Member.class);
		return ok(createAccount.render("新規登録", form));
	}

	/*
	*  送られてきた情報をフォーム取得しIDが存在するか判別
	*  なければ新規登録,あれば上書き
	*  index画面へ?
	*/
	public static Result saveAccount() {
		Form<Member> form = Form.form(Member.class).bindFromRequest();
		if(!form.hasErrors()) {
			// TODO IDがあるか判断し適切処理
		} else {
			// TODO 新規アカウント登録画面へ
		}
		return TODO;
	}

	public static Result myPage() {
		return ok(myPage.render());
	}
}
