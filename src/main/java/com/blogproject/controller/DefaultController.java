package com.blogproject.controller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.blogproject.auth.User;
import com.blogproject.comment.Comment;
import com.blogproject.config.RandomString;
import com.blogproject.post.Post;
import com.blogproject.repository.CommentRepository;
import com.blogproject.repository.PostRepository;
import com.blogproject.repository.UserRepository;
import com.blogproject.service.SecurityService;
import com.blogproject.service.UserService;
import com.blogproject.validator.UserValidator;

@Controller
public class DefaultController {
	@SuppressWarnings(value = { "unused" })
	private static Logger log = LoggerFactory.getLogger(DefaultController.class);

	@Autowired
	private PostRepository postrepository;

	@Autowired
	private CommentRepository commentrepository;

	@Autowired
	private UserService userService;

	@Autowired 
	private UserRepository userrepository;
	
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
			model.addAttribute("loggedUser", userService.findByUsername(currentUser.getUsername()));
		}

		return "/home";
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(Model model, @RequestParam("title") String title,
			@AuthenticationPrincipal UserDetails currentUser) {
		model.addAttribute("posts", postrepository.findBytitle(title));
		if (currentUser != null) {
			model.addAttribute("loggedUser", userService.findByUsername(currentUser.getUsername()));
		}

		return "/search";
	}

	@GetMapping(value = "/createpost")
	public String openPostCreate(Model model, Post post, @AuthenticationPrincipal UserDetails currentUser) {
		model.addAttribute("classActiveAddPost", "active");
		if (currentUser != null) {
			model.addAttribute("loggedUser", userService.findByUsername(currentUser.getUsername()));
		}
		return "/addpost";
	}

	@PostMapping(value = "/addPost")
	public String createPost(@Valid Post post, BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails currentUser, Model model,
			@RequestParam("file") MultipartFile multipartFile) throws IOException {

		if (bindingResult.hasErrors()) {
			model.addAttribute("classActiveAddPost", "active");
			return "/addpost";
		}

		// cover image
		if (!multipartFile.isEmpty()) {
			log.info("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEIIIIIIIIIIIII");
			post.setPath("/images/"+createFile(multipartFile, "src/main/resources/static/images/" ));
		} else {
			post.setPath("/images/no_image.jpg");
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
	public ModelAndView openPost(@PathVariable("id") Long id, Comment comment,
			@AuthenticationPrincipal UserDetails currentUser) {
		Post post = postrepository.findById(id);
		List<Comment> comments = commentrepository.findByPostIdOrderByLastUpdateTimeDesc(post.getId());

		ModelAndView postview = new ModelAndView("/post");
		if (currentUser != null) {
			postview.addObject("loggedUser", userService.findByUsername(currentUser.getUsername()));
		}
		postview.addObject(post);
		postview.addObject("comments", comments);
		return postview;
	}

	@GetMapping(value = "deleteComment/{id}")
	public String deleteComment(@PathVariable("id") Long id, @RequestParam("commentId") Long commentId,
			@AuthenticationPrincipal UserDetails currentUser) {
		Comment comment = commentrepository.findByid(commentId);
		if (comment == null || currentUser == null || !comment.getUserName().equals(currentUser.getUsername())) {
			return "/error";
		}
		commentrepository.delete(comment);
		return "redirect:/post/" + id;
	}

	@PostMapping(value = "addComment/{id}")
	public String addComment(@PathVariable("id") Long id, @Valid Comment comment, BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails currentUser, Model model, RedirectAttributes attributes) {

		Post post = postrepository.findById((long) id);
		if (post == null || currentUser == null) {
			return "/error";
		}
		model.addAttribute("loggedUser", userService.findByUsername(currentUser.getUsername()));

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
			postview.addObject("loggedUser", userService.findByUsername(currentUser.getUsername()));

			return postview;
		} else {
			return new ModelAndView("/error");
		}

	}

	@PostMapping(value = "changePost/{id}")
	public String changePost(@PathVariable("id") Long id, @Valid Post post, BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails currentUser, @RequestParam("file") MultipartFile multipartFile,
			RedirectAttributes attributes)
			throws IOException {

		Post originalPost = postrepository.findById(id);
		if (currentUser != null && currentUser.getUsername().equals(originalPost.getUserName())) {
			if (bindingResult.hasErrors()) {
				return "/changepost";
			}

			if (!multipartFile.isEmpty()) {
				deleteFile(originalPost.getPath());
				originalPost.setPath("/images/"+createFile(multipartFile, "src/main/resources/static/images/" ));
			}

			originalPost.setBody(post.getBody());
			originalPost.setTitle(post.getTitle());
			originalPost.setDate(LocalDateTime.now().format(formatter));
			originalPost.setLastUpdateTime((new Date()));
			postrepository.save(originalPost);
			attributes.addFlashAttribute("successChange", "successChange");
			
			return HOME;
		} else {
			return "redirect:/error";
		}

	}

	@GetMapping(value = "deletePost/{id}")
	public String deletePost(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails currentUser) {
		Post post = postrepository.findById(id);

		if (currentUser == null || !currentUser.getUsername().equals(post.getUserName())) {
			return "/error";
		}

		else {
			deleteFile(post.getPath());
			postrepository.delete(post);

		}

		return HOME;
	}
	
	@RequestMapping(value = "deletePost/{id}", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Long deletePost2(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails currentUser) {
		Post post = postrepository.findById(id);

		if (currentUser == null || !currentUser.getUsername().equals(post.getUserName())) {
			return null;
		}

		else {
			deleteFile(post.getPath());
			postrepository.delete(post);

		}

		return id;
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String registration(Model model,  @AuthenticationPrincipal UserDetails currentUser) {
		model.addAttribute("userForm", new User());
		model.addAttribute("classActiveRegistration", "active");
		if (currentUser != null) model.addAttribute("loggedUser", userService.findByUsername(currentUser.getUsername()));
		return "registration";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
		userValidator.validate(userForm, bindingResult);

		if (bindingResult.hasErrors()) {
			return "registration";
		}
		userForm.setRegistration((LocalDateTime.now().format(formatter)));
		userForm.setAvatar("/images/users/default_user.jpg");
		userService.save(userForm);

		securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

		return HOME;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, String error, String logout, @AuthenticationPrincipal UserDetails currentUser) {
		if (currentUser != null) model.addAttribute("loggedUser", userService.findByUsername(currentUser.getUsername()));
		model.addAttribute("classActiveLogin", "active");
		
		return "login";
	}
	
	@RequestMapping(value = "/profile/{username}", method = RequestMethod.GET)
	public String openProfile(@PathVariable("username") String userName, Model model, @AuthenticationPrincipal UserDetails currentUser) {
		User user = userService.findByUsername(userName);
		if (user == null) {
			return "/error";
		}
		List<Post> posts = postrepository.findByUserNameOrderByLastUpdateTimeDesc(userName);
		if (currentUser != null) model.addAttribute("loggedUser", userService.findByUsername(currentUser.getUsername()));
		model.addAttribute("user", user);
		model.addAttribute("posts", posts);		
		model.addAttribute("classActiveProfile", "active");
		return "/profile";
	}
	
	@RequestMapping(value = "/profile/{username}", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody User changeProfile(@PathVariable("username") String userName, @RequestParam("about") String about, @RequestParam("email") String email, Model model, @AuthenticationPrincipal UserDetails currentUser) {
		User user = userService.findByUsername(userName);
		if (user == null || currentUser == null || !user.getUsername().equals(currentUser.getUsername())) {
			return null;
		}
		if (!about.isEmpty()) {
			user.setAbout(about);
		}
		
		
		if (!email.isEmpty()) {
			user.setEmail(email);
		}
		userService.addDetails(user);
		
		return user;
	}
	
	@RequestMapping(value = "/profileavatar/{username}", method = RequestMethod.POST)
	public  String changeAvatar(@PathVariable("username") String userName,  @AuthenticationPrincipal UserDetails currentUser, @RequestParam("file") MultipartFile multipartFile,
			RedirectAttributes attributes) throws IOException {
		User user = userService.findByUsername(userName);
		if (user == null || currentUser == null || !user.getUsername().equals(currentUser.getUsername()) || multipartFile.isEmpty()) {
			return "/error";
		}
		String originalAvatarPath = user.getAvatar();
		user.setAvatar("/images/users/"+createFile(multipartFile, "src/main/resources/static/images/users/"));
		userService.addDetails(user);
		deleteFile(originalAvatarPath);
		attributes.addFlashAttribute("successAvatar", "successAvatar");
		return "redirect:/profile/"+userName;
	}
	
	
	
	

	public String createFile(MultipartFile multipartFile, String source) throws IOException {
		File convFile;
		String path = "";
		RandomString random = new RandomString();
		while (true) {
			path = random.nextString() + ".jpg";
			convFile = new File(source + path);
			if (!convFile.exists())
				break;
		}

		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(multipartFile.getBytes());
		fos.close();

		return  path;
	}

	public void deleteFile(String path) {
		// standart image, don't delete it!
		if (!path.equals("/images/no_image.jpg") && !path.equals("/images/users/default_user.jpg")) {
			File f = new File("src/main/resources/static" + path);
			if (f.exists())
				f.delete();

		}

	}

}