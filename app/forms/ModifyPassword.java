package forms;

import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;

public class ModifyPassword {
	@Required
	public String password;
	
	@Required
	@Pattern(value = "^[a-zA-Z0-9]*$")
	@MinLength(6)
	public String newPassword;
	
	@Required
	public String confirmNewPassword;
}
