function Student(id, firstName, lastName, birthday) {
    var self = this;
    self.id = id;
    self.firstName = firstName;
    self.lastName = lastName;
    self.birthday = birthday;
}

function Mark(studentId, value, submitTime) {
    var self = this;
    self.studentId = studentId;
    self.value = value;
    self.submitDate = submitTime;
}

function Subject(subjectName, teacher) {
    this.self = this;
    self.subjectName = subjectName;
    self.teacher = teacher;
}

window.onload = function () {
    function mainViewModel() {
        this.self = this;

        self.searchId = ko.observable();
        self.searchName = ko.observable();
        self.searchLastName = ko.observable();
        self.searchBirthday = ko.observable();

        self.lastIndex = function () {
            var underlyingArray = students();
            var length = underlyingArray.length;
            if (length === 0) return 0;
            return underlyingArray[length - 1].id;
        };
        // Students

        self.students = ko.observableArray(
        [
            new Student(1, "Andrzej", "Duda", "1992-12-13"),
            new Student(2, "Bartłomiej", "Duda", "1992-12-13")
        ]);

        self.addStudent = function () {
            self.students.push(new Student(self.lastIndex() + 1, "", "", ""));
        }

        self.deleteStudent = function (Student) {
            self.students.remove(Student);
        }

        //Marks

        self.searchMark = ko.observable();
        self.searchDate = ko.observable();

        self.marks = ko.observableArray(
        [
            new Mark(1, 4, "2002-03-12"),
            new Mark(2, 2, "2002-03-12"),
        ]);

        self.addMark = function () {
            self.marks.push(new Mark(0, "", ""));
        }

        self.deleteMark = function (Mark) {
            self.marks.remove(Mark);
        }

        //Subjects

        self.searchSubject = ko.observable();
        self.searchTeacher = ko.observable();

        self.subjects = ko.observableArray(
        [
            new Subject("po", "Peseł"),
            new Subject("WF", "Andrzej Duda"),
            new Subject("Dogelogy", "Peseł")
        ]);

        self.addSubject = function () {
            self.subjects.push(new Subject("", ""));
        }

        self.deleteSubject = function (Subject) {
            self.subjects.remove(Subject);
        }
    };

    ko.applyBindings(new mainViewModel());
}