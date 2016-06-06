package services;

import org.mindrot.jbcrypt.BCrypt;

import models.Chooser;
import models.Member;
import models.Template;

public class AdminService {
	/**
	 * 
	 * @param template
	 */
	public void saveTemplate(Template template) {
		template.save();
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
}