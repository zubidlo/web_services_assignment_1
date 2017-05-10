/* global toastr, ko */

//toastr.js setting
toastr.options = {
  "closeButton": true,
  "debug": false,
  "newestOnTop": false,
  "progressBar": true,
  "positionClass": "toast-top-right",
  "preventDuplicates": false,
  "onclick": null,
  "showDuration": "300",
  "hideDuration": "300",
  "timeOut": "2000",
  "extendedTimeOut": "1000",
  "showEasing": "swing",
  "hideEasing": "linear",
  "showMethod": "fadeIn",
  "hideMethod": "fadeOut"
};

genres = ["space opera", "military sci-fi", "hard sci-fi", "social sci-fi", "dystopia", "cyberpunk"];

//Book constructor
var Book = function (data) {
    var self = this;

    if (!data) {
        data = {
            id: 0,
            author: "",
            title: "",
            genre: "space opera",
            year: 0,
            description: "",
            price: 0
        };
    }

    self.id = ko.observable(data.id);
    self.author = ko.observable(data.author);
    self.title = ko.observable(data.title);
    self.genre = ko.observable(data.genre);
    self.year = ko.observable(data.year);
    self.description = ko.observable(data.description);
    self.price = ko.observable(data.price);
};

//navbar model
var navbarModel = (function () {
    var self = this;
    self.chooseByGenre = ko.observable(genres[0]);
    self.searchString = ko.observable("");
    
    return {
        chooseByGenre: self.chooseByGenre,
        searchString: self.searchString
    };
})();

//modal model
var modalModel = (function () {
    var self = this;
    self.modalTitle = ko.observable();
    self.currentBook = ko.observable(new Book());
    self.savedBook = new Book();
    
    self.isSavedBookSameAsCurrentBook = function () {
        var a = ko.toJSON(currentBook());
        var b = ko.toJSON(savedBook);
        return (a === b) ? true : false;
    };
    
    //show the modal with appropriate title, book and submit button shown and hidden
    self.showBookModal = function (title, book, $showButton, $hideButton) {
        self.modalTitle(title);
        self.savedBook = ko.toJS(book);
        self.currentBook(new Book(savedBook));
        $($showButton).show();
        $($hideButton).hide();
        $('#bookModal').modal('show');
    };

    //hide the modal
    self.hideBookModal = function () {
        $('#bookModal').modal('hide');
        self.currentBook(new Book());
    };
    
    return {
        currentBook: self.currentBook,
        showBookModal: self.showBookModal,
        hideBookModal: self.hideBookModal,
        isSavedBookSameAsCurrentBook: self.isSavedBookSameAsCurrentBook
    };
})();

//knockout booksModel singleton
var booksModel = (function () {
  
    var self = this;
    self.path = "webapi/books";
    self.bookList = ko.observableArray([]);
    
    self.ajax = function (url, method, data) {
        
        toastr.info("[" + method.toUpperCase() + "] " + url, "REQUEST");
        return $.ajax(url, {
            method: method,
            data: data,
            contentType: "application/json",
            error: function (jqXRR, textStatus, errorThrown) {
                toastr.error("Error: " + errorThrown, "RESPONSE");
            }
        });
    };
    
    //GET webapi/books :all the books
    //GET webapi/books?genre=space opera :all the books in space opera sub-genre
    //GET webapi/books?start=0&size=3 :first 3 books only
    //GET webapi/books?search=universe :search for universe word in every field except genre
    self.getAllBooks = function (query) {
        
        var path = self.path;
        if (query) {
            path += query; 
        }
        self.ajax(path, "get").done(function (response) {
            var mappedBooks = $.map(response, function (item) { return new Book(item); });
            self.bookList(mappedBooks);
            toastr.success((mappedBooks.length).toString() + " found", "RESPONSE");
        });
    };
    
    //initialization of bookList observable array
    self.getAllBooks();
    
    //POST webapi/books with new book in body :add new book to book list
    self.addCurrentBook = function () {
        
        self.ajax(self.path, "post", ko.toJSON(modalModel.currentBook)).done(function (response) {
            toastr.success(self.path + "/" + response.id + " created", "RESPONSE");
            self.bookList.push(new Book(response));
            modalModel.hideBookModal();
        });
    };

    //PUT webapi/books/1 with edited book in body :update book with id=1 in book list
    self.editCurrentBook = function () {
        
        var path = self.path + "/" + modalModel.currentBook().id();
        self.ajax(path, "put", ko.toJSON(modalModel.currentBook)).done(function (response) {
            toastr.success(response.title + " updated", "RESPONSE");
            var matchingBook = ko.utils.arrayFirst(self.bookList(), function (book) {
                return response.id === book.id();
            });          
            self.bookList.replace(matchingBook, new Book(response));
            modalModel.hideBookModal();
        });
    };

    //DELETE webapi/books/1 :remove book with id=1 from book list
    self.deleteBook = function (book) {
        
        var path = self.path + "/" + book.id();
        self.ajax(path, "delete").done(function (response) {
            toastr.success(book.title() + " removed", "RESPONSE");
            self.bookList.remove(book);
        });
    };
    
    //resets books to init state
    self.resetBooks = function () {
        self.ajax(self.path + "/reset", "get").done(function (response) {
            var mappedBooks = $.map(response, function (item) { return new Book(item); });
            self.bookList(mappedBooks);
            toastr.success((mappedBooks.length).toString() + " books", "RESPONSE");
        });
            
    };
    
    //public properties
    return {
        bookList: self.bookList,
        getAllBooks: self.getAllBooks,
        addCurrentBook: self.addCurrentBook,
        editCurrentBook: self.editCurrentBook,
        deleteBook: self.deleteBook,
        resetBooks: self.resetBooks
    };
})();

