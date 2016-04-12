package com.ling.springmvc.service;

import org.springframework.stereotype.Service;

import com.ling.springmvc.model.Course;

@Service
public interface CourseService {
	
	
	Course getCoursebyId(Integer courseId);
	

}
