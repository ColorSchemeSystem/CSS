package models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model.Finder;

@Entity
@Table(name="Members")
public class Member extends BaseModel {
	@Id
	public Long memberId;

	@Required(message = "入力してください")
	public String memberName;

	@Required(message = "入力してください")
	public String password;

	public String mail;

	public Long chooserId;
		
	public Timestamp lastLogin;
	
	public static final Finder<Long, Member> find = new Finder<Long, Member>(Long.class,Member.class);
}