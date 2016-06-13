package forms;

import java.util.List;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;

public class MyPage {
	@Required
	public String memberName;

	public Long memberId;

	@Email
	public String mail;

	public String nickName;

	public Boolean hsvpanel;

	public Boolean slider;

	public Boolean swatche;

	List<String> listSwatches;
}