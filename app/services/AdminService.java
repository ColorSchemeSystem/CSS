package services;

import org.mindrot.jbcrypt.BCrypt;

import models.*;
import play.Logger;
import play.data.Form;
import play.data.validation.ValidationError;
import forms.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import javax.persistence.OptimisticLockException;

/**
 * insert・updateの処理は楽観ロックがかかっている場合には 例外をキャッチして再試行するようにしている。
 *
 * @author masataka.okudera
 *
 */
public class AdminService {
	/**
	 *
	 * @param template
	 */
	public void deleteTemplate(Template template) {
		try {
			template.delete();
		} catch (OptimisticLockException e) {
			e.printStackTrace();
			Logger.info("処理が競合しました。");
			do {
				template = this.findTemplateById(template.templateId);
				if (template == null) {
					break;
				}
				try {
					template.delete();
					break;
				} catch (OptimisticLockException e2) {
					e2.printStackTrace();
					Logger.info("処理が競合しました。");
				}
			} while (true);
		}
	}

	public Template findTemplateById(Long id) {
		return Template.findById(id);
	}

	public List<Template> findTemplateByUser(Long id) {
		return Template.find.where().eq("Member_Member_Id", id).findList();
	}

	public List<Template> findTemplateByUser(Long id, int flg) {
		if (flg == 0) {
			return Template.find.where().eq("Member_Member_Id", id).eq("accessFlag", "0").findList();
		} else {
			return Template.find.where().eq("Member_Member_Id", id).eq("accessFlag", "1").findList();
		}
	}

