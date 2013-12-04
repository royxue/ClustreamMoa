package Clustream.addon;

public interface TaskMonitor {
	 
    public void setCurrentActivity(String activityDescription,
            double fracComplete);

    public void setCurrentActivityDescription(String activity);

    public void setCurrentActivityFractionComplete(double fracComplete);

    public boolean taskShouldAbort();

    public boolean resultPreviewRequested();

    public void setLatestResultPreview(Object latestPreview);

    public String getCurrentActivityDescription();

    public double getCurrentActivityFractionComplete();

    public void requestPause();

    public void requestResume();

    public void requestCancel();

    public boolean isPaused();

    public boolean isCancelled();

    public void requestResultPreview();

    public void requestResultPreview(ResultPreviewListener toInform);
    //public void requestResultPreview(ResultPreviewListener toInform);

    public Object getLatestResultPreview();
}