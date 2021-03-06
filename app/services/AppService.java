package services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import java.util.regex.Pattern;

import javax.persistence.OptimisticLockException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;

import dtos.PagingDto;
import models.Chooser;
import models.Image;
import models.Member;
import models.Template;
import play.Logger;
import play.Play;
import play.db.ebean.Model.Finder;

import com.avaje.ebean.PagingList;

public class AppService {
	/**
	 * @return
	 */
	public String escapeHtml(String html) {
		return html.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
	}

	/**
	 * @return
	 */
	public String getPublicFolderPath() {
		String path = Play.application().configuration().getString("paths.nginx");
		if(StringUtils.isEmpty(path)) {
			return Play.application().path().getPath() + "/public";
		}	else	{
			return path;
		}
	}

	/**
	 *
	 * @return
	 */
	public String getSnapShotsUrl() {
		String path = Play.application().configuration().getString("paths.snapshots");
		if(!StringUtils.isEmpty(path)) {
			return path;
		}	else	{
			return "/assets/snapshots";
		}
	}

	/**
	 * @return
	 */
	public String getMemberimagesUrl() {
		String path = Play.application().configuration().getString("paths.images");
		if(!StringUtils.isEmpty(path)) {
			return path;
		}	else	{
			return "/assets/member-images";
		}
	}

	/**
	 * @return
	 */
	public String getIframesUrl() {
		String path = Play.application().configuration().getString("paths.iframes");
		if(!StringUtils.isEmpty(path)) {
			return path;
		}	else	{
			return null;
		}
	}

	/**
	 *
	 * @return
	 */
	public List<Template> findAllTemplates() {
		return Template.find.all();
	}

	/**
	 *
	 * @param memberId
	 * @return
	 */
	public List<Template> findAllTemplates(Long memberId) {
		return Template.find.where().eq("member.memberId", memberId).findList();
	}

	/**
	 *
	 * @param page
	 * @param itemPerPage
	 * @return
	 */
	public PagingDto<Template> findTemplatesWithPages(int page,int itemPerPage,Long memberId) {
		Page<Template> templatePage;
		if(memberId == null) {
			templatePage = Template.find.where().eq("accessFlag", "0")
					.orderBy("templateId desc").findPagingList(itemPerPage).getPage(page -1);
		}	else	{
			templatePage = Template.find.where().eq("Member_Member_Id", memberId)
					.orderBy("templateId desc").findPagingList(itemPerPage).getPage(page -1);
		}
		PagingDto<Template> dto = new PagingDto<Template>();
		dto.data = templatePage.getList();
		dto.currentPage = page;
		dto.totalPage = templatePage.getTotalPageCount();
		return dto;
	}

	/**
	 *
	 * @param page
	 * @param itemPerPage
	 * @return
	 */
	public PagingDto<Template> findTemplatesWithPages(int page,int itemPerPage) {
		return findTemplatesWithPages(page, itemPerPage,(Long) null);
	}

	/**
	 * @param memberId
	 * @return
	 */
	public List<Image> findAllImages(Long memberId) {
		return Image.find.where().eq("member.memberId", memberId).findList();
	}

