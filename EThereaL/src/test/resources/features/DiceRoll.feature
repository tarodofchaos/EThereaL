Feature: Roll some dice

  Scenario: Roll the dice
    Given I roll three 4 sided dice
    And a bonus of 2
    When the dice roll
    Then value must be between 5 and 14 
    
   Scenario: Roll an invalid die
    Given I roll an invalid die
    When the dice roll
    Then value must be -1 

#    Examples: 
#      | roll  | value | status  |
#      | value |     6 | success |
#      | value |    17 | fail    |
