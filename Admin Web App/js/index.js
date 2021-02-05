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



//**********************INDEX.HTML*********************//
var ref = firebase.database().ref().child("for-verification");
ref.on("child_added", snap => {
  var name = snap.child("name").val();
  var age = snap.child("age").val();
  var number = snap.child("number").val();
  var selfieImage = snap.child("selfie").val();
  var validId = snap.child("id").val();
  var certificate = snap.child("docu").val();
  var uid = snap.child("uid").val();

  $("#tableBody").append(
    "<tr>" +
    "<td>" + name + "</td>" +
    "<td>" + age + "</td>" +
    "<td>" + number + "</td>" +
    "<td><a href='" + selfieImage + "'>Click here</a></td>" +
    "<td><a href='" + validId + "'>Click here</a></td>" +
    "<td><a href='" + certificate + "'>Click here</a></td>" +
    "<td><button value='" + uid + "' id='verifybtn' onclick='verify(this.value)'>Verify</button><button value='" + uid + "' id='notnowbtn' onclick='notNow(this.value)'>Not Now</button></td>" +
    "</tr>"
  )

});


function verify(val) {
  firebase.database().ref('users/' + val).update({
    verified: "VERIFIED"
  }, (error) => {
    if (error) {
      //failed
    } else {
      //successful
      firebase.database().ref('for-verification/' + val).remove();
      location.reload();
      alert("Account " + val + " is now verified.")
    }

  })

}

function notNow(val) {
  firebase.database().ref('users/' + val).update({
    verified: "TRY_AGAIN"
  }, (error) => {
    if (error) {
      //failed
    } else {
      //successful
      firebase.database().ref('for-verification/' + val).remove();
      location.reload();
      alert("Account " + val + " is not verified.")
    }

  })
}











