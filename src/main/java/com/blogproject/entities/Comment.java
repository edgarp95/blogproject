package com.blogproject.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "comment")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "userName")
	private String userName;

	@Column(name = "body", columnDefinition = "text", length = 10485760)
	@NotEmpty
	@NotBlank
	private String body;

	@Column(name = "date")
	private String date;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATE_TIME")
	private Date lastUpdateTime;

	@Column(name = "postId")
	private Long postId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public Comment(Long id, String userName, String body, String date, Date lastUpdateTime, Long postId) {
		super();
		this.id = id;
		this.userName = userName;
		this.body = body;
		this.date = date;
		this.lastUpdateTime = lastUpdateTime;
		this.postId = postId;
	}

	public Comment() {
		super();
		// TODO Auto-generated constructor stub
	}

}
