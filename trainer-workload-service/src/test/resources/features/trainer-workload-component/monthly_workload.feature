@component @trainer-workload-service
Feature: Monthly trainer workload management

  @positive
  Scenario: Add a trainer workload to a new month
    Given no workload exists for trainer "john.component"
    When the authenticated client adds 60 workload minutes for trainer "john.component" on "2026-08-05"
    Then the workload update is accepted
    And the stored monthly workload for trainer "john.component" in 2026-8 is 60 minutes
    And the authenticated client can retrieve 60 workload minutes for trainer "john.component" in 2026-8

  @negative @edge
  Scenario: Reject a workload with a non-positive duration
    Given no workload exists for trainer "john.component"
    When the authenticated client submits a workload with zero minutes for trainer "john.component"
    Then the workload update is rejected
    And no workload is stored

  @negative @edge
  Scenario: Reject deleting a workload that was never added
    Given no workload exists for trainer "john.component"
    When the authenticated client deletes 60 workload minutes for trainer "john.component" on "2026-08-05"
    Then the workload update is rejected
    And no workload is stored
