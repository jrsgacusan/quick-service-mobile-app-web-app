
// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
var firebaseConfig = {
  apiKey: "AIzaSyD3xvk1o2I1VZdgsQYUABU96rzlzY3gNjw",
  authDomain: "course-project-88fec.firebaseapp.com",
  databaseURL: "https://course-project-88fec.firebaseio.com",
  projectId: "course-project-88fec",
  storageBucket: "course-project-88fec.appspot.com",
  messagingSenderId: "412985801998",
  appId: "1:412985801998:web:bb774d95fbacbb188aeb5f",
  measurementId: "G-BZ38VP53KV"
};
// Initialize Firebase
firebase.initializeApp(firebaseConfig);
var database = firebase.database();

//**********************LOGIN.HTML*********************//

function signIn() {
  var email = document.getElementById("email");
  var password = document.getElementById("password");
  if (email.value == "") {
    console.log("Enter email");
    alert("Please enter your email.");
    email.focus();
    return;
  }

  if (password.value == "") {
    console.log("Enter a password");
    alert("Please enter a password.");
    password.focus();
    return;
  }

  firebase.auth().signInWithEmailAndPassword(email.value, password.value)
    .then((userCredential) => {
      //Signed in
      email.value = "";
      password.value = "";
      alert("You are now signed in!");
      var user = userCredential.user;
    })
    .catch((e) => {
      var errorMessage = e.message;
      alert(errorMessage)
    });


}


firebase.auth().onAuthStateChanged((user) => {
  if (user) {
    //Logged in
    window.location.href = "index.html"

  } else {
    // User is signed out
    // ...
    
  }
});








