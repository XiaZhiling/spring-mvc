package com.ling.springmvc.controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ling.springmvc.model.Course;
import com.ling.springmvc.service.CourseService;

@Controller
@RequestMapping("/courses")
public class CourseController {
	
	private static Logger log = LoggerFactory.getLogger(CourseController.class);
	
	private CourseService courseService;
	
	@Autowired
	public void setCourseService(CourseService courseService) {
		this.courseService = courseService;
	}
	
	//本方法将处理 /courses/view?courseID=123 形式的URL
	@RequestMapping(value = "/view",method = RequestMethod.GET)
	public String viewCourse(@RequestParam("courseID") Integer courseID,Model model) {
		log.debug("In viewCourse,courseID = {}",courseID);
		Course course = courseService.getCoursebyId(courseID);
		model.addAttribute(course);
		return "course_overview";
	}
	
	// //--RESFULF 风格本方法将处理 /courses/view2/345形式的URL
	@RequestMapping(value = "/view2/{courseID}",method = RequestMethod.GET)
	public String viewCourse2(@PathVariable("courseID") Integer courseID,Map<String, Object> map) {
		log.debug("In viewCourse2,courseID = {}",courseID);
		Course course = courseService.getCoursebyId(courseID);
		map.put("course", course);
		return "course_overview";
	}
	
	//servletAPI
	@RequestMapping("/view3")
	public String viewCourse3(HttpServletRequest request) {
		Integer courseID = Integer.valueOf(request.getParameter("courseID"));
		log.debug("In viewCourse3,courseID = {}",courseID);
		Course course = courseService.getCoursebyId(courseID);
		request.setAttribute("course", course);
		return "course_overview";
	}
	
	
	@RequestMapping(value ="/admin",method = RequestMethod.GET,params = {"add"})
	public String createCourse() {
		
		return "course_admin/edit";
	}
	
	@RequestMapping(value ="/save",method = RequestMethod.POST)
	public String doSave(Course course) {//也可以通过@ModelAttribute 声明页面和model的绑定
		//doSave(@ModelAttribute Course course)
		log.debug("Info of Course:");
		log.debug(ReflectionToStringBuilder.toString(course));//commons-lang
		//在此进行业务操作，比如数据库持久化
		course.setCourseId(123);
		return "redirect:view2/"+course.getCourseId();
	}
	
	@RequestMapping(value = "/upload",method = RequestMethod.GET)
	public String showUploadPage(@RequestParam(value = "multi",required = false)Boolean multi ){
		if(multi !=null&&multi){
			return "course_admin/multifile";
		}
		return "course_admin/file";
	}
	
	@RequestMapping(value = "/doUpload",method = RequestMethod.POST)
	public String doUploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		if(!file.isEmpty()){
			log.debug("Process file :{}",file.getOriginalFilename());
			FileUtils.copyInputStreamToFile(file.getInputStream(), new File("D:\\temp\\",System.currentTimeMillis()+ file.getOriginalFilename()));
		}
		return "success";
	}
	
	@RequestMapping(value="/doUpload2", method=RequestMethod.POST)
	public String doUploadFile2(MultipartHttpServletRequest multiRequest) throws IOException{
		
		Iterator<String> filesNames = multiRequest.getFileNames();
		while(filesNames.hasNext()){
			String fileName =filesNames.next();
			MultipartFile file =  multiRequest.getFile(fileName);
			if(!file.isEmpty()){
				log.debug("Process file: {}", file.getOriginalFilename());
				FileUtils.copyInputStreamToFile(file.getInputStream(), new File("D:\\temp\\", System.currentTimeMillis()+ file.getOriginalFilename()));
			}
		}
		return "success";
	}
	
	@RequestMapping(value="/{courseId}",method=RequestMethod.GET)
	public @ResponseBody Course getCourseInJson(@PathVariable Integer courseId){
		return  courseService.getCoursebyId(courseId);
	}
	
	
	@RequestMapping(value="/jsontype/{courseId}",method=RequestMethod.GET)
	public  ResponseEntity<Course> getCourseInJson2(@PathVariable Integer courseId){
		Course course =   courseService.getCoursebyId(courseId);		
		return new ResponseEntity<Course>(course, HttpStatus.OK);
	}


}
