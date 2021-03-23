//formatter

var ref = firebase.database().ref().child('services');

const arrayLabels = [
  'Computer Repair',
  'Home Cleaning',
  'Plumbing',
  'Electrical',
  'Moving',
  'Delivery',
  'Aircon',
  'Home Repair',
  'Auto Repair',
];
//First chart
ref.on('value', (serviceProvider) => {
  var servicesCountArray = [0, 0, 0, 0, 0, 0, 0, 0, 0];
  serviceProvider.forEach((service) => {
    service.forEach((serviceData) => {
      var data = serviceData.val();
      //Check the category for the first chart
      checkCategory(data.category);
    });
  });
  function checkCategory(category) {
    switch (category) {
      case 'Computer Repair':
        servicesCountArray[0] += 1;
        break;
      case 'Home Cleaning':
        servicesCountArray[1] += 1;
        break;
      case 'Plumbing':
        servicesCountArray[2] += 1;
        break;
      case 'Electrical':
        servicesCountArray[3] += 1;
        break;
      case 'Moving':
        servicesCountArray[4] += 1;
        break;
      case 'Delivery':
        servicesCountArray[5] += 1;
        break;
      case 'Aircon':
        servicesCountArray[6] += 1;
        break;
      case 'Home Repair':
        servicesCountArray[7] += 1;
        break;
      case 'Auto Repair':
        servicesCountArray[8] += 1;
        break;
    }
  }

  //data
  data = {
    datasets: [
      {
        data: servicesCountArray,
        backgroundColor: [
          'rgba(255, 99, 132, 0.5)',
          'rgba(54, 162, 235, 0.5)',
          'rgba(2, 25, 120, 0.5)',
          'rgba(75, 192, 192, 0.5)',
          'rgba(153, 102, 255, 0.5)',
          'rgba(255, 159, 64, 0.5)',
          'rgba(25, 87, 87, 0.5)',
          'rgba(40, 255, 2, 0.5)',
          'rgba(22, 11, 255, 0.5)',
        ],
        hoverOffset: 4,
      },
    ],
    labels: arrayLabels,
  };
  //Options
  var options = {
    tooltips: {
      enabled: true,
    },
    title: {
      display: true,
      text: 'Services Listed Per Service Category',
      fontSize: 24,
    },
    plugins: {
      datalabels: {
        formatter: (value, ctx) => {
          let datasets = ctx.chart.data.datasets;
          if (datasets.indexOf(ctx.dataset) === datasets.length - 1) {
            let sum = datasets[0].data.reduce((a, b) => a + b, 0);
            let percentage = Math.round((value / sum) * 100) + '%';
            return percentage;
          } else {
            return percentage;
          }
        },
        color: '#fff',
      },
    },
  };
  //Chart
  var ctx = document
    .getElementById('servicesListedPerCategory')
    .getContext('2d');
  var myPieChart = new Chart(ctx, {
    type: 'doughnut',
    data: data,
    options: options,
  });
});

