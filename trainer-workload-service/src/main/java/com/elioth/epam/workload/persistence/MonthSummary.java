package com.elioth.epam.workload.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthSummary {

    private int month;
    private long trainingsSummaryDuration;
}
