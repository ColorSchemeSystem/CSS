package dtos;

import java.util.ArrayList;
import java.util.List;

public class AjaxImageResultList {
	public boolean status = false;
	
	public Long memberId;
	
	public List<AjaxImageResultElement> elements = 
			new ArrayList<AjaxImageResultElement>();
}
