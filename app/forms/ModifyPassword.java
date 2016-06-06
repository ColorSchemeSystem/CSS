package forms;

import play.data.validation.Constraints.Required;

public class ModifyPassword {
	@Required
	public String password;
	@Required
	public String newPassword;
	@Required
	public String confirmNewPassword;
}