	/**
	 * @param id
	 * @param flg
	 */
	public void deleteMemberWithTemplate(Long id, int flg) {
		List<Template> list = findTemplateByUser(id, flg);
		for (Template temp : list) {
			if (temp != null) {
				temp.member = null;
				temp.accessFlag = 0;
				try {
					temp.update();
				} catch (OptimisticLockException e) {
					e.printStackTrace();
					Logger.info("処理が競合しました。");
					do {
						temp = this.findTemplateById(temp.templateId);
						if (temp == null) {
							break;
						}
						temp.member = null;
						temp.accessFlag = 0;
						try {
							temp.update();
							break;
						} catch (Exception e2) {
							e2.printStackTrace();
							Logger.info("処理が競合しました。");
						}
					} while (true);
				}
			}
		}
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	public List<Image> findImageByUser(Long id) {
		return Image.find.where().eq("Member_Member_id", id).findList();
	}

	public Image findImageById(Long id){
		return Image.find.byId(id);
	}

	public void updateImage(Image img){
		try{
			img.update();
		}catch(OptimisticLockException e){
			e.printStackTrace();
			Logger.info("処理が競合しました。");
			do {
				img = Image.find.byId(img.imageId);
				if(img == null){
					break;
				}
				try{
					img.update();
				}catch(OptimisticLockException e2){
					e2.printStackTrace();
					Logger.info("処理が競合しました。");
				}
			} while (true);
		}
	}

	public void deleteImage(Image img){
		try{
			img.delete();
		}catch(OptimisticLockException e){
			e.printStackTrace();
			Logger.info("処理が競合しました");
			do {
				img = Image.find.byId(img.imageId);
				if(img == null){
					break;
				}
				try{
					img.delete();
				}catch(OptimisticLockException e2){
					e2.printStackTrace();
					Logger.info("処理が競合しました");
				}
			}while (true);
		}
	}

	/**
	 *
	 * @param id
	 */
	public void deleteMemberWithImage(Long id) {
		List<Image> list = findImageByUser(id);
		for (Image img : list) {
			if (img != null) {
				img.member = null;
				try {
					img.update();
				} catch (OptimisticLockException e) {
					e.printStackTrace();
					Logger.info("処理が競合しました。");
					do {
						img = Image.find.byId(img.imageId);
						if (img == null) {
							break;
						}
						img.member = null;
						try {
							img.update();
						} catch (OptimisticLockException e2) {
							e2.printStackTrace();
							Logger.info("処理が競合しました。");
						}
					} while (true);
				}
			}
		}
	}

	/**
	 *
	 * @param img
	 */
	public void deleteImg(Image img) {
		try {
			img.delete();
		} catch (OptimisticLockException e) {
			e.printStackTrace();
			Logger.info("処理が競合しました。");
			do {
				img = Image.find.byId(img.imageId);
				if (img == null) {
					break;
				}
				try {
					img.delete();
					break;
				} catch (OptimisticLockException e2) {
					e2.printStackTrace();
					Logger.info("処理が競合しました。");
				}
			} while (true);
		}
	}

	/**
	 *
	 * @param memberId
	 * @return
	 */
	public Member findMemberById(Long memberId) {
		return Member.find.byId(memberId);
	}

	/**
	 *
	 * @param memberName
	 * @return
	 */
	public boolean memberExists(String memberName) {
		Member member = Member.find.where().eq("memberName", memberName).findUnique();
		if (member != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 *
	 * @param memberName
	 * @return
	 */
	public Member findMemberByMemberName(String memberName) {
		Member member = Member.find.where().eq("memberName", memberName).findUnique();
		return member;
	}

	/**
	 *
	 * @param member
	 */
	public void storeMember(Member member) {
		try {
			member.save();
		} catch (OptimisticLockException e) {
			e.printStackTrace();
			Logger.info("処理が競合しました。");
			do {
				Member newMember = this.findMemberById(member.memberId);
				newMember.memberName = member.memberName;
				newMember.nickName = member.nickName;
				newMember.password = member.password;
				newMember.mail = member.mail;
				newMember.chooser = member.chooser;
				newMember.lastLogin = member.lastLogin;
				try {
					newMember.save();
					break;
				} catch (OptimisticLockException e2) {
					e2.printStackTrace();
					Logger.info("処理が競合しました。");
				}
			} while (true);
		}
	}

	/**
	 *
	 * @param member
	 */
	public void updateMember(Member member) {
		try {
			member.update();
		} catch (OptimisticLockException e) {
			e.printStackTrace();
			Logger.info("処理が競合しました。");
			do {
				Member newMember = this.findMemberById(member.memberId);
				newMember.memberName = member.memberName;
				newMember.nickName = member.nickName;
				newMember.password = member.password;
				newMember.mail = member.mail;
				newMember.chooser = member.chooser;
				newMember.lastLogin = member.lastLogin;
				try {
					newMember.update();
					break;
				} catch (OptimisticLockException e2) {
					e2.printStackTrace();
					Logger.info("処理が競合しました。");
				}
			} while (true);
		}
	}

	/**
	 *
	 * @param member
	 */
	public void deleteMember(Member member) {
		try {
			member.delete();
		} catch (OptimisticLockException e) {
			e.printStackTrace();
			Logger.info("処理が競合しました。");
			do {
				member = this.findMemberById(member.memberId);
				if (member == null) {
					break;
				}
				try {
					member.delete();
					break;
				} catch (OptimisticLockException e2) {
					e2.printStackTrace();
					Logger.info("処理が競合しました。");
				}
			} while (true);
		}
	}

	/**
	 *
	 * @param password
	 * @return
	 */
	public String passwordHash(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	/**
	 *
	 * @param password
	 * @param hashedPassword
	 * @return
	 */
	public boolean checkpw(String password, String hashedPassword) {
		return BCrypt.checkpw(password, hashedPassword);
	}

	/**
	 *
	 * @param chooserId
	 * @return
	 */
	public Chooser findChooserByChooserId(Long chooserId) {
		return Chooser.find.byId(chooserId);
	}

	public Form<Member> addMemberErrors(Form<Member> form, String error, String errorColumn) {
		List<ValidationError> errors = new ArrayList<ValidationError>();
		errors.add(new ValidationError(errorColumn, error));
		form.errors().put(errorColumn, errors);
		return form;
	}

	public Form<ModifyPassword> addPasswordErrors(Form<ModifyPassword> form, String error, String errorColumn) {
		List<ValidationError> errors = new ArrayList<ValidationError>();
		errors.add(new ValidationError(errorColumn, error));
		form.errors().put(errorColumn, errors);
		return form;
	}

	/**
	 * @param imageName
	 * @param memberId
	 * @return
	 */
	public Image findImageByImageNameAndMemberId(String imageName, Long memberId) {
		List<Image> images = Image.find.where().eq("imageName", imageName).eq("member.memberId", memberId).findList();
		if (images.size() > 0) {
			return images.get(0);
		} else {
			return null;
		}
	}
}