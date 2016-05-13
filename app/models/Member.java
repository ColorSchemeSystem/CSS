package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Members")
public class Member extends BaseModel {
	@Id
	public Long memberId;

	public String memberName;

	public String password;

	public String mail;
}
