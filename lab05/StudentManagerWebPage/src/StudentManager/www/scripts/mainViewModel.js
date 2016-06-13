﻿"use strict;"

window.onload = function () {
    function mainViewModel() {
        this.self = this;

        self.baseAddress = "http://localhost:60732/api/";

        self.collectionMapLogic = function (data, collection) {
            console.log(data);
            var mapped = $.map(data, function (item) { return ko.mapping.fromJS(item); });
            collection(mapped);
            console.log(mapped);
        }

        self.chosenTableHash = ko.observable();

        // Students

        //Data

        self.searchId = ko.observable();
        self.searchName = ko.observable();
        self.searchLastName = ko.observable();
        self.searchBirthday = ko.observable();

        self.students = ko.observableArray([]);

        //Behavior

        self.goToStudents = function (students) {
            console.log(students);
            location.hash = students;
            return true;
        }

        self.addStudent = function () {
            self.students.push(new Student(self.lastIndex() + 1, "", "", ""));
        }

        self.deleteStudent = function (Student) {
            self.students.remove(Student);
        }

        self.lastIndex = function () {
            var underlyingArray = students();
            var length = underlyingArray.length;
            if (length === 0) return 0;
            return underlyingArray[length - 1].id;
        };

        self.getStudents = function () {
            $.getJSON(baseAddress + "students", function (allData) {
                self.collectionMapLogic(allData, self.students);
            });
        }

        //Marks

        //Data

        self.searchMark = ko.observable();
        self.searchDate = ko.observable();

        self.marks = ko.observableArray([]);

        //Behavior
        self.goToMarks = function (marks) {
            location.hash = marks;
            return true;
        }

        self.addMark = function () {
            self.marks.push(new Mark(0, "", ""));
        }

        self.deleteMark = function (Mark) {
            self.marks.remove(Mark);
        }
        self.getMarks = function () {
            $.getJSON(baseAddress + "marks", function (allData) {
                self.collectionMapLogic(allData, self.marks);
            });
        }

        //Subjects

        //Data

        self.searchSubject = ko.observable();
        self.searchTeacher = ko.observable();

        self.subjects = ko.observableArray([]);

        //Behavior

        self.goToSubjects = function (subjects) {
            location.hash = subjects;
            return true;
        }

        self.addSubject = function () {
            self.subjects.push(new Subject("", ""));
        }

        self.deleteSubject = function (Subject) {
            self.subjects.remove(Subject);
        }

        self.getSubjects = function () {
            $.getJSON(baseAddress + "subjects", function (allData) {
                self.collectionMapLogic(allData, self.subjects);
            });
        }

        // Client-side routes
        Sammy(function () {
            this.get('#:students', function () {
                self.chosenTableHash(this.params.students);
                self.getStudents();
            });

            this.get('#:marks', function () {
                self.chosenTableHash(this.params.marks);
                self.getMarks();
            });

            this.get('#:subjects', function () {
                self.chosenTableHash(this.params.subjects);
                self.getSubjects();
            });

            this.get('', function () { this.app.runRoute('get', '#students') });
        }).run();
    };

    ko.applyBindings(new mainViewModel());
}