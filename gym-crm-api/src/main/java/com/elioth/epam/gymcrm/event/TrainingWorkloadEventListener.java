package com.elioth.epam.gymcrm.event;

import com.elioth.epam.gymcrm.client.workload.TrainerWorkloadClient;
import com.elioth.epam.gymcrm.client.workload.TrainerWorkloadRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TrainingWorkloadEventListener {

    private final TrainerWorkloadClient workloadClient;

    public TrainingWorkloadEventListener(TrainerWorkloadClient workloadClient) {
        this.workloadClient = workloadClient;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onTrainingWorkloadChanged(
            TrainingWorkloadChangedEvent event
    ) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                event.trainerUsername(),
                event.trainerFirstName(),
                event.trainerLastName(),
                event.trainerActive(),
                event.trainingDate(),
                event.trainingDurationMinutes(),
                event.action().name()
        );

        workloadClient.updateWorkload(request);
    }
}
