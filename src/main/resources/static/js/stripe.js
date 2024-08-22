const stripe = Stripe('pk_test_51PWI2ZRwbHQlEjnL8fDq1zfgUoHHTRCBOZ3EjxnfPhPgqGvTZ6JtdGHL2uD4pSmrJ8Ijo8szA7tKgshbusVMhH2A00AwwJ4Wpl');
 const paymentButton = document.querySelector('#paymentButton');
 
 paymentButton.addEventListener('click', () => {
   stripe.redirectToCheckout({
     sessionId: sessionId
   })
 });