package main.job.daily;

import exceptions.JobException;
import main.job.JobEnum;

public class UpdateSplitsJob extends DailyJob {

    public UpdateSplitsJob() {
        super(JobEnum.UPDATE_SPLITS);        
    }

    @Override
    protected void doUpdate() throws JobException {
                
    }

}
