package Clustream.addon;

public class NullMonitor implements TaskMonitor {
	 
    @Override
    public void setCurrentActivity(String activityDescription,
            double fracComplete) {
    }

    @Override
    public void setCurrentActivityDescription(String activity) {
    }

    @Override
    public void setCurrentActivityFractionComplete(double fracComplete) {
    }

    @Override
    public boolean taskShouldAbort() {
        return false;
    }

    @Override
    public String getCurrentActivityDescription() {
        return null;
    }

    @Override
    public double getCurrentActivityFractionComplete() {
        return -1.0;
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void requestCancel() {
    }

    @Override
    public void requestPause() {
    }

    @Override
    public void requestResume() {
    }

    @Override
    public Object getLatestResultPreview() {
        return null;
    }

    @Override
    public void requestResultPreview() {
    }

    @Override
    public boolean resultPreviewRequested() {
        return false;
    }

    @Override
    public void setLatestResultPreview(Object latestPreview) {
    }

    @Override
    public void requestResultPreview(ResultPreviewListener toInform) {
    }
}


