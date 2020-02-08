Feature: Easy travel ticket booking

  @demo 
  Scenario: Travel booking function
     Given Open the browser
     Then click on loginicon
     Then Enter Username and Password and click on login button
     Then Fill the journey details and click on search button
     And click on booknow
     And click on next button for hotel confirmation
     And Enter the card details and click on next button 
     Then click on finish button
