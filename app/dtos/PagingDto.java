package dtos;

import java.util.ArrayList;
import java.util.List;
import com.avaje.ebean.PagingList;

public class PagingDto<T> {
	public List<T> data = new ArrayList<T>();
	public int currentPage;
	public int totalPage;
}
