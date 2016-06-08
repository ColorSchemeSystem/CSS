package dtos;

import java.util.ArrayList;
import java.util.List;

public class PagingDto<T> {
	public List<T> data = new ArrayList<T>();
	public int currentPage;
	public int totalPage;
}
