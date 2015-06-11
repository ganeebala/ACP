var list;
$.ajax({
	type : "POST",
	url : "/SpringMVC/configuration",	
	success : function(response) {
		// we have the response
		
		list = response.records;
	},
	error : function(e) {
		alert('Error: ' + e);
	}
});

Array.prototype.unique = function() {
    var seen = {};
    return this.filter(function(item) {
        return seen.hasOwnProperty(item) ? false : (seen[item] = true);
    });
};

function iif(value, def) {
    return value ? value : def;
}

function populateSelect(select, options) {
    select
        .empty()
        .append(
            options.map(function(item) {
                return '<option value="' + item + '">' + item + '</option>';
            })
        );
    select.multiselect('rebuild');
}

function newExtraUser(scores) {
    var r = '<tr>';
    if (typeof scores === 'undefined') {
        for (var i = 0; i <= colCount; i++) {
            r += '<td><input type="text" /></td>';
        }
    }
    else {
        for (var i = 0; i <= colCount; i++) {
            r += '<td><input type="text" value="'+ scores[i] +'" /></td>';
        }
    }
    r += "</tr>";
    return r;
}

function getScoresFile(files) {
    var file = files[0];
    if (file) {
        if (window.FileReader) {
            var reader = new window.FileReader();
            reader.onload = function(e) {
                var content = e.target.result;
                var students = content.split('\r\n'); // String Split by CR LF
                console.log(students);
                var rows = '';
                for (var i = 1, len = students.length; i < len; i++) {
                    var student = students[i];
                    if (student) {
                        var scores = student.split(',');
                        if (scores.length - 1 !== colCount) {
                            alert('Uploaded data does not match the existing format.');
                            return;
                        }
                        rows += newExtraUser(scores);
                    }
                }
                $('#extraStudents').append(rows);
            };
            reader.readAsText(file);
        }
        else {
            alert('File Reader not supported in your browser.');
        }
    }
    else {
        alert('Failed to load file');
    }
    //files[0]
}

var colCount = 0;

$(function() {
    $('#universitySelect').multiselect({
        includeSelectAllOption: true,
        onChange: function() {
            var selectedUniversities = iif($('#universitySelect').val(),[]);
            var selectedUsernames = list
                .filter(function(item) {
                    return selectedUniversities.indexOf(item.univ) > -1;
                })
                .map(function(item) {
                    return item.user;
                })
                .unique();
            populateSelect($('#usernameSelect'), selectedUsernames);
        }
    });

    $('#usernameSelect').multiselect({
        includeSelectAllOption: true,
        onChange: function () {
            var selectedUniversities = iif($('#universitySelect').val(), []);
            var selectedUsernames = iif($('#usernameSelect').val(), []);
            var selectedProjects = list
                .filter(function(item) {
                    return selectedUniversities.indexOf(item.univ) > -1
                        && selectedUsernames.indexOf(item.user) > -1;
                })
                .map(function(item) {
                    return item.projectName;
                })
                .unique();
            populateSelect($('#projectSelect'), selectedProjects);
        }
    });

    $('#projectSelect').multiselect({
        includeSelectAllOption: true,
        onChange: function() {
            var selectedUniversities = iif($('#universitySelect').val(), []);
            var selectedUsernames = iif($('#usernameSelect').val(), []);
            var selectedProjects = iif($('#projectSelect').val(), []);

            var selectedRecords = list
                .filter(function(item) {
                    return selectedUniversities.indexOf(item.univ) > -1
                        && selectedUsernames.indexOf(item.user) > -1
                        && selectedProjects.indexOf(item.projectName) > -1;
                })
                .map(function(item) {
                    return item.user;
                })
                .unique();
            $('#filteredStudents')
                .empty()
                .append(
                    selectedRecords.map(function(item) {
                        var r = $('<tr><td>' + item + '</td></tr>');
                        for (var i = 0; i < colCount; i++) {
                            r.append('<td><input type="text" /></td>');
                        }
                        return r;
                    })
                );
        }
    });

    $('#addUser').click(function() {
        $('#extraStudents').append(newExtraUser());
    });

    $('#addColumn')
        .click(function() {
            colCount++;
            var $studentTable = $('#studentTable');
            $studentTable
                .children('thead,tbody')
                .children()
                .append('<td><input type="text" /></td>');
            $studentTable
                .children('tfoot')
                .children()
                .append('<td/>');
        })
        .click();

    var universities = list.map(function(item) {
        return item.univ;
    }).unique();

    populateSelect($('#universitySelect'), universities);
});

function getScores() {
    return $('#studentTable')
        .children('thead,tbody')
        .children('tr')
        .get()
        .map(function(tr) {
            return $(tr)
                .children('th,td')
                .get()
                .map(function(value, index) {
                    if (!index) {
                        var username = $(value).text();
                        if (username) {
                            return username;
                        }
                    }
                    return $(value).children().val();
                });
        });
}