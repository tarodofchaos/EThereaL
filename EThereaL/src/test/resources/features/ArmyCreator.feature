Feature: Army creation and validation

    It involves all the scenarios of creating a validating an Army
    Consisting in a List of Hero and a List of Monster.
    This covers all the Transform steps for a process. 

  Background:
    Given I have a valid Hero
    And a valid Monster

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
    
  Scenario Outline: Validation of an army
    Given I generate an army from an API call
    And a <army> has a <value> as <attribute>
    When the army validator is invoked
    Then I should get a <boolean> value

    Examples:
    | army    |                                                            value                                       | attribute | boolean |
    |    hero |                                                                                                    100 |       dex |   false |
    |    hero |          "Several Species of Small Furry Animals Gathered Together in a Cave and Grooving with a Pict" |      race |   false |
    |    hero |                                                                                             "Musician" |     class |   false |
    | monster |                                                                                              "4ad5+12" | hitpoints |   false |
    | monster |                                                                                                  "300" |     level |   false |
    | monster | "The Attack Of The Wrath Of The War Of The Death Of The Strike Of The Sword Of The Blood Of The Beast" |      name |   false |
    |    hero |                                                                                                     20 |       dex |    true |
    |    hero |                                                                                                  "Elf" |      race |    true |
    |    hero |                                                                                              "Fighter" |     class |    true |
    | monster |                                                                                               "4d5+12" | hitpoints |    true |
    | monster |                                                                                                   "10" |     level |    true |
    | monster |                                                                                            "The Beast" |      name |    true |
     
     