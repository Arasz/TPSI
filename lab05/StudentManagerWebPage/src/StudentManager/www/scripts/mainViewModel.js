﻿"use strict;"

function remoteObservableCollection(baseUrl, collectionUrl) {
    var self = this;

    self.observableArray = ko.observableArray();

    self.url = baseUrl + collectionUrl;

    self.add = function (item) {
        $.ajax(self.url, {
            data: ko.toJSON(item),
            type: "post", contentType: "application/json",
            success: function (result) {
                console.log(result);
                self.observableArray.push(item);
            }
        });
    }

    self.getFromRemote = function () {
        $.getJSON(self.url, function (data) {
            console.log(data);
            var mapped = $.map(data, function (item) {
                return ko.mapping.fromJS(item);
            });
            self.observableArray(mapped);
            console.log(mapped);
        });
    }

    self.length = ko.computed(function () {
        return self.observableArray().length;
    });
}

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

    self.goToStudents = function (students, event) {
        location.hash = "students";
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
        location.hash = "marks";
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

    self.remoteSubjects = new remoteObservableCollection(baseAddress, "subjects");

    self.subjects = remoteSubjects.observableArray;

    self.subject = {
        name: ko.observable(),
        teacher: ko.observable(),
    }

    //Behavior

    self.goToSubjects = function () {
        location.hash = "subjects";
        return true;
    }

    self.addSubject = function () {
        var data = ko.mapping.toJS(self.subject);
        self.remoteSubjects.add(data);
        self.subject.name("");
        self.subject.teacher("");
    }

    self.deleteSubject = function (Subject) {
        self.subjects.remove(Subject);
    }

    self.getSubjects = function () {
        remoteSubjects.getFromRemote();
    }

    // Client-side routes
    Sammy(function () {
        this.get('#:name', function () {
            self.chosenTableHash(this.params.name);
            var name = "get" + this.params.name[0].toUpperCase() + this.params.name.slice(1);
            self[name].call(self);
        });

        this.get('', function () { this.app.runRoute('get', '#students') });
    }).run();
};

var viewModel = new mainViewModel();

$(document).ready(function () {
    ko.applyBindings(viewModel);
});