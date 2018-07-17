package com.blogproject.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.blogproject.auth.User;
import com.blogproject.post.Post;
import com.blogproject.repository.PostRepository;
import com.blogproject.service.SecurityService;
import com.blogproject.service.UserService;
import com.blogproject.validator.UserValidator;

@Controller
public class DefaultController {
	private static Logger log = LoggerFactory.getLogger(DefaultController.class);

	@Autowired
	private PostRepository postrepository;
	
	
	 @Autowired
	 private UserService userService;

	 @Autowired
	 private SecurityService securityService;

	 @Autowired
	 private UserValidator userValidator;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");

	@GetMapping("/")
	public ModelAndView home1() {
		log.info("OLEN!");
		ModelAndView homeView = new ModelAndView("/home");
		homeView.addObject("posts", postrepository.findAllByOrderByDateDesc());
		return homeView;
	}
	
	
	@GetMapping(value = "/createpost")
	public ModelAndView openPostCreate() {
		ModelAndView postCreateView = new ModelAndView("/addpost");
		postCreateView.addObject("post", new Post());
		
		return postCreateView;
	}
	
	@PostMapping(value = "/addPost")
	public String createPost(@Valid Post post, BindingResult bindingResult)  {
		User user = userService.findById((long) 1);
		post.setDate(LocalDateTime.now().format(formatter));
		post.setUserId(user.getId());
		post.setUserName(user.getUsername());
		log.debug(post.getBody());
		log.debug(post.getTitle());
		//TODO: Check bindingresult 
		postrepository.save(post);
		
		return "redirect:/";
		
	}




	
	  @RequestMapping(value = "/registration", method = RequestMethod.GET)
	    public String registration(Model model) {
	        model.addAttribute("userForm", new User());

	        return "registration";
	    }

	    @RequestMapping(value = "/registration", method = RequestMethod.POST)
	    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
	        userValidator.validate(userForm, bindingResult);

	        if (bindingResult.hasErrors()) {
	            return "registration";
	        }

	        userService.save(userForm);

	        securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

	        return "redirect:/";
	    }

	    @RequestMapping(value = "/login", method = RequestMethod.GET)
	    public String login(Model model, String error, String logout) {
	       

	      

	        return "login";
	    }

}