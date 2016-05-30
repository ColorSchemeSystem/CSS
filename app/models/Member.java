package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import play.data.validation.Constraints.Required;

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
}
