function Student(id, firstName, lastName, birthday) {
    var self = this;
    self.id = id;
    self.firstName = firstName;
    self.lastName = lastName;
    self.birthday = birthday;
}

function MainViewModel() {
    this.students = ko.observableArray(
        [new Student(1, "Andrzej", "Duda", "16-12-1992")]);

    this.addStudent = function () {
        this.students.push(new Student());
    }

    this.deleteStudent = function (student) {
        this.students.remove(student);
    }
}

ko.applyBindings(new MainViewModel());