@integration
Feature: Training workload synchronization between microservices

  @positive
  Scenario: Creating and deleting a training synchronizes the trainer workload
    Given the Gym CRM API and trainer workload service are available
    And an authenticated API client has recorded the baseline workload for trainer John Doe
    When the client creates a training for John Doe through the Gym CRM API
    Then the trainer workload service eventually reports the added workload
    When the client deletes the created training through the Gym CRM API
    Then the trainer workload service eventually returns to the baseline workload

  @negative
  Scenario: Repeating a deleted training request does not change the trainer workload
    Given the Gym CRM API and trainer workload service are available
    And an authenticated API client has recorded the baseline workload for trainer John Doe
    When the client creates a training for John Doe through the Gym CRM API
    Then the trainer workload service eventually reports the added workload
    When the client deletes the created training through the Gym CRM API
    Then the trainer workload service eventually returns to the baseline workload
    When the client deletes the same training again through the Gym CRM API
    Then the Gym CRM API rejects the repeated deletion
    And the trainer workload service still reports the baseline workload
