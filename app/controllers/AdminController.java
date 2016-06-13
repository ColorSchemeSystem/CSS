package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;
import play.cache.Cache;
import play.db.ebean.Model.Finder;
import play.libs.F.Promise;
import play.libs.WS;
import views.html.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

import com.avaje.ebean.Query;

import forms.MyPage;
import forms.ModifyPassword;
import forms.TemplateUpload;
import models.*;

import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import services.AdminService;
import services.ImageService;

public class AdminController extends BaseController {
	private static AdminService adminS = new AdminService();

	/**
	*  ログイン画面へ
	*/
	public static Result login() {
		Form<Member> form = Form.form(Member.class);
		return ok(login.render(null, "ログイン", form));
	}

	/**
	*  ログイン画面から送られてきたフォーム情報を取得しデータベースに保存されているか名前検索をかける
	*  なければlogin画面に戻る,あればpasswordが一致しているか確認
	*  一致していなければlogin画面に戻る,一致していたらログイン処理(1時間の間ログイン情報を保持)をしてindex画面へ
	*/
	public static Result loginEntry() {
		Form<Member> form = Form.form(Member.class).bindFromRequest();
		if(!form.hasErrors()) {
			// 名前を取得しデータベース検索
			Member mem = adminS.findMemberByMemberName(form.get().memberName);
			if(mem == null) {
				return badRequest(login.render(null, "ログインに失敗しました", form));
			}
			// パスワード確認
			String password = form.get().password;
			if(!adminS.checkpw(password, mem.password)) {
				// 一致していなかったらログイン画面へ
				return badRequest(login.render(null, "ログインに失敗しました", form));
			}
			// ログインする
			writeObjectOnSession("Member", mem);
		} else {
			return badRequest(login.render(null, "ログインに失敗しました", form));
		}
		return redirect("/");
	}

	/**
	*  ログアウト処理
	*/
	public static Result logout() {
		removeObjectSession("Member");
		return redirect("/");
	}

	/**
	*  新規アカウント登録画面
	*/
	public static Result createAccount() {
		Form<Member> form = Form.form(Member.class);
		return ok(createAccount.render(null, "新規登録", form));
	}

	/**
	*  送られてきた情報をフォーム取得しIDが存在するか判別
	*  なければ新規登録
	*  ログイン処理をしてindex画面へ
	*/
	public static Result saveAccount() {
		Form<Member> form = Form.form(Member.class).bindFromRequest();
		if(!form.hasErrors()) {
			if(adminS.memberExists(form.get().memberName)) {
				return badRequest(createAccount.render(null, "既に同じ名前のアカウントが存在します。", form));
			}	else	{
				Member mem = new Member();
				mem.memberName = form.get().memberName;
				mem.password = adminS.passwordHash(form.get().password);
				mem.mail = form.get().mail;
				mem.chooser = new Chooser();
				mem.nickName = form.get().nickName;
				adminS.storeMember(mem);
				writeObjectOnSession("Member", mem);
				return redirect("/");
			}
		} else {
			// 新規アカウント登録画面へ
			return badRequest(createAccount.render(null, "ERROR!　もう一度入力してください", form));
		}
	}

	/**
	 * @return
	 */
	public static Result editProfile() {
		Form<MyPage> form = Form.form(MyPage.class);
		Member mem = isLoggedIn();
		if(mem == null) {
			return redirect(routes.AdminController.login());
		}
		Chooser chooser = adminS.findChooserByChooserId(mem.chooser.chooserId);
		MyPage setting = new MyPage();
		setting.memberId = mem.memberId;
		setting.memberName = mem.memberName;
		setting.nickName = mem.nickName;
		setting.mail = mem.mail;
		setting.hsvpanel	= chooser.hsvpanel;
		setting.slider		= chooser.slider;
		setting.swatche		= chooser.swatche;
		form = form.fill(setting);
		return ok(editProfile.render(mem, form));
	}

	public static Result editColor(){
		Form<MyPage> form = Form.form(MyPage.class);
		Member mem = isLoggedIn();
		if(mem == null) {
			return redirect(routes.AdminController.login());
		}
		Chooser chooser = adminS.findChooserByChooserId(mem.chooser.chooserId);
		System.out.println("hsv"+chooser.hsvpanel);
		MyPage setting = new MyPage();
		setting.memberId = mem.memberId;
		setting.memberName = mem.memberName;
		setting.nickName = mem.nickName;
		setting.mail = mem.mail;
		setting.hsvpanel	= chooser.hsvpanel;
		setting.slider		= chooser.slider;
		setting.swatche		= chooser.swatche;
		form = form.fill(setting);
		return ok(editColor.render(mem, form));
	}

	/**
	 * @return
	 */
	public static Result saveColorSetting() {
		Form<MyPage> form = Form.form(MyPage.class).bindFromRequest();
		Member mem = isLoggedIn();
		if(mem == null) {
			System.out.println("memnone");
			return badRequest(editColor.render(mem, form));
		}
		if(!form.hasErrors()) {
			Chooser chooser = adminS.findChooserByChooserId(mem.chooser.chooserId);
			chooser.hsvpanel	= form.get().hsvpanel;
			chooser.slider		= form.get().slider;
			chooser.swatche		= form.get().swatche;
			chooser.update();
		} else {
			System.out.println("bad");
			return badRequest(editColor.render(mem, form));
		}
		return ok(editColor.render(mem, form));
	}

	public static Result saveProfileSetting(){
		Form<MyPage> form = Form.form(MyPage.class).bindFromRequest();
		Member mem = isLoggedIn();
		if(mem == null) {
			return badRequest(editColor.render(mem, form));
		}
		if(!form.hasErrors()) {
			if(!mem.memberName.equals(form.get().memberName) || !mem.mail.equals(form.get().mail) || !mem.nickName.equals(form.get().nickName)) {
				mem.memberName = form.get().memberName;
				mem.mail = form.get().mail;
				mem.nickName = form.get().nickName;
				System.out.println("beforemem = " + mem.nickName);
				adminS.updateMember(mem);
				System.out.println("aftermem = " + mem.nickName);
			}
		} else {
			return badRequest(editProfile.render(mem, form));
		}
		return ok(editProfile.render(mem, form));
	}

	/**
	 *
	 * @return
	 */
	public static Result editPass() {
		Member member = isLoggedIn();
		if(member == null) {
			return redirect(routes.AdminController.login());
		}
		Form<ModifyPassword> form = Form.form(ModifyPassword.class);
		return ok(editPass.render(form,member,""));
	}

	/**
	 * @return
	 */
	public static Result doModifyPassword() {
		Form<ModifyPassword> form = Form.form(ModifyPassword.class).bindFromRequest();
		Member member = isLoggedIn();
		if(member == null) {
			return redirect(routes.AdminController.login());
		}
		if(!form.hasErrors()) {
			boolean changeable = true;
			if(!adminS.checkpw(form.get().password,member.password)) {
				changeable = false;
			}
			if(!form.get().newPassword.equals(form.get().confirmNewPassword)) {
				changeable = false;
			}
			if(changeable) {
				member = adminS.findMemberById(member.memberId);
				member.password = form.get().newPassword;
				adminS.updateMember(member);
				removeObjectSession("Member");
				writeObjectOnSession("Member", member);
				return redirect(routes.AdminController.editPass());
			}	else	{
				return ok(editPass.render(form,member,"入力値が不正です。"));
			}
		}	else	{
			return ok(editPass.render(form,member,"入力値が不正です。"));
		}
	}
}