	/**
	 * @param page
	 * @param itemPerPage
	 * @return
	 */
	public PagingDto<Image> findImagesWithPages(int page,int itemPerPage, Long memberId) {
		Page<Image> imagePage = Image.find.where()
				.eq("Member_Member_Id", memberId).orderBy("imageId desc")
				.findPagingList(itemPerPage).getPage(page -1);
		PagingDto<Image> dto = new PagingDto<Image>();
		dto.data = imagePage.getList();
		dto.currentPage = page;
		dto.totalPage = imagePage.getTotalPageCount();
		return dto;
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	public Integer getMaxPage(Long id) {
        Finder<Long, Template> find = new Finder<Long, Template>(Long.class, Template.class);
        PagingList<Template> pagingList;
        if(id == null){
        	pagingList = find.where().eq("accessFlag", "0").orderBy("created desc").findPagingList(12);
        }else{
        	pagingList = find.where().eq("Member_Member_Id", id).orderBy("created desc").findPagingList(12);
        }
        return pagingList.getTotalPageCount();
        // getTotalPageCountを使用して最大ページ数取得
	}

	public Integer getMaxPageOfImage(Long id, int page){
		Finder<Long, Image> find = new Finder<Long, Image>(Long.class, Image.class);
        PagingList<Image> pagingList;
        pagingList = find.where().eq("Member_Member_Id", id).orderBy("created desc").findPagingList(page);
        return pagingList.getTotalPageCount();
	}

	/**
	 *
	 * @param template
	 */
	public void saveTemplate(Template template) {
		try {
			template.save();
		} catch (OptimisticLockException e) {
			e.printStackTrace();
			Logger.info("処理が競合しました。");
			do {
				Template newTemplate = Template.find.byId(template.templateId);
				newTemplate.templateName = template.templateName;
				newTemplate.templateMessage = template.templateMessage;
				newTemplate.accessFlag = template.accessFlag;
				try {
					newTemplate.save();
					break;
				} catch (OptimisticLockException e2) {
					e2.printStackTrace();
					Logger.info("処理が競合しました。");
				}
			} while (true);
		}
	}

	/**
	 * @param template
	 */
	public void updateTemplate(Template template){
		try {
			template.update();
		} catch (OptimisticLockException e) {
			e.printStackTrace();
			Logger.info("処理が競合しました。");
			do {
				Template newTemplate = Template.find.byId(template.templateId);
				newTemplate.templateName = template.templateName;
				newTemplate.templateMessage = template.templateMessage;
				newTemplate.accessFlag = template.accessFlag;
				try {
					newTemplate.update();
					break;
				} catch (OptimisticLockException e2) {
					e2.printStackTrace();
					Logger.info("処理が競合しました。");
				}
			} while (true);
		}
	}

	public Member findMemberById(Long id){
		return Member.find.byId(id);
	}

	/**
	 *
	 * @param image
	 */
	public void saveImage(Image image) {
		try {
			image.save();
		} catch (OptimisticLockException e) {
			e.printStackTrace();
			Logger.info("処理が競合しました。");
			do {
				Image newImage = Image.find.byId(image.imageId);
				newImage.member = image.member;
				newImage.imageName = image.imageName;
				newImage.imageMessage = image.imageMessage;
				newImage.imageType = image.imageType;
				try {
					newImage.save();
					break;
				} catch (OptimisticLockException e2) {
					e2.printStackTrace();
					Logger.info("処理が競合しました。");
				}
			} while (true);
		}
	}

	public static List<String> extractClasses(String html) {
		Document document = Jsoup.parse(html);
		Elements elements = document.body().children().select("*");
		Map<String, Integer> classesMap = new HashMap<String, Integer>();
		for (Element e : elements) {
			String classValue = e.attr("class");
			Pattern p = Pattern.compile("[\\s]+");
			if(!classValue.trim().isEmpty()){
				for (String c : p.split(classValue)) {
					if (classesMap.containsKey(c)) {
						classesMap.put(c, classesMap.get(c) + 1);
					} else {
						classesMap.put(c, 1);
					}
				}
			}
		}
		List<Map.Entry> entries = new ArrayList<Map.Entry>(classesMap.entrySet());
		Collections.sort(entries, new Comparator() {
			public int compare(Object o1, Object o2) {
				Map.Entry e1 = (Map.Entry) o1;
				Map.Entry e2 = (Map.Entry) o2;
				return ((Integer) e2.getValue()).compareTo((Integer) e1.getValue());
			}
		});
		List<String> classes = new ArrayList<String>();
		for (Map.Entry entry : entries) {
			classes.add((String) entry.getKey());
		}
		return classes;
	}

	/**
	 * @param chooserId
	 * @return
	 */
	public Chooser findChooserByChooserId(Long chooserId) {
		return Chooser.find.byId(chooserId);
	}

	public String readHtmlFile(File file){
		StringBuilder contentBuilder = new StringBuilder();
		try {
		    BufferedReader in = new BufferedReader(new FileReader(file));
		    String str;
		    while ((str = in.readLine()) != null) {
		        contentBuilder.append(str);
		    }
		    in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}

	public Template getTemp(Long id){
		return Template.findById(id);
	}
	
	/** 
	 * @param imageId
	 * @return
	 */
	public Image findImageById(Long imageId) {
		return Image.find.byId(imageId);
	}
}