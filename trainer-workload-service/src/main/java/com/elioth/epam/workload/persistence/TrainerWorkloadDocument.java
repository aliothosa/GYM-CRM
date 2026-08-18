package com.elioth.epam.workload.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "trainer_workloads")
@CompoundIndex(
        name = "trainer_name_idx",
        def = "{'trainerFirstName': 1, 'trainerLastName': 1}"
)
public class TrainerWorkloadDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String trainerUsername;

    private String trainerFirstName;
    private String trainerLastName;
    private Boolean trainerStatus;
    private List<YearSummary> years = new ArrayList<>();
}
