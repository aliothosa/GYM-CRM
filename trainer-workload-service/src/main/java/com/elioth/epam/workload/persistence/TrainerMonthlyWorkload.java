package com.elioth.epam.workload.persistence;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trainer_monthly_workload",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_trainer_workload_username_year_month",
                columnNames = {
                        "trainer_username",
                        "workload_year",
                        "workload_month"
                }
        )
)
public class TrainerMonthlyWorkload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainer_username", nullable = false, length = 100)
    private String trainerUsername;

    @Column(name = "trainer_first_name", nullable = false, length = 100)
    private String trainerFirstName;

    @Column(name = "trainer_last_name", nullable = false, length = 100)
    private String trainerLastName;

    @Column(name = "trainer_active", nullable = false)
    private boolean trainerActive;

    @Column(name = "workload_year", nullable = false)
    private int workloadYear;

    @Column(name = "workload_month", nullable = false)
    private int workloadMonth;

    @Column(name = "training_duration_minutes", nullable = false)
    private long trainingDurationMinutes;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
