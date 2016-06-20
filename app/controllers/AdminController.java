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

import dtos.PagingDto;
import forms.MyPage;
import forms.ModifyPassword;
import forms.TemplateUpload;
import models.*;

import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import services.*;

public class AdminController extends BaseController {
	private static AdminService adminS = new AdminService();
	private static AppService appS = new AppService();
	/**
	*  ログイン画面へ
	*/
	public static Result login() {
		Member member = isLoggedIn();
		if(member != null) {
			return redirect(routes.AdminController.editProfile());
		}
		Form<Member> form = Form.form(Member.class);
		return ok(login.render(null, "ログイン", form));
	}

	/**
	*  ログイン画面から送られてきたフォーム情報を取得しデータベースに保存されているか名前検索をかける
	*  なければlogin画面に戻る,あればpasswordが一致しているか確認
	*  一致していなければlogin画面に戻る,一致していたらログイン処理(1時間の間ログイン情報を保持)をしてindex画面へ
	*/
	public static Result loginEntry() {
		Member member = isLoggedIn();
		if(member != null) {
			return redirect(routes.AdminController.editProfile());
		}
		Form<Member> form = Form.form(Member.class).bindFromRequest();
		if(!form.hasErrors()) {
			// 名前を取得しデータベース検索
			Member mem = adminS.findMemberByMemberName(form.get().memberName);
			if(mem == null) {
				adminS.addMemberErrors(form, "ユーザIDが誤っています", "memberName");
				return badRequest(login.render(null, "ログイン", form));
			}
			// パスワード確認
			String password = form.get().password;
			if(!adminS.checkpw(password, mem.password)) {
				// 一致していなかったらログイン画面へ
				adminS.addMemberErrors(form, "パスワードが誤っています", "password");
				return badRequest(login.render(null, "ログイン", form));
			}
			// ログインする
			writeObjectOnSession("Member", mem);
		} else {
			adminS.addMemberErrors(form, "ユーザIDが誤っています", "memberName");
			adminS.addMemberErrors(form, "パスワードが誤っています", "password");
			return badRequest(login.render(null, "ログイン", form));
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
		Member member = isLoggedIn();
		if(member != null) {
			return redirect(routes.AdminController.editProfile());
		}
		Form<Member> form = Form.form(Member.class);
		return ok(createAccount.render(null, "新規登録", form));
	}

	/**
	*  送られてきた情報をフォーム取得しIDが存在するか判別
	*  なければ新規登録
	*  ログイン処理をしてindex画面へ
	*/
	public static Result saveAccount() {
		Member member = isLoggedIn();
		if(member != null) {
			return redirect(routes.AdminController.editProfile());
		}
		Form<Member> form = Form.form(Member.class).bindFromRequest();
		if(!form.hasErrors()) {
			if(adminS.memberExists(form.get().memberName)) {
				adminS.addMemberErrors(form, "同じユーザーIDがすでに存在しています", "memberName");
				return badRequest(createAccount.render(null, "新規登録", form));
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
			return badRequest(createAccount.render(null, "新規登録", form));
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
		return ok(editProfile.render(mem, form, ""));
	}

	public static Result editColor(){
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
		return ok(editColor.render(mem, form, ""));
	}

	/**
	 * @return
	 */
	public static Result saveColorSetting() {
		Form<MyPage> form = Form.form(MyPage.class).bindFromRequest();
		Member mem = isLoggedIn();
		if(mem == null) {
			return redirect(routes.AdminController.login());
		}
		if(!form.hasErrors()) {
			Chooser chooser = adminS.findChooserByChooserId(mem.chooser.chooserId);
			chooser.hsvpanel	= form.get().hsvpanel;
			chooser.slider		= form.get().slider;
			chooser.swatche		= form.get().swatche;
			chooser.update();
		} else {
			return badRequest(editColor.render(mem, form, "保存に失敗しました"));
		}
		return ok(editColor.render(mem, form, "保存しました"));
	}

	public static Result saveProfileSetting(){
		Form<MyPage> form = Form.form(MyPage.class).bindFromRequest();
		Member mem = isLoggedIn();
		if(mem == null) {
			return redirect(routes.AdminController.login());
		}
		if(!form.hasErrors()) {
			if(!mem.memberName.equals(form.get().memberName) || !mem.mail.equals(form.get().mail) || !mem.nickName.equals(form.get().nickName)) {
				mem.memberName = form.get().memberName;
				mem.mail = form.get().mail;
				mem.nickName = form.get().nickName;
				adminS.updateMember(mem);
			}
		} else {
			return badRequest(editProfile.render(mem, form, "保存に失敗しました"));
		}
		return ok(editProfile.render(mem, form, "保存しました"));
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
				adminS.addPasswordErrors(form, "パスワードが誤っています", "password");
				changeable = false;
			}
			if(!form.get().newPassword.equals(form.get().confirmNewPassword)) {
				changeable = false;
				adminS.addPasswordErrors(form, "パスワードが一致していません", "newPassword");
				adminS.addPasswordErrors(form, "パスワードが一致していません", "confirmNewPassword");
			}
			if(changeable) {
				member = adminS.findMemberById(member.memberId);
				member.password = adminS.passwordHash(form.get().newPassword);
				adminS.updateMember(member);
				removeObjectSession("Member");
				writeObjectOnSession("Member", member);
				form = Form.form(ModifyPassword.class);
				return ok(editPass.render(form,member,"パスワードを変更しました"));
			}	else	{
				form = Form.form(ModifyPassword.class);
				return ok(editPass.render(form,member,""));
			}
		}	else	{
			form = Form.form(ModifyPassword.class);
			return ok(editPass.render(form,member,""));
		}
	}

	public static Result editTempList(){
		Member member = isLoggedIn();
		String type = request().getQueryString("type");
		Integer page = 1;
		try {
			page = Integer.parseInt(request().getQueryString("page"));
		} catch(Exception e) {}
		PagingDto<Template> pagingDto;
		pagingDto = appS.findTemplatesWithPages(page, 12 , member.memberId);
		return ok(editTemp.render(pagingDto,member,appS.getSnapShotsUrl()));
	}

	public static Result updateTemp(){
		Form<TemplateUpload> form = Form.form(TemplateUpload.class).bindFromRequest();
		Member member = isLoggedIn();
		TemplateUpload editTmp = form.get();
		if(editTmp.templateName.trim().isEmpty()){
			editTmp.templateName = "template" + editTmp.templateId;
		}
		Template temp = adminS.findTemplateById(editTmp.templateId);
		temp.templateName = editTmp.templateName;
		temp.templateMessage = editTmp.templateMessage;
		adminS.updateTemplate(temp);
		PagingDto<Template> pagingDto;
		pagingDto = appS.findTemplatesWithPages(1, 12 , member.memberId);
		return ok(editTemp.render(pagingDto,member,appS.getSnapShotsUrl()));
	}
}