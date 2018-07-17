package com.blogproject.post;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name ="post")
public class Post {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "userId")
	private Long userId;
	
	@Column(name = "userName")
	private String userName;
	
	@Column(name = "title")
	@NotNull
	@NotBlank
	private String title;
	
	@Column(name = "body", columnDefinition="text", length=10485760)
	@NotNull
	@NotBlank
	private String body;
	
	
	@Column(name = "date")
	private String date;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Post() {
		super();
	}
	public Post(Long id, Long userId, String title, String body, String date, String userName) {
		super();
		this.id = id;
		this.userId = userId;
		this.title = title;
		this.body = body;
		this.date = date;
		this.userName = userName;
	}
	
	
	
	

}
