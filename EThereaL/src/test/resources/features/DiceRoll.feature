@tag
Feature: Roll some dice

  @tag1
  Scenario: Roll the dice
    Given I roll three 4 sided dice
    And a bonus of 2
    When the dice roll
    Then value must be between 5 and 14 

#    Examples: 
#      | roll  | value | status  |
#      | value |     6 | success |
#      | value |    17 | fail    |
