package com.blogproject.controller;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.blogproject.auth.User;
import com.blogproject.comment.Comment;
import com.blogproject.post.Post;
import com.blogproject.repository.CommentRepository;
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
	private CommentRepository commentrepository;

	@Autowired
	private UserService userService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserValidator userValidator;
	static final String HOME = "redirect:/";
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");

	@GetMapping("/")
	public String home1(Model model, @RequestParam(defaultValue = "0") int page,
			@AuthenticationPrincipal UserDetails currentUser) {

		model.addAttribute("posts", postrepository.findAllByOrderByLastUpdateTimeDesc(new PageRequest(page, 5)));
		model.addAttribute("currentPage", page);
		model.addAttribute("classActiveHome", "active");
		if (currentUser != null) {
			model.addAttribute("user", userService.findByUsername(currentUser.getUsername()));
		}

		return "/home";
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(Model model, @RequestParam("title") String title,
			@AuthenticationPrincipal UserDetails currentUser) {
		log.info("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		model.addAttribute("posts", postrepository.findBytitle(title));
		if (currentUser != null) {
			model.addAttribute("user", userService.findByUsername(currentUser.getUsername()));
		}

		return "/search";
	}

	@GetMapping(value = "/createpost")
	public String openPostCreate(Model model, Post post) {
		model.addAttribute("classActiveAddPost", "active");
		return "/addpost";
	}

	@PostMapping(value = "/addPost")
	public String createPost(@Valid Post post, BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails currentUser, Model model) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("classActiveAddPost", "active");
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
	public ModelAndView openPost(@PathVariable("id") Long id, Comment comment, @AuthenticationPrincipal UserDetails currentUser) {
		Post post = postrepository.findById(id);
		List<Comment> comments = commentrepository.findByPostIdOrderByLastUpdateTimeDesc(post.getId());

		
		ModelAndView postview = new ModelAndView("/post");
		if (currentUser != null) {
			postview.addObject("user", userService.findByUsername(currentUser.getUsername()));
		}
		postview.addObject(post);
		postview.addObject("comments", comments);
		return postview;
	}
	
	@GetMapping(value = "deleteComment/{id}")
	public String deleteComment(@PathVariable("id") Long id,@RequestParam("commentId") Long commentId, @AuthenticationPrincipal UserDetails currentUser) {
		Comment comment = commentrepository.findByid(commentId);
		if (comment == null || currentUser == null  || !comment.getUserName().equals(currentUser.getUsername())) {
			return "/error";
		}
		commentrepository.delete(comment);
		return "redirect:/post/"+id;
	}

	@PostMapping(value = "addComment/{id}")
	public String addComment(@PathVariable("id") Long id, @Valid Comment comment, BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails currentUser, Model model, RedirectAttributes attributes) {

		Post post = postrepository.findById((long) id);
		if (post == null || currentUser == null) {
			return "/error";
		}
		model.addAttribute("user", userService.findByUsername(currentUser.getUsername()));
		
		
		List<Comment> comments = commentrepository.findByPostIdOrderByLastUpdateTimeDesc(post.getId());
		model.addAttribute("post", post);
		model.addAttribute("comments", comments);
		
		if (bindingResult.hasErrors()) {
			return "/post";
		}
		

		User user = userService.findByUsername(currentUser.getUsername());
		comment.setPostId(id);
		comment.setDate(LocalDateTime.now().format(formatter));
		comment.setUserName(user.getUsername());
		comment.setLastUpdateTime((new Date()));
		commentrepository.save(comment);
		
		attributes.addFlashAttribute("commentSuccess", "commentSuccess");
		return "redirect:/post/" + id;
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
		model.addAttribute("classActiveRegistration", "active");
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