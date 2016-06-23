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

public class AdminService {
	/**
	 *
	 * @param template
	 */
	public void saveTemplate(Template template) {
		template.save();
	}

	public void updateTemplate(Template template){
		template.update();
	}

	public void deleteTemplate(Template template){
		template.delete();
	}

	public Template findTemplateById(Long id){
		return Template.findById(id);
	}

	public List<Template> findTemplateByUser(Long id){
		return Template.find.where().eq("Member_Member_Id", id).findList();
	}

	public List<Template> findTemplateByUser(Long id, int flg){
		if(flg == 0){
			return Template.find.where().eq("Member_Member_Id", id).eq("accessFlag", "0").findList();
		}else{
			return Template.find.where().eq("Member_Member_Id", id).eq("accessFlag", "1").findList();
		}
	}

	public void deleteMemberWithTemplate(Long id, int flg){
		List<Template> list = findTemplateByUser(id, flg);
		for(Template temp : list){
			if(temp != null){
				temp.member = null;
				temp.accessFlag = 0;
				temp.update();
			}
		}
		List<Template> restList = findTemplateByUser(id);
		for(Template temp : restList){
			if(temp != null){
				temp.delete();
			}
		}
	}

	public List<Image> findImageByUser(Long id){
		return Image.find.where().eq("Member_Member_id", id).findList();
	}

	public void deleteMemberWithImage(Long id){
		List<Image> list = findImageByUser(id);
		for(Image img : list){
			if(img != null){
				img.member = null;
				img.update();
			}
		}
	}

	public void deleteImg(Image img){
		img.delete();
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
		if(member != null) {
			return true;
		}	else	{
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
		member.save();
	}

	public void updateMember(Member member){
		member.update();
	}

	public void deleteMember(Member member){
		member.delete();
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

	public Form<Member> addMemberErrors(Form<Member> form, String error, String errorColumn){
		List<ValidationError> errors = new ArrayList<ValidationError>();
		errors.add(new ValidationError(errorColumn, error));
		form.errors().put(errorColumn, errors);
		return form;
	}

	public Form<ModifyPassword> addPasswordErrors(Form<ModifyPassword> form, String error, String errorColumn){
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
		List<Image> images = Image.find.where()
				.eq("imageName", imageName).eq("member.memberId", memberId)
				.findList();
		if(images.size() > 0) {
			return images.get(0);
		}	else	{
			return null;
		}
	}
}