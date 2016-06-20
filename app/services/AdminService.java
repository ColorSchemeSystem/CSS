package services;

import org.mindrot.jbcrypt.BCrypt;

import models.*;
import play.data.Form;
import play.data.validation.ValidationError;
import forms.*;
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

	public Template findTemplateById(Long id){
		return Template.findById(id);
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
}