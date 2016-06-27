package forms;

import java.util.List;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

public class MyPage {
	@Required
	@MaxLength(50)
	public String memberName;

	public Long memberId;

	@Email
	@MaxLength(255)
	public String mail;

	@MaxLength(50)
	public String nickName;

	public Boolean hsvpanel;

	public Boolean slider;

	public Boolean swatche;

	List<String> listSwatches;
}