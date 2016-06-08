package forms;

import java.util.List;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;

public class MyPage {
	@Required
	public String memberName;
	@Required
	@Email
	public String mail;
	public Integer hsvpanel;
	public Integer slider;
	public Integer swatche;

	List<String> listSwatches;
}