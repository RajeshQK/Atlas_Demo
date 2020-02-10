@APITest
Feature: Create User Details
  

  @demo @Dev
  Scenario: Verify user creation
     Given Post endpoint given
    When Enter Username and Job
    Then Post the api
    And Get response
