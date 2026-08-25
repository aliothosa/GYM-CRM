@component @gym-crm-api
Feature: Training management in the Gym CRM API

  @positive
  Scenario: Create a training and publish its workload change
    Given an existing trainer "trainer.component" and trainee "trainee.component"
    When the authenticated client creates a 60-minute training on "2026-08-05" for trainer "trainer.component" and trainee "trainee.component"
    Then the training is accepted
    And an ADD workload message for trainer "trainer.component" with 60 minutes is sent

  @negative
  Scenario: Reject a training for an unknown trainer
    Given an existing trainer "trainer.component" and trainee "trainee.component"
    When the authenticated client creates a 60-minute training on "2026-08-05" for trainer "missing.trainer" and trainee "trainee.component"
    Then the training is rejected as invalid
    And no workload message is sent
