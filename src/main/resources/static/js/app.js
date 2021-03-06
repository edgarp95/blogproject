function deletePostPre(id) {
	var a = document.getElementById("deletePostUrl");
	a.href = "/deletePost/" + id;
}
function deletePostPre1(id) {

	var a = document.getElementById("deletePostUrl1");
	a.href = "/deletePost/" + id;
}


function addProfileDetailsPre() {
	$('#aboutError').hide();
	$('#aboutError2').hide();
	$('#emailError').hide();
	$('#noinfoError').hide();
	$('#successProfileChange').hide();
	$('#successDelete').hide();
}

function addProfileDetails(index) {
	addProfileDetailsPre();
	
	var emailInput = document.getElementById("profileEmail").value
	var aboutInput = document.getElementById("profileAbout").value
	var email = "";
	var about = "";
	var errors = false;
	var regex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	if (aboutInput.trim().length > 0) {
		if (aboutInput.length > 1000) {
			$('#aboutError').show();
			errors = true;
		}
		else if (aboutInput.trim().length < 3) {
			$('#aboutError2').show();
			errors = true;

		}
		else about = aboutInput;
	}
	if (emailInput.trim().length > 0) {
		if (!regex.test(emailInput.toLowerCase())) {
			errors = true;
			$('#emailError').show();
		}
		else {
			email = emailInput.toLowerCase();
		}
	}
	
	if (!errors && (email != "" || about != ""))  {
		$.ajax({
			type : "POST",
			url : "/profile/" + index + "?about="+about+"&email="+email,
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			success : function(data) {
				$('#addProfileDetailsModal').modal('hide');
				if (data.about != null) {
					document.getElementById("aboutText").innerHTML = data.about;
				
				}
				if (data.email != null) {
					document.getElementById("aboutText").innerHTML = data.about;
					$("#emailUrl").attr("href", "mailto:"+data.email);
				}
				$('#successProfileChange').fadeTo(3000, 500).slideUp(500, function(){
		               $('#successProfileChange').slideUp(500);
				})
				

			}
		})
		
		
		
		console.log("Success!")
		
	}
	
	else if (aboutInput.trim().length == 0 && emailInput.trim().length == 0) {
		$('#noinfoError').show();
	}
	

}

function deletePost(id) {
	console.log(id);
	$.ajax({
		type : "POST",
		url : "/deletePost/" + id,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {
			if (data !== null) {
				$("#"+data).remove();

				$('#successDelete').fadeTo(3000, 500).slideUp(500, function(){
		               $('#successDelete').slideUp(500);
				})
			}
			
		
		}
	})
	
}

function upVote(id, username) {
	$.ajax({
		type : "POST",
		url : "/upVote/" + id+"?userName="+username,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {
			if (data !== null) {
				$("#rating"+data.id).text(data.rating)
				
				$('#successVote'+data.id).fadeTo(2000, 500).slideUp(500, function(){
		               $('#successVote'+data.id).slideUp(500);
				})
			}
			
		
		}
	})
}

function downVote(id, username) {
	$.ajax({
		type : "POST",
		url : "/downVote/" + id+"?userName="+username,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {
			if (data !== null) {
				$("#rating"+data.id).text(data.rating)
				$('#successVote'+data.id).fadeTo(2000, 500).slideUp(500, function(){
		               $('#successVote'+data.id).slideUp(500);
				})
			}
			
		
		}
	})
}

