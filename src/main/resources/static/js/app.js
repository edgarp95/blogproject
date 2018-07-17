function deletePerson(index) {
	var a = document.getElementById("deletepersonUrl");
	a.href = "/deleteperson/" + index
}

function deleteHousework(index) {
	var a = document.getElementById("deletehouseworkUrl");
	a.href = "/deletehousework/" + index
}

function submitHouseworkPersonPre(index) {
	$('#addPersonHouseworkAlert').hide();
	document.getElementById("personAddHouseworkId").value = index;
}

function submitHouseworkPerson() {
	$('#addHoursAlert').hide()
	$('#personAddPlayinghoursSuccess').hide()
	$('#addHoursAlert').hide()
	$('#addHoursAlert3').hide()
	$('#personhouseworkSuccess').hide();
	$('#addPersonHouseworkAlert').hide();

	var values = $('#multipleSelect').val();
	var id = $('#personAddHouseworkId').val();
	console.log(values)
	if (values.length > 0) { 
		console.log(id)
		console.log(values)
		var url = ""; // the script where you handle the form input.
		$.ajax({
			type : "POST",
			url : "/personAddHousework/"+id,
			data : {
				myArray : values
			},
			dataType : "json", 
			success : function(data) {
				if (data === null) {
					console.log("404 method returned null")
				}
				else {
					console.log(data)
					var cell = document.getElementById("person"
											+ id);
					cell.innerHTML = data.points;
					$('#personAddHousework').modal('hide');
					$('#personhouseworkSuccess').show();
			
				}
				
			}

		});
	}
	else {
		$('#addPersonHouseworkAlert').show()
		console.log("Nothing chosen!")
	}


	

};

function addPlayingHoursPre(index) {
	$('#addHoursAlert').hide()
	$('#personAddPlayinghoursSuccess').hide()
	$('#addHoursAlert').hide()
	$('#addHoursAlert3').hide()
	$('#personhouseworkSuccess').hide();
	$('#addPersonHouseworkAlert').hide();
	document.getElementById("personaddPlayinghours").value = index;
}

function addPlayingHours() {
	var input = document.getElementById("addPlayinghoursInput").value
	if (!isNaN(input) && input !== '' && input > 0) {
		var id = document.getElementById("personaddPlayinghours").value;
		var personPoints;
		$.ajax({
			type : "GET",
			url : "/getperson/" + id,
			data : JSON.stringify(input),
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			success : function(data) {
				console.log("Info peab siin olema!")
				if (data === null) {
					console.log("Error 404!")
					$('#addHoursAlert2').show()
				}

				else {
					console.log(data)
					personPoints = data.points;
					if (input > personPoints) {
						console.log("Error too many points added!")
						$('#addHoursAlert').show()
					} else {
						$.ajax({
							type : "POST",
							url : "/reducepersonhousework/" + id,
							data : JSON.stringify(input),
							contentType : "application/json; charset=utf-8",
							dataType : "json",
							success : function(data) {
								$('#addPlayinghours').modal('hide')
								var cell = document.getElementById("person"
										+ id);
								cell.innerHTML = data.points;
								$('#personAddPlayinghoursSuccess').show();
							}

						});

					}

				}
			},

		});

	// empty input!	
	} else {
		$('#addHoursAlert3').show()

	}
}

function changeHouseworkPre(id) {
	$('#changeHouseworkSuccess').hide();
	$('#changeHouseworkError').hide();
	document.getElementById("houseworkpointsId").value = id;
}

function changeHousework() {
	var input = document.getElementById("houseworkpointsInput").value
	if (!isNaN(input) && input > 0) {
		var id = document.getElementById("houseworkpointsId").value;
		$.ajax({
			type : "POST",
			url : "/changehousework/" + id,
			data : JSON.stringify(input),
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			success : function(data) {
				console.log(data);
				$('#changeHousework').modal('hide')
				var cell = document.getElementById("housework"
						+ id);
				cell.innerHTML = data.points;
				$('#changeHouseworkSuccess').show();
			}

		});
		
	}
	else {
		$('#changeHouseworkError').show();
	}
	
}

function personHistory(index) {
	$.ajax({
		type : "GET",
		url : "/history/" + index,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {
			var element = document.getElementById("historyBody");
			var innerElement ="";
			var tableBody =$("#historyTable").find('tbody')
			var tableText = $("#historyTableText")
			tableBody.empty();
			tableText.empty();
			if (!$.trim(data)) {
				
				tableText.append("<b>Antud kasutajal pole veel ühtegi tegevust!</b>");
				
			}
			else {
				tableText.append("<b>Kasutaja tegevused: </b>");
				
				$.each(data, function(k,v) {
		
					var dataPiece = data[k];
					
					
					tableBody.append("<tr><td>"+dataPiece.id+"</td><td>"+dataPiece.type+"</td><td>"+dataPiece.name+"</td><td>"+dataPiece.points+"</td><td>"+dataPiece.date+"</td></tr>");
					
					
				});
			}
			

		}

	});
}