//knockout bindings
ko.applyBindings(navbarModel, $("#navbar")[0]);
ko.applyBindings(booksModel, $("#booksTable")[0]);
ko.applyBindings(modalModel, $("#bookModal")[0]);

//necessary to prevent default because page reload would reset knockout models
$('#byGenreForm').submit(function(event) {
    event.preventDefault();
});

//with only 'change' would trigger on page load, hence 'keyup' is added
$('#chooseGenre').bind('keyup change', function(){
    booksModel.getAllBooks("?genre=" + navbarModel.chooseByGenre());
});

//necessary to prevent default because page reload would reset knockout models
$('#searchForm').submit(function(event) {
    event.preventDefault();
    var str = navbarModel.searchString();
    booksModel.getAllBooks("?search=" + str);
    navbarModel.searchString("");
});

//necessary to prevent default because page reload would reset knockout models
$('#showAllNovelsbtn').click(function (event) {
    event.preventDefault();
    booksModel.getAllBooks();
});

//show modalFrom with add button
$('#showAddModalbtn').click(function () {
    modalModel.showBookModal("Add", new Book(), $('#addBookbtn'), $('#editBookbtn'));
});

//reset books to initial state
$('#resetNovelsbtn').click(function (event) {
    event.preventDefault();
    booksModel.resetBooks();
});

//optimalization: only one handler for all buttons in knockout template
var everyEditButtonInTable = "tr td button.btn-primary";
$('#booksTableBody').on('click', everyEditButtonInTable, function () {
    var bookToEdit = ko.dataFor(this);
    //show modalFrom with edit button on click
    modalModel.showBookModal("Edit", bookToEdit, $('#editBookbtn'), $('#addBookbtn'));
});

var checkValidity = function ($form) {
    if($form[0].checkValidity()) {
        return true;
    }
    toastr.error('author, title : required, price: max 2 decimal places', 'Invalid Input!');
    return false;
};

var submitAddBook = function () {
    if (checkValidity($('#modalForm'))) {
        booksModel.addCurrentBook();
    }
};

var submitEditBook = function () {
    if (modalModel.isSavedBookSameAsCurrentBook()) { 
        toastr.warning('There is nothing to update.', 'WARNING');
        return false;
    }
    
    if (checkValidity($('#modalForm'))) {
        booksModel.editCurrentBook();
        return false;
    }
};

$('#modalForm').keydown(function (event) {
    if (event.keyCode === 13) {
        var modalTitle = $('.modal-header h4.modal-title').text();
        
        if (modalTitle === "Add") {
            submitAddBook();
        } else {
            submitEditBook();
        }
    }
});

$('#addBookbtn').click(function () {
    submitAddBook();
});

$('#editBookbtn').click(function () {
    submitEditBook();
});

//optimalization: only one handler for all buttons in knockout template
var everyTrashButtonInTable = "tr td button.btn-danger";
$('#booksTableBody').on('click', everyTrashButtonInTable, function () {
    var bookToRemove = ko.dataFor(this);
    booksModel.deleteBook(bookToRemove);
});