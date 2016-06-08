package models;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;

@Entity
@Table(name="Members")
public class Member extends BaseModel {
	@Id
	public Long memberId;

	@Required
	public String memberName;

	@Required
	@Pattern(value = "^[a-zA-Z0-9]*$")
	@MinLength(6)
	public String password;

	public String mail;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	public Chooser chooser;
		
	public Timestamp lastLogin;
	
	public static final Finder<Long, Member> find = new Finder<Long, Member>(Long.class,Member.class);
}