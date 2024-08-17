let maxDate = new Date();
 maxDate = maxDate.setMonth(maxDate.getMonth() + 3);
 
 flatpickr('#reservationDate', {
   mode: "range",
   locale: 'ja',
   minDate: 'today',
   maxDate: maxDate,
   enableTime: true,
  dateFormat: "Y-m-d H:i"
 });