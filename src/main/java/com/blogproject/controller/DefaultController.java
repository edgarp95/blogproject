package com.blogproject.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
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
	static final String HOME = "redirect:/";
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");

	@GetMapping("/")
	public ModelAndView home1(@AuthenticationPrincipal UserDetails currentUser) {
		ModelAndView homeView = new ModelAndView("/home");
		if (currentUser != null) {
			homeView.addObject("user", userService.findByUsername(currentUser.getUsername()));
		}

		homeView.addObject("posts", postrepository.findAllByOrderByLastUpdateTimeDesc());
		return homeView;
	}

	@GetMapping(value = "/createpost")
	public String openPostCreate(Post post) {

		return "/addpost";
	}

	@PostMapping(value = "/addPost")
	public String createPost(@Valid Post post, BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails currentUser) {

		if (bindingResult.hasErrors()) {
			return "/addpost";
		}

		User user = userService.findByUsername(currentUser.getUsername());
		post.setDate(LocalDateTime.now().format(formatter));
		post.setUserId(user.getId());
		post.setUserName(user.getUsername());
		post.setLastUpdateTime((new Date()));
		postrepository.save(post);

		return HOME;

	}

	@GetMapping(value = "post/{id}")
	public ModelAndView openPost(@PathVariable("id") Long id, @ModelAttribute("userForm") User userForm) {
		Post post = postrepository.findById(id);
		ModelAndView postview = new ModelAndView("/post");
		postview.addObject(post);
		return postview;
	}

	@GetMapping(value = "changePost/{id}")
	public ModelAndView changePostOpen(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails currentUser) {
		Post post = postrepository.findById(id);

		if (currentUser != null && currentUser.getUsername().equals(post.getUserName())) {

			ModelAndView postview = new ModelAndView("/changepost");
			postview.addObject(post);
			return postview;
		} else {
			return new ModelAndView("/error");
		}

	}

	@PostMapping(value = "changePost/{id}")
	public String changePost(@PathVariable("id") Long id, @Valid Post post, BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails currentUser) {

		Post originalPost = postrepository.findById(id);
		if (currentUser != null && currentUser.getUsername().equals(originalPost.getUserName())) {
			if (bindingResult.hasErrors()) {
				return "/changepost";
			}

			originalPost.setBody(post.getBody());
			originalPost.setTitle(post.getTitle());
			originalPost.setDate(LocalDateTime.now().format(formatter));
			originalPost.setLastUpdateTime((new Date()));
			postrepository.save(originalPost);
			// TODO: Success message!
			return HOME;
		} else {
			return "redirect:/error";
		}

	}

	@GetMapping(value = "deletePost/{id}")
	public String deletePost(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails currentUser) {
		Post post = postrepository.findById(id);

		if (currentUser == null || !currentUser.getUsername().equals(post.getUserName())) {
			return "redirect:/error";
		}

		else {
			postrepository.delete(post);

		}

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