//Second chart
ref.on('value', (snap) => {
  var servicesAveragePriceArray = [];

  let priceArray = [0, 0, 0, 0, 0, 0, 0, 0, 0];
  let totalCount = [0, 0, 0, 0, 0, 0, 0, 0, 0];
  let average = [0, 0, 0, 0, 0, 0, 0, 0, 0];
  let someArray = [];
  let array0 = [];
  let array1 = [];
  let array2 = [];
  let array3 = [];
  let array4 = [];
  let array5 = [];
  let array6 = [];
  let array7 = [];
  let array8 = [];
  let mostExpensiveArray = [];
  let cheapestArray = [];

  snap.forEach((serviceprovider) => {
    serviceprovider.forEach((data) => {
      someArray.push(data.val());
    });
  });

  //execute everything here
  someArray.forEach(myFunction);
  function myFunction(data) {
    switch (data.category) {
      case 'Computer Repair':
        priceArray[0] += data.price;
        totalCount[0] += 1;
        array0.push(data.price);
        break;
      case 'Home Cleaning':
        priceArray[1] += data.price;
        totalCount[1] += 1;
        array1.push(data.price);
        break;
      case 'Plumbing':
        priceArray[2] += data.price;
        totalCount[2] += 1;
        array2.push(data.price);
        break;
      case 'Electrical':
        priceArray[3] += data.price;
        totalCount[3] += 1;
        array3.push(data.price);
        break;
      case 'Moving':
        priceArray[4] += data.price;
        totalCount[4] += 1;
        array4.push(data.price);
        break;
      case 'Delivery':
        priceArray[5] += data.price;
        totalCount[5] += 1;
        array5.push(data.price);
        break;
      case 'Aircon':
        priceArray[6] += data.price;
        totalCount[6] += 1;
        array6.push(data.price);
        break;
      case 'Home Repair':
        priceArray[7] += data.price;
        totalCount[7] += 1;
        array7.push(data.price);
        break;
      case 'Auto Repair':
        priceArray[8] += data.price;
        totalCount[8] += 1;
        array8.push(data.price);
        break;
    }
  }
  getLowestAndHighest();
  for (i = 0; i < 9; i++) {
    let averagea = priceArray[i] / totalCount[i];
    if (isNaN(averagea)) {
      average[i] = 0;
    } else {
      average[i] = averagea.toFixed(2);
    }

    if (!isFinite(mostExpensiveArray[i])) {
      mostExpensiveArray[i] = 0;
    }
    if (!isFinite(cheapestArray[i])) {
      cheapestArray[i] = 0;
    }
  }

  function getLowestAndHighest() {
    mostExpensiveArray[0] = Math.max(...array0);
    cheapestArray[0] = Math.min(...array0);
    mostExpensiveArray[1] = Math.max(...array1);
    cheapestArray[1] = Math.min(...array1);
    mostExpensiveArray[2] = Math.max(...array2);
    cheapestArray[2] = Math.min(...array2);
    mostExpensiveArray[3] = Math.max(...array3);
    cheapestArray[3] = Math.min(...array3);
    mostExpensiveArray[4] = Math.max(...array4);
    cheapestArray[4] = Math.min(...array4);
    mostExpensiveArray[5] = Math.max(...array5);
    cheapestArray[5] = Math.min(...array5);
    mostExpensiveArray[6] = Math.max(...array6);
    cheapestArray[6] = Math.min(...array6);
    mostExpensiveArray[7] = Math.max(...array7);
    cheapestArray[7] = Math.min(...array7);
    mostExpensiveArray[8] = Math.max(...array8);
    cheapestArray[8] = Math.min(...array8);
  }

  //Options
  var options2 = {
    title: {
      display: true,
      text: 'Price Per Category(Php)',
      fontSize: 24,
    },
    legend: { display: false },
    scales: {
      xAxes: [
        {
          gridLines: {
            offsetGridLines: true,
          },
          ticks: {
            beginAtZero: true,
          },
        },
      ],
    },
  };
  //data
  data2 = {
    datasets: [
      {
        data: average,
        backgroundColor: [
          'rgba(255, 99, 132, 0.5)',
          'rgba(54, 162, 235, 0.5)',
          'rgba(2, 25, 120, 0.5)',
          'rgba(75, 192, 192, 0.5)',
          'rgba(153, 102, 255, 0.5)',
          'rgba(255, 159, 64, 0.5)',
          'rgba(25, 87, 87, 0.5)',
          'rgba(40, 255, 2, 0.5)',
          'rgba(22, 11, 255, 0.5)',
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(2, 25, 120, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
          'rgba(255, 159, 64,1)',
          'rgba(25, 87, 87,1)',
          'rgba(40, 255, 2, 1)',
          'rgba(22, 11, 255, 1)',
        ],
        borderWidth: 1,
        hoverOffset: 4,
        label: 'Total Average Price',
      },
      {
        data: cheapestArray,
        backgroundColor: [
          'rgba(255, 99, 132, 0.5)',
          'rgba(54, 162, 235, 0.5)',
          'rgba(2, 25, 120, 0.5)',
          'rgba(75, 192, 192, 0.5)',
          'rgba(153, 102, 255, 0.5)',
          'rgba(255, 159, 64, 0.5)',
          'rgba(25, 87, 87, 0.5)',
          'rgba(40, 255, 2, 0.5)',
          'rgba(22, 11, 255, 0.5)',
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(2, 25, 120, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
          'rgba(255, 159, 64,1)',
          'rgba(25, 87, 87,1)',
          'rgba(40, 255, 2, 1)',
          'rgba(22, 11, 255, 1)',
        ],
        borderWidth: 1,
        hoverOffset: 4,
        label: 'Cheapest Listing',
      },
      {
        data: mostExpensiveArray,
        backgroundColor: [
          'rgba(255, 99, 132, 0.5)',
          'rgba(54, 162, 235, 0.5)',
          'rgba(2, 25, 120, 0.5)',
          'rgba(75, 192, 192, 0.5)',
          'rgba(153, 102, 255, 0.5)',
          'rgba(255, 159, 64, 0.5)',
          'rgba(25, 87, 87, 0.5)',
          'rgba(40, 255, 2, 0.5)',
          'rgba(22, 11, 255, 0.5)',
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(2, 25, 120, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
          'rgba(255, 159, 64,1)',
          'rgba(25, 87, 87,1)',
          'rgba(40, 255, 2, 1)',
          'rgba(22, 11, 255, 1)',
        ],
        borderWidth: 1,
        hoverOffset: 4,
        label: 'Most Expensive Listing',
      },
    ],
    labels: arrayLabels,
  };
  //Chart
  var ctx = document.getElementById('averagePricePerCategory');
  var myChart = new Chart(ctx, {
    type: 'bar',
    data: data2,
    options: options2,
  });
});

//Third chart
var ref2 = firebase.database().ref().child('service_requests');
ref2.on('value', (snapshot) => {
  let countArray = [0, 0, 0, 0, 0, 0, 0, 0, 0];
  let categoryArray = [];

  snapshot.forEach((request) => {
    categoryArray.push(request.val().category);
  });

  categoryArray.forEach((value) => {
    switch (value) {
      case 'Computer Repair':
        countArray[0] += 1;
        break;
      case 'Home Cleaning':
        countArray[1] += 1;
        break;
      case 'Plumbing':
        countArray[2] += 1;
        break;
      case 'Electrical':
        countArray[3] += 1;
        break;
      case 'Moving':
        countArray[4] += 1;
        break;
      case 'Delivery':
        countArray[5] += 1;
        break;
      case 'Aircon':
        countArray[6] += 1;
        break;
      case 'Home Repair':
        countArray[7] += 1;
        break;
      case 'Auto Repair':
        countArray[8] += 1;
        break;
    }
  });
  var data = {
    datasets: [
      {
        label: 'Total request listed',
        data: countArray,
        backgroundColor: [
          'rgba(255, 99, 132, 0.5)',
          'rgba(54, 162, 235, 0.5)',
          'rgba(2, 25, 120, 0.5)',
          'rgba(75, 192, 192, 0.5)',
          'rgba(153, 102, 255, 0.5)',
          'rgba(255, 159, 64, 0.5)',
          'rgba(25, 87, 87, 0.5)',
          'rgba(40, 255, 2, 0.5)',
          'rgba(22, 11, 255, 0.5)',
        ],
      },
    ],

    // These labels appear in the legend and in the tooltips when hovering different arcs
    labels: arrayLabels,
  };
  //options
  var options = {
    title: {
      display: true,
      text: 'Real-Time Most Requested Service',
      fontSize: 24,
    },
    legend: { display: false },
    scales: {
      xAxes: [
        {
          gridLines: {
            offsetGridLines: true,
          },
          ticks: {
            beginAtZero: true,
          },
        },
      ],
    },
  };

  //horizontal bar chart
  var ctx = document.getElementById('mostRequested').getContext('2d');
  var myBarChart = new Chart(ctx, {
    type: 'horizontalBar',
    data: data,
    options: options,
  });
});

//Fourth chart
var ref3 = firebase.database().ref().child('booked_by');
ref3.on('value', (snap) => {
  let bookings = [];
  var one = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  var two = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  var three = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  var four = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  var five = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  var six = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  var seven = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  var eight = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  var nine = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  snap.forEach((buyer) => {
    buyer.forEach((booking) => {
      //get the object
      let bookingObj = booking.val();
      //puish to the array
      bookings.push(bookingObj);
      //get the date
      var d = new Date(bookingObj.dateOrdered);
      var monthOrdered = d.getMonth() + 1;

      checkNow(monthOrdered, bookingObj.category);
    });
  });

  function checkNow(monthOrdered, category) {
    switch (category) {
      case 'Computer Repair':
        addValueToArray(one, monthOrdered);
        break;
      case 'Home Cleaning':
        addValueToArray(two, monthOrdered);
        break;
      case 'Plumbing':
        addValueToArray(three, monthOrdered);
        break;
      case 'Electrical':
        addValueToArray(four, monthOrdered);
        break;
      case 'Moving':
        addValueToArray(five, monthOrdered);
        break;
      case 'Delivery':
        addValueToArray(six, monthOrdered);
        break;
      case 'Aircon':
        addValueToArray(seven, monthOrdered);
        break;
      case 'Home Repair':
        addValueToArray(eight, monthOrdered);
        break;
      case 'Auto Repair':
        addValueToArray(nine, monthOrdered);
        break;
    }
  }

  function addValueToArray(array, month) {
    if (month == 1) {
      array[0] += 1;
    } else if (month == 2) {
      array[1] += 1;
    } else if (month == 3) {
      array[2] += 1;
    } else if (month == 4) {
      array[3] += 1;
    } else if (month == 5) {
      array[4] += 1;
    } else if (month == 6) {
      array[5] += 1;
    } else if (month == 7) {
      array[6] += 1;
    } else if (month == 8) {
      array[7] += 1;
    } else if (month == 9) {
      array[8] += 1;
    } else if (month == 10) {
      array[9] += 1;
    } else if (month == 11) {
      array[10] += 1;
    } else if (month == 12) {
      array[11] += 1;
    }
  }
  //Data
  var data = {
    labels: [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec',
    ],
    datasets: [
      {
        data: one,
        label: 'Computer Repair',
        borderColor: 'rgba(255, 99, 132, 0.5)',
        fill: false,
      },
      {
        data: two,
        label: 'Home Cleaning',
        borderColor: 'rgba(54, 162, 235, 0.5)',
        fill: false,
      },
      {
        data: three,
        label: 'Plumbing',
        borderColor: 'rgba(2, 25, 120, 0.5)',
        fill: false,
      },
      {
        data: four,
        label: 'Electrical',
        borderColor: 'rgba(75, 192, 192, 0.5)',
        fill: false,
      },
      {
        data: five,
        label: 'Moving',
        borderColor: 'rgba(153, 102, 255, 0.5)',
        fill: false,
      },
      {
        data: six,
        label: 'Delivery',
        borderColor: 'rgba(255, 159, 64, 0.5)',
        fill: false,
      },
      {
        data: seven,
        label: 'Aircon',
        borderColor: 'rgba(25, 87, 87, 0.5)',
        fill: false,
      },
      {
        data: eight,
        label: 'Home Repair',
        borderColor: 'rgba(40, 255, 2, 0.5)',
        fill: false,
      },
      {
        data: nine,
        label: 'Auto Repair',
        borderColor: 'rgba(22, 11, 255, 0.5)',
        fill: false,
      },
    ],
  };
  //Options
  var options = {
    title: {
      display: true,
      text: 'Bookings Per Category',
      fontSize: 24,
    },
    elements: {
      line: {
        tension: 0,
      },
    },
  };
  //Chart
  var ctx = document.getElementById('lineChart').getContext('2d');
  var myChart = new Chart(ctx, {
    type: 'line',
    data: data,
    options: options,
  });
});

//Fifth chart and sixth chart
var ref4 = firebase.database().ref().child('users');
ref4.on('value', (snap) => {
  let ageLabel = ['18-24', '25-39', '40-60', '60+'];
  let ageCount = [0, 0, 0, 0];
  let userLabel = ['Buyer', 'Service Provider'];
  let userIntentCount = [0, 0];
  let users = [];
  snap.forEach((user) => {
    users.push(user.val());
  });

  users.forEach((object) => {
    object.age >= 18 && object.age <= 24 ? (ageCount[0] += 1) : null;
    object.age >= 25 && object.age <= 39 ? (ageCount[1] += 1) : null;
    object.age >= 40 && object.age <= 60 ? (ageCount[2] += 1) : null;
    object.age >= 61 ? (ageCount[3] += 1) : null;

    object.verified == 'VERIFIED'
      ? (userIntentCount[1] += 1)
      : (userIntentCount[0] += 1);
  });

  //data
  dataAge = {
    datasets: [
      {
        data: ageCount,
        backgroundColor: [
          'rgba(255, 99, 132, 0.5)',
          'rgba(54, 162, 235, 0.5)',
          'rgba(2, 25, 120, 0.5)',
          'rgba(75, 192, 192, 0.5)',
        ],
        hoverOffset: 4,
      },
    ],
    labels: ageLabel,
  };
  dataUsers = {
    datasets: [
      {
        data: userIntentCount,
        backgroundColor: ['rgba(255, 99, 132, 0.5)', 'rgba(54, 162, 235, 0.5)'],
        hoverOffset: 4,
      },
    ],
    labels: userLabel,
  };
  //Options
  var options = {
    tooltips: {
      enabled: true,
    },
    title: {
      display: true,
      text: 'Age Demographics',
      fontSize: 24,
    },
    plugins: {
      datalabels: {
        formatter: (value, ctx) => {
          let datasets = ctx.chart.data.datasets;
          if (datasets.indexOf(ctx.dataset) === datasets.length - 1) {
            let sum = datasets[0].data.reduce((a, b) => a + b, 0);
            let percentage = Math.round((value / sum) * 100) + '%';
            return percentage;
          } else {
            return percentage;
          }
        },
        color: '#fff',
      },
    },
  };
  var options2 = {
    tooltips: {
      enabled: true,
    },
    title: {
      display: true,
      text: 'Users and Service Providers Percentage',
      fontSize: 24,
    },
    plugins: {
      datalabels: {
        formatter: (value, ctx) => {
          let datasets = ctx.chart.data.datasets;
          if (datasets.indexOf(ctx.dataset) === datasets.length - 1) {
            let sum = datasets[0].data.reduce((a, b) => a + b, 0);
            let percentage = Math.round((value / sum) * 100) + '%';
            return percentage;
          } else {
            return percentage;
          }
        },
        color: '#fff',
      },
    },
  };
  //Age
  var ctx = document.getElementById('demographicsAge').getContext('2d');
  var myPieChart = new Chart(ctx, {
    type: 'pie',
    data: dataAge,
    options: options,
  });

  //Users
  var ctx = document.getElementById('demographicsUser').getContext('2d');
  var myPieChart = new Chart(ctx, {
    type: 'pie',
    data: dataUsers,
    options: options2,
  });
});
