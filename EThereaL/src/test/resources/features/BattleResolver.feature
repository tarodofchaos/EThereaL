Feature: Army creation via API Gateway

  Scenario: Generate an army with fifty heroes and a thousand monsters
    Given I generate an army from an API call
    When the army creator is called
    Then the army size has to be fifty Heroes 
    And and a thousand Monsters 
    And no errors happened
    
  Scenario: Try to generate an army with more heroes than available
    Given I generate an army from an API call with more heroes than available
    When the army creator is called
    Then I should get an Exception with the message "Not enough heroes to create the army!!"
    
  Scenario: Try to generate an army with an invalid parameter
    Given I generate an army from an API call with invalid parameters
    When the army creator is called
    Then I should get an Exception with the error message