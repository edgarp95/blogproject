function deletePostPre(id) {
	var a = document.getElementById("deletePostUrl");
	a.href = "/deletePost/" + id;
}


function addProfileDetailsPre() {
	$('#aboutError').hide();
	$('#aboutError2').hide();
	$('#emailError').hide();
	$('#noinfoError').hide();
}

function addProfileDetails(index) {
	$('#aboutError').hide();
	$('#aboutError2').hide();
	$('#emailError').hide();
	$('#noinfoError').hide();
	
	
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

			}
		})
		
		
		
		console.log("Success!")
		
	}
	
	else if (aboutInput.trim().length == 0 && emailInput.trim().length == 0) {
		$('#noinfoError').show();
	}
	

}


